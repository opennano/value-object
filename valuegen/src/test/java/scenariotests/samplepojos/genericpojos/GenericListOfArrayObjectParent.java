package scenariotests.samplepojos.genericpojos;

import java.util.List;

public class GenericListOfArrayObjectParent<T> {

  public static class GenericListOfArrayObject
      extends GenericListOfArrayObjectParent<List<Integer>> {}

  private T[] testedField;

  public T[] getTestedField() {
    return testedField;
  }

  public void setTestedField(T[] testedField) {
    this.testedField = testedField;
  }
}
