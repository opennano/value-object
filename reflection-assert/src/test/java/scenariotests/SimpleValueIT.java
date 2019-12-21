package scenariotests;

import static com.github.opennano.reflectionassert.LeniencyMode.IGNORE_DEFAULTS;
import static com.github.opennano.reflectionassert.LeniencyMode.LENIENT_DATES;
import static com.github.opennano.reflectionassert.LeniencyMode.LENIENT_ORDER;
import static com.github.opennano.reflectionassert.ReflectionAssertions.assertReflectionEquals;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

import org.junit.jupiter.api.Test;

import com.github.opennano.reflectionassert.LeniencyMode;

public class SimpleValueIT extends BaseIntegrationTest {

  //////////// STRING VALUE TESTS ////////////

  @Test
  public void assertReflectionEquals_stringValuesSame() {
    assertReflectionEquals("x", "x");
  }

  @Test
  public void assertReflectionEquals_stringValuesDifferent() {
    assertComparisonThrowsWithMessage("x", "y", "$", "\"x\"", "\"y\"");
  }

  @Test
  public void assertReflectionEquals_stringValuesBothDefault() {
    assertReflectionEquals(null, null);
  }

  @Test
  public void assertReflectionEquals_stringValuesDefaultActualStrict() {
    assertComparisonThrowsWithMessage("x", null, "$", "\"x\"", "null");
  }

  @Test
  public void assertReflectionEquals_stringValuesDefaultActualLenient() {
    assertComparisonThrowsWithMessage("x", null, "$", "\"x\"", "null", IGNORE_DEFAULTS);
  }

  @Test
  public void assertReflectionEquals_stringValuesDefaultExpectedStrict() {
    assertComparisonThrowsWithMessage(null, "x", "$", "null", "\"x\"");
  }

  @Test
  public void assertReflectionEquals_stringValuesDefaultExpectedLenient() {
    assertReflectionEquals(null, "x", new LeniencyMode[] {IGNORE_DEFAULTS});
  }

  ////////// DATE VALUE TESTS ////////////

  @Test
  public void assertReflectionEquals_dateValuesDifferent() {
    // use date objects as its toString returns different values depending on time zone
    assertComparisonThrowsWithMessage(
        new Date(1), new Date(2), "$", new Date(1).toString(), new Date(0).toString());
  }

  @Test
  public void assertReflectionEquals_dateValuesDifferentIgnored() {
    assertReflectionEquals(new Date(1), new Date(2), LENIENT_DATES);
  }

  @Test
  public void assertReflectionEquals_dateValuesDefaultActualIgnored() {
    // use date object as its toString returns different values depending on time zone
    assertComparisonThrowsWithMessage(
        new Date(1), null, "$", new Date(1).toString(), "null", LENIENT_DATES);
  }

  @Test
  public void assertReflectionEquals_dateValuesDefaultActualIgnoredLenient() {
    // use date object as its toString returns different values depending on time zone
    assertComparisonThrowsWithMessage(
        new Date(1), null, "$", new Date(1).toString(), "null", LENIENT_DATES, IGNORE_DEFAULTS);
  }

  @Test
  public void assertReflectionEquals_dateValuesDefaultExpectedIgnored() {
    // use date object as its toString returns different values depending on time zone
    assertComparisonThrowsWithMessage(
        null, new Date(1), "$", "null", new Date(1).toString(), LENIENT_DATES);
  }

  @Test
  public void assertReflectionEquals_dateValuesDefaultExpectedIgnoredLenient() {
    assertReflectionEquals(null, new Date(1), new LeniencyMode[] {LENIENT_DATES, IGNORE_DEFAULTS});
  }

  ////////// CALENDAR VALUE TESTS ////////////

  @Test
  public void assertReflectionEquals_calendarValuesDifferent() {
    // use calendar objects as its toString returns different values depending on time zone
    assertComparisonThrowsWithMessage(
        calendarFromMillis(1),
        calendarFromMillis(2),
        "$",
        calendarFromMillis(1).toString(),
        calendarFromMillis(2).toString());
  }

  @Test
  public void assertReflectionEquals_calendarValuesDifferentIgnored() {
    assertReflectionEquals(calendarFromMillis(1), calendarFromMillis(2), LENIENT_DATES);
  }

  @Test
  public void assertReflectionEquals_calendarValuesDefaultActualIgnored() {
    // use calendar object as its toString returns different values depending on time zone
    assertComparisonThrowsWithMessage(
        calendarFromMillis(1), null, "$", calendarFromMillis(1).toString(), "null", LENIENT_DATES);
  }

  @Test
  public void assertReflectionEquals_calendarValuesDefaultActualIgnoredLenient() {
    // use calendar object as its toString returns different values depending on time zone
    assertComparisonThrowsWithMessage(
        calendarFromMillis(1),
        null,
        "$",
        calendarFromMillis(1).toString(),
        "null",
        LENIENT_DATES,
        IGNORE_DEFAULTS);
  }

  @Test
  public void assertReflectionEquals_calendarValuesDefaultExpectedIgnored() {
    // use calendar object as its toString returns different values depending on time zone
    assertComparisonThrowsWithMessage(
        null, calendarFromMillis(1), "$", "null", calendarFromMillis(1).toString(), LENIENT_DATES);
  }

  @Test
  public void assertReflectionEquals_calendarValuesDefaultExpectedIgnoredLenient() {
    assertReflectionEquals(
        null, calendarFromMillis(1), new LeniencyMode[] {LENIENT_DATES, IGNORE_DEFAULTS});
  }

  ////////// ENUM VALUE TESTS ////////////

  @Test
  public void assertReflectionEquals_enumValuesSame() {
    assertReflectionEquals(LENIENT_ORDER, LENIENT_ORDER);
  }

  @Test
  public void assertReflectionEquals_enumValuesDifferent() {
    assertComparisonThrowsWithMessage(
        LENIENT_ORDER, IGNORE_DEFAULTS, "$", "LENIENT_ORDER", "IGNORE_DEFAULTS");
  }

  @Test
  public void assertReflectionEquals_enumValuesBothDefault() {
    assertReflectionEquals(null, null);
  }

  @Test
  public void assertReflectionEquals_enumValuesDefaultActualStrict() {
    assertComparisonThrowsWithMessage(LENIENT_ORDER, null, "$", "LENIENT_ORDER", "null");
  }

  @Test
  public void assertReflectionEquals_enumValuesDefaultActualLenient() {
    assertComparisonThrowsWithMessage(
        LENIENT_ORDER, null, "$", "LENIENT_ORDER", "null", IGNORE_DEFAULTS);
  }

  @Test
  public void assertReflectionEquals_enumValuesDefaultExpectedStrict() {
    assertComparisonThrowsWithMessage(null, LENIENT_ORDER, "$", "null", "LENIENT_ORDER");
  }

  @Test
  public void assertReflectionEquals_enumValuesDefaultExpectedLenient() {
    assertReflectionEquals(null, LENIENT_ORDER, new LeniencyMode[] {IGNORE_DEFAULTS});
  }

  ////////// FILE VALUE TESTS ////////////

  @Test
  public void assertReflectionEquals_fileValuesSame() {
    assertReflectionEquals(LENIENT_ORDER, LENIENT_ORDER);
  }

  @Test
  public void assertReflectionEquals_fileValuesDifferent() {
    assertComparisonThrowsWithMessage(
        new File("some/path"),
        new File("some/other/path"),
        "$",
        "File<some/path>",
        "File<some/other/path>");
  }

  @Test
  public void assertReflectionEquals_fileValuesDefaultActualStrict() {
    assertComparisonThrowsWithMessage(new File("some/path"), null, "$", "File<some/path>", "null");
  }

  @Test
  public void assertReflectionEquals_fileValuesDefaultActualLenient() {
    assertComparisonThrowsWithMessage(new File("some/path"), null, "$", "File<some/path>", "null");
  }

  @Test
  public void assertReflectionEquals_fileValuesDefaultExpectedStrict() {
    assertComparisonThrowsWithMessage(null, new File("some/path"), "$", "null", "File<some/path>");
  }

  @Test
  public void assertReflectionEquals_fileValuesDefaultExpectedLenient() {
    assertReflectionEquals(null, new File("some/path"), new LeniencyMode[] {IGNORE_DEFAULTS});
  }

  ////////// UTILITY METHODS //////////

  private Object calendarFromMillis(int millis) {
    Calendar instance = Calendar.getInstance();
    instance.setTimeInMillis(millis);
    return instance;
  }
}
