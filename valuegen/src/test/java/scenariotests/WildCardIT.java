package scenariotests;

import static com.github.opennano.reflectionassert.hamcrest.ReflectionEqualsMatcher.reflectionEquals;
import static com.github.opennano.valuegen.Valuegen.createValueObject;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import scenariotests.samplepojos.wildcardpojos.WildCardIntegerLowerBoundObject;
import scenariotests.samplepojos.wildcardpojos.WildCardIntegerUpperBoundObject;
import scenariotests.samplepojos.wildcardpojos.WildCardNumberLowerBoundObject;
import scenariotests.samplepojos.wildcardpojos.WildCardNumberUpperBoundObject;

public class WildCardIT {

  ////////// WILDCARD PARAMETERS //////////

  @Test
  public void generateWildCardIntegerUpperBoundObject() {
    // produce a list of integers because Integer satisfies this wildcard
    // in other words, this compiles:
    // List<? extends Integer> x = Arrays.asList(1);
    WildCardIntegerUpperBoundObject expected = new WildCardIntegerUpperBoundObject();
    expected.setTestedField(Arrays.asList(1));

    WildCardIntegerUpperBoundObject actual =
        createValueObject(WildCardIntegerUpperBoundObject.class);

    assertThat(actual, reflectionEquals(expected));
  }

  @Test
  public void generateWildCardIntegerLowerBoundObject() {
    // produce a list of integers because Integer satisfies this wildcard
    // in other words, this compiles:
    // List<? super Integer> x = Arrays.asList(1);
    WildCardIntegerLowerBoundObject expected = new WildCardIntegerLowerBoundObject();
    expected.setTestedField(Arrays.asList(1));

    WildCardIntegerLowerBoundObject actual =
        createValueObject(WildCardIntegerLowerBoundObject.class);

    assertThat(actual, reflectionEquals(expected));
  }

  @Test
  public void generateWildCardNumberUpperBoundObject() {
    // produce a list of integers because Integer satisfies this wildcard
    // in other words, this compiles:
    // List<? extends Number> x = Arrays.asList(1);
    WildCardNumberUpperBoundObject expected = new WildCardNumberUpperBoundObject();
    expected.setTestedField(Arrays.asList(1));

    WildCardNumberUpperBoundObject actual = createValueObject(WildCardNumberUpperBoundObject.class);

    assertThat(actual, reflectionEquals(expected));
  }

  @Test
  public void generateWildCardNumberLowerBoundObject() {
    // produce a list of objects because an Integer satisfies this wildcard
    // in other words, this compiles:
    // List<? super Number> x = Arrays.asList(1);
    WildCardNumberLowerBoundObject expected = new WildCardNumberLowerBoundObject();
    expected.setTestedField(Arrays.asList(1));

    WildCardNumberLowerBoundObject actual = createValueObject(WildCardNumberLowerBoundObject.class);

    assertThat(actual, reflectionEquals(expected));
  }
}
