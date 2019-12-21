package com.github.opennano.reflectionassert.comparers;

import static com.github.opennano.reflectionassert.diffs.NullDiff.NULL_TOKEN;

import com.github.opennano.reflectionassert.diffs.Diff;
import com.github.opennano.reflectionassert.diffs.SimpleDiff;
import com.github.opennano.reflectionassert.worker.ComparerManager;
import com.github.opennano.reflectionassert.worker.ValueComparer;

/** a comparer that can compare any subclass of {@link Number} */
public class LenientNumberComparer extends ValueComparer {

  @Override
  public boolean canCompare(Object expected, Object actual) {
    return areBothOneOfTheseTypes(expected, actual, Number.class);
  }

  /**
   * Compare two numeric values.
   *
   * @param path the path so far (from root down to the objects being compared)
   * @param expected the expected object
   * @param actual the actual object
   * @param comparer unused
   * @param fullDiff unused
   */
  @Override
  public Diff compare(
      String path, Object expected, Object actual, ComparerManager comparer, boolean fullDiff) {

    return asDouble(expected).equals(asDouble(actual))
        ? NULL_TOKEN
        : new SimpleDiff(path, expected, actual);
  }

  private Double asDouble(Object value) {
    return ((Number) value).doubleValue();
  }
}
