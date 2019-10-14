package com.github.opennano.reflectionassert.comparers;

import static com.github.opennano.reflectionassert.diffs.NullDiff.NULL_TOKEN;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.github.opennano.reflectionassert.diffs.Diff;
import com.github.opennano.reflectionassert.diffs.MissingValueDiff;
import com.github.opennano.reflectionassert.diffs.PartialDiff;
import com.github.opennano.reflectionassert.diffs.UnexpectedValueDiff;
import com.github.opennano.reflectionassert.worker.ComparerManager;

/**
 * Compares collections and arrays. Since order doesn't matter diffs can't be strictly computed.
 * Diffs typically are presented as a pair of unmatched expected and actual elements.
 *
 * <p>Compared to an {@link OrderedCollectionComparer} performance of this comparer is far
 * worse--O(n^2) versus O(n).
 *
 * <p>TODO implement a separate (faster) cache for has any diff by CacheKey.
 */
public class UnorderedCollectionComparer extends CollectionComparer {

  private static final String PROPERTY_PATH_TEMPLATE = "%s[*]";

  /**
   * Compare the given collections or arrays, ignoring element order.
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

    // wrap in a new list for convenience
    List<?> expectedList = asNewList(expected);
    List<?> actualList = asNewList(actual);

    String elementPath = appendIndexToPath(path);
    // compare all pairs of items in each collection, removing any that match
    for (Iterator<?> expectedIterator = expectedList.iterator();
        expectedIterator.hasNext() && !actualList.isEmpty(); ) {

      Object expectedItem = expectedIterator.next();
      for (Iterator<?> actualIterator = actualList.iterator(); actualIterator.hasNext(); ) {
        if (comparer.getDiff(elementPath, expectedItem, actualIterator.next(), false)
            == NULL_TOKEN) {
          // found matching element--remove from both collections
          expectedIterator.remove();
          actualIterator.remove();
          break;
        }
      }
    }

    // all remaining elements are either missing or unexpected and are reported as diffs
    List<Diff> diffs = new ArrayList<>();
    expectedList.forEach(item -> diffs.add(new MissingValueDiff(elementPath, item)));
    actualList.forEach(item -> diffs.add(new UnexpectedValueDiff(elementPath, item)));

    return createDiff(path, diffs, fullDiff);
  }

  private String appendIndexToPath(String path) {
    return String.format(PROPERTY_PATH_TEMPLATE, path);
  }
}
