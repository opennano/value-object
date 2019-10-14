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
    List<Integer> expected = Arrays.asList(1);
    List<Integer> actual = Arrays.asList(2);

    when(mockComparerManager.getDiff("mockPath[0]", 1, 2, true)).thenReturn(mockDiff1);

    Diff actualDiff = comparer.compare("mockPath", expected, actual, mockComparerManager, true);

    assertEquals(ParentDiff.class, actualDiff.getClass());
    List<?> actualChildren = (List<?>) ReflectionTestUtils.getField(actualDiff, "childDiffs");
    assertEquals(1, actualChildren.size());
    assertEquals(mockDiff1, actualChildren.iterator().next());
  }

  @Test
  public void compare_simpleListsSame() {
    List<Integer> expected = Arrays.asList(1);
    List<Integer> actual = Arrays.asList(1);

    when(mockComparerManager.getDiff("mockPath[0]", 1, 1, true)).thenReturn(NULL_TOKEN);

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
    assertEquals("mockPath[0]", actualChild.getPath());
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
    assertEquals("mockPath[0]", actualChild.getPath());
    assertEquals(null, actualChild.getExpectedValue());
    assertEquals(2, actualChild.getActualValue());
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
    Constructor<? extends Collection<?>> ctor = type.getConstructor(Collection.class);
    List<?> twoItemList = Arrays.asList(1, 2);
    Collection<?> expected = ctor.newInstance(twoItemList);
    Collection<?> actual = twoItemList;

    when(mockComparerManager.getDiff(anyString(), any(), any(), eq(true))).thenReturn(NULL_TOKEN);

    Diff actualDiff = comparer.compare("mockPath", expected, actual, mockComparerManager, true);

    assertEquals(NULL_TOKEN, actualDiff);
  }

  @Test
  public void compare_enumSetIsOrderedToo() {
    Collection<?> expected = EnumSet.allOf(LeniencyMode.class);
    Collection<?> actual = EnumSet.allOf(LeniencyMode.class);

    when(mockComparerManager.getDiff(anyString(), any(), any(), eq(true))).thenReturn(NULL_TOKEN);

    Diff actualDiff = comparer.compare("mockPath", expected, actual, mockComparerManager, true);

    assertEquals(NULL_TOKEN, actualDiff);
  }

  @ParameterizedTest
  @ValueSource(
      classes = {
        HashSet.class,
      })
  public void compare_disallowedCollections(Class<? extends Collection<?>> type) throws Exception {
    Constructor<? extends Collection<?>> ctor = type.getConstructor(Collection.class);
    Collection<?> expected = ctor.newInstance(Arrays.asList(IGNORE_DEFAULTS, LENIENT_ORDER));
    Collection<?> actual = Arrays.asList(IGNORE_DEFAULTS, LENIENT_ORDER);

    assertThrows(
        ReflectionAssertionInputException.class,
        () -> comparer.compare("mockPath", expected, actual, mockComparerManager, true));
  }

  @Test
  public void compare_notACollection() {
    assertThrows(
        ReflectionAssertionInternalException.class,
        () -> comparer.compare("mockPath", "", "", mockComparerManager, true));
  }

  @Test
  public void compare_unorderedOkWhenOnlyOneItem() {
    Collection<?> expected = new HashSet<>(Arrays.asList(LENIENT_ORDER));
    Collection<?> actual = new HashSet<>(Arrays.asList(LENIENT_ORDER));

    when(mockComparerManager.getDiff("mockPath[0]", LENIENT_ORDER, LENIENT_ORDER, true))
        .thenReturn(NULL_TOKEN);

    Diff actualDiff = comparer.compare("mockPath", expected, actual, mockComparerManager, true);

    assertEquals(NULL_TOKEN, actualDiff);
  }

  @Test
  public void compare_unorderedOkWhenEmpty() {
    Collection<?> expected = new HashSet<>(0);
    Collection<?> actual = new HashSet<>(0);

    Diff actualDiff = comparer.compare("mockPath", expected, actual, mockComparerManager, true);

    assertEquals(NULL_TOKEN, actualDiff);
  }

  @Test
  public void compare_emptyLists() {
    List<Integer> expected = Collections.emptyList();
    List<Integer> actual = Collections.emptyList();

    Diff actualDiff = comparer.compare("mockPath", expected, actual, mockComparerManager, true);

    assertEquals(NULL_TOKEN, actualDiff);
  }

  @Test
  public void compare_propagatesPartialFlag() {
    List<Integer> expected = Arrays.asList(1);
    List<Integer> actual = Arrays.asList(1);

    when(mockComparerManager.getDiff("mockPath[0]", 1, 1, false)).thenReturn(NULL_TOKEN);

    Diff actualDiff = comparer.compare("mockPath", expected, actual, mockComparerManager, false);

    assertEquals(NULL_TOKEN, actualDiff);
  }
}
