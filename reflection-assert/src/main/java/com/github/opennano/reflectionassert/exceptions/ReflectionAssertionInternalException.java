package com.github.opennano.reflectionassert.exceptions;

/**
 * Any unexpected errors occurring in this library are generally thrown as instances of this class.
 * Throwing standard Java exceptions could interfere with expectations for negative tests. If this
 * exception is thrown it's likely a bug in the libary.
 */
public class ReflectionAssertionInternalException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public ReflectionAssertionInternalException() {
    super();
  }

  public ReflectionAssertionInternalException(String message, Throwable cause) {
    super(message, cause);
  }

  public ReflectionAssertionInternalException(String message) {
    super(message);
  }

  public ReflectionAssertionInternalException(Throwable cause) {
    super(cause);
  }
}
