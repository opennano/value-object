package scenariotests.samplepojos.simplepojos;

public class InnerTypeObject {

  public static class InnerType {

    private String testedField;

    public String getTestedField() {
      return testedField;
    }

    public void setTestedField(String testedField) {
      this.testedField = testedField;
    }
  }
}
