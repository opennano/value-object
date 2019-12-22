package scenariotests.samplepojos.genericpojos;

public class GenericArrayObjectParent<T> {

  public static class GenericArrayObject extends GenericArrayObjectParent<Integer> {}

  private T[] testedField;

  public T[] getTestedField() {
    return testedField;
  }

  public void setTestedField(T[] testedField) {
    this.testedField = testedField;
  }
}
