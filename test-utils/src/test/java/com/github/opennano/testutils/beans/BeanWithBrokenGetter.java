package com.github.opennano.testutils.beans;

public class BeanWithBrokenGetter {
  @SuppressWarnings("unused") //intentionally unused
  private String stringField;

  public String getStringField() {
    return "something else!";
  }

  public void setStringField(String stringField) {
    this.stringField = stringField;
  }
}
