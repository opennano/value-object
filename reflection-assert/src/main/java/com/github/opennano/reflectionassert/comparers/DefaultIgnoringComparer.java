package com.github.opennano.reflectionassert.comparers;

import static com.github.opennano.reflectionassert.diffs.NullDiff.NULL_TOKEN;

import com.github.opennano.reflectionassert.LeniencyMode;
import com.github.opennano.reflectionassert.diffs.Diff;
import com.github.opennano.reflectionassert.worker.ComparerManager;
import com.github.opennano.reflectionassert.worker.ValueComparer;

/**
 * A comparer that ignores all diffs when the expected value is a Java default (null, 0, false,
 * etc.).
 *
 * <p>@see {@link LeniencyMode#IGNORE_DEFAULTS}.
 */
public class DefaultIgnoringComparer extends ValueComparer {

  /**
   * @param expected the expected object
   * @param actual the actual object
   * @return true if the expected object is a Java default
   */
  @Override
  public boolean canCompare(Object expected, Object actual) {
    // handle objects whose expected value is null
    if (expected == null) {
      return true;
    }
    // handle primitive booleans whose expected value is false
    if (expected instanceof Boolean && !(Boolean) expected) {
      return true;
    }
    // handle primitive chars whose expected value is the null character
    if (expected instanceof Character && (Character) expected == Character.MIN_VALUE) {
      return true;
    }
    // handle primitive numeric types whose expected value is 0
    if (expected instanceof Number && ((Number) expected).doubleValue() == 0d) {
      return true;
    }
    return false;
  }

  /**
   * Always return the null token--this comparer never creates real diffs.
   *
   * @param path unused
   * @param expected unused
   * @param actual unused
   * @param comparer unused
   * @param fullDiff unused
   */
  @Override
  public Diff compare(
      String path, Object expected, Object actual, ComparerManager comparer, boolean fullDiff) {

    return NULL_TOKEN;
  }
}
