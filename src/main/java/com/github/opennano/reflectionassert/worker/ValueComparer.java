package com.github.opennano.reflectionassert.worker;

import static com.github.opennano.reflectionassert.diffs.NullDiff.NULL_TOKEN;
import static com.github.opennano.reflectionassert.diffs.PartialDiff.PARTIAL_DIFF_TOKEN;

import java.util.List;
import java.util.stream.Stream;

import com.github.opennano.reflectionassert.diffs.Diff;
import com.github.opennano.reflectionassert.diffs.ParentDiff;

/**
 * Interface for comparing two objects, producing a {@link Diff}.
 *
 * <p>One should always first call {@link #canCompare(Object, Object)} to ensure comparison is
 * supported by this delegate before calling {@link #compare(Object, Object, ComparerManager)}.
 */
public abstract class ValueComparer {

  /**
   * Call this method before calling {@link #compare(Object, Object, ComparerManager)}. If this
   * method returns false this comparer should not be used to compare the given objects.
   */
  public abstract boolean canCompare(Object left, Object right);

  /**
   * Compares the given objects and returns the diffs, or a cacheable null token if there are none.
   * The comparer parameter is used to perform deeper comparisons as needed (e.g. comparing fields
   * within objects or items in a collection).
   */
  public abstract Diff compare(
      String path, Object left, Object right, ComparerManager comparer, boolean fullDiff);

  /** factory method that creates a parent diff if appropriate, otherwise a token */
  protected Diff createDiff(String path, List<Diff> fieldDiffs, boolean fullDiff) {
    if (fieldDiffs.isEmpty()) {
      return NULL_TOKEN;
    }
    return fullDiff ? new ParentDiff(path, fieldDiffs) : PARTIAL_DIFF_TOKEN;
  }

  protected boolean areBothOneOfTheseTypes(Object left, Object right, Class<?>... types) {
    if (left == null || right == null) {
      return false;
    }
    return Stream.of(types).anyMatch(type -> areBothSameType(type, left, right));
  }

  protected boolean areBothSameType(Class<?> type, Object... value) {
    return Stream.of(value).allMatch(val -> type.isAssignableFrom(val.getClass()));
  }
}
