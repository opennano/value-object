package scenariotests;

import static com.github.opennano.reflectionassert.hamcrest.ReflectionEqualsMatcher.reflectionEquals;
import static com.github.opennano.valuegen.ProxyMatcher.isProxy;
import static com.github.opennano.valuegen.Valuegen.createValueObject;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;

import com.github.opennano.valuegen.GeneratorConfig;
import com.github.opennano.valuegen.generator.TypeInfo;
import com.github.opennano.valuegen.generator.strategies.SubtypeStrategy;

public class CustomSubtypeStrategyIT {

  @Test
  public void skipTypesSubtypeStrategy_success() {
    GeneratorConfig config = new GeneratorConfig();
    config.setSubTypeStrategy(SubtypeStrategy.SKIP_TYPES);

    Serializable valueObject = (Serializable) createValueObject(Serializable.class, config);
    assertThat(valueObject, isProxy()); // value is created
  }

  @Test
  public void customSubtypeStrategy_succeeds() {
    AtomicReference<TypeInfo> callbackTypeInfo = new AtomicReference<>();
    AtomicInteger callbackCount = new AtomicInteger();

    TypeInfo expectedInfo = new TypeInfo();
    expectedInfo.setResolvedClass(Serializable.class);

    GeneratorConfig config = new GeneratorConfig();
    config.setSubTypeStrategy(
        typeInfo -> {
          callbackTypeInfo.set(typeInfo);
          callbackCount.incrementAndGet();
          return Object.class;
        });

    Object valueObject = createValueObject(Serializable.class, config);
    assertThat(valueObject.getClass(), is(sameInstance(Object.class)));
    assertThat(callbackTypeInfo.get(), reflectionEquals(expectedInfo));
    assertThat(callbackCount.get(), is(1));
  }

  @Test
  public void customSubtypeStrategy_throws() {
    GeneratorConfig config = new GeneratorConfig();
    config.setSubTypeStrategy(
        typeInfo -> {
          throw new RuntimeException();
        });

    Serializable valueObject = (Serializable) createValueObject(Serializable.class, config);
    assertThat(valueObject, is(nullValue()));
  }
}
