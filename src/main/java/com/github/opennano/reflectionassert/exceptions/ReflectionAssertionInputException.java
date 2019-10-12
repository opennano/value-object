package com.github.opennano.reflectionassert.exceptions;

/**
 * Any problems caused by the objects being compared are reported with this exception. An example
 * would be using an unordered collection (like HashSet) in an ordered collection comparison. These
 * errors represent bad input not bugs.
 */
public class ReflectionAssertionInputException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public ReflectionAssertionInputException() {
    super();
  }

  public ReflectionAssertionInputException(String message, Throwable cause) {
    super(message, cause);
  }

  public ReflectionAssertionInputException(String message) {
    super(message);
  }

  public ReflectionAssertionInputException(Throwable cause) {
    super(cause);
  }
}
