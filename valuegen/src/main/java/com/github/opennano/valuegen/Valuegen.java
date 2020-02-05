package com.github.opennano.valuegen;

import static com.github.opennano.valuegen.utils.AutoboxingUtil.boxedEquivalent;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

import org.objenesis.Objenesis;

import com.github.opennano.valuegen.generator.TypeInfo;
import com.github.opennano.valuegen.generator.ValueGeneratorDelegate;
import com.github.opennano.valuegen.generator.ValueObjectGenerator;
import com.github.opennano.valuegen.generator.strategies.CycleStrategy;
import com.github.opennano.valuegen.generator.strategies.SubtypeStrategy;
import com.github.opennano.valuegen.utils.ReflectionUtil;

/**
 * Generate a value object with mocked data for just about any pojo with this class. Usage:
 *
 * <p><code>Valuegen.createValueObject(MyBean.class)</code>
 *
 * <p>An object is instantiated and populated with mock data as much as possible. Every effort is
 * made to create some kind of value, but in the event a value can't be produced no value will be
 * set, in which case the field is left to its default value. The following is a non-exhaustive list
 * of interesting types and the strategies that are (or could be) applied to instantiating them. In
 * theory all behavior can be customized using a custom {@link ValueGeneratorDelegate}. See {@link
 * ValueObjectGenerator#createDefaultDelegateChain(GeneratorConfig)}.
 *
 * <ul>
 *   <li>Abstract classes and interfaces are resolved to subtypes where possible. When the default
 *       {@link SubtypeStrategy#UNIQUE_SUBTYPE} is used, a subtype is used as the value object type
 *       if exactly one exists on the classpath, otherwise a dynamic proxy class is created.
 *   <li>Object cycles are handled by reusing the value generated on the previous encounter when the
 *       default {@link CycleStrategy#REUSE_ANCESTOR_VALUE} is used.
 *   <li>Transient, static, or synthetic fields are ignored.
 *   <li>Generics are fully supported--wildcard types and type variables are are handled by
 *       inspecting the class hierarchy to determine the best type to use, for example a field of
 *       type T will generate a string value if somewhere in the type hierarchy, T is bound to
 *       String. Just about any generic mess can be resolved (see the scenario test for
 *       LudicrousTypeObject for an extreme example), but the complexity of reflecting on generic
 *       types in Java is not to be underestimated, so the resolution is probably not perfect.
 *   <li>Private and final access modifiers are disabled using {@link Field#setAccessible(boolean)}
 *   <li>Constructors are almost entirely unused, courtesy of {@link Objenesis}. In some cases,
 *       however, Objenesis may produce an unusable object.
 * </ul>
 */
public class Valuegen {

  /**
   * Use this type-safe convenience method to create a value object of the provided type using the
   * default configuration.
   *
   * @param <T> the type of object to create
   * @param valueObjectClass the generated object will be an instance of this class
   * @return a value object with all fields set to mock values
   */
  public static <T> T createValueObject(Class<T> valueObjectClass) {
    // technically this is a bad cast, but autoboxing will make it appear correct
    // not really a great solution, but I'm not sure how to do it better
    @SuppressWarnings("unchecked")
    Class<T> castingType =
        valueObjectClass.isPrimitive()
            ? (Class<T>) boxedEquivalent(valueObjectClass)
            : valueObjectClass;

    return castingType.cast(createValueObject((Type) valueObjectClass, new GeneratorConfig()));
  }

  /**
   * Create a value object of the provided type using all default configurations.
   *
   * @param valueObjectType the type of the object to create
   * @return a value object with all fields set to mock values
   */
  public static Object createValueObject(Type valueObjectType) {
    return createValueObject(valueObjectType, new GeneratorConfig());
  }

  /**
   * Create a value object of the provided type.
   *
   * @param valueObjectType the type of the object to create
   * @param config a configuration for specifying custom behaviors
   * @param additionalValueGenerators additional value object generators to use when constructing a
   *     value object
   * @return a value object with all fields set to mock values
   */
  public static Object createValueObject(
      Type valueObjectType,
      GeneratorConfig config,
      ValueGeneratorDelegate... additionalValueGenerators) {

    TypeInfo typeInfo = ReflectionUtil.getTypeInfo(valueObjectType, null, null);
    return new ValueObjectGenerator(config, additionalValueGenerators)
        .valueFor(typeInfo, null, null, null);
  }

  private Valuegen() {
    // no-op: uninstantiable
  }
}
