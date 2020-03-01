package com.github.opennano.testutils.beans.valid;

public class BeanWithExtraMethods {
  private String stringField;

  public String getStringField() {
    return stringField;
  }

  public void setStringField(String stringField) {
    this.stringField = stringField;
  }
  
  // should be ignored //

  public void someMethod() {
    throw new UnsupportedOperationException();
  }
}
