package scenariotests.samplepojos.collectionpojos;

import java.util.Map;

public class MapOfMapsObject {

  private Map<String, Map<String, Integer>> testedField;

  public Map<String, Map<String, Integer>> getTestedField() {
    return testedField;
  }

  public void setTestedField(Map<String, Map<String, Integer>> testedField) {
    this.testedField = testedField;
  }
}
