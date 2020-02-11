package com.github.opennano.testutils;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * This one is difficult to test in any meaningful sense as the purpose of the util is to provide
 * code coverage of java enums. The strategy here is just to test a variety of enum types and make
 * sure the tool doesn't fail. It's entirely possible that future changes in the jdk could cause the
 * util to no longer provide full code coverage without failing these tests, but tests based on the
 * util should at least have confidence that it will not outright fail.
 */
public class EnumTestUtilTest {

  @Test
  public void assertEnumValid_regular() {
    EnumTestUtil.assertEnumValid(RegularEnum.class);
  }

  @Test
  public void assertEnumValid_empty() {
    assertThrows(
        IllegalArgumentException.class, () -> EnumTestUtil.assertEnumValid(EmptyEnum.class));
  }

  @Test
  public void assertEnumValid_poisoned() {
    EnumTestUtil.assertEnumValid(PoisonedEnum.class);
  }

  @Test
  public void assertEnumValid_fancy() {
    EnumTestUtil.assertEnumValid(RegularEnum.class);
  }

  @Test // asinine test for 100% coverage
  public void assertIsNotInstantiable() {
    SingletonTestUtil.assertIsNotInstantiable(EnumTestUtil.class);
  }

  public enum RegularEnum {
    VALUE1,
    VALUE2;
  }

  public enum EmptyEnum {}

  public enum PoisonedEnum {
    VALUE1,
    VALUE2;

    public PoisonedEnum[] getEnumConstants() {
      throw new IllegalArgumentException("exception testing, nothing to see here");
    }
  }

  public enum FancyEnum {
    VALUE1("a"),
    VALUE2("b", "c");

    private String field1;
    private String field2;

    FancyEnum(String field1) {
      this(field1, null);
    }

    FancyEnum(String field1, String field2) {
      this.field1 = field1;
      this.field2 = field2;
    }

    public String youFancyHun() {
      return field1 + field2;
    }
  }
}
