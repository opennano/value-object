package scenariotests;

import static com.github.opennano.jsongen.Jsongen.asJson;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

import scenariotests.pojos.ExceptionFieldObject;
import scenariotests.pojos.ShadowedFieldObject;

public class EdgeCaseIT extends BaseIntegrationTest {

  @Test
  public void shadowedFieldsAreLost() {
    String typeName = ShadowedFieldObject.class.getName();
    String expected = simpleJson("testedField", "mockTestedField2", TYPE_PROPERTY, typeName);
    assertThat(asJson(ShadowedFieldObject.class), jsonEquals(expected));
  }

  @Test
  public void generateExceptionFieldObject() {
    // core java types often produce a mess, but they do work
    assertThat(
        asJson(ExceptionFieldObject.class),
        jsonEquals(fileContents("mock-simple-object-with-exception-field.json")));
  }
}
