package com.github.opennano.testutils.beans.valid;

@SuppressWarnings("unused") //intentionally hidden methods and fields
public class BeanWithWeirdMethodAccessors {
  private String privateField;
  private String protectedField;
  private String defaultField;

  // only public accessors are tested //
  
  private String getPrivateField() {
    throw new UnsupportedOperationException();
  }

  private void setPrivateField(String privateField) {
    throw new UnsupportedOperationException();
  }

  protected String getProtectedField() {
    throw new UnsupportedOperationException();
  }

  protected void setProtectedField(String protectedField) {
    throw new UnsupportedOperationException();
  }

  /* default */ String getDefaultField() {
    throw new UnsupportedOperationException();
  }

  /* default */ void setDefaultField(String defaultField) {
    throw new UnsupportedOperationException();
  }
}
