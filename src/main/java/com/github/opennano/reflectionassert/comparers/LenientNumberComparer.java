package com.github.opennano.reflectionassert.comparers;

import static com.github.opennano.reflectionassert.diffs.NullDiff.NULL_TOKEN;

import com.github.opennano.reflectionassert.diffs.Diff;
import com.github.opennano.reflectionassert.diffs.SimpleDiff;
import com.github.opennano.reflectionassert.worker.ComparerManager;
import com.github.opennano.reflectionassert.worker.ValueComparer;

/** a comparer that can compare any subclass of {@link Number} */
public class LenientNumberComparer extends ValueComparer {

  @Override
  public boolean canCompare(Object left, Object right) {
    return areBothOneOfTheseTypes(left, right, Number.class);
  }

  /** compares two numeric values */
  @Override
  public Diff compare(
      String path, Object left, Object right, ComparerManager comparer, boolean fullDiff) {

    return asDouble(left).equals(asDouble(right)) ? NULL_TOKEN : new SimpleDiff(path, left, right);
  }

  private Double asDouble(Object value) {
    return ((Number) value).doubleValue();
  }
}
