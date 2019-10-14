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
import com.github.opennano.reflectionassert.diffs.PartialDiff;
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
  public boolean canCompare(Object expected, Object actual) {
    return areBothOneOfTheseTypes(expected, actual, Map.class);
  }

  /**
   * Compares the given maps by looping over the keys and comparing their values. The keys are
   * compared using standard map semantics (equals and hashcode), while values are compared with
   * reflection.
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
    // create copies so we can remove elements.
    Map<Object, Object> expectedMap = new HashMap<>((Map<?, ?>) expected);
    Map<Object, Object> actualMap = new HashMap<>((Map<?, ?>) actual);

    List<Diff> diffs = new ArrayList<>();
    for (Iterator<Map.Entry<Object, Object>> expectedIter = expectedMap.entrySet().iterator();
        expectedIter.hasNext(); ) {

      Map.Entry<Object, Object> expectedEntry = expectedIter.next();
      Object expectedKey = expectedEntry.getKey();
      Object expectedValue = expectedEntry.getValue();
      String childPath = appendKeyToPath(path, expectedKey);

      // remove corresponding match from the actual map, if any
      Object actualValue = actualMap.remove(expectedKey);
      if (actualValue == null) {
        // expected has a key but actual doesn't
        diffs.add(new MissingValueDiff(childPath, expectedValue));
      } else {
        // key present in both--compare values
        Diff valueDiff = comparer.getDiff(childPath, expectedValue, actualValue, fullDiff);
        if (valueDiff != NULL_TOKEN) {
          diffs.add(valueDiff);
        }
      }
    }

    // any item that matched a expected entry has been removed--the rest are unexpected
    actualMap.entrySet().forEach(entry -> addUnexpectedItemDiff(path, entry, diffs));

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
