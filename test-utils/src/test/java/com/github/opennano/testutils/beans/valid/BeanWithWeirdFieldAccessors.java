package com.github.opennano.testutils.beans.valid;

public class BeanWithWeirdFieldAccessors {
  
  // field visibility doesn't matter //
  
  public String publicField;
  protected String protectedField;
  /* default */ String defaultField;

  public String getPublicField() {
    return publicField;
  }

  public void setPublicField(String publicField) {
    this.publicField = publicField;
  }

  public String getProtectedField() {
    return protectedField;
  }

  public void setProtectedField(String protectedField) {
    this.protectedField = protectedField;
  }

  public String getDefaultField() {
    return defaultField;
  }

  public void setDefaultField(String defaultField) {
    this.defaultField = defaultField;
  }
}
