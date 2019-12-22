package scenariotests.samplepojos.wildcardpojos;

import java.util.List;

public class WildCardIntegerUpperBoundObject {

  private List<? extends Integer> testedField;

  public List<? extends Integer> getTestedField() {
    return testedField;
  }

  public void setTestedField(List<? extends Integer> testedField) {
    this.testedField = testedField;
  }
}
