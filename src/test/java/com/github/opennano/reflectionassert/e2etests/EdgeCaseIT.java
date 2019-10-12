package com.github.opennano.reflectionassert.e2etests;

import static com.github.opennano.reflectionassert.LeniencyMode.IGNORE_DEFAULTS;
import static com.github.opennano.reflectionassert.ReflectionAssertions.assertReflectionEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.github.opennano.reflectionassert.e2etests.samplepojos.BidirectionalOne;
import com.github.opennano.reflectionassert.e2etests.samplepojos.BidirectionalTwo;
import com.github.opennano.reflectionassert.e2etests.samplepojos.DeepObject;
import com.github.opennano.reflectionassert.exceptions.ReflectionAssertionException;

public class EdgeCaseIT extends BaseIntegrationTest {

  // still needed:
  // coverage unit tests

  ////////// CIRCULAR REFERENCE //////////

  @Test
  public void assertReflectionEquals_circularReference() {
    assertReflectionEquals(bidirectionalReference(), bidirectionalReference());
  }

  ////////// NESTED COLLECTIONS //////////

  @Test
  public void assertReflectionEquals_listOfListsSame() {
    assertReflectionEquals(listOf(listOf(1, 2)), listOf(listOf(1, 2)));
  }

  @Test
  public void assertReflectionEquals_listOfListsDifferent() {
    assertComparisonThrowsWithMessage(
        listOf(listOf(1, 1)), listOf(listOf(1, 2)), "[0][1]", "1", "2");
  }

  @Test
  public void assertReflectionEquals_listOfArraysSame() {
    assertReflectionEquals(listOf(new int[] {1, 2}), listOf(new int[] {1, 2}));
  }

  @Test
  public void assertReflectionEquals_assertReflectionEquals_listOfArraysDifferent() {
    assertComparisonThrowsWithMessage(
        listOf(new int[] {1, 1}), listOf(new int[] {1, 2}), "[0][1]", "1", "2");
  }

  @Test
  public void assertReflectionEquals_mapOfmapsSame() {
    assertReflectionEquals(mapOf(1, mapOf(2, 3)), mapOf(1, mapOf(2, 3)));
  }

  @Test
  public void assertReflectionEquals_mapOfmapsDifferent() {
    assertComparisonThrowsWithMessage(
        mapOf(1, mapOf(2, 3)), mapOf(1, mapOf(2, 4)), "{1}{2}", "3", "4");
  }

  @Test
  public void assertReflectionEquals_mapOflistsSame() {
    assertReflectionEquals(mapOf(1, listOf(2, 3)), mapOf(1, listOf(2, 3)));
  }

  @Test
  public void assertReflectionEquals_mapOflistsDifferent() {
    assertComparisonThrowsWithMessage(
        mapOf(1, listOf(2, 3)), mapOf(1, listOf(2, 4)), "{1}[1]", "3", "4");
  }

  @Test
  public void assertReflectionEquals_listOfMapsSame() {
    assertReflectionEquals(listOf(2, mapOf(1, 3)), listOf(2, mapOf(1, 3)));
  }

  @Test
  public void assertReflectionEquals_listOfMapsDifferent() {
    assertComparisonThrowsWithMessage(
        listOf(2, mapOf(1, 3)), listOf(2, mapOf(1, 4)), "[1]{1}", "3", "4");
  }

  ////////// INCOMPARABLE TYPES //////////

  @Test
  public void assertReflectionEquals_valueTypesMismatch() {
    assertComparisonThrowsWithMessage(
        "x", 1, "", "object of type 'java.lang.String'", "object of type 'java.lang.Integer'");
  }

  @Test
  public void assertReflectionEquals_objectTypesMismatch() {
    assertComparisonThrowsWithMessage(
        mapOf("x", 1),
        mapOf("x", "y"),
        "{x}",
        "object of type 'java.lang.Integer'",
        "object of type 'java.lang.String'");
  }

  ////////// DEEP OBJECT PATH //////////

  @Test
  public void assertReflectionEquals_deepObjectSame() {
    assertReflectionEquals(new DeepObject(123), new DeepObject(123));
  }

  @Test
  public void assertReflectionEquals_deepObjectDifferent() {
    assertComparisonThrowsWithMessage(
        new DeepObject(123),
        new DeepObject(321),
        "root.wrappedField.listField[0].wrappedField.mapField{xyz}.wrappedField",
        "123",
        "321");
  }

  @Test
  public void assertReflectionEquals_deepObjectNullExpectedStrict() {
    assertComparisonThrowsWithMessage(
        new DeepObject(null),
        new DeepObject(321),
        "root.wrappedField.listField[0].wrappedField.mapField{xyz}.wrappedField",
        "null",
        "321");
  }

  @Test
  public void assertReflectionEquals_deepObjectNullExpectedLenient() {
    assertReflectionEquals(new DeepObject(null), new DeepObject(321), IGNORE_DEFAULTS);
  }

  @Test
  public void assertReflectionEquals_deepObjectNullActualStrict() {
    assertComparisonThrowsWithMessage(
        new DeepObject(123),
        new DeepObject(null),
        "root.wrappedField.listField[0].wrappedField.mapField{xyz}.wrappedField",
        "123",
        "null");
  }

  @Test
  public void assertReflectionEquals_deepObjectNullActualLenient() {
    assertComparisonThrowsWithMessage(
        new DeepObject(123),
        new DeepObject(null),
        "root.wrappedField.listField[0].wrappedField.mapField{xyz}.wrappedField",
        "123",
        "null",
        IGNORE_DEFAULTS);
  }

  ////////// LENIENT NUMBER COMPARISONS //////////

  @Test
  public void assertReflectionEquals_canCompareAnyNumericTypes() {
    List<Number> numbers = Arrays.asList((byte) 1, (short) 1, 1, 1l, 1f, 1d);
    for (Number left : numbers) {
      for (Number right : numbers) {
        assertReflectionEquals(left, right);
      }
    }
  }

  @Test // chars could be treated as numbers but shouldn't be
  public void assertReflectionEquals_cantCompareCharToNumber() {
    assertComparisonThrowsWithMessage(
        (char) 26,
        26,
        "",
        "object of type 'java.lang.Character'",
        "object of type 'java.lang.Integer'");
  }

  ////////// CUSTOM MESSAGE //////////

  @Test
  public void assertReflectionEquals_customErrorMessage() {
    Class<ReflectionAssertionException> type = ReflectionAssertionException.class;
    Throwable thrown = assertThrows(type, () -> assertReflectionEquals("mock message", 1, 2));
    
    String message = thrown.getMessage();
    assertTrue(message.startsWith("mock message"));
  }

  ////////// UTILITY METHODS //////////

  private Object bidirectionalReference() {
    BidirectionalOne one = new BidirectionalOne();
    BidirectionalTwo two = new BidirectionalTwo();
    one.setTwo(two);
    two.setOne(one);
    return one;
  }
}
