package scenariotests.samplepojos.genericpojos;

public class GenericNumberBoundTypeObject<T extends Number> {

  private T testedField;

  public T getTestedField() {
    return testedField;
  }

  public void setTestedField(T testedField) {
    this.testedField = testedField;
  }
}
