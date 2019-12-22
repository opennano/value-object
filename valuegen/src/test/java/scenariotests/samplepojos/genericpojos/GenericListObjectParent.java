package scenariotests.samplepojos.genericpojos;

import java.util.List;

public class GenericListObjectParent<T> {

  public static class GenericListObject extends GenericListObjectParent<Integer> {}

  private List<T> testedField;

  public List<T> getTestedField() {
    return testedField;
  }

  public void setTestedField(List<T> testedField) {
    this.testedField = testedField;
  }
}
