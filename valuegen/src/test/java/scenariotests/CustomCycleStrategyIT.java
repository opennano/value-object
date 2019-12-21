package scenariotests;

import static com.github.opennano.valuegen.Valuegen.createValueObject;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;

import com.github.opennano.valuegen.GeneratorConfig;
import com.github.opennano.valuegen.generator.strategies.CycleStrategy;

import scenariotests.samplepojos.realisticpojos.BidirectionalOne;
import scenariotests.samplepojos.realisticpojos.DuplicatedBidirectionalField;
import scenariotests.samplepojos.realisticpojos.DuplicatedField;

public class CustomCycleStrategyIT {

  @Test
  public void nullCycleStrategy_simpleCycle() {
    GeneratorConfig config = new GeneratorConfig();
    config.setCycleStrategy(CycleStrategy.NULL_DESCENDENT);

    BidirectionalOne actual = (BidirectionalOne) createValueObject(BidirectionalOne.class, config);
    assertThat(actual.getTwo().getOne(), is(nullValue()));
  }

  @Test
  public void nullCycleStrategy_notACycle() {
    // just because one type of object appears twice doesn't mean it's a cycle
    // the relation must be parent-child to trigger the cycle handling behavior

    GeneratorConfig config = new GeneratorConfig();
    config.setCycleStrategy(CycleStrategy.NULL_DESCENDENT);

    DuplicatedField actual = (DuplicatedField) createValueObject(DuplicatedField.class, config);
    assertThat(actual.getA(), is(notNullValue()));
    assertThat(actual.getB(), is(notNullValue()));
  }

  @Test
  public void generateObjectWithMultipleCyclesOfSameType() {
    // multiple cycles can exist for a single type, in which case null is set on all

    GeneratorConfig config = new GeneratorConfig();
    config.setCycleStrategy(CycleStrategy.NULL_DESCENDENT);

    DuplicatedBidirectionalField actual =
        (DuplicatedBidirectionalField)
            createValueObject(DuplicatedBidirectionalField.class, config);

    assertThat(actual.getA().getParent(), is(nullValue()));
    assertThat(actual.getB().getParent(), is(nullValue()));
  }

  @Test
  public void customCycleStrategy_simpleCycle() {
    AtomicReference<Class<?>> callbackValueClass = new AtomicReference<>();
    AtomicReference<Object> callbackParentValue = new AtomicReference<>();
    AtomicInteger callbackCount = new AtomicInteger();

    GeneratorConfig config = new GeneratorConfig();
    config.setCycleStrategy(
        (valueClass, parentValue) -> {
          callbackValueClass.set(valueClass);
          callbackParentValue.set(parentValue);
          callbackCount.incrementAndGet();
          return null;
        });

    // assert that we got called once with the expected arguments
    BidirectionalOne actual = (BidirectionalOne) createValueObject(BidirectionalOne.class, config);
    assertThat(callbackValueClass.get(), sameInstance(BidirectionalOne.class));
    assertThat(callbackParentValue.get(), is(actual));
    assertThat(callbackCount.get(), is(1));
  }

  @Test
  public void customCycleStrategy_notACycle() {
    AtomicInteger callbackCount = new AtomicInteger();
    GeneratorConfig config = new GeneratorConfig();
    config.setCycleStrategy(
        (valueClass, parentValue) -> {
          callbackCount.incrementAndGet();
          return null;
        });

    createValueObject(DuplicatedField.class, config);
    assertThat(callbackCount.get(), is(0));
  }
}
