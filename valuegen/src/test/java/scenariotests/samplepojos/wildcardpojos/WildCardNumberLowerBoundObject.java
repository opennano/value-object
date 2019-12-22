package scenariotests.samplepojos.wildcardpojos;

import java.util.List;

public class WildCardNumberLowerBoundObject {

  private List<? super Number> testedField;

  public List<? super Number> getTestedField() {
    return testedField;
  }

  public void setTestedField(List<? super Number> testedField) {
    this.testedField = testedField;
  }
}
