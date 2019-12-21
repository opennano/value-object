package scenariotests.pojos;

@SuppressWarnings("unused")
public class DuplicatedField {

  private Child a;
  private Child b;

  private static class Child {
    private String testedField;
  }
}
