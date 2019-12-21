package scenariotests.samplepojos.genericpojos;

import java.util.Map;

public class GenericMapObjectParent<K, V> {

  public static class GenericMapObject extends GenericMapObjectParent<String, Integer> {}

  private Map<K, V> testedField;

  public Map<K, V> getTestedField() {
    return testedField;
  }

  public void setTestedField(Map<K, V> testedField) {
    this.testedField = testedField;
  }
}
