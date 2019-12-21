package scenariotests;

import static com.github.opennano.jsongen.Jsongen.asJson;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

import scenariotests.pojos.AmbiguousObject;
import scenariotests.pojos.DeepObject;
import scenariotests.pojos.User;

public class HappyPathIT extends BaseIntegrationTest {

  @Test
  public void generateSimpleUserJavaBean() {
    assertThat(asJson(User.class), jsonEquals(fileContents("mock-user-object.json")));
  }

  @Test
  public void generateDeeplyNestedObject() {
    assertThat(asJson(DeepObject.class), jsonEquals(fileContents("mock-deep-object.json")));
  }

  @Test
  public void generateAmbiguousObject() {
    // Serializable interface will be proxied
    assertThat(
        asJson(AmbiguousObject.class),
        jsonEquals(fileContents("mock-object-with-object-field.json")));
  }
}
