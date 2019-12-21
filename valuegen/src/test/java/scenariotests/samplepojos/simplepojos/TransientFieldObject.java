package scenariotests.samplepojos.simplepojos;

public class TransientFieldObject {

  private transient String testedField;

  public String getTestedField() {
    return testedField;
  }

  public void setTestedField(String testedField) {
    this.testedField = testedField;
  }
}
