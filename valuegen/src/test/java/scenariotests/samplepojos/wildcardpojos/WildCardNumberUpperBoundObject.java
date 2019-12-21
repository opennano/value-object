package scenariotests.samplepojos.wildcardpojos;

import java.util.List;

public class WildCardNumberUpperBoundObject {

  private List<? extends Number> testedField;

  public List<? extends Number> getTestedField() {
    return testedField;
  }

  public void setTestedField(List<? extends Number> testedField) {
    this.testedField = testedField;
  }
}
