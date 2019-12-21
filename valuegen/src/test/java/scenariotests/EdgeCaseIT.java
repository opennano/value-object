package scenariotests;

import static com.github.opennano.reflectionassert.hamcrest.ReflectionEqualsMatcher.reflectionEquals;
import static com.github.opennano.valuegen.Valuegen.createValueObject;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

import org.junit.jupiter.api.Test;

import scenariotests.samplepojos.extremepojos.ExceptionFieldObject;
import scenariotests.samplepojos.extremepojos.GenericBoundedAboveAndBelow;
import scenariotests.samplepojos.extremepojos.LudicrousTypeObjectParent.LudicrousTypeObject;
import scenariotests.samplepojos.extremepojos.ShadowedFieldObject;

public class EdgeCaseIT {

  @Test
  public void generateObjectWithShadowedField() {
    // not a problem to generate such a value object
    ShadowedFieldObject actual = createValueObject(ShadowedFieldObject.class);

    assertThat(actual.getField(), is("mockTestedField2"));
    assertThat(actual.getShadowedField(), is("mockTestedField"));
  }

  @Test
  public void generateExceptionFieldObject() {
    // core java types often produce a messy object, but they do work
    ExceptionFieldObject actual = createValueObject(ExceptionFieldObject.class);
    assertThat(actual.getTestedField().getClass(), is(equalTo(Exception.class)));
  }

  @Test
  public void generateGenericBoundedAboveAndBelow() {
    GenericBoundedAboveAndBelow<Number> expected = new GenericBoundedAboveAndBelow<>();
    expected.setTestedField(Arrays.asList(1));

    GenericBoundedAboveAndBelow<?> actual = createValueObject(GenericBoundedAboveAndBelow.class);
    assertThat(actual, reflectionEquals(expected));
  }

  @Test
  public void generateLudicrousTypeObject() {
    // a field that uses a tour-do-force of all Java types
    LudicrousTypeObject<BigDecimal> expected = new LudicrousTypeObject<>();
    TreeMap<String, List<? super BigDecimal[][]>> map = new TreeMap<>();
    map.put("mockTestedFieldKey", Arrays.asList(new BigDecimal[][][] {{{new BigDecimal(1)}}}));
    expected.setTestedField(map);

    assertThat(createValueObject(LudicrousTypeObject.class), reflectionEquals(expected));
  }
}
