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
 * <p>See {@link LeniencyMode#IGNORE_DEFAULTS}.
 */
public class DefaultIgnoringComparer extends ValueComparer {

  /** returns true if the left object is a java default */
  @Override
  public boolean canCompare(Object left, Object right) {
    // handle objects whose expected value is null
    if (left == null) {
      return true;
    }
    // handle primitive booleans whose expected value is false
    if (left instanceof Boolean && !(Boolean) left) {
      return true;
    }
    // handle primitive chars whose expected value is the null character
    if (left instanceof Character && (Character) left == Character.MIN_VALUE) {
      return true;
    }
    // handle primitive numeric types whose expected value is 0
    if (left instanceof Number && ((Number) left).doubleValue() == 0d) {
      return true;
    }
    return false;
  }

  /** always returns the null token--this comparer never creates real diffs */
  @Override
  public Diff compare(
      String name, Object left, Object right, ComparerManager comparer, boolean fullDiff) {

    return NULL_TOKEN;
  }
}
