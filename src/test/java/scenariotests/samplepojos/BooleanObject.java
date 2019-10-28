package scenariotests.samplepojos;

public class BooleanObject {

  @SuppressWarnings("unused") // used only by reflection
  private boolean testedField;

  public BooleanObject(boolean testedField) {
    this.testedField = testedField;
  }
}
