package com.github.opennano.reflectionassert.e2etests.samplepojos;

public class FloatObject {

  @SuppressWarnings("unused") // used only by reflection
  private float testedField;

  public FloatObject(float testedField) {
    this.testedField = testedField;
  }
}
