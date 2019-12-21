package scenariotests.pojos;

import java.io.Serializable;

public class AmbiguousObject {

  @SuppressWarnings("unused") // used only by reflection
  private Serializable testedField;
}
