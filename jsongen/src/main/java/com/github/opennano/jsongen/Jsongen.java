package com.github.opennano.jsongen;

import static com.github.opennano.jsongen.serializer.JsongenSerializerModifier.isProxy;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.introspect.ClassIntrospector.MixInResolver;
import com.fasterxml.jackson.databind.ser.BeanSerializerFactory;
import com.fasterxml.jackson.databind.ser.SerializerFactory;
import com.github.opennano.jsongen.serializer.JsongenSerializerModifier;
import com.github.opennano.valuegen.GeneratorConfig;
import com.github.opennano.valuegen.ValueGenerationException;
import com.github.opennano.valuegen.Valuegen;
import com.github.opennano.valuegen.generator.ValueGeneratorDelegate;
import com.github.opennano.valuegen.generator.strategies.MapKeyStrategy;

/**
 * Generate sample json for just about any pojo with this class. The quickest way to run is as
 * follows:
 *
 * <p><code>Jsongen.printSample(MyBean.class)</code> You may also get the json as a string via
 * {@link #asJson(Class)}.
 *
 * <p>A sample object is created and recursively populated as much as possible, then serialized
 * using GSON. For simple pojos this should produce a complete object as formatted json. For more
 * complex objects the tool might skip fields that it can't figure out how to serialize. For more
 * info on how value objects are created see {@link Valuegen}.
 *
 * <p>In the event the value object is not generated to your liking, consider using a custom {@link
 * GeneratorConfig} or contribute extra {@link ValueGeneratorDelegate} instances to {@link
 * Valuegen#createValueObject(Type, GeneratorConfig, ValueGeneratorDelegate...)}. You may also take
 * a more "brute force" approach by programmatically altering the returned value object. In any
 * case, once the object is as desired in memory, serialize it using {@link Jsongen#toJson(Object)}.
 *
 * <p>Proxy classes, which are generated when {@link Valuegen} can't figure out a concrete subtype
 * to use, are treated like a plain Java {@link Object} (i.e. serialized to '{}').
 *
 * <p>If an error occurs serializing a field, the Java default value will apply (typically this
 * means it will be null and therefore not serialized).
 *
 * <p>Dates are serialized in UTC time zone in the following ISO8601 compliant format: {@value
 * #DATE_FORMAT}.
 */
public class Jsongen {

  private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
  private static final TimeZone UTC = TimeZone.getTimeZone("UTC");

  private static final GeneratorConfig generatorConfig;

  private static final BeanCycleInfo cycleInfo = new BeanCycleInfo();

  /**
   * Don't include type information for obvious types or types that Jackson can't decorate as a
   * simple property. For example, 1 is obviously an int and preferred to [{"type" :
   * "java.lang.int"}, 1] or any other such mess.
   */
  private static final Collection<Class<?>> IMPLICIT_TYPES =
      Collections.unmodifiableCollection(
          Arrays.asList(
              String.class,
              Collection.class,
              Map.class,
              Byte.class,
              Short.class,
              Long.class,
              Float.class,
              Double.class,
              Boolean.class,
              Character.class,
              Date.class,
              Calendar.class));

  static {
    // the default serialization strategy is not quite what we want
    // for one, Jackson can handle complex map key types so allow those
    // for two, when we detect a bean cycle we need to register it
    // this is so that we can tell Jackson to use a special reference property
    generatorConfig = new GeneratorConfig();
    generatorConfig.setMapKeyStrategy(MapKeyStrategy.ANY_KEY_TYPE);
    generatorConfig.setCycleStrategy(
        (type, value) -> {
          cycleInfo.registerCyclicType(type);
          return value;
        });
  }

  /**
   * Produce a value object serialized as json that is suitable for testing and prints the json to
   * standard out. A value object is created and populated with mock data as much as possible, then
   * converted to json.
   *
   * @param type the type of instance to generate
   */
  public static void printSample(Class<?> type) {
    System.out.println(asJson(type));
  }

  /**
   * Produce a value object serialized as json that is suitable for testing. A value object is
   * created and populated with mock data as much as possible, then converted to json.
   *
   * @param type the type of instance to generate
   * @return a value object serialized as json
   */
  public static String asJson(Class<?> type) {
    return toJson(Valuegen.createValueObject(type, generatorConfig));
  }

  /**
   * Convert the provided object to json via Jackson.
   *
   * @param valueObject a value object you wish to serialize
   * @return the provided value object serialized as json
   */
  public static String toJson(Object valueObject) {

    SerializerFactory serializerFactory =
        BeanSerializerFactory.instance.withSerializerModifier(new JsongenSerializerModifier());

    ObjectMapper mapper = new ObjectMapper();
    mapper.setSerializerFactory(serializerFactory);
    mapper.setVisibility(PropertyAccessor.ALL, Visibility.NONE);
    mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
    mapper.configure(SerializationFeature.WRITE_CHAR_ARRAYS_AS_JSON_ARRAYS, true);
    mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
    mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    mapper.setSerializationInclusion(Include.NON_NULL);
    mapper.setDateFormat(new SimpleDateFormat(DATE_FORMAT));
    mapper.setTimeZone(UTC);
    mapper.setLocale(Locale.US); // for consistency, should it even matter
    mapper.setMixInResolver(new IdAndTypeMixInResolver());

    try {
      return mapper.writeValueAsString(valueObject);
    } catch (JsonProcessingException e) {
      throw new ValueGenerationException(
          "failed to create json from value object: " + valueObject, e);
    }
  }

  private Jsongen() {
    // no-op: uninstantiable
  }

  public static final class IdAndTypeMixInResolver implements MixInResolver {
    @Override
    public Class<?> findMixInClassFor(Class<?> type) {
      if (Object.class.equals(type) || isProxy(type) || type.isArray() || isImplicitlyTyped(type)) {
        // doesn't appear to be any way to have a cycle with these types
        // the "just identifiable" mixin was thus removed
        return NoTypePropertyMixin.class;
      }
      return cycleInfo.isCyclicType(type)
          ? IdentifiableWithTypeMixin.class
          : TypePropertyMixin.class;
    }

    private boolean isImplicitlyTyped(Class<?> type) {
      return IMPLICIT_TYPES.stream().anyMatch(imp -> imp.isAssignableFrom(type));
    }

    @Override
    public MixInResolver copy() {
      return this;
    }
  }

  @JsonTypeInfo(use = JsonTypeInfo.Id.NONE)
  private abstract static class NoTypePropertyMixin {}

  @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@type")
  private abstract static class TypePropertyMixin {}

  @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@type")
  @JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
  private abstract static class IdentifiableWithTypeMixin {}

  public static class BeanCycleInfo {

    private Set<Class<?>> cyclicTypes = new HashSet<>();

    public void registerCyclicType(Class<?> valueClass) {
      cyclicTypes.add(valueClass);
    }

    public boolean isCyclicType(Class<?> type) {
      return cyclicTypes.contains(type);
    }
  }
}
