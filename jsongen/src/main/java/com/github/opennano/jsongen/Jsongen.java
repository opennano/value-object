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
import com.github.opennano.valuegen.Valuegen;
import com.github.opennano.valuegen.generator.strategies.MapKeyStrategy;

/**
 * Generate sample json for just about any pojo with this class. The quickest way to run is as
 * follows:
 *
 * <p><code>Jsongen.printSample(MyBean.class)</code> You may also get the json as a string via
 * {@link #asJson(Class)}.
 *
 * <p>A sample object is created and populated as much as possible, then serialized using GSON. For
 * simple pojos this should produce a complete object as formatted json. For more complex objects
 * the tool might skip fields that it can't figure out how to serialize. For more info on how value
 * objects are created see {@link Valuegen}.
 *
 * <p>In the event the value object needs to be customized, you can create a value object first
 * using {@link Valuegen#createValueObject(Class)}, apply customizations programmatically, then
 * serialize using {@link Jsongen#toJson(Object)}.
 *
 * <p>Object cycles are handled by setting subsequent encounters to null, thereby not serializing
 * them at all (null values are ignored by this library).
 *
 * <p>Proxy classes are handled by interpreting the proxy as a plain Object.
 *
 * <p>If an error occurs serializing a field a message will be logged and the field will be left
 * alone (typically defaulting to null and therefore not serialized).
 *
 * <p>Dates are serialized in UTC time zone in the following ISO8601 compliant format: {@value
 * #DATE_FORMAT}.
 *
 * <p>Map keys must be strings; other key types will be marshaled to an empty JSON object.
 */
public class Jsongen {

  private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
  private static final TimeZone UTC = TimeZone.getTimeZone("UTC");

  private static final GeneratorConfig generatorConfig;

  private static final BeanCycleInfo cycleInfo = new BeanCycleInfo();

  /**
   * don't include type information for obvious types or types that Jackson can't decorate as a
   * simple property
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
   */
  public static void printSample(Class<?> type) {
    System.out.println(asJson(type));
  }

  /**
   * Produce a value object serialized as json that is suitable for testing. A value object is
   * created and populated with mock data as much as possible, then converted to json.
   */
  public static String asJson(Class<?> type) {
    return toJson(Valuegen.createValueObject(type, generatorConfig));
  }

  /** Convert the provided object to json via {@link Gson}. */
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

    mapper.setMixInResolver(
        new MixInResolver() {

          @Override
          public Class<?> findMixInClassFor(Class<?> type) {
            if (Object.class.equals(type)
                || isProxy(type)
                || type.isArray()
                || isImplicitlyTyped(type)) {
              return cycleInfo.isCyclicType(type) ? IdentifiableMixin.class : NoTypePropertyMixin.class;
            }
            return cycleInfo.isCyclicType(type) ? IdentifiableWithTypeMixin.class : TypePropertyMixin.class;
          }

          private boolean isImplicitlyTyped(Class<?> type) {
            return IMPLICIT_TYPES.stream().anyMatch(imp -> imp.isAssignableFrom(type));
          }

          @Override
          public MixInResolver copy() {
            return this;
          }
        });

    try {
      return mapper.writeValueAsString(valueObject);
    } catch (JsonProcessingException e) {
      throw new IllegalStateException("failed to create json from value object: " + valueObject, e);
    }
  }

  private Jsongen() {
    // no-op: uninstantiable
  }

  @JsonTypeInfo(use = JsonTypeInfo.Id.NONE)
  private abstract static class NoTypePropertyMixin {}

  @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@type")
  private abstract static class TypePropertyMixin {}

  @JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
  private abstract static class IdentifiableMixin {}
  
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
