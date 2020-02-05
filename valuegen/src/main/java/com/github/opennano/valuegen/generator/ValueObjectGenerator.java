package com.github.opennano.valuegen.generator;

import static com.github.opennano.valuegen.utils.ReflectionUtil.getTypeInfo;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.opennano.valuegen.GeneratorConfig;
import com.github.opennano.valuegen.generator.delegates.ArrayValueDelegate;
import com.github.opennano.valuegen.generator.delegates.BooleanValueDelegate;
import com.github.opennano.valuegen.generator.delegates.CharacterValueDelegate;
import com.github.opennano.valuegen.generator.delegates.CollectionValueDelegate;
import com.github.opennano.valuegen.generator.delegates.DateValueDelegate;
import com.github.opennano.valuegen.generator.delegates.EnumValueDelegate;
import com.github.opennano.valuegen.generator.delegates.FileValueDelegate;
import com.github.opennano.valuegen.generator.delegates.MapValueDelegate;
import com.github.opennano.valuegen.generator.delegates.NumberValueDelegate;
import com.github.opennano.valuegen.generator.delegates.ObjectValueDelegate;
import com.github.opennano.valuegen.generator.delegates.StringValueDelegate;
import com.github.opennano.valuegen.generator.delegates.UrlValueDelegate;

/**
 * This class is responsible for creating a value object for any type of object passed in. As this
 * is not a trivial task, the work is broken down by type and passed to a chain of {@link
 * ValueGeneratorDelegate} objects to do the actual work.
 */
public class ValueObjectGenerator {

  private List<ValueGeneratorDelegate> delegateChain;

  /**
   * Create a value object delegate chain according to the provided configuration. Any type for
   * which a value object is needed should be handled by at least one {@link ValueGeneratorDelegate}
   * in the chain.
   *
   * @param config the Jsongen configuration to be used
   * @return the delegate chain
   */
  public static List<ValueGeneratorDelegate> createDefaultDelegateChain(GeneratorConfig config) {
    return Arrays.asList(
        new StringValueDelegate(),
        new NumberValueDelegate(),
        new BooleanValueDelegate(),
        new DateValueDelegate(),
        new EnumValueDelegate(),
        new FileValueDelegate(),
        new CharacterValueDelegate(),
        new UrlValueDelegate(),
        new CollectionValueDelegate(),
        new ArrayValueDelegate(),
        new MapValueDelegate(config),
        new ObjectValueDelegate(config)); // should always be last
  }

  public ValueObjectGenerator(
      GeneratorConfig config, ValueGeneratorDelegate... additionalValueGenerators) {

    this(
        Stream.concat(
                Stream.of(additionalValueGenerators), createDefaultDelegateChain(config).stream())
            .collect(Collectors.toList()));
  }

  public ValueObjectGenerator(List<ValueGeneratorDelegate> delegateChain) {
    this.delegateChain = delegateChain;
  }

  /** standard value generation path for a {@link Field} */
  public Object valueFor(Field field, Class<?> owningClass) {

    // resolve whatever type we get into a class we can instantiate
    // and parameter types (e.g. Integer in List<Integer>) for the delegates
    TypeInfo typeInfo = getTypeInfo(field.getGenericType(), owningClass, field);

    return valueFor(typeInfo, field, field.getName(), owningClass);
  }

  /** standard value generation path for a class not directly associated to a field */
  public Object valueFor(
      TypeInfo typeInfo, Field declaringField, String nameHint, Class<?> owningClass) {

    Class<?> valueClass = typeInfo.getResolvedClass();
    ValueGeneratorDelegate delegate =
        delegateChain
            .stream()
            .filter(gen -> gen.handlesClass(valueClass))
            .findFirst()
            .orElseThrow(
                () -> new IllegalStateException("no suitable generator for type: " + valueClass));

    return delegate.generateValue(typeInfo, declaringField, nameHint, owningClass, this);
  }
}
