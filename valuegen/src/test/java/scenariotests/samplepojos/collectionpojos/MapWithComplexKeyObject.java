package scenariotests.samplepojos.collectionpojos;

import java.util.Map;

public class MapWithComplexKeyObject {

  private Map<MapKey, Integer> testedField;

  public Map<MapKey, Integer> getTestedField() {
    return testedField;
  }

  public void setTestedField(Map<MapKey, Integer> testedField) {
    this.testedField = testedField;
  }

  public static final class MapKey {
    private String keyStringField;
    private int keyIntField;

    public String getKeyStringField() {
      return keyStringField;
    }

    public void setKeyStringField(String keyStringField) {
      this.keyStringField = keyStringField;
    }

    public int getKeyIntField() {
      return keyIntField;
    }

    public void setKeyIntField(int keyIntField) {
      this.keyIntField = keyIntField;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + keyIntField;
      result = prime * result + ((keyStringField == null) ? 0 : keyStringField.hashCode());
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null) return false;
      if (getClass() != obj.getClass()) return false;
      MapKey other = (MapKey) obj;
      if (keyIntField != other.keyIntField) return false;
      if (keyStringField == null) {
        if (other.keyStringField != null) return false;
      } else if (!keyStringField.equals(other.keyStringField)) return false;
      return true;
    }
  }
}
