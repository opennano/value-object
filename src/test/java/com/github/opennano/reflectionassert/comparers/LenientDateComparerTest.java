package com.github.opennano.reflectionassert.comparers;

import static com.github.opennano.reflectionassert.diffs.NullDiff.NULL_TOKEN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Calendar;
import java.util.Date;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.opennano.reflectionassert.comparers.LenientDateComparer;
import com.github.opennano.reflectionassert.diffs.Diff;
import com.github.opennano.reflectionassert.diffs.SimpleDiff;

@ExtendWith(MockitoExtension.class)
public class LenientDateComparerTest {

  @InjectMocks private LenientDateComparer comparer;

  @Test
  public void canCompare_expectedNull() {
    assertTrue(comparer.canCompare(null, new Date(0)));
  }

  @Test
  public void canCompare_expectedNullInvalidActual() {
    assertFalse(comparer.canCompare(null, ""));
  }

  @Test
  public void canCompare_bothNull() {
    assertFalse(comparer.canCompare(null, null));
  }

  @Test
  public void canCompare_actualNull() {
    assertTrue(comparer.canCompare(new Date(0), null));
  }

  @Test
  public void canCompare_actualNullInvalidExpected() {
    assertFalse(comparer.canCompare("", null));
  }

  @Test
  public void canCompare_bothDates() {
    assertTrue(comparer.canCompare(new Date(0), new Date(0)));
  }

  @Test
  public void canCompare_objectNotOk() {
    assertFalse(comparer.canCompare(new Object(), new Date(0)));
  }

  @Test
  public void canCompare_bothCalendars() {
    assertTrue(comparer.canCompare(Calendar.getInstance(), Calendar.getInstance()));
  }

  @Test
  public void canCompare_cantMixTypes() {
    assertFalse(comparer.canCompare(new Date(0), Calendar.getInstance()));
  }

  @Test
  public void compare_expectedNull() {
    Diff actual = comparer.compare("mockPath", null, 2, null, false);

    assertEquals(SimpleDiff.class, actual.getClass());
    assertEquals("mockPath", actual.getPath());
    assertEquals(null, actual.getExpectedValue());
    assertEquals(2, actual.getActualValue());
  }

  @Test
  public void compare_actualNull() {
    Diff actual = comparer.compare("mockPath", 1, null, null, false);

    assertEquals(SimpleDiff.class, actual.getClass());
    assertEquals("mockPath", actual.getPath());
    assertEquals(1, actual.getExpectedValue());
    assertEquals(null, actual.getActualValue());
  }

  @Test
  public void compare_neitherNull() {
    assertSame(NULL_TOKEN, comparer.compare("mockPath", 1, 1, null, false));
  }
}
