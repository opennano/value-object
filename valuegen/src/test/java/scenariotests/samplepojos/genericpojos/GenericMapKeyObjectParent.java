package scenariotests.samplepojos.genericpojos;

import java.util.Map;

public class GenericMapKeyObjectParent<T> {

  public static class GenericMapKeyObject extends GenericMapKeyObjectParent<String> {}

  private Map<T, Integer> testedField;

  public Map<T, Integer> getTestedField() {
    return testedField;
  }

  public void setTestedField(Map<T, Integer> testedField) {
    this.testedField = testedField;
  }
}
