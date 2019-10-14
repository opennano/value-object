package com.github.opennano.reflectionassert.comparers;

import static com.github.opennano.reflectionassert.diffs.NullDiff.NULL_TOKEN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

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
  public void canCompare_expectedNull() {
    assertFalse(comparer.canCompare(null, new HashMap<>()));
  }

  @Test
  public void canCompare_actualNull() {
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
    Map<?, ?> expected = mapOf("x", mockValue1);
    Map<?, ?> actual = mapOf("x", mockValue2);
    when(mockComparerManager.getDiff("mockPath{x}", mockValue1, mockValue2, true))
        .thenReturn(mockDiff1);

    Diff actualDiff = comparer.compare("mockPath", expected, actual, mockComparerManager, true);

    assertEquals(ParentDiff.class, actualDiff.getClass());
    List<?> actualChildren = (List<?>) ReflectionTestUtils.getField(actualDiff, "childDiffs");
    assertEquals(1, actualChildren.size());
    assertEquals(mockDiff1, actualChildren.iterator().next());
  }

  @Test
  public void compare_same() {
    Map<?, ?> expected = mapOf("x", mockValue1);
    Map<?, ?> actual = mapOf("x", mockValue2);
    when(mockComparerManager.getDiff("mockPath{x}", mockValue1, mockValue2, true))
        .thenReturn(NULL_TOKEN);

    Diff actualDiff = comparer.compare("mockPath", expected, actual, mockComparerManager, true);

    assertEquals(NULL_TOKEN, actualDiff);
  }

  @Test
  public void compare_empty() {
    Map<?, ?> expected = mapOf();
    Map<?, ?> actual = mapOf();

    Diff actualDiff = comparer.compare("mockPath", expected, actual, mockComparerManager, true);

    assertEquals(NULL_TOKEN, actualDiff);
  }

  @Test
  public void compare_missingOne() {
    Map<?, ?> expected = mapOf("x", mockValue1);
    Map<?, ?> actual = mapOf();

    Diff actualDiff = comparer.compare("mockPath", expected, actual, mockComparerManager, true);

    assertEquals(ParentDiff.class, actualDiff.getClass());
    List<?> actualChildren = (List<?>) ReflectionTestUtils.getField(actualDiff, "childDiffs");
    assertEquals(1, actualChildren.size());
    MissingValueDiff actualChild = (MissingValueDiff) actualChildren.iterator().next();
    assertEquals("mockPath{x}", actualChild.getPath());
    assertEquals(mockValue1, actualChild.getExpectedValue());
    assertEquals(null, actualChild.getActualValue());
  }

  @Test
  public void compare_unexpectedOne() {
    Map<?, ?> expected = mapOf();
    Map<?, ?> actual = mapOf("x", mockValue2);

    Diff actualDiff = comparer.compare("mockPath", expected, actual, mockComparerManager, true);

    assertEquals(ParentDiff.class, actualDiff.getClass());
    List<?> actualChildren = (List<?>) ReflectionTestUtils.getField(actualDiff, "childDiffs");
    assertEquals(1, actualChildren.size());
    UnexpectedValueDiff actualChild = (UnexpectedValueDiff) actualChildren.iterator().next();
    assertEquals("mockPath{x}", actualChild.getPath());
    assertEquals(null, actualChild.getExpectedValue());
    assertEquals(mockValue2, actualChild.getActualValue());
  }

  @Test
  public void compare_mismatchedKeys() {
    Map<?, ?> expected = mapOf("x", mockValue1);
    Map<?, ?> actual = mapOf("y", mockValue2);

    Diff actualDiff = comparer.compare("mockPath", expected, actual, mockComparerManager, true);

    assertEquals(ParentDiff.class, actualDiff.getClass());
    List<?> actualChildren = (List<?>) ReflectionTestUtils.getField(actualDiff, "childDiffs");
    assertEquals(2, actualChildren.size());
  }

  @Test
  public void compare_propagatesPartialFlag() {
    Map<?, ?> expected = mapOf("x", mockValue1);
    Map<?, ?> actual = mapOf("x", mockValue2);
    when(mockComparerManager.getDiff("mockPath{x}", mockValue1, mockValue2, false))
        .thenReturn(NULL_TOKEN);

    Diff actualDiff = comparer.compare("mockPath", expected, actual, mockComparerManager, false);

    assertEquals(NULL_TOKEN, actualDiff);
  }

  @Test
  public void compare_noPath() {
    Map<?, ?> expected = mapOf("x", mockValue1);
    Map<?, ?> actual = mapOf("x", mockValue2);
    when(mockComparerManager.getDiff("mockPath{x}", mockValue1, mockValue2, false))
        .thenReturn(NULL_TOKEN);

    Diff actualDiff = comparer.compare("mockPath", expected, actual, mockComparerManager, false);

    assertEquals(NULL_TOKEN, actualDiff);
  }

  protected Map<?, ?> mapOf(Object... keysAndValues) {
    Map<Object, Object> map = new HashMap<>(keysAndValues.length);
    for (int i = 0; i < keysAndValues.length; i += 2) {
      map.put(keysAndValues[i], keysAndValues[i + 1]);
    }
    return map;
  }
}
