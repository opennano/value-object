package com.github.opennano.reflectionassert.testutils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.lang.reflect.Constructor;
import java.util.stream.Stream;

public class ExceptionTestUtil {

  private static final String STANDARD_MESSAGE = "message";

  private ExceptionTestUtil() {
    //no-op: singleton
  }

  /**
   * Asserts that the provided exception type follows the normal conventions around constructing
   * exceptions. In particular, an exception type may have any of the four standard constructors,
   * and those constructors should delegate to the super type method. If your exception type has
   * additional constructors you will need to test those manually. If your exception type does not
   * follow these conventions or is an inner type, you're on your own :)
   */
  public static <T extends Throwable> void assertValidException(Class<T> type) {
    try {
      maybeTestNoArgConstructor(type);
      maybeTestStringConstructor(type);
      maybeTestThrowableConstructor(type);
      maybeTestStringAndThrowableConstructor(type);
    } catch (Exception exception) {
      String message = "failed to test exception type " + type.getSimpleName();
      throw new IllegalStateException(message, exception);
    }
  }

  private static <T extends Throwable> void maybeTestNoArgConstructor(Class<T> type) {
    T exception = instantiate(type);
    if (exception == null) { // no 0-arg constructor
      return;
    }

    assertNull(exception.getMessage());
    assertNull(exception.getCause());
  }

  private static <T extends Throwable> void maybeTestStringConstructor(Class<T> type) {
    T exception = instantiate(type, STANDARD_MESSAGE);
    if (exception == null) { // no String-only constructor
      return;
    }

    assertEquals(STANDARD_MESSAGE, exception.getMessage());
    assertNull(exception.getCause());
  }

  private static <T extends Throwable> void maybeTestThrowableConstructor(Class<T> type) {
    Throwable cause = new Throwable();
    T exception = instantiate(type, cause);
    if (exception == null) { // no Throwable-only constructor
      return;
    }

    assertNotNull(exception.getMessage()); //java fills in a default message in this case
    assertEquals(cause, exception.getCause());
  }

  private static <T extends Throwable> void maybeTestStringAndThrowableConstructor(Class<T> type) {
    Throwable cause = new Throwable();
    T exception = instantiate(type, STANDARD_MESSAGE, cause);
    if (exception == null) { // no Throwable-only constructor
      return;
    }

    assertEquals(STANDARD_MESSAGE, exception.getMessage());
    assertEquals(cause, exception.getCause());
  }

  /**
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
    } catch (NoSuchMethodException exception) { //NOSONAR it's fine if this doesn't exist
      return null;
    }

    try {
      constructor.setAccessible(true);
      return constructor.newInstance(args);
    } catch (Exception exception) {
      String message = "failed to instantiate exception class by constructor";
      throw new IllegalArgumentException(message, exception);
    }
  }
}
