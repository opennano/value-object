package com.github.opennano.reflectionassert.e2etests.samplepojos;

public class BooleanObject {

  @SuppressWarnings("unused") // used only by reflection
  private boolean testedField;

  public BooleanObject(boolean testedField) {
    this.testedField = testedField;
  }
}
