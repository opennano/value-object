package com.github.opennano.reflectionassert.comparers;

import static com.github.opennano.reflectionassert.diffs.NullDiff.NULL_TOKEN;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Queue;
import java.util.SortedSet;
import java.util.stream.Stream;

import com.github.opennano.reflectionassert.diffs.Diff;
import com.github.opennano.reflectionassert.diffs.MissingValueDiff;
import com.github.opennano.reflectionassert.diffs.UnexpectedValueDiff;
import com.github.opennano.reflectionassert.exceptions.ReflectionAssertionInputException;
import com.github.opennano.reflectionassert.worker.ComparerManager;

public class OrderedCollectionComparer extends CollectionComparer {

  /** value objects must be an array or instance of one of these types to be comparable */
  private static final List<Class<?>> ORDERED_TYPES =
      Arrays.asList(List.class, SortedSet.class, EnumSet.class, LinkedHashSet.class, Queue.class);

  private static final String PROPERTY_PATH_TEMPLATE = "%s[%s]";

  @Override
  public Diff compare(
      String path, Object left, Object right, ComparerManager comparer, boolean fullDiff) {

    assertAllOrdered(left, right);
    List<?> leftList = asNewList(left);
    List<?> rightList = asNewList(right);

    // compare each item by index, reporting a diff if one exists

    int index = 0;
    List<Diff> diffs = new ArrayList<>();
    Iterator<?> leftIterator = leftList.iterator();
    Iterator<?> rightIterator = rightList.iterator();
    while (leftIterator.hasNext() && rightIterator.hasNext()) {
      Object leftItem = leftIterator.next();
      Object rightItem = rightIterator.next();
      String elementPath = appendIndexToPath(path, index);
      Diff itemDiff = comparer.getDiff(elementPath, leftItem, rightItem, fullDiff);
      if (itemDiff != NULL_TOKEN) {
        diffs.add(itemDiff);
      }
      index++;
    }

    // check for "extra" elements in either collection
    // maybe the left has more or the right has more, but never both
    while (leftIterator.hasNext()) {
      Object leftItem = leftIterator.next();
      String elementPath = appendIndexToPath(path, index);
      diffs.add(new MissingValueDiff(elementPath, leftItem));
      index++;
    }
    while (rightIterator.hasNext()) {
      Object rightItem = rightIterator.next();
      String elementPath = appendIndexToPath(path, index);
      diffs.add(new UnexpectedValueDiff(elementPath, rightItem));
      index++;
    }

    return createDiff(path, diffs, fullDiff);
  }

  private String appendIndexToPath(String path, int index) {
    return String.format(PROPERTY_PATH_TEMPLATE, path, index);
  }

  private void assertAllOrdered(Object... values) {
    Stream.of(values).forEach(this::assertOrdered);
  }

  private void assertOrdered(Object value) {
    if (!(value instanceof Collection)) {
      return; // either the wrong type (fails later) or an array
    }
    Collection<?> collection = (Collection<?>) value;
    if (collection.size() < 2) {
      // order doesn't matter if there are 1 or 0 elements
      return;
    }

    Class<?> type = value.getClass();
    ORDERED_TYPES
        .stream()
        .filter(orderedType -> orderedType.isAssignableFrom(type))
        .findFirst()
        .orElseThrow(() -> unorderedCollectionException(type));
  }

  private RuntimeException unorderedCollectionException(Class<?> type) {
    StringBuilder sb = new StringBuilder();
    sb.append("Probable unordered collection detected. ");
    sb.append("Consider using ordered collections (e.g. List or LinkedHashSet) ");
    sb.append("or use LENIENT_ORDER mode for order invariant comparisons: ");
    sb.append(type.getName());
    return new ReflectionAssertionInputException(sb.toString());
  }
}
