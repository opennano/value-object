package scenariotests.pojos;

public class ShadowedFieldObject extends ShadowedField {

  private String testedField;
  
  public String getField() {
	  return testedField;
  }
}

class ShadowedField {

  private String testedField;
  
  public String getShadowedField() {
	  return testedField;
  }
}
