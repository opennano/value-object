package scenariotests.samplepojos;

public class StringObject {

  @SuppressWarnings("unused") // used only by reflection
  private String testedField;

  public StringObject(String testedField) {
    this.testedField = testedField;
  }
}
