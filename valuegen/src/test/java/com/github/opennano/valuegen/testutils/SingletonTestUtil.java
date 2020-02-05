package com.github.opennano.valuegen.testutils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

/**
 * This util just makes it easy to achieve 100% path coverage of singletons, which should have
 * private constructors.
 *
 * <p>This works by invoking the private zero-arg constructor and simply ensuring it doesn't fail.
 */
public class SingletonTestUtil {

  private SingletonTestUtil() {
    // no-op: singleton
  }

  /**
   * Convenience method to verify that a class is not instantiable via a no-arg constructor.
   *
   * <p>The target class must define a no-arg constructor, though it may be private.
   *
   * @param <T> the type of the singleton to test
   * @param type the Class object of the singleton to test
   */
  public static <T> void assertIsNotInstantiable(Class<T> type) {
    try {
      Constructor<T> ctor = type.getDeclaredConstructor(); // never null

      if (!Modifier.isPrivate(ctor.getModifiers())) {
        String message = "zero-arg constructor is not private for type ";
        throw new IllegalStateException(message + type.getSimpleName());
      }

      ctor.setAccessible(true);
      ctor.newInstance(); // success if doesn't throw
    } catch (ReflectiveOperationException e) {
      String message = "zero-arg constructor threw an exception for type ";
      throw new IllegalStateException(message + type.getSimpleName(), e);
    }
  }
}
