package scenariotests;

import static com.github.opennano.valuegen.Valuegen.createValueObject;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;

import com.github.opennano.valuegen.GeneratorConfig;
import com.github.opennano.valuegen.generator.strategies.MapKeyStrategy;

import scenariotests.samplepojos.collectionpojos.MapWithComplexKeyObject;
import scenariotests.samplepojos.collectionpojos.MapWithComplexKeyObject.MapKey;
import scenariotests.samplepojos.collectionpojos.MapWithObjectKeyObject;

public class CustomMapStrategyIT {

  @Test
  public void stringOnlyKeyStrategy_nonStringKey() {
    GeneratorConfig config = new GeneratorConfig();
    config.setMapKeyStrategy(MapKeyStrategy.STRING_KEYS_ONLY);

    Map<?, ?> map = (Map<?, ?>) createValueObject(Map.class, config);
    assertThat(map.size(), is(1)); // value is created
  }

  @Test
  public void stringOnlyKeyStrategy_stringKey() {
    GeneratorConfig config = new GeneratorConfig();
    config.setMapKeyStrategy(MapKeyStrategy.STRING_KEYS_ONLY);

    MapWithComplexKeyObject valueObject =
        (MapWithComplexKeyObject) createValueObject(MapWithComplexKeyObject.class, config);

    assertThat(valueObject.getTestedField().size(), is(0)); // value is not created
  }

  @Test
  public void stringOnlyKeyStrategy_objectKey() {
    GeneratorConfig config = new GeneratorConfig();
    config.setMapKeyStrategy(MapKeyStrategy.STRING_KEYS_ONLY);

    MapWithObjectKeyObject valueObject =
        (MapWithObjectKeyObject) createValueObject(MapWithObjectKeyObject.class, config);

    assertThat(valueObject.getTestedField().size(), is(1)); // value is created
  }

  @Test
  public void stringOnlyKeyStrategy_succeeds() {
    AtomicReference<Class<?>> callbackType = new AtomicReference<>();
    AtomicInteger callbackCount = new AtomicInteger();

    GeneratorConfig config = new GeneratorConfig();
    config.setMapKeyStrategy(
        type -> {
          callbackType.set(type);
          callbackCount.incrementAndGet();
          return String.class;
        });
    
    MapWithComplexKeyObject valueObject =
        (MapWithComplexKeyObject) createValueObject(MapWithComplexKeyObject.class, config);

    assertThat(valueObject.getTestedField().size(), is(1)); // value is created
    assertThat(callbackType.get(), sameInstance(MapKey.class));
    assertThat(callbackCount.get(), is(1));
  }

  @Test
  public void stringOnlyKeyStrategy_returnNull() {
    AtomicReference<Class<?>> callbackType = new AtomicReference<>();
    AtomicInteger callbackCount = new AtomicInteger();

    GeneratorConfig config = new GeneratorConfig();
    config.setMapKeyStrategy(
        type -> {
          callbackType.set(type);
          callbackCount.incrementAndGet();
          return null;
        });
    
    MapWithComplexKeyObject valueObject =
        (MapWithComplexKeyObject) createValueObject(MapWithComplexKeyObject.class, config);

    assertThat(valueObject.getTestedField().size(), is(0)); // value is not created
    assertThat(callbackType.get(), sameInstance(MapKey.class));
    assertThat(callbackCount.get(), is(1));
  }

  @Test
  public void stringOnlyKeyStrategy_throws() {
    AtomicReference<Class<?>> callbackType = new AtomicReference<>();
    AtomicInteger callbackCount = new AtomicInteger();

    GeneratorConfig config = new GeneratorConfig();
    config.setMapKeyStrategy(
        type -> {
          callbackType.set(type);
          callbackCount.incrementAndGet();
          throw new RuntimeException();
        });
    
    MapWithComplexKeyObject valueObject =
        (MapWithComplexKeyObject) createValueObject(MapWithComplexKeyObject.class, config);

    assertThat(valueObject.getTestedField().size(), is(0)); // value is not created
    assertThat(callbackType.get(), sameInstance(MapKey.class));
    assertThat(callbackCount.get(), is(1));
  }
}
