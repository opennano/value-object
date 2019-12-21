package scenariotests.samplepojos.simplepojos;

public class StaticInnerTypeObject {

  public static class StaticInnerType {

    private String testedField;

    public String getTestedField() {
      return testedField;
    }

    public void setTestedField(String testedField) {
      this.testedField = testedField;
    }
  }
}
