package scenariotests;

import static com.github.opennano.reflectionassert.hamcrest.ReflectionEqualsMatcher.reflectionEquals;
import static com.github.opennano.valuegen.Valuegen.createValueObject;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import scenariotests.samplepojos.simplepojos.ExcitingEnum;

/** exhaustively test all primitive or boxed value types */
public class PrimitiveValueIT {

  @ParameterizedTest
  @ValueSource(
      classes = {
        boolean.class,
        Boolean.class,
      })
  public void generateBooleanValue(Class<?> type) {
    assertThat(createValueObject(type), reflectionEquals(true));
  }

  @ParameterizedTest
  @ValueSource(
      classes = {
        char.class,
        Character.class,
      })
  public void generateCharacterValue(Class<?> type) {
    assertThat(createValueObject(type), reflectionEquals('a'));
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
  public void generateFixedPointValue(Class<?> type) {
    assertThat(createValueObject(type), reflectionEquals(1));
  }

  @ParameterizedTest
  @ValueSource(
      classes = {
        String.class,
        CharSequence.class,
      })
  public void generateStringValue(Class<?> type) {
    assertThat(createValueObject(type), reflectionEquals("mockString"));
  }

  @Test
  public void generateEnumValue() {
    assertThat(createValueObject(ExcitingEnum.class), reflectionEquals(ExcitingEnum.OOH));
  }

  @Test
  public void generateDateValue() {
    assertThat(createValueObject(Date.class), reflectionEquals(new Date(1000)));
  }

  @Test
  public void generateCalendarValue() {
    Calendar expected = Calendar.getInstance();
    expected.setTimeZone(TimeZone.getTimeZone("UTC"));
    expected.setTime(new Date(1000));
    assertThat(createValueObject(Calendar.class), reflectionEquals(expected));
  }

  @Test
  public void generateFileValue() {
    assertThat(createValueObject(File.class), reflectionEquals(new File("/mock/path/file")));
  }
}
