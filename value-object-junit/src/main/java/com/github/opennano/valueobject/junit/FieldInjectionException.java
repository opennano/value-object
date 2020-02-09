package com.github.opennano.valueobject.junit;

import org.junit.platform.commons.JUnitException;

/** Thrown if a value object could not be injected into a field. */
public class FieldInjectionException extends JUnitException {

  private static final long serialVersionUID = 1L;

  public FieldInjectionException(String message) {
    super(message);
  }

  public FieldInjectionException(String message, Throwable cause) {
    super(message, cause);
  }
}
