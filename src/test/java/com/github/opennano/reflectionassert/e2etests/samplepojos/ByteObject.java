package com.github.opennano.reflectionassert.e2etests.samplepojos;

public class ByteObject {

  @SuppressWarnings("unused") // used only by reflection
  private byte testedField;

  public ByteObject(byte testedField) {
    this.testedField = testedField;
  }
}
