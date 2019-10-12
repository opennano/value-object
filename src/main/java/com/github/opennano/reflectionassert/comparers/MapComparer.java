package com.github.opennano.reflectionassert.comparers;

import static com.github.opennano.reflectionassert.diffs.NullDiff.NULL_TOKEN;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.github.opennano.reflectionassert.diffs.Diff;
import com.github.opennano.reflectionassert.diffs.MissingValueDiff;
import com.github.opennano.reflectionassert.diffs.UnexpectedValueDiff;
import com.github.opennano.reflectionassert.worker.ComparerManager;
import com.github.opennano.reflectionassert.worker.ValueComparer;

/**
 * A comparer for maps. This will compare all values with corresponding keys, where the equality of
 * keys is the usual map sematics (i.e. equals and hashcode).
 */
public class MapComparer extends ValueComparer {

  private static final String PROPERTY_PATH_TEMPLATE = "%s{%s}";

  /** returns true when both objects are maps. */
  @Override
  public boolean canCompare(Object left, Object right) {
    return areBothOneOfTheseTypes(left, right, Map.class);
  }

  /**
   * Compares the given maps by looping over the keys and comparing their values. The keys are
   * compared using standard map semantics (equals and hashcode), while values are compared with
   * reflection.
   */
  @Override
  public Diff compare(
      String path, Object left, Object right, ComparerManager comparer, boolean fullDiff) {
    // create copies so we can remove elements.
    Map<Object, Object> leftMap = new HashMap<>((Map<?, ?>) left);
    Map<Object, Object> rightMap = new HashMap<>((Map<?, ?>) right);

    List<Diff> diffs = new ArrayList<>();
    for (Iterator<Map.Entry<Object, Object>> leftIter = leftMap.entrySet().iterator();
        leftIter.hasNext(); ) {

      Map.Entry<Object, Object> leftEntry = leftIter.next();
      Object leftKey = leftEntry.getKey();
      Object leftValue = leftEntry.getValue();
      String childPath = appendKeyToPath(path, leftKey);

      // remove corresponding match from the right map, if any
      Object rightValue = rightMap.remove(leftKey);
      if (rightValue == null) {
        // left has a key but right doesn't
        diffs.add(new MissingValueDiff(childPath, leftValue));
      } else {
        // key present in both--compare values
        Diff valueDiff = comparer.getDiff(childPath, leftValue, rightValue, fullDiff);
        if (valueDiff != NULL_TOKEN) {
          diffs.add(valueDiff);
        }
      }
    }

    // any item that matched a left entry has been removed--the rest are unexpected
    rightMap.entrySet().forEach(entry -> addUnexpectedItemDiff(path, entry, diffs));

    return createDiff(path, diffs, fullDiff);
  }

  private String appendKeyToPath(String name, Object key) {
    // would it be better to use reflection to describe the key?
    // an ideal solution would be to print only the values used in equals and hashcode
    // not sure how that could be done though
    // non-primitive keys will always be pretty ugly in any case
    return String.format(PROPERTY_PATH_TEMPLATE, name, key.toString());
  }

  private void addUnexpectedItemDiff(String name, Entry<?, ?> entry, List<Diff> diffs) {
    String childPath = appendKeyToPath(name, entry.getKey());
    diffs.add(new UnexpectedValueDiff(childPath, entry.getValue()));
  }
}
