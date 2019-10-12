package com.github.opennano.reflectionassert.testutils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

/**
 * This admittedly senseless test just makes it easy to achieve 100% coverage of singletons.
 *
 * <p>This works by invoking the non-public constructor and simply ensuring it doesn't fail.
 */
public class SingletonTestUtil {

  private SingletonTestUtil() {
    //no-op: singleton
  }

  /**
   * Convenience method to verify that a class is not instantiable via a no-arg constructor. This also provides code coverage
   *
   * <p>The target class must define a no-arg constructor, though it may be private.
   */
  public static <T> T assertIsNotInstantiable(Class<T> type) {
    try {
      Constructor<T> ctor = type.getDeclaredConstructor(); //never null

      if (!Modifier.isPrivate(ctor.getModifiers())) {
        String message = "zero-arg constructor is not private for type ";
        throw new IllegalStateException(message + type.getSimpleName());
      }

      ctor.setAccessible(true);
      return ctor.newInstance(); //success if doesn't throw
    } catch (ReflectiveOperationException e) {
      String message = "zero-arg constructor threw an exception for type ";
      throw new IllegalStateException(message + type.getSimpleName(), e);
    }
  }
}
