package com.github.opennano.reflectionassert.testutils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.lang.reflect.Constructor;
import java.util.stream.Stream;

public class ExceptionTestUtil {

  private static final String STANDARD_MESSAGE = "mock message";

  private ExceptionTestUtil() {
    // no-op: singleton
  }

  /**
   * Asserts that the provided exception type follows the normal conventions around constructing
   * exceptions. In particular, an exception type may have any of the four standard constructors,
   * and those constructors should delegate to the super type method. If your exception type has
   * additional constructors you will need to test those manually. If your exception type does not
   * follow these conventions or is an inner type, you're on your own :)
   *
   * @param <T> the type of Throwable to test
   * @param type the Class object of the Throwable to test
   */
  public static <T extends Throwable> void assertValidException(Class<T> type) {
    maybeTestNoArgConstructor(type);
    maybeTestStringConstructor(type);
    maybeTestThrowableConstructor(type);
    maybeTestStringAndThrowableConstructor(type);
  }

  private static <T extends Throwable> void maybeTestNoArgConstructor(Class<T> type) {
    T exception = instantiate(type);
    maybeTestException(exception, null, null);
  }

  private static <T extends Throwable> void maybeTestStringConstructor(Class<T> type) {
    T exception = instantiate(type, STANDARD_MESSAGE);
    maybeTestException(exception, STANDARD_MESSAGE, null);
  }

  private static <T extends Throwable> void maybeTestThrowableConstructor(Class<T> type) {
    Throwable cause = new Throwable();
    T exception = instantiate(type, cause);

    // this scenario is a little different than the others
    // java fills in a default message when no message is specified
    if (exception != null) {
      assertNotNull(exception.getMessage());
      assertSame(cause, exception.getCause());
    }
  }

  private static <T extends Throwable> void maybeTestStringAndThrowableConstructor(Class<T> type) {
    Throwable cause = new Throwable();
    T exception = instantiate(type, STANDARD_MESSAGE, cause);
    maybeTestException(exception, STANDARD_MESSAGE, cause);
  }

  /*
   * Creates a new instance of the specified type using the provided arguments, defeating any
   * private access modifiers.
   *
   * <p>This method will return null only if no constructor matching the provided arguments exists
   * in the class. Supertype constructors are ignored.
   */
  private static <T extends Throwable> T instantiate(Class<T> type, Object... args) {
    Constructor<T> constructor = null;
    try {
      Class<?>[] types = Stream.of(args).map(Object::getClass).toArray(Class[]::new);
      constructor = type.getConstructor(types);
    } catch (NoSuchMethodException exception) { // NOSONAR it's fine if this doesn't exist
      return null;
    }

    try {
      constructor.setAccessible(true);
      return constructor.newInstance(args);
    } catch (Exception e) {
      String message = "failed to instantiate exception class by constructor";
      throw new IllegalArgumentException(message, e);
    }
  }

  private static void maybeTestException(
      Throwable exception, String expectedMessage, Throwable expectedCause) {

    if (exception != null) {
      assertEquals(expectedMessage, exception.getMessage());
      assertSame(expectedCause, exception.getCause());
    }
  }
}
