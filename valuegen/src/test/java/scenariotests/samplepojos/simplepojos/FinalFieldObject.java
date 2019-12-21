package scenariotests.samplepojos.simplepojos;

public class FinalFieldObject {

  private final String testedField;

  public FinalFieldObject(String testedField) {
    this.testedField = testedField;
  }

  public String getTestedField() {
    return testedField;
  }
}
