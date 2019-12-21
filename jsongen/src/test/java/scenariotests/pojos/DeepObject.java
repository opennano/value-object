package scenariotests.pojos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused") // used only by reflection
public class DeepObject {

  private Level1 level1Field;

  private static class Level1 {
    private List<Level2> level2Fields;
  }

  private static class Level2 {
    private Map<String, Level3> level3Field;
  }

  private static class Level3 {
    private String level4Field;
  }
}
