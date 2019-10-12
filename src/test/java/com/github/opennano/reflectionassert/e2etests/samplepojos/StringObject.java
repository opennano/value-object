package com.github.opennano.reflectionassert.e2etests.samplepojos;

public class StringObject {

  @SuppressWarnings("unused") // used only by reflection
  private String testedField;

  public StringObject(String testedField) {
    this.testedField = testedField;
  }
}
