package com.github.opennano.testutils;

import java.lang.reflect.Constructor;
import java.util.Objects;
import java.util.Optional;
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
    instantiate(type).ifPresent(ex -> validateMessageAndCause(ex, null, null));
  }

  private static <T extends Throwable> void maybeTestStringConstructor(Class<T> type) {
    instantiate(type, STANDARD_MESSAGE)
        .ifPresent(ex -> validateMessageAndCause(ex, STANDARD_MESSAGE, null));
  }

  private static <T extends Throwable> void maybeTestThrowableConstructor(Class<T> type) {
    Throwable cause = new Throwable();
    instantiate(type, cause)
        .ifPresent(
            ex -> {
              // java fills in a default message when no message is specified
              if (ex.getMessage() == null) {
                throw new TestUtilException(
                    "throwable constructor shouldn't produce a null message");
              }

              validateCause(ex, cause);
            });
  }

  private static <T extends Throwable> void maybeTestStringAndThrowableConstructor(Class<T> type) {
    Throwable cause = new Throwable();
    instantiate(type, STANDARD_MESSAGE, cause)
        .ifPresent(ex -> validateMessageAndCause(ex, STANDARD_MESSAGE, cause));
  }

  /*
   * Creates a new instance of the specified type using the provided arguments, defeating any
   * private access modifiers.
   *
   * <p>This method will return null only if no constructor matching the provided arguments exists
   * in the class. Supertype constructors are ignored.
   */
  private static <T extends Throwable> Optional<T> instantiate(Class<T> type, Object... args) {
    Constructor<T> constructor = null;
    try {
      Class<?>[] types = Stream.of(args).map(Object::getClass).toArray(Class[]::new);
      constructor = type.getConstructor(types);
    } catch (NoSuchMethodException exception) { // NOSONAR it's fine if this doesn't exist
      return Optional.empty();
    }

    try {
      constructor.setAccessible(true);
      return Optional.of(constructor.newInstance(args));
    } catch (Exception e) {
      String message = "failed to instantiate exception class by constructor";
      throw new TestUtilException(message, e);
    }
  }

  private static void validateMessageAndCause(
      Throwable exception, String expectedMessage, Throwable expectedCause) {

    validateMessage(exception, expectedMessage);
    validateCause(exception, expectedCause);
  }

  private static void validateMessage(Throwable exception, String expectedMessage) {
    if (!Objects.equals(expectedMessage, exception.getMessage())) {
      String message = "expected the message to be '%s', but was '%s'";
      throw new TestUtilException(String.format(message, expectedMessage, exception.getMessage()));
    }
  }

  private static void validateCause(Throwable exception, Throwable expectedCause) {
    if (expectedCause != exception.getCause()) {
      String template = "expected the cause to be '%s', but was '%s'";
      throw new TestUtilException(String.format(template, expectedCause, exception.getCause()));
    }
  }
}
