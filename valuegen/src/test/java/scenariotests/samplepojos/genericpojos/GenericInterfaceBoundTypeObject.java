package scenariotests.samplepojos.genericpojos;

import java.io.Serializable;

public class GenericInterfaceBoundTypeObject<T extends Serializable> {

  private T testedField;

  public T getTestedField() {
    return testedField;
  }

  public void setTestedField(T testedField) {
    this.testedField = testedField;
  }
}
