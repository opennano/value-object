package com.github.opennano.reflectionassert.comparers;

import static com.github.opennano.reflectionassert.diffs.NullDiff.NULL_TOKEN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.opennano.reflectionassert.comparers.DefaultIgnoringComparer;

@ExtendWith(MockitoExtension.class)
public class DefaultIgnoringComparerTest {

  @InjectMocks private DefaultIgnoringComparer comparer;

  @Test
  public void canCompare_leftNull() {
    assertTrue(comparer.canCompare(null, ""));
  }

  @Test
  public void canCompare_rightNull() {
    assertFalse(comparer.canCompare("", null));
  }

  @Test
  public void canCompare_bothNull() {
    assertTrue(comparer.canCompare(null, null));
  }

  @Test
  public void canCompare_neitherNull() {
    assertFalse(comparer.canCompare("", ""));
  }

  @Test
  public void canCompare_leftFalse() {
    assertTrue(comparer.canCompare(false, true));
  }

  @Test
  public void canCompare_rightFalse() {
    assertFalse(comparer.canCompare(true, false));
  }

  @Test
  public void canCompare_bothFalse() {
    assertTrue(comparer.canCompare(false, false));
  }

  @Test
  public void canCompare_neitherFalse() {
    assertFalse(comparer.canCompare(true, true));
  }

  @Test
  public void canCompare_leftNullChar() {
    assertTrue(comparer.canCompare(Character.MIN_VALUE, 'a'));
  }

  @Test
  public void canCompare_rightNullChar() {
    assertFalse(comparer.canCompare('a', Character.MIN_VALUE));
  }

  @Test
  public void canCompare_bothNullChar() {
    assertTrue(comparer.canCompare(Character.MIN_VALUE, Character.MIN_VALUE));
  }

  @Test
  public void canCompare_neitherNullChar() {
    assertFalse(comparer.canCompare('a', 'a'));
  }

  @Test
  public void canCompare_leftZero() {
    assertTrue(comparer.canCompare(0, 1));
  }

  @Test
  public void canCompare_rightZero() {
    assertFalse(comparer.canCompare(1, 0));
  }

  @Test
  public void canCompare_bothZero() {
    assertTrue(comparer.canCompare(0, 0));
  }

  @Test
  public void canCompare_neitherZero() {
    assertFalse(comparer.canCompare(1, 1));
  }

  @Test
  public void canCompare_leftZeroDouble() {
    assertTrue(comparer.canCompare(0d, 1d));
  }

  @Test
  public void compare() {
    assertEquals(NULL_TOKEN, comparer.compare(null, null, null, null, false));
  }
}
