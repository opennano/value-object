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
import com.github.opennano.reflectionassert.diffs.PartialDiff;
import com.github.opennano.reflectionassert.diffs.UnexpectedValueDiff;
import com.github.opennano.reflectionassert.exceptions.ReflectionAssertionInputException;
import com.github.opennano.reflectionassert.worker.ComparerManager;

public class OrderedCollectionComparer extends CollectionComparer {

  /** value objects must be an array or instance of one of these types to be comparable */
  private static final List<Class<?>> ORDERED_TYPES =
      Arrays.asList(List.class, SortedSet.class, EnumSet.class, LinkedHashSet.class, Queue.class);

  private static final String PROPERTY_PATH_TEMPLATE = "%s[%s]";

  /**
   * Compare the given collections or arrays by comparing each element by index.
   *
   * @param path the path so far (from root down to the objects being compared)
   * @param expected the expected object
   * @param actual the actual object
   * @param comparer used when recursion is necessary on child objects
   * @param fullDiff when false comparison should end at the first found difference, in which case a
   *     {@link PartialDiff#PARTIAL_DIFF_TOKEN} should be returned.
   */
  @Override
  public Diff compare(
      String path, Object expected, Object actual, ComparerManager comparer, boolean fullDiff) {

    assertAllOrdered(expected, actual);
    List<?> expectedList = asNewList(expected);
    List<?> actualList = asNewList(actual);

    // compare each item by index, reporting a diff if one exists

    int index = 0;
    List<Diff> diffs = new ArrayList<>();
    Iterator<?> expectedIterator = expectedList.iterator();
    Iterator<?> actualIterator = actualList.iterator();
    while (expectedIterator.hasNext() && actualIterator.hasNext()) {
      Object expectedItem = expectedIterator.next();
      Object actualItem = actualIterator.next();
      String elementPath = appendIndexToPath(path, index);
      Diff itemDiff = comparer.getDiff(elementPath, expectedItem, actualItem, fullDiff);
      if (itemDiff != NULL_TOKEN) {
        diffs.add(itemDiff);
      }
      index++;
    }

    // check for "extra" elements in either collection
    // maybe the expected has more or the actual has more, but never both
    while (expectedIterator.hasNext()) {
      Object expectedItem = expectedIterator.next();
      String elementPath = appendIndexToPath(path, index);
      diffs.add(new MissingValueDiff(elementPath, expectedItem));
      index++;
    }
    while (actualIterator.hasNext()) {
      Object actualItem = actualIterator.next();
      String elementPath = appendIndexToPath(path, index);
      diffs.add(new UnexpectedValueDiff(elementPath, actualItem));
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
