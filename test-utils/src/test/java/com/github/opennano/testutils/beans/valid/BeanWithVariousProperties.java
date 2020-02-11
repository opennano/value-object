package com.github.opennano.testutils.beans.valid;

import java.util.Collection;
import java.util.Date;

public class BeanWithVariousProperties {
  private String stringField;
  private int byteField;
  private Float floatField;
  private char charField;
  private boolean booleanField;
  private String[] arrayField;
  private Collection<String> collectionField;
  private Date dateField;
  private Object objectField;

  public String getStringField() {
    return stringField;
  }

  public void setStringField(String stringField) {
    this.stringField = stringField;
  }

  public int getByteField() {
    return byteField;
  }

  public void setByteField(int byteField) {
    this.byteField = byteField;
  }

  public Float getFloatField() {
    return floatField;
  }

  public void setFloatField(Float floatField) {
    this.floatField = floatField;
  }

  public char getCharField() {
    return charField;
  }

  public void setCharField(char charField) {
    this.charField = charField;
  }

  public boolean isBooleanField() {
    return booleanField;
  }

  public void setBooleanField(boolean booleanField) {
    this.booleanField = booleanField;
  }

  public String[] getArrayField() {
    return arrayField;
  }

  public void setArrayField(String[] arrayField) {
    this.arrayField = arrayField;
  }

  public Collection<String> getCollectionField() {
    return collectionField;
  }

  public void setCollectionField(Collection<String> collectionField) {
    this.collectionField = collectionField;
  }

  public Date getDateField() {
    return dateField;
  }

  public void setDateField(Date dateField) {
    this.dateField = dateField;
  }

  public Object getObjectField() {
    return objectField;
  }

  public void setObjectField(Object objectField) {
    this.objectField = objectField;
  }
}
