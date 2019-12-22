package scenariotests.samplepojos.genericpojos;

import java.util.List;

public class GenericListObject2Parent<T> {

  public static class GenericListObject2 extends GenericListObject2Parent<List<Integer>> {}

  private T testedField;

  public T getTestedField() {
    return testedField;
  }

  public void setTestedField(T testedField) {
    this.testedField = testedField;
  }
}
