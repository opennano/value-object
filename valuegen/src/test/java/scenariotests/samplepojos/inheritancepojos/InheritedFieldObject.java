package scenariotests.samplepojos.inheritancepojos;

public class InheritedFieldObject extends ParentObject {

  private String childField;

  public String getChildField() {
    return childField;
  }

  public void setChildField(String childField) {
    this.childField = childField;
  }
}

class ParentObject {

  private String parentField;

  public String getParentField() {
    return parentField;
  }

  public void setParentField(String parentField) {
    this.parentField = parentField;
  }
}
