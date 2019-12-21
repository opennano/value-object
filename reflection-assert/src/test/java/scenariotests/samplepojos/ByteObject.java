package scenariotests.samplepojos;

public class ByteObject {

  @SuppressWarnings("unused") // used only by reflection
  private byte testedField;

  public ByteObject(byte testedField) {
    this.testedField = testedField;
  }
}
