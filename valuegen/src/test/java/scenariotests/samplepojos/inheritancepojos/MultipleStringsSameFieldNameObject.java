package scenariotests.samplepojos.inheritancepojos;

public class MultipleStringsSameFieldNameObject {

  private String testedField;
  private DuplicateFieldObject nestedField;

  public String getTestedField() {
    return testedField;
  }

  public void setTestedField(String testedField) {
    this.testedField = testedField;
  }

  public DuplicateFieldObject getNestedField() {
    return nestedField;
  }

  public void setNestedField(DuplicateFieldObject nestedField) {
    this.nestedField = nestedField;
  }

  public static class DuplicateFieldObject {

    private String testedField;

    public String getTestedField() {
      return testedField;
    }

    public void setTestedField(String testedField) {
      this.testedField = testedField;
    }
  }
}
