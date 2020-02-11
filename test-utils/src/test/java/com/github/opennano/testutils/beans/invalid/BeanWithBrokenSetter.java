package com.github.opennano.testutils.beans.invalid;

public class BeanWithBrokenSetter {
  private String stringField;

  public String getStringField() {
    return stringField;
  }

  public void setStringField(String stringField) {
    this.stringField = "something else!";
  }
}
