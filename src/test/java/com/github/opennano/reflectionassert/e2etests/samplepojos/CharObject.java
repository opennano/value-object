package com.github.opennano.reflectionassert.e2etests.samplepojos;

public class CharObject {

  @SuppressWarnings("unused") // used only by reflection
  private char testedField;

  public CharObject(char testedField) {
    this.testedField = testedField;
  }
}
