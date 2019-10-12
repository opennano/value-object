package com.github.opennano.reflectionassert.exceptions;

/** this exception should be thrown when an equality assertion fails */
public class ReflectionAssertionException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public ReflectionAssertionException() {
    super();
  }

  public ReflectionAssertionException(String message, Throwable cause) {
    super(message, cause);
  }

  public ReflectionAssertionException(String message) {
    super(message);
  }

  public ReflectionAssertionException(Throwable cause) {
    super(cause);
  }
}
