package com.github.opennano.reflectionassert.e2etests.samplepojos;

public class LongObject {

  @SuppressWarnings("unused") // used only by reflection
  private long testedField;

  public LongObject(long testedField) {
    this.testedField = testedField;
  }
}
