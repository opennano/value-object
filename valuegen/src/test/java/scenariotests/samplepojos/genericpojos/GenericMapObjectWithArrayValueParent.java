package scenariotests.samplepojos.genericpojos;

import java.util.Map;

public class GenericMapObjectWithArrayValueParent<T> {

  public static class GenericMapObjectWithArrayValue
      extends GenericMapObjectWithArrayValueParent<Integer> {}

  private Map<String, T[]> testedField;

  public Map<String, T[]> getTestedField() {
    return testedField;
  }

  public void setTestedField(Map<String, T[]> testedField) {
    this.testedField = testedField;
  }
}
