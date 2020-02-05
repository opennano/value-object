package com.github.opennano.valuegen.generator.delegates;

import static com.github.opennano.reflectionassert.hamcrest.ReflectionEqualsMatcher.lenientEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.opennano.valuegen.ValueGenerationException;
import com.github.opennano.valuegen.generator.TypeInfo;
import com.github.opennano.valuegen.utils.AutoboxingUtil;

@ExtendWith(MockitoExtension.class)
public class NumberValueDelegateTest {

  @InjectMocks private NumberValueDelegate delegate;

  @ParameterizedTest
  @ValueSource(
      classes = {
        byte.class,
        Byte.class,
        short.class,
        Short.class,
        int.class,
        Integer.class,
        long.class,
        Long.class,
        float.class,
        Float.class,
        double.class,
        Double.class,
        Number.class,
        BigDecimal.class,
      })
  public void handlesClass_handledTypes(Class<?> type) {
    assertThat(delegate.handlesClass(type), is(true));
  }

  @ParameterizedTest
  @ValueSource(classes = {String.class, AtomicInteger.class, Object.class})
  public void handlesClass_notHandledTypes(Class<?> type) {
    assertThat(delegate.handlesClass(type), is(false));
  }

  @ParameterizedTest
  @ValueSource(
      classes = {
        byte.class,
        Byte.class,
        short.class,
        Short.class,
        int.class,
        Integer.class,
        long.class,
        Long.class,
        float.class,
        Float.class,
        double.class,
        Double.class,
      })
  public void generateValue_basicTypes(Class<?> type) {
    Object numberValue = delegate.generateValue(new TypeInfo(type), null, null, null, null);

    assertThat(numberValue, lenientEquals(1));

    Class<?> boxedType = type.isPrimitive() ? AutoboxingUtil.boxedEquivalent(type) : type;
    assertThat(numberValue.getClass(), equalTo(boxedType));
  }

  @Test
  public void generateValue_incrementsAndResets() {
    TypeInfo typeInfo = new TypeInfo(Integer.class);
    for (int i = 1; i < 127; i++) {
      int intValue = delegate.generateValue(typeInfo, null, null, null, null).intValue();
      assertThat(intValue, equalTo(i));
    }
    int intValue = delegate.generateValue(typeInfo, null, null, null, null).intValue();
    assertThat(intValue, equalTo(1));
  }

  @Test
  public void generateValue_numberTypeTreatedAsInteger() {
    TypeInfo typeInfo = new TypeInfo(Number.class);
    Object numberValue = delegate.generateValue(typeInfo, null, null, null, null);

    assertThat(numberValue, lenientEquals(1));
    assertThat(numberValue.getClass(), equalTo(Integer.class));
  }

  @Test
  public void generateValue_bigDecimalTypeIsSupported() {
    TypeInfo typeInfo = new TypeInfo(BigDecimal.class);
    Object numberValue = delegate.generateValue(typeInfo, null, null, null, null);

    assertThat(numberValue.getClass(), equalTo(BigDecimal.class));
    assertThat(((BigDecimal) numberValue).intValue(), is(1));
  }

  //  @Test TODO handle these
  public void generateValue_concurrencyTypesAreSupported() {
    TypeInfo typeInfo = new TypeInfo(AtomicInteger.class);
    Object numberValue = delegate.generateValue(typeInfo, null, null, null, null);

    assertThat(numberValue.getClass(), equalTo(AtomicInteger.class));
    assertThat(((AtomicInteger) numberValue).get(), is(1));
  }

  @Test
  public void generateValue_strangeTypesThrowAHelpfulMessage() {
    TypeInfo typeInfo = new TypeInfo(StrangeNumber.class);
    assertThrows(
        ValueGenerationException.class,
        () -> delegate.generateValue(typeInfo, null, null, null, null));
  }

  /** This number type can't be instantiated in any meaningful way, so an error should be thrown */
  private static class StrangeNumber extends Number {

    private static final long serialVersionUID = 1L;

    @Override
    public int intValue() {
      return 0;
    }

    @Override
    public long longValue() {
      return 0;
    }

    @Override
    public float floatValue() {
      return 0;
    }

    @Override
    public double doubleValue() {
      return 0;
    }
  }
}
