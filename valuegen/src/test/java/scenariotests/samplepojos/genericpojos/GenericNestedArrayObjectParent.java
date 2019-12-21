package scenariotests.samplepojos.genericpojos;

public class GenericNestedArrayObjectParent<T> {

  public static class GenericNestedArrayObject extends GenericNestedArrayObjectParent<Integer> {}

  private T[][] testedField;

  public T[][] getTestedField() {
    return testedField;
  }

  public void setTestedField(T[][] testedField) {
    this.testedField = testedField;
  }
}
