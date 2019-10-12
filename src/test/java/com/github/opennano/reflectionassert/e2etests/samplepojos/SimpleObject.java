package com.github.opennano.reflectionassert.e2etests.samplepojos;

public class SimpleObject {

  @SuppressWarnings("unused") // used only by reflection
  private Object testedField;

  public SimpleObject(Object testedField) {
    this.testedField = testedField;
  }
}
