package com.github.opennano.testutils.beans.valid;

public class BeanWithExtraCtor {
  private String stringField;

  public BeanWithExtraCtor() {}

  public BeanWithExtraCtor(String stringField) {
    this.stringField = stringField;
  }

  public String getStringField() {
    return stringField;
  }

  public void setStringField(String stringField) {
    this.stringField = stringField;
  }
}
