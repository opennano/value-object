package com.github.opennano.testutils.beans.invalid;

public class BeanWithoutZeroArgCtor {
  private String stringField;

  public BeanWithoutZeroArgCtor(String stringField) {
    this.stringField = stringField;
  }

  public String getStringField() {
    return stringField;
  }

  public void setStringField(String stringField) {
    this.stringField = stringField;
  }
}
