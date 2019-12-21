package scenariotests;

import static com.github.opennano.jsongen.Jsongen.asJson;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import scenariotests.pojos.MapWithComplexKeyObject;

/** only test types that are interesting from a json marshaling perspective */
public class SimpleValueIT extends BaseIntegrationTest {
  @ParameterizedTest
  @ValueSource(
      classes = {
        byte[].class,
        Byte[].class,
        short[].class,
        Short[].class,
        int[].class,
        Integer[].class,
        long[].class,
        Long[].class,
        float[].class,
        Float[].class,
        double[].class,
        Double[].class,
      })
  public void generateNumericArrayValue(Class<?> type) {
    assertThat(asJson(type), jsonEquals("[1]"));
  }

  @ParameterizedTest
  @ValueSource(
      classes = {
        boolean[].class,
        Boolean[].class,
      })
  public void generateBooleanArrayValue(Class<?> type) {
    assertThat(asJson(type), jsonEquals("[true]"));
  }

  @ParameterizedTest
  @ValueSource(
      classes = {
        char[].class,
        Character[].class,
      })
  public void generateCharacterArrayValue(Class<?> type) {
    assertThat(asJson(type), jsonEquals("[\"a\"]"));
  }

  @Test
  public void generateStringArrayValue() {
    assertThat(asJson(String[].class), jsonEquals("[\"mockString\"]"));
  }

  @Test
  public void generateSimpleListValue() {
    assertThat(asJson(List.class), jsonEquals("[{}]"));
  }

  @Test
  public void generateSimpleMapValue() {
    assertThat(asJson(Map.class), jsonEquals("{\"mockKey\":{}}"));
  }

  @Test
  public void generateMapWithComplexKeyObject() {
    assertThat(
        asJson(MapWithComplexKeyObject.class),
        jsonEquals(fileContents("mock-map-with-complex-key-object.json")));
  }

  @Test
  public void generateStringValue() {
    assertThat(asJson(String.class), is("\"mockString\""));
  }

  @Test
  public void generateDateValue() {
    assertThat(asJson(Date.class), is("\"1970-01-01T00:00:01.000Z\""));
  }

  @Test
  public void generateCalendarValue() {
    assertThat(asJson(Calendar.class), is("\"1970-01-01T00:00:01.000Z\""));
  }
}
