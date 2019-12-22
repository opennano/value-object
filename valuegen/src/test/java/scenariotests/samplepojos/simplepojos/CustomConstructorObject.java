package scenariotests.samplepojos.simplepojos;

public class CustomConstructorObject {

  private String testedField;

  public CustomConstructorObject(String testedField) {
    this.testedField = testedField;
  }

  public String getTestedField() {
    return testedField;
  }
}
