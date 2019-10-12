package com.github.opennano.reflectionassert.comparers;

import static java.util.Collections.EMPTY_LIST;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.opennano.reflectionassert.comparers.CollectionComparer;
import com.github.opennano.reflectionassert.diffs.Diff;
import com.github.opennano.reflectionassert.exceptions.ReflectionAssertionInternalException;
import com.github.opennano.reflectionassert.worker.ComparerManager;

@ExtendWith(MockitoExtension.class)
public class CollectionComparerTest {

  private static class StubCollectionComparer extends CollectionComparer {

    @Override
    public Diff compare(
        String path, Object left, Object right, ComparerManager comparer, boolean fullDiff) {
    	
      return null;
    }
  }

  @InjectMocks private StubCollectionComparer comparer;

  @Test
  public void canCompare_leftNull() {
    assertFalse(comparer.canCompare(null, EMPTY_LIST));
  }

  @Test
  public void canCompare_rightNull() {
    assertFalse(comparer.canCompare(EMPTY_LIST, null));
  }

  @Test
  public void canCompare_bothNull() {
    assertFalse(comparer.canCompare(null, null));
  }

  @Test
  public void canCompare_lists() {
    assertTrue(comparer.canCompare(EMPTY_LIST, EMPTY_LIST));
  }

  @Test
  public void canCompare_arrays() {
    assertTrue(comparer.canCompare(new int[] {}, new int[] {}));
  }

  @Test
  public void canCompare_listsAndArrays() {
    assertTrue(comparer.canCompare(EMPTY_LIST, new int[] {}));
  }

  @Test
  public void canCompare_leftWrongType() {
    assertFalse(comparer.canCompare("", EMPTY_LIST));
  }

  @Test
  public void canCompare_rightWrongType() {
    assertFalse(comparer.canCompare(EMPTY_LIST, ""));
  }

  @Test
  public void canCompare_bothWrongType() {
    assertFalse(comparer.canCompare("", ""));
  }

  @Test
  public void asNewList_emptyList() {
    List<?> newList = comparer.asNewList(EMPTY_LIST);
    assertEquals(0, newList.size());
  }

  @Test
  public void asNewList_emptyArray() {
    List<?> newList = comparer.asNewList(new Object[0]);
    assertEquals(0, newList.size());
  }

  @Test
  public void asNewList_emptyPrimitiveArray() {
    List<?> newList = comparer.asNewList(new int[0]);
    assertEquals(0, newList.size());
  }

  @Test
  public void asNewList_list() {
    List<?> newList = comparer.asNewList(Arrays.asList(1));
    assertEquals(1, newList.size());
    assertNotSame(EMPTY_LIST, newList);
  }

  @Test
  public void asNewList_set() {
    assertEquals(1, comparer.asNewList(new HashSet<>(Arrays.asList(1))).size());
  }

  @Test
  public void asNewList_array() {
    assertEquals(1, comparer.asNewList(new String[] {""}).size());
  }

  @Test
  public void asNewList_primitiveArrayReturnsBoxedList() {
    List<?> list = comparer.asNewList(new int[] {2});
    assertEquals(1, list.size());
    assertEquals(Integer.class, list.iterator().next().getClass());
  }

  @Test
  public void asNewList_unsupportedType() {
    assertThrows(ReflectionAssertionInternalException.class, () -> comparer.asNewList(1));
  }
}
