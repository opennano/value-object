package com.github.opennano.reflectionassert.e2etests;

import static com.github.opennano.reflectionassert.LeniencyMode.IGNORE_DEFAULTS;
import static com.github.opennano.reflectionassert.LeniencyMode.LENIENT_ORDER;
import static com.github.opennano.reflectionassert.ReflectionAssertions.assertReflectionEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import com.github.opennano.reflectionassert.LeniencyMode;
import com.github.opennano.reflectionassert.e2etests.samplepojos.DeepObject;
import com.github.opennano.reflectionassert.exceptions.ReflectionAssertionException;

/** ensure that comparisons yielding multiple diffs are reported correctly */
public class MultipleDiffIT extends BaseIntegrationTest {

  @Test
  public void assertReflectionEquals_missingAndUnexpected() {
    String expectedMessage =
        cat(
            "The following differences were found:",
            "",
            "Path:       $[*]",
            "Missing:    2",
            "",
            "Path:       $[*]",
            "Unexpected: 3");

    assertComparisonThrowsWithExactMessage(
        Arrays.asList(1, 2), Arrays.asList(1, 3), expectedMessage, LENIENT_ORDER, IGNORE_DEFAULTS);
  }

  @Test
  public void assertReflectionEquals_mismatchMissingAndUnexpected() {
    String expectedMessage =
        cat(
            "The following differences were found:",
            "",
            "Path:       ${0}",
            "Expected:   1",
            "Actual:     2",
            "",
            "Path:       ${1}",
            "Missing:    2",
            "",
            "Path:       ${2}",
            "Unexpected: 3");

    assertComparisonThrowsWithExactMessage(mapOf(0, 1, 1, 2), mapOf(0, 2, 2, 3), expectedMessage);
  }

  @Test
  public void assertReflectionEquals_fiveMismatches() {
    String expectedMessage =
        cat(
            "The following differences were found:",
            "",
            "Path:       $[0]",
            "Expected:   1",
            "Actual:     0",
            "",
            "Path:       $[1]",
            "Expected:   2",
            "Actual:     1",
            "",
            "Path:       $[2]",
            "Expected:   3",
            "Actual:     2",
            "",
            "Path:       $[3]",
            "Expected:   4",
            "Actual:     3",
            "",
            "Path:       $[4]",
            "Expected:   5",
            "Actual:     4");

    assertComparisonThrowsWithExactMessage(ints(1, 5), ints(0, 4), expectedMessage);
  }

  @Test
  public void assertReflectionEquals_sixMismatchesStartsToSummarize() {
    String expectedMessage =
        cat(
            "The following differences were found:",
            "",
            "Path:       $[0]",
            "Expected:   1",
            "Actual:     0",
            "",
            "Path:       $[1]",
            "Expected:   2",
            "Actual:     1",
            "",
            "Path:       $[2]",
            "Expected:   3",
            "Actual:     2",
            "",
            "Path:       $[3]",
            "Expected:   4",
            "Actual:     3",
            "",
            "Path:       $[4]",
            "Expected:   5",
            "Actual:     4",
            "",
            "Additional differences:",
            "",
            "Path:       $[5]");

    assertComparisonThrowsWithExactMessage(ints(1, 6), ints(0, 5), expectedMessage);
  }

  @Test
  public void assertReflectionEquals_nineteenMismatchesSummarizes() {
    String expectedMessage =
        cat(
            "The following differences were found:",
            "",
            "Path:       $[0]",
            "Expected:   1",
            "Actual:     0",
            "",
            "Path:       $[1]",
            "Expected:   2",
            "Actual:     1",
            "",
            "Path:       $[2]",
            "Expected:   3",
            "Actual:     2",
            "",
            "Path:       $[3]",
            "Expected:   4",
            "Actual:     3",
            "",
            "Path:       $[4]",
            "Expected:   5",
            "Actual:     4",
            "",
            "Additional differences:",
            "",
            "Path:       $[5]",
            "Path:       $[6]",
            "Path:       $[7]",
            "Path:       $[8]",
            "Path:       $[9]",
            "Path:       $[10]",
            "Path:       $[11]",
            "Path:       $[12]",
            "Path:       $[13]",
            "Path:       $[14]",
            "Path:       $[15]",
            "Path:       $[16]",
            "Path:       $[17]",
            "Path:       $[18]");

    assertComparisonThrowsWithExactMessage(ints(1, 19), ints(0, 18), expectedMessage);
  }

  @Test
  public void assertReflectionEquals_twentyMismatchesSummarizes() {
    String expectedMessage =
        cat(
            "The following differences were found:",
            "",
            "Path:       $[0]",
            "Expected:   1",
            "Actual:     0",
            "",
            "Path:       $[1]",
            "Expected:   2",
            "Actual:     1",
            "",
            "Path:       $[2]",
            "Expected:   3",
            "Actual:     2",
            "",
            "Path:       $[3]",
            "Expected:   4",
            "Actual:     3",
            "",
            "Path:       $[4]",
            "Expected:   5",
            "Actual:     4",
            "",
            "Additional differences:",
            "",
            "Path:       $[5]",
            "Path:       $[6]",
            "Path:       $[7]",
            "Path:       $[8]",
            "Path:       $[9]",
            "Path:       $[10]",
            "Path:       $[11]",
            "Path:       $[12]",
            "Path:       $[13]",
            "Path:       $[14]",
            "Path:       $[15]",
            "Path:       $[16]",
            "Path:       $[17]",
            "Path:       $[18]",
            "Path:       $[19]");

    assertComparisonThrowsWithExactMessage(ints(1, 20), ints(0, 19), expectedMessage);
  }

  @Test
  public void assertReflectionEquals_twentyOneMismatchesStartsSuppressing() {
    String expectedMessage =
        cat(
            "The following differences were found:",
            "",
            "Path:       $[0]",
            "Expected:   1",
            "Actual:     0",
            "",
            "Path:       $[1]",
            "Expected:   2",
            "Actual:     1",
            "",
            "Path:       $[2]",
            "Expected:   3",
            "Actual:     2",
            "",
            "Path:       $[3]",
            "Expected:   4",
            "Actual:     3",
            "",
            "Path:       $[4]",
            "Expected:   5",
            "Actual:     4",
            "",
            "Additional differences:",
            "",
            "Path:       $[5]",
            "Path:       $[6]",
            "Path:       $[7]",
            "Path:       $[8]",
            "Path:       $[9]",
            "Path:       $[10]",
            "Path:       $[11]",
            "Path:       $[12]",
            "Path:       $[13]",
            "Path:       $[14]",
            "Path:       $[15]",
            "Path:       $[16]",
            "Path:       $[17]",
            "Path:       $[18]",
            "(2 more not shown)");

    assertComparisonThrowsWithExactMessage(ints(1, 21), ints(0, 20), expectedMessage);
  }

  ////////// CACHING //////////

  @Test
  public void assertReflectionEquals_cachedDiff() {
    String expectedMessage =
        cat(
            "The following differences were found:",
            "",
            "Path:       $[0]",
            "Expected:   1",
            "Actual:     2",
            "",
            "Path:       $[1]",
            "Expected:   1",
            "Actual:     2");

    assertComparisonThrowsWithExactMessage(
        Arrays.asList(1, 1), Arrays.asList(2, 2), expectedMessage);
  }

  @Test
  public void assertReflectionEquals_deepSubtreeCachedDiff() {
    String expectedMessage =
        cat(
            "The following differences were found:",
            "",
            "Path:       ${x}",
            "Expected:   1",
            "Actual:     2",
            "",
            "Path:       ${y}.root.wrappedField.listField[0].wrappedField.mapField{xyz}.wrappedField",
            "Expected:   1",
            "Actual:     2");

    // the 1 <--> 2 diff will be cached from value comparison of key 'x'
    // make sure the path gets updated correctly
    assertComparisonThrowsWithExactMessage(
        mapOf("x", 1, "y", new DeepObject(1)),
        mapOf("x", 2, "y", new DeepObject(2)),
        expectedMessage);
  }

  @Test
  public void assertReflectionEquals_cachedListDiff() {
    String expectedMessage =
        cat(
            "The following differences were found:",
            "",
            "Path:       ${x}[0]",
            "Expected:   1",
            "Actual:     2",
            "",
            "Path:       ${y}[0]",
            "Expected:   1",
            "Actual:     2");

    // the 1 <--> 2 comparison will be cached from value comparison of key 'x'
    // make sure the path gets updated correctly
    assertComparisonThrowsWithExactMessage(
        mapOf("x", listOf(1), "y", listOf(1)),
        mapOf("x", listOf(2), "y", listOf(2)),
        expectedMessage);
  }

  @Test
  public void assertReflectionEquals_aWouldBeParentIsAPartialDiff() {
    // the parent node representing the comparison of listOf(1) <--> listOf(2)
    // would appear to be reusable, but since it is inside another list with order ignored
    // it should be reported as a partial diff, in which case the next time the comparison
    // happens the cached value can't be used

    String expectedMessage =
        cat(
            "The following differences were found:",
            "",
            "Path:       ${1}[*]",
            "Missing:    [1]",
            "",
            "Path:       ${1}[*]",
            "Unexpected: [2]",
            "",
            "Path:       ${2}[*]",
            "Missing:    1",
            "",
            "Path:       ${2}[*]",
            "Unexpected: 2");

    assertComparisonThrowsWithExactMessage(
        mapOf(1, listOf(listOf(1)), 2, listOf(1)),
        mapOf(1, listOf(listOf(2)), 2, listOf(2)),
        expectedMessage,
        LENIENT_ORDER);
  }

  protected void assertComparisonThrowsWithExactMessage(
      Object expected, Object actual, String expectedMessage, LeniencyMode... modes) {

    Class<ReflectionAssertionException> type = ReflectionAssertionException.class;
    Throwable thrown = assertThrows(type, () -> assertReflectionEquals(expected, actual, modes));
    assertEquals(expectedMessage.trim(), thrown.getMessage().trim());
  }

  private String cat(String... lines) {
    return String.join(System.lineSeparator(), lines);
  }

  private int[] ints(int startInclusive, int endInclusive) {
    return IntStream.rangeClosed(startInclusive, endInclusive).toArray();
  }
}
