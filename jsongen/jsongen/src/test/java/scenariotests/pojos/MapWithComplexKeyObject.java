package scenariotests.pojos;

import java.util.Map;

@SuppressWarnings("unused") // used only by reflection
public class MapWithComplexKeyObject {

  private Map<MapKey, Integer> testedField;

  private static final class MapKey {
	  private String keyStringField;
	  private int keyIntField;
  }
}
