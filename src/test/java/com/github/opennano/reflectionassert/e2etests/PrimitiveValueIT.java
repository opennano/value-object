package com.github.opennano.reflectionassert.e2etests;

import static com.github.opennano.reflectionassert.LeniencyMode.IGNORE_DEFAULTS;
import static com.github.opennano.reflectionassert.ReflectionAssertions.assertReflectionEquals;

import org.junit.jupiter.api.Test;

/** exhaustively test all primitive value comparisons */
public class PrimitiveValueIT extends BaseIntegrationTest {

  // boolean value tests

  @Test
  public void assertReflectionEquals_booleanValuesSame() {
    assertReflectionEquals(true, true);
  }

  @Test
  public void assertReflectionEquals_booleanValuesBothDefault() {
    assertReflectionEquals(false, false);
  }

  @Test
  public void assertReflectionEquals_booleanValuesDefaultActualStrict() {
    assertComparisonThrowsWithMessage(true, false, "$", "true", "false");
  }

  @Test
  public void assertReflectionEquals_booleanValuesDefaultActualLenient() {
    assertComparisonThrowsWithMessage(true, false, "$", "true", "false", IGNORE_DEFAULTS);
  }

  @Test
  public void assertReflectionEquals_booleanValuesDefaultExpectedStrict() {
    assertComparisonThrowsWithMessage(false, true, "$", "false", "true");
  }

  @Test
  public void assertReflectionEquals_booleanValuesDefaultExpectedLenient() {
    assertReflectionEquals(false, true, IGNORE_DEFAULTS);
  }

  // char value tests

  @Test
  public void assertReflectionEquals_charValuesSame() {
    assertReflectionEquals('a', 'a');
  }

  @Test
  public void assertReflectionEquals_charValuesDifferent() {
    assertComparisonThrowsWithMessage('a', 'b', "$", "'a'", "'b'");
  }

  @Test
  public void assertReflectionEquals_charValuesBothDefault() {
    assertReflectionEquals(Character.MIN_VALUE, Character.MIN_VALUE);
  }

  @Test
  public void assertReflectionEquals_charValuesDefaultActualStrict() {
    assertComparisonThrowsWithMessage('a', Character.MIN_VALUE, "$", "'a'", "'\u0000'");
  }

  @Test
  public void assertReflectionEquals_charValuesDefaultActualLenient() {
    assertComparisonThrowsWithMessage(
        'a', Character.MIN_VALUE, "$", "'a'", "'\u0000'", IGNORE_DEFAULTS);
  }

  @Test
  public void assertReflectionEquals_charValuesDefaultExpectedStrict() {
    assertComparisonThrowsWithMessage(Character.MIN_VALUE, 'a', "$", "'\u0000'", "'a'");
  }

  @Test
  public void assertReflectionEquals_charValuesDefaultExpectedLenient() {
    assertReflectionEquals(Character.MIN_VALUE, 'a', IGNORE_DEFAULTS);
  }

  // byte value tests

  @Test
  public void assertReflectionEquals_byteValuesSame() {
    assertReflectionEquals((byte) 1, (byte) 1);
  }

  @Test
  public void assertReflectionEquals_byteValuesDifferent() {
    assertComparisonThrowsWithMessage((byte) 1, (byte) 2, "$", "1", "2");
  }

  @Test
  public void assertReflectionEquals_byteValuesBothDefault() {
    assertReflectionEquals((byte) 0, (byte) 0);
  }

  @Test
  public void assertReflectionEquals_byteValuesDefaultActualStrict() {
    assertComparisonThrowsWithMessage((byte) 1, (byte) 0, "$", "1", "0");
  }

  @Test
  public void assertReflectionEquals_byteValuesDefaultActualLenient() {
    assertComparisonThrowsWithMessage((byte) 1, (byte) 0, "$", "1", "0", IGNORE_DEFAULTS);
  }

  @Test
  public void assertReflectionEquals_byteValuesDefaultExpectedStrict() {
    assertComparisonThrowsWithMessage((byte) 0, (byte) 1, "$", "0", "1");
  }

  @Test
  public void assertReflectionEquals_byteValuesDefaultExpectedLenient() {
    assertReflectionEquals((byte) 0, (byte) 1, IGNORE_DEFAULTS);
  }

  // short value tests

  @Test
  public void assertReflectionEquals_shortValuesSame() {
    assertReflectionEquals((short) 1, (short) 1);
  }

  @Test
  public void assertReflectionEquals_shortValuesDifferent() {
    assertComparisonThrowsWithMessage((short) 1, (short) 2, "$", "1", "2");
  }

  @Test
  public void assertReflectionEquals_shortValuesBothDefault() {
    assertReflectionEquals((short) 0, (short) 0);
  }

  @Test
  public void assertReflectionEquals_shortValuesDefaultActualStrict() {
    assertComparisonThrowsWithMessage((short) 1, (short) 0, "$", "1", "0");
  }

  @Test
  public void assertReflectionEquals_shortValuesDefaultActualLenient() {
    assertComparisonThrowsWithMessage((short) 1, (short) 0, "$", "1", "0", IGNORE_DEFAULTS);
  }

  @Test
  public void assertReflectionEquals_shortValuesDefaultExpectedStrict() {
    assertComparisonThrowsWithMessage((short) 0, (short) 1, "$", "0", "1");
  }

  @Test
  public void assertReflectionEquals_shortValuesDefaultExpectedLenient() {
    assertReflectionEquals((short) 0, (short) 1, IGNORE_DEFAULTS);
  }

  // int value tests

  @Test
  public void assertReflectionEquals_intValuesSame() {
    assertReflectionEquals(1, 1);
  }

  @Test
  public void assertReflectionEquals_intValuesDifferent() {
    assertComparisonThrowsWithMessage(1, 2, "$", "1", "2");
  }

  @Test
  public void assertReflectionEquals_intValuesBothDefault() {
    assertReflectionEquals(0, 0);
  }

  @Test
  public void assertReflectionEquals_intValuesDefaultActualStrict() {
    assertComparisonThrowsWithMessage(1, 0, "$", "1", "0");
  }

  @Test
  public void assertReflectionEquals_intValuesDefaultActualLenient() {
    assertComparisonThrowsWithMessage(1, 0, "$", "1", "0", IGNORE_DEFAULTS);
  }

  @Test
  public void assertReflectionEquals_intValuesDefaultExpectedStrict() {
    assertComparisonThrowsWithMessage(0, 1, "$", "0", "1");
  }

  @Test
  public void assertReflectionEquals_intValuesDefaultExpectedLenient() {
    assertReflectionEquals(0, 1, IGNORE_DEFAULTS);
  }

  // long value tests

  @Test
  public void assertReflectionEquals_longValuesSame() {
    assertReflectionEquals(1l, 1l);
  }

  @Test
  public void assertReflectionEquals_longValuesDifferent() {
    assertComparisonThrowsWithMessage(1, 2, "$", "1", "2");
  }

  @Test
  public void assertReflectionEquals_longValuesBothDefault() {
    assertReflectionEquals(0l, 0l);
  }

  @Test
  public void assertReflectionEquals_longValuesDefaultActualStrict() {
    assertComparisonThrowsWithMessage(1l, 0l, "$", "1", "0");
  }

  @Test
  public void assertReflectionEquals_longValuesDefaultActualLenient() {
    assertComparisonThrowsWithMessage(1l, 0l, "$", "1", "0", IGNORE_DEFAULTS);
  }

  @Test
  public void assertReflectionEquals_longValuesDefaultExpectedStrict() {
    assertComparisonThrowsWithMessage(0l, 1l, "$", "0", "1");
  }

  @Test
  public void assertReflectionEquals_longValuesDefaultExpectedLenient() {
    assertReflectionEquals(0l, 1l, IGNORE_DEFAULTS);
  }

  // float value tests

  @Test
  public void assertReflectionEquals_floatValuesSame() {
    assertReflectionEquals(1f, 1f);
  }

  @Test
  public void assertReflectionEquals_floatValuesDifferent() {
    assertComparisonThrowsWithMessage(1f, 2f, "$", "1.0", "2.0");
  }

  @Test
  public void assertReflectionEquals_floatValuesBothDefault() {
    assertReflectionEquals(0f, 0f);
  }

  @Test
  public void assertReflectionEquals_floatValuesDefaultActualStrict() {
    assertComparisonThrowsWithMessage(1f, 0f, "$", "1.0", "0.0");
  }

  @Test
  public void assertReflectionEquals_floatValuesDefaultActualLenient() {
    assertComparisonThrowsWithMessage(1f, 0f, "$", "1.0", "0.0", IGNORE_DEFAULTS);
  }

  @Test
  public void assertReflectionEquals_floatValuesDefaultExpectedStrict() {
    assertComparisonThrowsWithMessage(0f, 1f, "$", "0.0", "1.0");
  }

  @Test
  public void assertReflectionEquals_floatValuesDefaultExpectedLenient() {
    assertReflectionEquals(0f, 1f, IGNORE_DEFAULTS);
  }

  // double value tests

  @Test
  public void assertReflectionEquals_doubleValuesSame() {
    assertReflectionEquals(1d, 1d);
  }

  @Test
  public void assertReflectionEquals_doubleValuesDifferent() {
    assertComparisonThrowsWithMessage(1d, 2d, "$", "1.0", "2.0");
  }

  @Test
  public void assertReflectionEquals_doubleValuesBothDefault() {
    assertReflectionEquals(0d, 0d);
  }

  @Test
  public void assertReflectionEquals_doubleValuesDefaultActualStrict() {
    assertComparisonThrowsWithMessage(1d, 0d, "$", "1.0", "0.0");
  }

  @Test
  public void assertReflectionEquals_doubleValuesDefaultActualLenient() {
    assertComparisonThrowsWithMessage(1d, 0d, "$", "1.0", "0.0", IGNORE_DEFAULTS);
  }

  @Test
  public void assertReflectionEquals_doubleValuesDefaultExpectedStrict() {
    assertComparisonThrowsWithMessage(0d, 1d, "$", "0.0", "1.0");
  }

  @Test
  public void assertReflectionEquals_doubleValuesDefaultExpectedLenient() {
    assertReflectionEquals(0d, 1d, IGNORE_DEFAULTS);
  }
}
