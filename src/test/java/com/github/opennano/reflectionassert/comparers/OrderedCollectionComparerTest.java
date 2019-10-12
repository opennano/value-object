package com.github.opennano.reflectionassert.comparers;

import static com.github.opennano.reflectionassert.LeniencyMode.IGNORE_DEFAULTS;
import static com.github.opennano.reflectionassert.LeniencyMode.LENIENT_ORDER;
import static com.github.opennano.reflectionassert.diffs.NullDiff.NULL_TOKEN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.TreeSet;
import java.util.Vector;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.github.opennano.reflectionassert.LeniencyMode;
import com.github.opennano.reflectionassert.diffs.Diff;
import com.github.opennano.reflectionassert.diffs.MissingValueDiff;
import com.github.opennano.reflectionassert.diffs.ParentDiff;
import com.github.opennano.reflectionassert.diffs.UnexpectedValueDiff;
import com.github.opennano.reflectionassert.exceptions.ReflectionAssertionInputException;
import com.github.opennano.reflectionassert.exceptions.ReflectionAssertionInternalException;
import com.github.opennano.reflectionassert.worker.ComparerManager;

@ExtendWith(MockitoExtension.class)
public class OrderedCollectionComparerTest {

  @InjectMocks private OrderedCollectionComparer comparer;
  @Mock private ComparerManager mockComparerManager;
  @Mock private Diff mockDiff1;

  @Test
  public void compare_simpleListsDifferent() {
    List<Integer> left = Arrays.asList(1);
    List<Integer> right = Arrays.asList(2);

    when(mockComparerManager.getDiff("mockPath[0]", 1, 2, true)).thenReturn(mockDiff1);

    Diff actual = comparer.compare("mockPath", left, right, mockComparerManager, true);

    assertEquals(ParentDiff.class, actual.getClass());
    List<?> actualChildren = (List<?>) ReflectionTestUtils.getField(actual, "childDiffs");
    assertEquals(1, actualChildren.size());
    assertEquals(mockDiff1, actualChildren.iterator().next());
  }

  @Test
  public void compare_simpleListsSame() {
    List<Integer> left = Arrays.asList(1);
    List<Integer> right = Arrays.asList(1);

    when(mockComparerManager.getDiff("mockPath[0]", 1, 1, true)).thenReturn(NULL_TOKEN);

    Diff actual = comparer.compare("mockPath", left, right, mockComparerManager, true);

    assertEquals(NULL_TOKEN, actual);
  }
  
  // FIXME add  test for reordered elements

  @Test
  public void compare_missingValue() {
    List<Integer> left = Arrays.asList(1);
    List<Integer> right = Collections.emptyList();

    Diff actual = comparer.compare("mockPath", left, right, mockComparerManager, true);

    assertEquals(ParentDiff.class, actual.getClass());
    List<?> actualChildren = (List<?>) ReflectionTestUtils.getField(actual, "childDiffs");
    assertEquals(1, actualChildren.size());
    MissingValueDiff childDiff = (MissingValueDiff) actualChildren.iterator().next();
    assertEquals("mockPath[0]", childDiff.getPath());
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
    assertEquals("mockPath[0]", childDiff.getPath());
    assertEquals(null, childDiff.getLeftValue());
    assertEquals(2, childDiff.getRightValue());
  }

  @ParameterizedTest
  @ValueSource(
      classes = {
        ArrayList.class,
        LinkedList.class,
        Vector.class,
        LinkedHashSet.class,
        TreeSet.class,
        PriorityQueue.class
      })
  public void compare_allowedCollections(Class<? extends Collection<?>> type) throws Exception {
    List<?> twoItemList = Arrays.asList(1, 2);
    Constructor<? extends Collection<?>> ctor = type.getConstructor(Collection.class);
    Collection<?> left = ctor.newInstance(twoItemList);
    Collection<?> right = twoItemList;

    when(mockComparerManager.getDiff(anyString(), any(), any(), eq(true))).thenReturn(NULL_TOKEN);

    Diff actual = comparer.compare("mockPath", left, right, mockComparerManager, true);

    assertEquals(NULL_TOKEN, actual);
  }

  @Test
  public void compare_enumSetIsOrderedToo() {
    Collection<?> left = EnumSet.allOf(LeniencyMode.class);
    Collection<?> right = EnumSet.allOf(LeniencyMode.class);

    when(mockComparerManager.getDiff(anyString(), any(), any(), eq(true))).thenReturn(NULL_TOKEN);

    Diff actual = comparer.compare("mockPath", left, right, mockComparerManager, true);

    assertEquals(NULL_TOKEN, actual);
  }

  @ParameterizedTest
  @ValueSource(
      classes = {
        HashSet.class,
      })
  public void compare_disallowedCollections(Class<? extends Collection<?>> type) throws Exception {
    Constructor<? extends Collection<?>> ctor = type.getConstructor(Collection.class);
    Collection<?> left = ctor.newInstance(Arrays.asList(IGNORE_DEFAULTS, LENIENT_ORDER));
    Collection<?> right = Arrays.asList(IGNORE_DEFAULTS, LENIENT_ORDER);

    assertThrows(
        ReflectionAssertionInputException.class,
        () -> comparer.compare("mockPath", left, right, mockComparerManager, true));
  }

  @Test
  public void compare_notACollection() {
    assertThrows(
        ReflectionAssertionInternalException.class,
        () -> comparer.compare("mockPath", "", "", mockComparerManager, true));
  }

  @Test
  public void compare_unorderedOkWhenOnlyOneItem() {
    Collection<?> left = new HashSet<>(Arrays.asList(LENIENT_ORDER));
    Collection<?> right = new HashSet<>(Arrays.asList(LENIENT_ORDER));

    when(mockComparerManager.getDiff("mockPath[0]", LENIENT_ORDER, LENIENT_ORDER, true))
        .thenReturn(NULL_TOKEN);

    Diff actual = comparer.compare("mockPath", left, right, mockComparerManager, true);

    assertEquals(NULL_TOKEN, actual);
  }

  @Test
  public void compare_unorderedOkWhenEmpty() {
    Collection<?> left = new HashSet<>(0);
    Collection<?> right = new HashSet<>(0);

    Diff actual = comparer.compare("mockPath", left, right, mockComparerManager, true);

    assertEquals(NULL_TOKEN, actual);
  }

  @Test
  public void compare_emptyLists() {
    List<Integer> left = Collections.emptyList();
    List<Integer> right = Collections.emptyList();

    Diff actual = comparer.compare("mockPath", left, right, mockComparerManager, true);

    assertEquals(NULL_TOKEN, actual);
  }

  @Test
  public void compare_propagatesPartialFlag() {
    List<Integer> left = Arrays.asList(1);
    List<Integer> right = Arrays.asList(1);

    when(mockComparerManager.getDiff("mockPath[0]", 1, 1, false)).thenReturn(NULL_TOKEN);

    Diff actual = comparer.compare("mockPath", left, right, mockComparerManager, false);

    assertEquals(NULL_TOKEN, actual);
  }
}
