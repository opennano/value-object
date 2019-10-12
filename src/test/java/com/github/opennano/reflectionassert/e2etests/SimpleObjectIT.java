package com.github.opennano.reflectionassert.e2etests;

import static com.github.opennano.reflectionassert.LeniencyMode.IGNORE_DEFAULTS;
import static com.github.opennano.reflectionassert.LeniencyMode.LENIENT_DATES;
import static com.github.opennano.reflectionassert.ReflectionAssertions.assertReflectionEquals;

import java.util.Date;

import org.junit.jupiter.api.Test;

import com.github.opennano.reflectionassert.e2etests.samplepojos.BooleanObject;
import com.github.opennano.reflectionassert.e2etests.samplepojos.ByteObject;
import com.github.opennano.reflectionassert.e2etests.samplepojos.CharObject;
import com.github.opennano.reflectionassert.e2etests.samplepojos.DateObject;
import com.github.opennano.reflectionassert.e2etests.samplepojos.DoubleObject;
import com.github.opennano.reflectionassert.e2etests.samplepojos.FloatObject;
import com.github.opennano.reflectionassert.e2etests.samplepojos.IntObject;
import com.github.opennano.reflectionassert.e2etests.samplepojos.LongObject;
import com.github.opennano.reflectionassert.e2etests.samplepojos.ShortObject;
import com.github.opennano.reflectionassert.e2etests.samplepojos.SimpleObject;
import com.github.opennano.reflectionassert.e2etests.samplepojos.StringObject;

/** test simple objects with a single field of various types */
public class SimpleObjectIT extends BaseIntegrationTest {

  ////////// BOOLEAN FIELD TESTS //////////

  @Test
  public void assertReflectionEquals_booleansSame() {
    assertReflectionEquals(new BooleanObject(true), new BooleanObject(true));
  }

  @Test
  public void assertReflectionEquals_booleansBothDefault() {
    assertReflectionEquals(new BooleanObject(false), new BooleanObject(false));
  }

  @Test
  public void assertReflectionEquals_booleansDefaultActualStrict() {
    assertComparisonThrowsWithMessage(
        new BooleanObject(true), new BooleanObject(false), "testedField", "true", "false");
  }

  @Test
  public void assertReflectionEquals_booleansDefaultActualLenient() {
    assertComparisonThrowsWithMessage(
        new BooleanObject(true),
        new BooleanObject(false),
        "testedField",
        "true",
        "false",
        IGNORE_DEFAULTS);
  }

  @Test
  public void assertReflectionEquals_booleansDefaultExpectedStrict() {
    assertComparisonThrowsWithMessage(
        new BooleanObject(false), new BooleanObject(true), "testedField", "false", "true");
  }

  @Test
  public void assertReflectionEquals_booleansDefaultExpectedLenient() {
    assertReflectionEquals(new BooleanObject(false), new BooleanObject(true), IGNORE_DEFAULTS);
  }

  ////////// CHAR FIELD TESTS //////////

  @Test
  public void assertReflectionEquals_charsSame() {
    assertReflectionEquals(new CharObject('a'), new CharObject('a'));
  }

  @Test
  public void assertReflectionEquals_charsDifferent() {
    assertComparisonThrowsWithMessage(
        new CharObject('a'), new CharObject('b'), "testedField", "'a'", "'b'");
  }

  @Test
  public void assertReflectionEquals_charsBothDefault() {
    assertReflectionEquals(
        new CharObject(Character.MIN_VALUE), new CharObject(Character.MIN_VALUE));
  }

  @Test
  public void assertReflectionEquals_charsDefaultActualStrict() {
    assertComparisonThrowsWithMessage(
        new CharObject('a'), new CharObject(Character.MIN_VALUE), "testedField", "'a'", "'\u0000'");
  }

  @Test
  public void assertReflectionEquals_charsDefaultActualLenient() {
    assertComparisonThrowsWithMessage(
        new CharObject('a'),
        new CharObject(Character.MIN_VALUE),
        "testedField",
        "'a'",
        "'\u0000'",
        IGNORE_DEFAULTS);
  }

  @Test
  public void assertReflectionEquals_charsDefaultExpectedStrict() {
    assertComparisonThrowsWithMessage(
        new CharObject(Character.MIN_VALUE), new CharObject('a'), "testedField", "'\u0000'", "'a'");
  }

  @Test
  public void assertReflectionEquals_charsDefaultExpectedLenient() {
    assertReflectionEquals(
        new CharObject(Character.MIN_VALUE), new CharObject('a'), IGNORE_DEFAULTS);
  }

  ////////// BYTE FIELD TESTS //////////

  @Test
  public void assertReflectionEquals_bytesSame() {
    assertReflectionEquals(new ByteObject((byte) 1), new ByteObject((byte) 1));
  }

  @Test
  public void assertReflectionEquals_bytesDifferent() {
    assertComparisonThrowsWithMessage(
        new ByteObject((byte) 1), new ByteObject((byte) 2), "testedField", "1", "2");
  }

  @Test
  public void assertReflectionEquals_bytesBothDefault() {
    assertReflectionEquals(new ByteObject((byte) 0), new ByteObject((byte) 0));
  }

  @Test
  public void assertReflectionEquals_bytesDefaultActualStrict() {
    assertComparisonThrowsWithMessage(
        new ByteObject((byte) 1), new ByteObject((byte) 0), "testedField", "1", "0");
  }

  @Test
  public void assertReflectionEquals_bytesDefaultActualLenient() {
    assertComparisonThrowsWithMessage(
        new ByteObject((byte) 1),
        new ByteObject((byte) 0),
        "testedField",
        "1",
        "0",
        IGNORE_DEFAULTS);
  }

  @Test
  public void assertReflectionEquals_bytesDefaultExpectedStrict() {
    assertComparisonThrowsWithMessage(
        new ByteObject((byte) 0), new ByteObject((byte) 1), "testedField", "0", "1");
  }

  @Test
  public void assertReflectionEquals_bytesDefaultExpectedLenient() {
    assertReflectionEquals(new ByteObject((byte) 0), new ByteObject((byte) 1), IGNORE_DEFAULTS);
  }

  ////////// SHORT FIELD TESTS //////////

  @Test
  public void assertReflectionEquals_shortsSame() {
    assertReflectionEquals(new ShortObject((short) 1), new ShortObject((short) 1));
  }

  @Test
  public void assertReflectionEquals_shortsDifferent() {
    assertComparisonThrowsWithMessage(
        new ShortObject((short) 1), new ShortObject((short) 2), "testedField", "1", "2");
  }

  @Test
  public void assertReflectionEquals_shortsBothDefault() {
    assertReflectionEquals(new ShortObject((short) 0), new ShortObject((short) 0));
  }

  @Test
  public void assertReflectionEquals_shortsDefaultActualStrict() {
    assertComparisonThrowsWithMessage(
        new ShortObject((short) 1), new ShortObject((short) 0), "testedField", "1", "0");
  }

  @Test
  public void assertReflectionEquals_shortsDefaultActualLenient() {
    assertComparisonThrowsWithMessage(
        new ShortObject((short) 1),
        new ShortObject((short) 0),
        "testedField",
        "1",
        "0",
        IGNORE_DEFAULTS);
  }

  @Test
  public void assertReflectionEquals_shortsDefaultExpectedStrict() {
    assertComparisonThrowsWithMessage(
        new ShortObject((short) 0), new ShortObject((short) 1), "testedField", "0", "1");
  }

  @Test
  public void assertReflectionEquals_shortsDefaultExpectedLenient() {
    assertReflectionEquals(new ShortObject((short) 0), new ShortObject((short) 1), IGNORE_DEFAULTS);
  }

  ////////// INT FIELD TESTS //////////

  @Test
  public void assertReflectionEquals_intsSame() {
    assertReflectionEquals(new IntObject(1), new IntObject(1));
  }

  @Test
  public void assertReflectionEquals_intsDifferent() {
    assertComparisonThrowsWithMessage(new IntObject(1), new IntObject(2), "testedField", "1", "2");
  }

  @Test
  public void assertReflectionEquals_intsBothDefault() {
    assertReflectionEquals(new IntObject(0), new IntObject(0));
  }

  @Test
  public void assertReflectionEquals_intsDefaultActualStrict() {
    assertComparisonThrowsWithMessage(new IntObject(1), new IntObject(0), "testedField", "1", "0");
  }

  @Test
  public void assertReflectionEquals_intsDefaultActualLenient() {
    assertComparisonThrowsWithMessage(
        new IntObject(1), new IntObject(0), "testedField", "1", "0", IGNORE_DEFAULTS);
  }

  @Test
  public void assertReflectionEquals_intsDefaultExpectedStrict() {
    assertComparisonThrowsWithMessage(new IntObject(0), new IntObject(1), "testedField", "0", "1");
  }

  @Test
  public void assertReflectionEquals_intsDefaultExpectedLenient() {
    assertReflectionEquals(new IntObject(0), new IntObject(1), IGNORE_DEFAULTS);
  }

  ////////// LONG FIELD TESTS //////////

  @Test
  public void assertReflectionEquals_longsSame() {
    assertReflectionEquals(new LongObject(1l), new LongObject(1l));
  }

  @Test
  public void assertReflectionEquals_longsDifferent() {
    assertComparisonThrowsWithMessage(
        new LongObject(1l), new LongObject(2l), "testedField", "1", "2");
  }

  @Test
  public void assertReflectionEquals_longsBothDefault() {
    assertReflectionEquals(new LongObject(0l), new LongObject(0l));
  }

  @Test
  public void assertReflectionEquals_longsDefaultActualStrict() {
    assertComparisonThrowsWithMessage(
        new LongObject(1l), new LongObject(0l), "testedField", "1", "0");
  }

  @Test
  public void assertReflectionEquals_longsDefaultActualLenient() {
    assertComparisonThrowsWithMessage(
        new LongObject(1l), new LongObject(0l), "testedField", "1", "0", IGNORE_DEFAULTS);
  }

  @Test
  public void assertReflectionEquals_longsDefaultExpectedStrict() {
    assertComparisonThrowsWithMessage(
        new LongObject(0l), new LongObject(1l), "testedField", "0", "1");
  }

  @Test
  public void assertReflectionEquals_longsDefaultExpectedLenient() {
    assertReflectionEquals(new LongObject(0l), new LongObject(1l), IGNORE_DEFAULTS);
  }

  ////////// FLOAT FIELD TESTS //////////

  @Test
  public void assertReflectionEquals_floatsSame() {
    assertReflectionEquals(new FloatObject(1f), new FloatObject(1f));
  }

  @Test
  public void assertReflectionEquals_floatsDifferent() {
    assertComparisonThrowsWithMessage(
        new FloatObject(1f), new FloatObject(2f), "testedField", "1.0", "2.0");
  }

  @Test
  public void assertReflectionEquals_floatsBothDefault() {
    assertReflectionEquals(new FloatObject(0f), new FloatObject(0f));
  }

  @Test
  public void assertReflectionEquals_floatsDefaultActualStrict() {
    assertComparisonThrowsWithMessage(
        new FloatObject(1f), new FloatObject(0f), "testedField", "1.0", "0.0");
  }

  @Test
  public void assertReflectionEquals_floatsDefaultActualLenient() {
    assertComparisonThrowsWithMessage(
        new FloatObject(1f), new FloatObject(0f), "testedField", "1.0", "0.0", IGNORE_DEFAULTS);
  }

  @Test
  public void assertReflectionEquals_floatsDefaultExpectedStrict() {
    assertComparisonThrowsWithMessage(
        new FloatObject(0f), new FloatObject(1f), "testedField", "0.0", "1.0");
  }

  @Test
  public void assertReflectionEquals_floatsDefaultExpectedLenient() {
    assertReflectionEquals(new FloatObject(0f), new FloatObject(1f), IGNORE_DEFAULTS);
  }

  ////////// DOUBLE FIELD TESTS //////////

  @Test
  public void assertReflectionEquals_doublesSame() {
    assertReflectionEquals(new DoubleObject(1d), new DoubleObject(1d));
  }

  @Test
  public void assertReflectionEquals_doublesDifferent() {
    assertComparisonThrowsWithMessage(
        new DoubleObject(1d), new DoubleObject(2d), "testedField", "1.0", "2.0");
  }

  @Test
  public void assertReflectionEquals_doublesBothDefault() {
    assertReflectionEquals(new DoubleObject(0d), new DoubleObject(0d));
  }

  @Test
  public void assertReflectionEquals_doublesDefaultActualStrict() {
    assertComparisonThrowsWithMessage(
        new DoubleObject(1d), new DoubleObject(0d), "testedField", "1.0", "0.0");
  }

  @Test
  public void assertReflectionEquals_doublesDefaultActualLenient() {
    assertComparisonThrowsWithMessage(
        new DoubleObject(1d), new DoubleObject(0d), "testedField", "1.0", "0.0", IGNORE_DEFAULTS);
  }

  @Test
  public void assertReflectionEquals_doublesDefaultExpectedStrict() {
    assertComparisonThrowsWithMessage(
        new DoubleObject(0d), new DoubleObject(1d), "testedField", "0.0", "1.0");
  }

  @Test
  public void assertReflectionEquals_doublesDefaultExpectedLenient() {
    assertReflectionEquals(new DoubleObject(0d), new DoubleObject(1d), IGNORE_DEFAULTS);
  }

  //////// OBJECT FIELD TESTS //////////

  @Test
  public void assertReflectionEquals_objectsSame() {
    assertReflectionEquals(new SimpleObject(1), new SimpleObject(1));
  }

  @Test
  public void assertReflectionEquals_objectsDifferent() {
    assertComparisonThrowsWithMessage(
        new SimpleObject(1), new SimpleObject(2), "testedField", "1", "2");
  }

  @Test
  public void assertReflectionEquals_objectsBothDefault() {
    assertReflectionEquals(new SimpleObject(0), new SimpleObject(0));
  }

  @Test
  public void assertReflectionEquals_objectsDefaultActualStrict() {
    assertComparisonThrowsWithMessage(
        new SimpleObject(1), new SimpleObject(0), "testedField", "1", "0");
  }

  @Test
  public void assertReflectionEquals_objectsDefaultActualLenient() {
    assertComparisonThrowsWithMessage(
        new SimpleObject(1), new SimpleObject(0), "testedField", "1", "0", IGNORE_DEFAULTS);
  }

  @Test
  public void assertReflectionEquals_objectsDefaultExpectedStrict() {
    assertComparisonThrowsWithMessage(
        new SimpleObject(0), new SimpleObject(1), "testedField", "0", "1");
  }

  @Test
  public void assertReflectionEquals_objectsDefaultExpectedLenient() {
    assertReflectionEquals(new SimpleObject(0f), new SimpleObject(1f), IGNORE_DEFAULTS);
  }

  ////////// STRING FIELD TESTS //////////

  @Test
  public void assertReflectionEquals_stringsSame() {
    assertReflectionEquals(new StringObject("x"), new StringObject("x"));
  }

  @Test
  public void assertReflectionEquals_stringsDifferent() {
    assertComparisonThrowsWithMessage(
        new StringObject("x"), new StringObject("y"), "testedField", "\"x\"", "\"y\"");
  }

  @Test
  public void assertReflectionEquals_stringsBothDefault() {
    assertReflectionEquals(new StringObject(null), new StringObject(null));
  }

  @Test
  public void assertReflectionEquals_stringsDefaultActualStrict() {
    assertComparisonThrowsWithMessage(
        new StringObject("x"), new StringObject(null), "testedField", "\"x\"", "null");
  }

  @Test
  public void assertReflectionEquals_stringsDefaultActualLenient() {
    assertComparisonThrowsWithMessage(
        new StringObject("x"),
        new StringObject(null),
        "testedField",
        "\"x\"",
        "null",
        IGNORE_DEFAULTS);
  }

  @Test
  public void assertReflectionEquals_stringsDefaultExpectedStrict() {
    assertComparisonThrowsWithMessage(
        new StringObject(null), new StringObject("x"), "testedField", "null", "\"x\"");
  }

  @Test
  public void assertReflectionEquals_stringsDefaultExpectedLenient() {
    assertReflectionEquals(new StringObject(null), new StringObject("x"), IGNORE_DEFAULTS);
  }

  ////////// DATE FIELD TESTS //////////

  @Test
  public void assertReflectionEquals_datesDifferent() {
    // use date objects as its toString returns different values depending on time zone
    assertComparisonThrowsWithMessage(
        new DateObject(new Date(1)),
        new DateObject(new Date(2)),
        "testedField",
        new Date(1).toString(),
        new Date(2).toString());
  }

  @Test
  public void assertReflectionEquals_datesDifferentIgnored() {
    assertReflectionEquals(new DateObject(new Date(1)), new DateObject(new Date(2)), LENIENT_DATES);
  }

  @Test
  public void assertReflectionEquals_datesDefaultActualIgnored() {
    // use date object as its toString returns different values depending on time zone
    assertComparisonThrowsWithMessage(
        new DateObject(new Date(1)),
        new DateObject(null),
        "testedField",
        new Date(1).toString(),
        "null",
        LENIENT_DATES);
  }

  @Test
  public void assertReflectionEquals_datesDefaultActualIgnoredLenient() {
    // use date object as its toString returns different values depending on time zone
    assertComparisonThrowsWithMessage(
        new DateObject(new Date(1)),
        new DateObject(null),
        "testedField",
        new Date(1).toString(),
        "null",
        LENIENT_DATES,
        IGNORE_DEFAULTS);
  }

  @Test
  public void assertReflectionEquals_datesDefaultExpectedIgnored() {
    // use date object as its toString returns different values depending on time zone
    assertComparisonThrowsWithMessage(
        new DateObject(null),
        new DateObject(new Date(1)),
        "testedField",
        "null",
        new Date(1).toString(),
        LENIENT_DATES);
  }

  @Test
  public void assertReflectionEquals_datesDefaultExpectedIgnoredLenient() {
    assertReflectionEquals(
        new DateObject(null), new DateObject(new Date(1)), LENIENT_DATES, IGNORE_DEFAULTS);
  }
}
