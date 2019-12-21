package scenariotests.samplepojos.simplepojos;

public abstract class SingleImplementationAbstractClass {

  public static class SingleImplementationOfAbstractClass
      extends SingleImplementationAbstractClass {

    private long testedField;

    public long getTestedField() {
      return testedField;
    }

    public void setTestedField(long testedField) {
      this.testedField = testedField;
    }
  }
}
