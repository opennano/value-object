package com.github.opennano.reflectionassert.comparers;

import static com.github.opennano.reflectionassert.diffs.NullDiff.NULL_TOKEN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.github.opennano.reflectionassert.LeniencyMode.*;
import com.github.opennano.reflectionassert.diffs.Diff;
import com.github.opennano.reflectionassert.diffs.SimpleDiff;
import com.github.opennano.reflectionassert.exceptions.ReflectionAssertionInternalException;

@ExtendWith(MockitoExtension.class)
public class SimpleComparerTest {

  @InjectMocks private SimpleComparer comparer;

  @Test
  public void canCompare_expectedNull() {
    assertTrue(comparer.canCompare(null, 1));
  }

  @Test
  public void canCompare_actualNull() {
    assertTrue(comparer.canCompare(1, null));
  }

  @Test
  public void canCompare_bothNull() {
    assertTrue(comparer.canCompare(null, null));
  }

  @Test
  public void canCompare_bothSame() {
    Object value = new Object();
    assertTrue(comparer.canCompare(value, value));
  }

  @Test
  public void canCompare_bothJavaLang() {
    assertTrue(comparer.canCompare(new RuntimeException(), new Exception()));
  }

  @Test
  public void canCompare_expectedJavaLang() {
    assertTrue(comparer.canCompare(new RuntimeException(), ""));
  }

  @Test
  public void canCompare_actualJavaLang() {
    assertTrue(comparer.canCompare("", new RuntimeException()));
  }

  @Test
  public void canCompare_bothDates() {
    assertTrue(comparer.canCompare(new Date(0), new Date(1)));
  }

  @Test
  public void canCompare_expectedDate() {
    assertFalse(comparer.canCompare(new Date(0), new StringWriter()));
  }

  @Test
  public void canCompare_actualDate() {
    assertFalse(comparer.canCompare(new StringWriter(), new Date(0)));
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
  public void compare_bothNull() {
    Diff actual = comparer.compare("mockPath", null, null, null, false);

    assertEquals(NULL_TOKEN, actual);
  }

  @Test
  public void compare_sameDates() {
    Diff actual = comparer.compare("mockPath", new Date(1), new Date(1), null, false);

    assertEquals(NULL_TOKEN, actual);
  }

  @Test
  public void compare_differentEnums() {
    Diff actual = comparer.compare("mockPath", IGNORE_DEFAULTS, LENIENT_ORDER, null, false);

    assertNotEquals(NULL_TOKEN, actual);
  }

  @Test
  public void compare_differentNumbers() {
    Diff actual = comparer.compare("mockPath", 1, 2, null, false);

    assertNotEquals(NULL_TOKEN, actual);
  }

  @Test
  public void compare_differentStrings() {
    Diff actual = comparer.compare("mockPath", "x", "y", null, false);

    assertNotEquals(NULL_TOKEN, actual);
  }

  @Test
  public void compare_differentFiles() {
    Diff actual =
        comparer.compare("mockPath", new File("mock/path"), new File("other/path"), null, false);

    assertNotEquals(NULL_TOKEN, actual);
  }

  @Test
  public void compare_differentDates() {
    Diff actual = comparer.compare("mockPath", new Date(0), new Date(1), null, false);

    assertNotEquals(NULL_TOKEN, actual);
  }

  @Test
  public void compare_differentCalendars() {
    Calendar expected = Calendar.getInstance();
    expected.setTime(new Date(0));
    Calendar actual = Calendar.getInstance();
    actual.setTime(new Date(1));
    Diff actualDiff = comparer.compare("mockPath", expected, actual, null, false);

    assertNotEquals(NULL_TOKEN, actualDiff);
  }

  @Test
  public void compare_javaLangTypes() {
    Diff actual = comparer.compare("mockPath", new Exception("x"), new Exception("y"), null, false);

    assertNotEquals(NULL_TOKEN, actual);
  }

  @Test
  public void compare_unexpectedType() {
    assertThrows(
        ReflectionAssertionInternalException.class,
        () -> comparer.compare("mockPath", Arrays.asList(0), Arrays.asList(1), null, false));
  }

  @Test
  public void compare_different() {
    Diff actual = comparer.compare("mockPath", 1, 2, null, false);

    assertEquals(SimpleDiff.class, actual.getClass());
    assertEquals("mockPath", actual.getPath());
    assertEquals(1, actual.getExpectedValue());
    assertEquals(2, actual.getActualValue());
  }

  @Test
  public void compare_subTypeExpectedOk() {
    Object expected = new Object();
    Diff actual = comparer.compare("mockPath", expected, 1, null, false);

    assertEquals(SimpleDiff.class, actual.getClass());
    assertEquals("mockPath", actual.getPath());
    assertEquals(expected, actual.getExpectedValue());
    assertEquals(1, actual.getActualValue());
  }

  @Test
  public void compare_subTypeActualNotOk() {
    Diff actual = comparer.compare("mockPath", 1, new Object(), null, false);

    assertEquals(SimpleDiff.class, actual.getClass());
    assertEquals("mockPath", actual.getPath());
    assertEquals(Integer.class, actual.getExpectedValue());
    assertEquals(Object.class, actual.getActualValue());
  }
}
