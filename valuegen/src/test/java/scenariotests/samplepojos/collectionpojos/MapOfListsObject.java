package scenariotests.samplepojos.collectionpojos;

import java.util.List;
import java.util.Map;

public class MapOfListsObject {

  private Map<String, List<Integer>> testedField;

  public Map<String, List<Integer>> getTestedField() {
    return testedField;
  }

  public void setTestedField(Map<String, List<Integer>> testedField) {
    this.testedField = testedField;
  }
}
