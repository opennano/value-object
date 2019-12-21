package scenariotests.samplepojos.genericpojos;

import java.util.Map;

public class GenericMapValueObjectParent<T> {

  public static class GenericMapValueObject extends GenericMapValueObjectParent<Integer> {}

  private Map<String, T> testedField;

  public Map<String, T> getTestedField() {
    return testedField;
  }

  public void setTestedField(Map<String, T> testedField) {
    this.testedField = testedField;
  }
}
