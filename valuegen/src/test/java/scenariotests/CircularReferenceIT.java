package scenariotests;

import static com.github.opennano.reflectionassert.hamcrest.ReflectionEqualsMatcher.reflectionEquals;
import static com.github.opennano.valuegen.Valuegen.createValueObject;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.sameInstance;

import org.junit.jupiter.api.Test;

import scenariotests.samplepojos.realisticpojos.BidirectionalOne;
import scenariotests.samplepojos.realisticpojos.BidirectionalTwo;
import scenariotests.samplepojos.realisticpojos.DuplicatedBidirectionalField;
import scenariotests.samplepojos.realisticpojos.DuplicatedField;
import scenariotests.samplepojos.realisticpojos.DuplicatedField.Child;

public class CircularReferenceIT {

  @Test
  public void generateObjectWithCycle() {
    // simplest case: object one has an object two, which has an object one
    BidirectionalOne expected = new BidirectionalOne();
    BidirectionalTwo two = new BidirectionalTwo();
    expected.setTwo(two);
    two.setOne(expected);

    BidirectionalOne actual = createValueObject(BidirectionalOne.class);
    assertThat(actual, reflectionEquals(expected));
    assertThat(actual.getTwo().getOne(), is(sameInstance(actual)));
  }

  @Test
  public void generateObjectWithMultipleNonCyclicInstances() {
    // just because one type of object appears twice doesn't mean it's a cycle
    // the relation must be parent-child to trigger the cycle handling behavior
    DuplicatedField expected = new DuplicatedField();

    Child childA = new Child();
    expected.setA(childA);

    Child childB = new Child();
    expected.setB(childB);

    DuplicatedField actual = createValueObject(DuplicatedField.class);
    assertThat(actual, reflectionEquals(expected));
    assertThat(actual.getA(), is(not(sameInstance(actual.getB()))));
  }

  @Test
  public void generateObjectWithMultipleCyclesOfSameType() {
    // multiple cycles can exist for a single type, in which case the parent is reused
    DuplicatedBidirectionalField expected = new DuplicatedBidirectionalField();

    DuplicatedBidirectionalField.Child theChild = new DuplicatedBidirectionalField.Child();
    theChild.setParent(expected);
    expected.setA(theChild);
    expected.setB(theChild);

    DuplicatedBidirectionalField actual = createValueObject(DuplicatedBidirectionalField.class);
    assertThat(actual, reflectionEquals(expected));
    assertThat(actual.getA().getParent(), is(sameInstance(actual.getB().getParent())));
  }
}
