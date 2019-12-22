package scenariotests.samplepojos.realisticpojos;

public class DuplicatedField {

  private Child a;
  private Child b;

  public Child getA() {
    return a;
  }

  public void setA(Child a) {
    this.a = a;
  }

  public Child getB() {
    return b;
  }

  public void setB(Child b) {
    this.b = b;
  }

  public static class Child {}
}
