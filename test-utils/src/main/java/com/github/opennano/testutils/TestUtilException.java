package com.github.opennano.testutils;

public class TestUtilException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public TestUtilException(String message) {
    super(message);
  }

  public TestUtilException(String message, Throwable cause) {
    super(message, cause);
  }
}
