package scenariotests.samplepojos.genericpojos;

import java.util.List;

public class GenericArrayOfListObjectParent<T> {

  public static class GenericArrayOfListObject extends GenericArrayOfListObjectParent<Integer[]> {}

  private List<T> testedField;

  public List<T> getTestedField() {
    return testedField;
  }

  public void setTestedField(List<T> testedField) {
    this.testedField = testedField;
  }
}
