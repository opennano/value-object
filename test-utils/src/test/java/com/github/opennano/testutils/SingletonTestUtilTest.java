package com.github.opennano.testutils;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class SingletonTestUtilTest {

  @Test
  public void assertUsesSingletonPattern() {
    SingletonTestUtil.assertIsNotInstantiable(NotInstantiable.class);
  }

  @Test
  public void assertUsesSingletonPattern_oneArgConstructor() {
    assertThrows(
        IllegalStateException.class,
        () -> SingletonTestUtil.assertIsNotInstantiable(OneArgConstructor.class));
  }

  @Test
  public void assertUsesSingletonPattern_defaultAccessConstructor() {
    assertThrows(
        IllegalStateException.class,
        () -> SingletonTestUtil.assertIsNotInstantiable(DefaultAccessConstructor.class));
  }

  @Test
  public void assertUsesSingletonPattern_protectedAccessConstructor() {
    assertThrows(
        IllegalStateException.class,
        () -> SingletonTestUtil.assertIsNotInstantiable(ProtectedAccessConstructor.class));
  }

  @Test
  public void assertUsesSingletonPattern_publicAccessConstructor() {
    assertThrows(
        IllegalStateException.class,
        () -> SingletonTestUtil.assertIsNotInstantiable(PublicAccessConstructor.class));
  }

  @Test
  public void assertUsesSingletonPattern_poisonedConstructor() {
    assertThrows(
        IllegalStateException.class,
        () -> SingletonTestUtil.assertIsNotInstantiable(PoisonedConstructor.class));
  }

  @Test // asinine test for 100% coverage
  public void assertIsNotInstantiable() {
    SingletonTestUtil.assertIsNotInstantiable(SingletonTestUtil.class);
  }

  public static class NotInstantiable {
    private NotInstantiable() {
      // no-op: test code
    }
  }

  public static class OneArgConstructor {
    private OneArgConstructor(Object ignored) {
      // no-op: test code
    }
  }

  public static class DefaultAccessConstructor {
    /* default */ DefaultAccessConstructor() {
      // no-op: test code
    }
  }

  public static class ProtectedAccessConstructor {
    protected ProtectedAccessConstructor() {
      // no-op: test code
    }
  }

  public static class PublicAccessConstructor {
    public PublicAccessConstructor() {
      // no-op: test code
    }
  }

  public static class PoisonedConstructor {
    public PoisonedConstructor() {
      throw new IllegalStateException("exception testing, nothing to see here");
    }
  }
}
