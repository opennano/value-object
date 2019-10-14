package com.github.opennano.reflectionassert.e2etests;

import static com.github.opennano.reflectionassert.LeniencyMode.IGNORE_DEFAULTS;
import static com.github.opennano.reflectionassert.ReflectionAssertions.assertReflectionEquals;

import java.util.Collections;

import org.junit.jupiter.api.Test;

import com.github.opennano.reflectionassert.LeniencyMode;

public class MapIT extends BaseIntegrationTest {

  @Test
  public void assertReflectionEquals_mapValuesSame() {
    assertReflectionEquals(mapOf(1, 1), mapOf(1, 1));
  }

  @Test
  public void assertReflectionEquals_mapValuesDifferent() {
    assertComparisonThrowsWithMessage(mapOf(1, 1), mapOf(1, 2), "${1}", "1", "2");
  }

  @Test
  public void assertReflectionEquals_canCompareAcrossMapImplementationsSame() {
    assertReflectionEquals(mapOf(1, 1), Collections.synchronizedMap(mapOf(1, 1)));
  }

  @Test
  public void assertReflectionEquals_canCompareAcrossMapImplementationsDifferent() {
    assertComparisonThrowsWithMessage(
        mapOf(1, 1), Collections.synchronizedMap(mapOf(1, 2)), "${1}", "1", "2");
  }

  @Test
  public void assertReflectionEquals_multipleEntriesSame() {
    assertReflectionEquals(mapOf(1, 1, 2, 2), mapOf(1, 1, 2, 2));
  }

  @Test
  public void assertReflectionEquals_multiValuemapValuesDifferent() {
    assertComparisonThrowsWithMessage(mapOf(1, 1, 2, 2), mapOf(1, 1, 2, 3), "${2}", "2", "3");
  }

  @Test
  public void assertReflectionEquals_multiValueMapExtraValue() {
    assertComparisonThrowsWithMessage(mapOf(1, 1, 2, 2), mapOf(1, 1), "${2}", "2", null);
  }

  @Test
  public void assertReflectionEquals_multiValueMapMissingValue() {
    assertComparisonThrowsWithMessage(mapOf(1, 1), mapOf(1, 1, 2, 2), "${2}", null, "2");
  }

  @Test
  public void assertReflectionEquals_mapsEmpty() {
    assertReflectionEquals(mapOf(), mapOf());
  }

  @Test
  public void assertReflectionEquals_mapsNullExpectedStrict() {
    assertComparisonThrowsWithMessage(null, mapOf(), "$", "null", "{}");
  }

  @Test
  public void assertReflectionEquals_mapsNullExpectedLenient() {
    assertReflectionEquals(null, mapOf(), new LeniencyMode[] {IGNORE_DEFAULTS});
  }

  @Test
  public void assertReflectionEquals_mapsNullActualStrict() {
    assertComparisonThrowsWithMessage(mapOf(), null, "$", "{}", "null");
  }

  @Test
  public void assertReflectionEquals_mapsNullActualLenient() {
    assertComparisonThrowsWithMessage(mapOf(), null, "$", "{}", "null", IGNORE_DEFAULTS);
  }

  @Test
  public void assertReflectionEquals_mapWithComplexKey() {
    assertReflectionEquals(mapOf(mapOf(1, 2), 2), mapOf(mapOf(1, 2), 2));
  }

  @Test
  public void assertReflectionEquals_mapWithComplexKeyValueDiff() {
    assertComparisonThrowsWithMessage(
        mapOf(mapOf(1, 2), 2), mapOf(mapOf(1, 2), 3), "${{1=2}}", "2", "3");
  }

  @Test
  public void assertReflectionEquals_mapWithComplexKeyMissing() {
    assertComparisonThrowsWithMessage(mapOf(mapOf(1, 2), 2), mapOf(), "${{1=2}}", "2", null);
  }

  @Test
  public void assertReflectionEquals_mapWithComplexKeyUnexpected() {
    assertComparisonThrowsWithMessage(mapOf(), mapOf(mapOf(1, 2), 2), "${{1=2}}", null, "2");
  }
}
