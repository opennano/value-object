package com.github.opennano.reflectionassert.e2etests.samplepojos;

public class ShortObject {

  @SuppressWarnings("unused") // used only by reflection
  private short testedField;

  public ShortObject(short testedField) {
    this.testedField = testedField;
  }
}
