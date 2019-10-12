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
    List<Integer> left = Arrays.asList(1);
    List<Integer> right = Arrays.asList(2);

    when(mockComparerManager.getDiff("mockPath[*]", 1, 2, false)).thenReturn(mockDiff1);

    Diff actual = comparer.compare("mockPath", left, right, mockComparerManager, true);

    assertEquals(ParentDiff.class, actual.getClass());
    List<?> actualChildren = (List<?>) ReflectionTestUtils.getField(actual, "childDiffs");
    assertEquals(2, actualChildren.size());

    Iterator<?> iterator = actualChildren.iterator();
    MissingValueDiff childDiff1 = (MissingValueDiff) iterator.next();
    assertEquals("mockPath[*]", childDiff1.getPath());
    assertEquals(1, childDiff1.getLeftValue());
    assertEquals(null, childDiff1.getRightValue());

    UnexpectedValueDiff childDiff2 = (UnexpectedValueDiff) iterator.next();
    assertEquals("mockPath[*]", childDiff2.getPath());
    assertEquals(null, childDiff2.getLeftValue());
    assertEquals(2, childDiff2.getRightValue());
  }

  @Test
  public void compare_sameOrderNoDiff() {
    List<Integer> left = Arrays.asList(1, 2);
    List<Integer> right = Arrays.asList(1, 2);

    when(mockComparerManager.getDiff(any(), any(), any(), eq(false))).thenReturn(NULL_TOKEN);

    Diff actual = comparer.compare("mockPath", left, right, mockComparerManager, false);

    assertEquals(NULL_TOKEN, actual);
  }

  @Test
  public void compare_differentOrderNoDiff() {
    List<Integer> left = Arrays.asList(1, 2);
    List<Integer> right = Arrays.asList(2, 1);

    when(mockComparerManager.getDiff(any(), any(), any(), eq(false))).thenReturn(NULL_TOKEN);

    Diff actual = comparer.compare("mockPath", left, right, mockComparerManager, false);

    assertEquals(NULL_TOKEN, actual);
  }

  @Test
  public void compare_partialDiff() {
    List<Integer> left = Arrays.asList(1);
    List<Integer> right = Arrays.asList(2);

    when(mockComparerManager.getDiff("mockPath[*]", 1, 2, false)).thenReturn(mockDiff1);

    Diff actual = comparer.compare("mockPath", left, right, mockComparerManager, false);

    assertEquals(PARTIAL_DIFF_TOKEN, actual);
  }

  @Test
  public void compare_partialDiffIgnoredWhenFullyMatched() {
    List<Integer> left = Arrays.asList(1);
    List<Integer> right = Arrays.asList(1);

    when(mockComparerManager.getDiff("mockPath[*]", 1, 1, false)).thenReturn(NULL_TOKEN);

    Diff actual = comparer.compare("mockPath", left, right, mockComparerManager, false);

    assertEquals(NULL_TOKEN, actual);
  }

  @Test
  public void compare_simpleListsSame() {
    List<Integer> left = Arrays.asList(1);
    List<Integer> right = Arrays.asList(1);

    when(mockComparerManager.getDiff("mockPath[*]", 1, 1, false)).thenReturn(NULL_TOKEN);

    Diff actual = comparer.compare("mockPath", left, right, mockComparerManager, true);

    assertEquals(NULL_TOKEN, actual);
  }

  @Test
  public void compare_missingValue() {
    List<Integer> left = Arrays.asList(1);
    List<Integer> right = Collections.emptyList();

    Diff actual = comparer.compare("mockPath", left, right, mockComparerManager, true);

    assertEquals(ParentDiff.class, actual.getClass());
    List<?> actualChildren = (List<?>) ReflectionTestUtils.getField(actual, "childDiffs");
    assertEquals(1, actualChildren.size());
    MissingValueDiff childDiff = (MissingValueDiff) actualChildren.iterator().next();
    assertEquals("mockPath[*]", childDiff.getPath());
    assertEquals(1, childDiff.getLeftValue());
    assertEquals(null, childDiff.getRightValue());
  }

  @Test
  public void compare_unexpectedValue() {
    List<Integer> left = Collections.emptyList();
    List<Integer> right = Arrays.asList(2);

    Diff actual = comparer.compare("mockPath", left, right, mockComparerManager, true);

    assertEquals(ParentDiff.class, actual.getClass());
    List<?> actualChildren = (List<?>) ReflectionTestUtils.getField(actual, "childDiffs");
    assertEquals(1, actualChildren.size());
    UnexpectedValueDiff childDiff = (UnexpectedValueDiff) actualChildren.iterator().next();
    assertEquals("mockPath[*]", childDiff.getPath());
    assertEquals(null, childDiff.getLeftValue());
    assertEquals(2, childDiff.getRightValue());
  }

  @Test
  public void compare_emptyLists() {
    List<Integer> left = Collections.emptyList();
    List<Integer> right = Collections.emptyList();

    Diff actual = comparer.compare("mockPath", left, right, mockComparerManager, false);

    assertEquals(NULL_TOKEN, actual);
  }

  @Test
  public void compare_notACollection() {
    assertThrows(
        ReflectionAssertionInternalException.class,
        () -> comparer.compare("mockPath", "", "", mockComparerManager, true));
  }

  @Test
  public void compare_alwaysSetsPartialFlag() {
    List<Integer> left = Arrays.asList(1);
    List<Integer> right = Arrays.asList(1);

    when(mockComparerManager.getDiff("mockPath[*]", 1, 1, false)).thenReturn(NULL_TOKEN);

    Diff actual = comparer.compare("mockPath", left, right, mockComparerManager, false);

    assertEquals(NULL_TOKEN, actual);
  }
}
