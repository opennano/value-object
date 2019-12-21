package scenariotests.samplepojos.wildcardpojos;

import java.util.List;

public class WildCardIntegerLowerBoundObject {

  private List<? super Integer> testedField;

  public List<? super Integer> getTestedField() {
    return testedField;
  }

  public void setTestedField(List<? super Integer> testedField) {
    this.testedField = testedField;
  }
}
