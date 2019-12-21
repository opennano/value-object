package com.github.opennano.reflectionassert.comparers;

import static com.github.opennano.reflectionassert.diffs.NullDiff.NULL_TOKEN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.github.opennano.reflectionassert.diffs.Diff;
import com.github.opennano.reflectionassert.diffs.MissingValueDiff;
import com.github.opennano.reflectionassert.diffs.ParentDiff;
import static com.github.opennano.reflectionassert.diffs.PartialDiff.*;
import com.github.opennano.reflectionassert.diffs.UnexpectedValueDiff;
import com.github.opennano.reflectionassert.exceptions.ReflectionAssertionInternalException;
import com.github.opennano.reflectionassert.worker.ComparerManager;

@ExtendWith(MockitoExtension.class)
public class UnorderedCollectionComparerTest {

  @InjectMocks private UnorderedCollectionComparer comparer;
  @Mock private ComparerManager mockComparerManager;
  @Mock private Diff mockDiff1;

  @Test
  public void compare_simpleListsDifferent() {
    List<Integer> expected = Arrays.asList(1);
    List<Integer> actual = Arrays.asList(2);

    when(mockComparerManager.getDiff("mockPath[*]", 1, 2, false)).thenReturn(mockDiff1);

    Diff actualDiff = comparer.compare("mockPath", expected, actual, mockComparerManager, true);

    assertEquals(ParentDiff.class, actualDiff.getClass());
    List<?> actualChildren = (List<?>) ReflectionTestUtils.getField(actualDiff, "childDiffs");
    assertEquals(2, actualChildren.size());

    Iterator<?> iterator = actualChildren.iterator();
    MissingValueDiff actualChild1 = (MissingValueDiff) iterator.next();
    assertEquals("mockPath[*]", actualChild1.getPath());
    assertEquals(1, actualChild1.getExpectedValue());
    assertEquals(null, actualChild1.getActualValue());

    UnexpectedValueDiff actualChild2 = (UnexpectedValueDiff) iterator.next();
    assertEquals("mockPath[*]", actualChild2.getPath());
    assertEquals(null, actualChild2.getExpectedValue());
    assertEquals(2, actualChild2.getActualValue());
  }

  @Test
  public void compare_sameOrderNoDiff() {
    List<Integer> expected = Arrays.asList(1, 2);
    List<Integer> actual = Arrays.asList(1, 2);

    when(mockComparerManager.getDiff(any(), any(), any(), eq(false))).thenReturn(NULL_TOKEN);

    Diff actualDiff = comparer.compare("mockPath", expected, actual, mockComparerManager, false);

    assertEquals(NULL_TOKEN, actualDiff);
  }

  @Test
  public void compare_differentOrderNoDiff() {
    List<Integer> expected = Arrays.asList(1, 2);
    List<Integer> actual = Arrays.asList(2, 1);

    when(mockComparerManager.getDiff(any(), any(), any(), eq(false))).thenReturn(NULL_TOKEN);

    Diff actualDiff = comparer.compare("mockPath", expected, actual, mockComparerManager, false);

    assertEquals(NULL_TOKEN, actualDiff);
  }

  @Test
  public void compare_partialDiff() {
    List<Integer> expected = Arrays.asList(1);
    List<Integer> actual = Arrays.asList(2);

    when(mockComparerManager.getDiff("mockPath[*]", 1, 2, false)).thenReturn(mockDiff1);

    Diff actualDiff = comparer.compare("mockPath", expected, actual, mockComparerManager, false);

    assertEquals(PARTIAL_DIFF_TOKEN, actualDiff);
  }

  @Test
  public void compare_partialDiffIgnoredWhenFullyMatched() {
    List<Integer> expected = Arrays.asList(1);
    List<Integer> actual = Arrays.asList(1);

    when(mockComparerManager.getDiff("mockPath[*]", 1, 1, false)).thenReturn(NULL_TOKEN);

    Diff actualDiff = comparer.compare("mockPath", expected, actual, mockComparerManager, false);

    assertEquals(NULL_TOKEN, actualDiff);
  }

  @Test
  public void compare_simpleListsSame() {
    List<Integer> expected = Arrays.asList(1);
    List<Integer> actual = Arrays.asList(1);

    when(mockComparerManager.getDiff("mockPath[*]", 1, 1, false)).thenReturn(NULL_TOKEN);

    Diff actualDiff = comparer.compare("mockPath", expected, actual, mockComparerManager, true);

    assertEquals(NULL_TOKEN, actualDiff);
  }

  @Test
  public void compare_missingValue() {
    List<Integer> expected = Arrays.asList(1);
    List<Integer> actual = Collections.emptyList();

    Diff actualDiff = comparer.compare("mockPath", expected, actual, mockComparerManager, true);

    assertEquals(ParentDiff.class, actualDiff.getClass());
    List<?> actualChildren = (List<?>) ReflectionTestUtils.getField(actualDiff, "childDiffs");
    assertEquals(1, actualChildren.size());
    MissingValueDiff actualChild = (MissingValueDiff) actualChildren.iterator().next();
    assertEquals("mockPath[*]", actualChild.getPath());
    assertEquals(1, actualChild.getExpectedValue());
    assertEquals(null, actualChild.getActualValue());
  }

  @Test
  public void compare_unexpectedValue() {
    List<Integer> expected = Collections.emptyList();
    List<Integer> actual = Arrays.asList(2);

    Diff actualDiff = comparer.compare("mockPath", expected, actual, mockComparerManager, true);

    assertEquals(ParentDiff.class, actualDiff.getClass());
    List<?> actualChildren = (List<?>) ReflectionTestUtils.getField(actualDiff, "childDiffs");
    assertEquals(1, actualChildren.size());
    UnexpectedValueDiff actualChild = (UnexpectedValueDiff) actualChildren.iterator().next();
    assertEquals("mockPath[*]", actualChild.getPath());
    assertEquals(null, actualChild.getExpectedValue());
    assertEquals(2, actualChild.getActualValue());
  }

  @Test
  public void compare_emptyLists() {
    List<Integer> expected = Collections.emptyList();
    List<Integer> actual = Collections.emptyList();

    Diff actualDiff = comparer.compare("mockPath", expected, actual, mockComparerManager, false);

    assertEquals(NULL_TOKEN, actualDiff);
  }

  @Test
  public void compare_notACollection() {
    assertThrows(
        ReflectionAssertionInternalException.class,
        () -> comparer.compare("mockPath", "", "", mockComparerManager, true));
  }

  @Test
  public void compare_alwaysSetsPartialFlag() {
    List<Integer> expected = Arrays.asList(1);
    List<Integer> actual = Arrays.asList(1);

    when(mockComparerManager.getDiff("mockPath[*]", 1, 1, false)).thenReturn(NULL_TOKEN);

    Diff actualDiff = comparer.compare("mockPath", expected, actual, mockComparerManager, false);

    assertEquals(NULL_TOKEN, actualDiff);
  }
}
