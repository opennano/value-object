package scenariotests;

import static com.github.opennano.jsongen.Jsongen.asJson;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

import scenariotests.pojos.BidirectionalOne;
import scenariotests.pojos.DuplicatedBidirectionalField;
import scenariotests.pojos.DuplicatedField;

/**
 * Test serialization of different types of cycles. A cycle occurs when a node in the object graph
 * is descendent of some parent node of the same type. See the first test for the simplest possible
 * example.
 */
public class CircularReferenceIT extends BaseIntegrationTest {

  @Test
  public void generateObjectWithCycle() {
    // simplest case: object one has an object two, which has an object one
    assertThat(
        asJson(BidirectionalOne.class), jsonEquals(fileContents("mock-simple-cycle-object.json")));
  }

  @Test
  public void generateObjectWithMultipleNonCyclicInstances() {
    // just because one type of object appears twice doesn't mean it's a cycle
    // the relation must be parent-child to trigger the cycle handling behavior
    assertThat(
        asJson(DuplicatedField.class),
        jsonEquals(fileContents("mock-duplicated-field-object.json")));
  }

  @Test
  public void generateObjectWithMultipleCyclesOfSameType() {
    // multiple cycles can exist for a single type
    assertThat(
        asJson(DuplicatedBidirectionalField.class),
        jsonEquals(fileContents("mock-multi-cycle-same-type-object.json")));
  }
}
