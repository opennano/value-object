package scenariotests.samplepojos.extremepojos;

import java.util.List;
import java.util.Map;

public class DeepObject {

  private Level1 level1Field;

  public Level1 getLevel1Field() {
    return level1Field;
  }

  public void setLevel1Field(Level1 level1Field) {
    this.level1Field = level1Field;
  }

  public static class Level1 {
    private List<Level2> level2Fields;

    public List<Level2> getLevel2Fields() {
      return level2Fields;
    }

    public void setLevel2Fields(List<Level2> level2Fields) {
      this.level2Fields = level2Fields;
    }
  }

  public static class Level2 {
    private Map<String, Level3> level3Field;

    public Map<String, Level3> getLevel3Field() {
      return level3Field;
    }

    public void setLevel3Field(Map<String, Level3> level3Field) {
      this.level3Field = level3Field;
    }
  }

  public static class Level3 {
    private String level4Field;

    public String getLevel4Field() {
      return level4Field;
    }

    public void setLevel4Field(String level4Field) {
      this.level4Field = level4Field;
    }
  }
}
