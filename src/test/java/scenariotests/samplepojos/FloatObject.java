package scenariotests.samplepojos;

public class FloatObject {

  @SuppressWarnings("unused") // used only by reflection
  private float testedField;

  public FloatObject(float testedField) {
    this.testedField = testedField;
  }
}
