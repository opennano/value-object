package scenariotests.samplepojos.genericpojos;

public class GenericIntegerObjectParent<T> {

  public static class GenericIntegerObject extends GenericIntegerObjectParent<Integer> {}

  private T testedField;

  public T getTestedField() {
    return testedField;
  }

  public void setTestedField(T testedField) {
    this.testedField = testedField;
  }
}
