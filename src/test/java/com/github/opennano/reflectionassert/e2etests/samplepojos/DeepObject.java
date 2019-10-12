package com.github.opennano.reflectionassert.e2etests.samplepojos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused") // used only by reflection
public class DeepObject {

  private Wrapper root;

  /** puts the provided value at the end of a long property path */
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
