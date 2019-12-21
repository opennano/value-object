package scenariotests.pojos;

@SuppressWarnings("unused")
public class DuplicatedBidirectionalField {

  private Child a;
  private Child b;

  private static class Child {
    private GrandChild grandChild;
  }

  private static class GrandChild {
    private Child parent;
  }
}
