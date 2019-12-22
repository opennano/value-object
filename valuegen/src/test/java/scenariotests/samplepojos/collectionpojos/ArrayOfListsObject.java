package scenariotests.samplepojos.collectionpojos;

import java.util.List;

//unused as yet
public class ArrayOfListsObject {

  @SuppressWarnings("unused") // used only by reflection
  private List<Integer>[] testedField;

  public ArrayOfListsObject(List<Integer>[] testedField) {
    this.testedField = testedField;
  }
}
