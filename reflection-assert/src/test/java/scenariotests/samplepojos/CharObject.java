package scenariotests.samplepojos;

public class CharObject {

  @SuppressWarnings("unused") // used only by reflection
  private char testedField;

  public CharObject(char testedField) {
    this.testedField = testedField;
  }
}
