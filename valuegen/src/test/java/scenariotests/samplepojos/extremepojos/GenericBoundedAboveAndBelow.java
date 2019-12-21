package scenariotests.samplepojos.extremepojos;

import java.util.List;

public class GenericBoundedAboveAndBelow<T extends Number> {

  private List<? super T> testedField;

  public List<? super T> getTestedField() {
    return testedField;
  }

  public void setTestedField(List<? super T> testedField) {
    this.testedField = testedField;
  }
}
