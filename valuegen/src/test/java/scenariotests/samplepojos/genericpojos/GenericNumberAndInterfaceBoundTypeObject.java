package scenariotests.samplepojos.genericpojos;

public class GenericNumberAndInterfaceBoundTypeObject<T extends Number & Cloneable> {

  private T testedField;

  public T getTestedField() {
    return testedField;
  }

  public void setTestedField(T testedField) {
    this.testedField = testedField;
  }

  public static final class CustomNumber extends Number implements Cloneable {

    private static final long serialVersionUID = 1L;
    
    private int value;

    public CustomNumber(int value) {
      this.value = value;
    }

    @Override
    public int intValue() {
      return value;
    }

    @Override
    public long longValue() {
      return value;
    }

    @Override
    public float floatValue() {
      return value;
    }

    @Override
    public double doubleValue() {
      return value;
    }
  }
}
