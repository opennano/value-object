package com.github.opennano.reflectionassert.comparers;

import static com.github.opennano.reflectionassert.diffs.NullDiff.NULL_TOKEN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.github.opennano.reflectionassert.diffs.Diff;
import com.github.opennano.reflectionassert.diffs.ParentDiff;
import com.github.opennano.reflectionassert.diffs.SimpleDiff;
import com.github.opennano.reflectionassert.exceptions.ReflectionAssertionInternalException;
import com.github.opennano.reflectionassert.worker.ComparerManager;

@ExtendWith(MockitoExtension.class)
public class ObjectComparerTest {

  @SuppressWarnings("unused") // used by reflection
  private static class StaticFieldObject {
    private static Double ignoredStaticField = Math.random();
  }

  @SuppressWarnings("unused") // used by reflection
  private class SimpleObject extends StaticFieldObject {
    private transient Double ignoredField = Math.random();

    private String testedField; // not ignored

    public SimpleObject(String string) {
      this.testedField = string;
    }
  }

  @InjectMocks private ObjectComparer comparer;
  @Mock private ComparerManager mockComparerManager;
  @Mock private Diff mockDiff1;
  @Mock private Diff mockDiff2;
  @Mock private Object mockValue1;
  @Mock private Object mockValue2;

  @Test
  public void canCompare_leftNull() {
    assertFalse(comparer.canCompare(null, new SimpleObject("x")));
  }

  @Test
  public void canCompare_rightNull() {
    assertFalse(comparer.canCompare(new SimpleObject("x"), null));
  }

  @Test
  public void canCompare_bothNull() {
    assertFalse(comparer.canCompare(null, null));
  }

  @Test
  public void canCompare_bothNonNull() {
    assertTrue(comparer.canCompare(new SimpleObject("x"), new SimpleObject("x")));
  }

  @Test
  public void compare_differentValues() {
    SimpleObject left = new SimpleObject("x");
    SimpleObject right = new SimpleObject("y");

    when(mockComparerManager.getDiff("mockPath.testedField", "x", "y", true)).thenReturn(mockDiff1);

    Diff actual = comparer.compare("mockPath", left, right, mockComparerManager, true);

    assertEquals(ParentDiff.class, actual.getClass());
    List<?> actualChildren = (List<?>) ReflectionTestUtils.getField(actual, "childDiffs");
    assertEquals(1, actualChildren.size());
    assertEquals(mockDiff1, actualChildren.iterator().next());
  }

  @Test
  public void compare_same() {
    SimpleObject left = new SimpleObject("x");
    SimpleObject right = new SimpleObject("x");

    when(mockComparerManager.getDiff("mockPath.testedField", "x", "x", true))
        .thenReturn(NULL_TOKEN);

    Diff actual = comparer.compare("mockPath", left, right, mockComparerManager, true);

    assertEquals(NULL_TOKEN, actual);
  }

  @Test
  public void compare_rootPath() {
    SimpleObject left = new SimpleObject("x");
    SimpleObject right = new SimpleObject("y");

    when(mockComparerManager.getDiff("$testedField", "x", "y", true)).thenReturn(mockDiff1);

    Diff actual = comparer.compare("$", left, right, mockComparerManager, true);

    assertEquals(ParentDiff.class, actual.getClass());
    List<?> actualChildren = (List<?>) ReflectionTestUtils.getField(actual, "childDiffs");
    assertEquals(1, actualChildren.size());
    assertEquals(mockDiff1, actualChildren.iterator().next());
  }

  @Test
  public void compare_actualIsSubtype() {
    Object left = new Object();
    SimpleObject right = new SimpleObject("y");

    Diff actual = comparer.compare("mockPath", left, right, mockComparerManager, true);

    assertEquals(NULL_TOKEN, actual);
  }

  @Test
  public void compare_actualIsSupertype() {
    SimpleObject left = new SimpleObject("y");
    Object right = new Object();

    Diff actual = comparer.compare("mockPath", left, right, mockComparerManager, true);

    assertEquals(SimpleDiff.class, actual.getClass());
    assertEquals("mockPath", actual.getPath());
    assertEquals(SimpleObject.class, actual.getLeftValue());
    assertEquals(Object.class, actual.getRightValue());
  }

  @Test
  public void compare_propagatesPartialFlag() {
    SimpleObject left = new SimpleObject("x");
    SimpleObject right = new SimpleObject("x");

    when(mockComparerManager.getDiff("mockPath.testedField", "x", "x", false))
        .thenReturn(NULL_TOKEN);

    Diff actual = comparer.compare("mockPath", left, right, mockComparerManager, false);

    assertEquals(NULL_TOKEN, actual);
  }

  @Test
  public void getFieldDiff_forceException() throws Exception {
    Field testedField = SimpleObject.class.getDeclaredField("testedField");

    assertThrows(
        ReflectionAssertionInternalException.class,
        () ->
            ReflectionTestUtils.invokeMethod(
                comparer, "getFieldDiff", "", testedField, "", "", null, true));
  }
}
