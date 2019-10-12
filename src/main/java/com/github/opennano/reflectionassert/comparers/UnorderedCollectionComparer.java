package com.github.opennano.reflectionassert.comparers;

import static com.github.opennano.reflectionassert.diffs.NullDiff.NULL_TOKEN;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.github.opennano.reflectionassert.diffs.Diff;
import com.github.opennano.reflectionassert.diffs.MissingValueDiff;
import com.github.opennano.reflectionassert.diffs.UnexpectedValueDiff;
import com.github.opennano.reflectionassert.worker.ComparerManager;

/**
 * Compares collections and arrays. Since order doesn't matter diffs can't be strictly computed.
 * Diffs typically are presented as a pair of unmatched left and right elements.
 *
 * <p>Compared to an {@link OrderedCollectionComparer} performance of this comparer is far
 * worse--O(n^2) versus O(n).
 *
 * <p>TODO implement a separate (faster) cache for has any diff by CacheKey.
 */
public class UnorderedCollectionComparer extends CollectionComparer {

  private static final String PROPERTY_PATH_TEMPLATE = "%s[*]";

  /** compares the given collections or arrays */
  @Override
  public Diff compare(
      String path, Object left, Object right, ComparerManager comparer, boolean fullDiff) {

    // wrap in a new list for convenience
    List<?> leftList = asNewList(left);
    List<?> rightList = asNewList(right);

    String elementPath = appendIndexToPath(path);
    // compare all pairs of items in each collection, removing any that match
    for (Iterator<?> leftIterator = leftList.iterator();
        leftIterator.hasNext() && !rightList.isEmpty(); ) {

      Object leftItem = leftIterator.next();
      for (Iterator<?> rightIterator = rightList.iterator(); rightIterator.hasNext(); ) {
        if (comparer.getDiff(elementPath, leftItem, rightIterator.next(), false) == NULL_TOKEN) {
          // found matching element--remove from both collections
          leftIterator.remove();
          rightIterator.remove();
          break;
        }
      }
    }

    // all remaining elements are either missing or unexpected and are reported as diffs
    List<Diff> diffs = new ArrayList<>();
    leftList.forEach(item -> diffs.add(new MissingValueDiff(elementPath, item)));
    rightList.forEach(item -> diffs.add(new UnexpectedValueDiff(elementPath, item)));

    return createDiff(path, diffs, fullDiff);
  }

  private String appendIndexToPath(String path) {
    return String.format(PROPERTY_PATH_TEMPLATE, path);
  }
}
