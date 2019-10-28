package scenariotests.samplepojos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused") // used only by reflection
public class DeepObject {

  private Wrapper root;

  /**
   * Puts the provided value at the end of a long property path
   *
   * @param terminalValue the value to put at the end of the deep object path
   */
  public DeepObject(Object terminalValue) {

    Map<String, Object> map = new HashMap<>();
    map.put("xyz", new Wrapper(terminalValue));

    MapType mapType = new MapType();
    mapType.mapField = map;

    ListType listType = new ListType();
    listType.listField = Arrays.asList(new Wrapper(mapType));

    root = new Wrapper(listType);
  }

  private static class Wrapper {
    private Object wrappedField;

    private Wrapper(Object wrapped) {
      this.wrappedField = wrapped;
    }
  }

  private static class ListType {
    private List<?> listField;
  }

  private static class MapType {
    private Map<String, Object> mapField;
  }
}
