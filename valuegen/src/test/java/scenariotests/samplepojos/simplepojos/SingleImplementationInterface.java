package scenariotests.samplepojos.simplepojos;

public interface SingleImplementationInterface {

  public static class SingleImplementationOfInterface implements SingleImplementationInterface {

    private long testedField;

    public long getTestedField() {
      return testedField;
    }

    public void setTestedField(long testedField) {
      this.testedField = testedField;
    }
  }
}
