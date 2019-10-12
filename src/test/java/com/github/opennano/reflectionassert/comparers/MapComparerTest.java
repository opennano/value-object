package com.github.opennano.reflectionassert.comparers;

import static com.github.opennano.reflectionassert.diffs.NullDiff.NULL_TOKEN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.github.opennano.reflectionassert.comparers.MapComparer;
import com.github.opennano.reflectionassert.diffs.Diff;
import com.github.opennano.reflectionassert.diffs.MissingValueDiff;
import com.github.opennano.reflectionassert.diffs.ParentDiff;
import com.github.opennano.reflectionassert.diffs.UnexpectedValueDiff;
import com.github.opennano.reflectionassert.worker.ComparerManager;

@ExtendWith(MockitoExtension.class)
public class MapComparerTest {

  @InjectMocks private MapComparer comparer;
  @Mock private ComparerManager mockComparerManager;
  @Mock private Diff mockDiff1;
  @Mock private Diff mockDiff2;
  @Mock private Object mockValue1;
  @Mock private Object mockValue2;

  @Test
  public void canCompare_leftNull() {
    assertFalse(comparer.canCompare(null, new HashMap<>()));
  }

  @Test
  public void canCompare_rightNull() {
    assertFalse(comparer.canCompare(new HashMap<>(), null));
  }

  @Test
  public void canCompare_bothNull() {
    assertFalse(comparer.canCompare(null, null));
  }

  @Test
  public void canCompare_wrongType() {
    assertFalse(comparer.canCompare("", ""));
  }

  @Test
  public void canCompare_sameType() {
    assertTrue(comparer.canCompare(new HashMap<>(), new HashMap<>()));
  }

  @Test
  public void canCompare_canCompareAcrossMapTypes() {
    assertTrue(comparer.canCompare(new LinkedHashMap<>(), new HashMap<>()));
  }

  @Test
  public void canCompare_bothMustBeMaps() {
    assertFalse(comparer.canCompare("", new HashMap<>()));
  }

  @Test
  public void compare_differentValues() {
    Map<?, ?> left = mapOf("x", mockValue1);
    Map<?, ?> right = mapOf("x", mockValue2);
    when(mockComparerManager.getDiff("mockPath{x}", mockValue1, mockValue2, true))
        .thenReturn(mockDiff1);

    Diff actual = comparer.compare("mockPath", left, right, mockComparerManager, true);

    assertEquals(ParentDiff.class, actual.getClass());
    List<?> actualChildren = (List<?>) ReflectionTestUtils.getField(actual, "childDiffs");
    assertEquals(1, actualChildren.size());
    assertEquals(mockDiff1, actualChildren.iterator().next());
  }

  @Test
  public void compare_same() {
    Map<?, ?> left = mapOf("x", mockValue1);
    Map<?, ?> right = mapOf("x", mockValue2);
    when(mockComparerManager.getDiff("mockPath{x}", mockValue1, mockValue2, true))
        .thenReturn(NULL_TOKEN);

    Diff actual = comparer.compare("mockPath", left, right, mockComparerManager, true);

    assertEquals(NULL_TOKEN, actual);
  }

  @Test
  public void compare_empty() {
    Map<?, ?> left = mapOf();
    Map<?, ?> right = mapOf();

    Diff actual = comparer.compare("mockPath", left, right, mockComparerManager, true);

    assertEquals(NULL_TOKEN, actual);
  }

  @Test
  public void compare_missingOne() {
    Map<?, ?> left = mapOf("x", mockValue1);
    Map<?, ?> right = mapOf();

    Diff actual = comparer.compare("mockPath", left, right, mockComparerManager, true);

    assertEquals(ParentDiff.class, actual.getClass());
    List<?> actualChildren = (List<?>) ReflectionTestUtils.getField(actual, "childDiffs");
    assertEquals(1, actualChildren.size());
    MissingValueDiff actualDiff = (MissingValueDiff) actualChildren.iterator().next();
    assertEquals("mockPath{x}", actualDiff.getPath());
    assertEquals(mockValue1, actualDiff.getLeftValue());
    assertEquals(null, actualDiff.getRightValue());
  }

  @Test
  public void compare_unexpectedOne() {
    Map<?, ?> left = mapOf();
    Map<?, ?> right = mapOf("x", mockValue2);

    Diff actual = comparer.compare("mockPath", left, right, mockComparerManager, true);

    assertEquals(ParentDiff.class, actual.getClass());
    List<?> actualChildren = (List<?>) ReflectionTestUtils.getField(actual, "childDiffs");
    assertEquals(1, actualChildren.size());
    UnexpectedValueDiff actualDiff = (UnexpectedValueDiff) actualChildren.iterator().next();
    assertEquals("mockPath{x}", actualDiff.getPath());
    assertEquals(null, actualDiff.getLeftValue());
    assertEquals(mockValue2, actualDiff.getRightValue());
  }

  @Test
  public void compare_mismatchedKeys() {
    Map<?, ?> left = mapOf("x", mockValue1);
    Map<?, ?> right = mapOf("y", mockValue2);

    Diff actual = comparer.compare("mockPath", left, right, mockComparerManager, true);

    assertEquals(ParentDiff.class, actual.getClass());
    List<?> actualChildren = (List<?>) ReflectionTestUtils.getField(actual, "childDiffs");
    assertEquals(2, actualChildren.size());
  }

  @Test
  public void compare_propagatesPartialFlag() {
    Map<?, ?> left = mapOf("x", mockValue1);
    Map<?, ?> right = mapOf("x", mockValue2);
    when(mockComparerManager.getDiff("mockPath{x}", mockValue1, mockValue2, false))
        .thenReturn(NULL_TOKEN);

    Diff actual = comparer.compare("mockPath", left, right, mockComparerManager, false);

    assertEquals(NULL_TOKEN, actual);
  }

  @Test
  public void compare_noPath() {
    Map<?, ?> left = mapOf("x", mockValue1);
    Map<?, ?> right = mapOf("x", mockValue2);
    when(mockComparerManager.getDiff("mockPath{x}", mockValue1, mockValue2, false))
        .thenReturn(NULL_TOKEN);

    Diff actual = comparer.compare("mockPath", left, right, mockComparerManager, false);

    assertEquals(NULL_TOKEN, actual);
  }

  protected Map<?, ?> mapOf(Object... keysAndValues) {
    Map<Object, Object> map = new HashMap<>(keysAndValues.length);
    for (int i = 0; i < keysAndValues.length; i += 2) {
      map.put(keysAndValues[i], keysAndValues[i + 1]);
    }
    return map;
  }
}
