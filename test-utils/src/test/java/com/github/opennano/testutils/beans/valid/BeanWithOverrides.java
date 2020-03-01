package com.github.opennano.testutils.beans.valid;

public class BeanWithOverrides {
  private String stringField;

  public String getStringField() {
    return stringField;
  }

  public void setStringField(String stringField) {
    this.stringField = stringField;
  }
  
  // all these should be ignored //

  @Override
  public boolean equals(Object obj) {
    throw new UnsupportedOperationException();
  }

  @Override
  protected Object clone() throws CloneNotSupportedException {
    throw new UnsupportedOperationException();
  }

  @Override
  protected void finalize() throws Throwable {
    throw new UnsupportedOperationException();
  }
}
