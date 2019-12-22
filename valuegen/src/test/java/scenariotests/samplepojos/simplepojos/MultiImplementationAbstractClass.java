package scenariotests.samplepojos.simplepojos;

public abstract class MultiImplementationAbstractClass {

  public static class FirstImplementationOfAbstractClass extends MultiImplementationAbstractClass {}

  public static class SecondImplementationOfAbstractClass
      extends MultiImplementationAbstractClass {}
}
