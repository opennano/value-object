package com.github.opennano.valuegen;

public class ValueGenerationException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public ValueGenerationException() {
    super();
  }

  public ValueGenerationException(String message, Throwable cause) {
    super(message, cause);
  }

  public ValueGenerationException(String message) {
    super(message);
  }

  public ValueGenerationException(Throwable cause) {
    super(cause);
  }
}
