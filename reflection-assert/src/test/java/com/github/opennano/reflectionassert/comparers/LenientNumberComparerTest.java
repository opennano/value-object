package com.github.opennano.reflectionassert.comparers;

import static com.github.opennano.reflectionassert.diffs.NullDiff.NULL_TOKEN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.opennano.reflectionassert.comparers.LenientNumberComparer;
import com.github.opennano.reflectionassert.diffs.Diff;

@ExtendWith(MockitoExtension.class)
public class LenientNumberComparerTest {

  @InjectMocks private LenientNumberComparer comparer;

  @Test
  public void canCompare_yes() {
    assertTrue(comparer.canCompare(1, 2));
  }

  @Test
  public void canCompare_no() {
    assertFalse(comparer.canCompare(1, "2"));
  }

  @Test
  public void compare_producesDiff() {
    Diff actual = comparer.compare("mockPath", 1, 2, null, false);

    assertEquals("mockPath", actual.getPath());
    assertEquals(1, actual.getExpectedValue());
    assertEquals(2, actual.getActualValue());
  }

  @Test
  public void compare_noDiff() {
    assertSame(NULL_TOKEN, comparer.compare("mockPath", 1, 1, null, false));
  }

  @Test
  public void compare_NaNDiff() {
    Diff actual = comparer.compare("mockPath", 1, Double.NaN, null, false);

    assertEquals("mockPath", actual.getPath());
    assertEquals(1, actual.getExpectedValue());
    assertEquals(Double.NaN, actual.getActualValue());
  }

  @Test
  public void compare_NaNNoDiff() {
    assertSame(NULL_TOKEN, comparer.compare("mockPath", Double.NaN, Double.NaN, null, false));
  }

  @Test
  public void compare_InfDiff() {
    Diff actual = comparer.compare("mockPath", 1, Double.POSITIVE_INFINITY, null, false);

    assertEquals("mockPath", actual.getPath());
    assertEquals(1, actual.getExpectedValue());
    assertEquals(Double.POSITIVE_INFINITY, actual.getActualValue());
  }

  @Test
  public void compare_InfNoDiff() {
    assertSame(
        NULL_TOKEN,
        comparer.compare(
            "mockPath", Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, null, false));
  }
}
