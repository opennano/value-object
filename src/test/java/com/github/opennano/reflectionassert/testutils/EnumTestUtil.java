package com.github.opennano.reflectionassert.testutils;

public class EnumTestUtil {

  private EnumTestUtil() {
    // no-op: singleton
  }

  /**
   * This admittedly senseless test just makes it easy to achieve 100% coverage of java enums.
   *
   * <p>This works by superficially covering the bytecode Java generates for an enum, which is
   * achieved by calling the implicit valueOf static method on the enum. And no, that doesn't make
   * much sense :(
   *
   * <p>see https://stackoverflow.com/questions/4512358/emma-coverage-on-enum-types
   *
   * @param <T> a subtype of {@link Enum}
   * @param enumType the Class object of the enum to test
   */
  public static <T extends Enum<T>> void assertEnumValid(Class<T> enumType) {
    try {
      enumType
          .getMethod("valueOf", String.class)
          .invoke(null, enumType.getEnumConstants()[0].name());
    } catch (Exception e) {
      throw new IllegalArgumentException(
          "failed to call method valueOf on enum " + enumType.getSimpleName(), e);
    }
  }
}
