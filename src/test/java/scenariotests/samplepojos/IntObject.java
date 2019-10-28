package scenariotests.samplepojos;

public class IntObject {

  @SuppressWarnings("unused") // used only by reflection
  private int testedField;

  public IntObject(int testedField) {
    this.testedField = testedField;
  }
}
