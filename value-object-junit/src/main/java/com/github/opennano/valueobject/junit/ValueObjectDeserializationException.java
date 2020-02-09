package com.github.opennano.valueobject.junit;

import org.junit.platform.commons.JUnitException;

/** Thrown if a value object could not be injected into a field. */
public class ValueObjectDeserializationException extends JUnitException {

  private static final long serialVersionUID = 1L;

  public ValueObjectDeserializationException(String message) {
    super(message);
  }

  public ValueObjectDeserializationException(String message, Throwable cause) {
    super(message, cause);
  }
}
