package com.github.opennano.reflectionassert.worker;

import static com.github.opennano.reflectionassert.diffs.NullDiff.NULL_TOKEN;
import static com.github.opennano.reflectionassert.diffs.PartialDiff.PARTIAL_DIFF_TOKEN;

import java.util.List;
import java.util.stream.Stream;

import com.github.opennano.reflectionassert.diffs.Diff;
import com.github.opennano.reflectionassert.diffs.ParentDiff;
import com.github.opennano.reflectionassert.diffs.PartialDiff;

/**
 * Instances of this class can compare two objects and produce a {@link Diff}.
 *
 * <p>One should always first call {@link #canCompare(Object, Object)} to ensure comparison is
 * supported by this delegate before calling {@link #compare(String, Object, Object,
 * ComparerManager, boolean)}.
 */
public abstract class ValueComparer {

  /**
   * Call this method before calling {@link #compare(String, Object, Object, ComparerManager,
   * boolean)}.
   *
   * @param expected the expected object
   * @param actual the actual object
   * @return false if this comparer should not be used to compare the given objects
   */
  public abstract boolean canCompare(Object expected, Object actual);

  /**
   * Compares the given objects and returns the diffs, or a cacheable null token if there are none.
   * The comparer parameter is used to perform deeper comparisons as needed (e.g. comparing fields
   * within objects or items in a collection).
   *
   * @param path the path so far (from root down to the objects being compared)
   * @param expected the expected object
   * @param actual the actual object
   * @param comparer used when recursion is necessary on child objects
   * @param fullDiff when false comparison should end at the first found difference, in which case a
   *     {@link PartialDiff#PARTIAL_DIFF_TOKEN} should be returned.
   * @return a Diff containing the results of the comparison
   */
  public abstract Diff compare(
      String path, Object expected, Object actual, ComparerManager comparer, boolean fullDiff);

  /**
   * Factory method that creates a parent diff if appropriate, otherwise a token.
   *
   * @param path the path so far (from root down to the objects being compared)
   * @param childDiffs the differences detected in children of the current node
   * @param fullDiff when false and there are differences a {@link PartialDiff#PARTIAL_DIFF_TOKEN}
   *     is returned instead of a parent diff.
   * @return a new subtype of Diff appropriate for the types of parameters provided
   */
  protected Diff createDiff(String path, List<Diff> childDiffs, boolean fullDiff) {
    if (childDiffs.isEmpty()) {
      return NULL_TOKEN;
    }
    return fullDiff ? new ParentDiff(path, childDiffs) : PARTIAL_DIFF_TOKEN;
  }

  protected boolean areBothOneOfTheseTypes(Object expected, Object actual, Class<?>... types) {
    if (expected == null || actual == null) {
      return false;
    }
    return Stream.of(types).anyMatch(type -> areBothSameType(type, expected, actual));
  }

  protected boolean areBothSameType(Class<?> type, Object... value) {
    return Stream.of(value).allMatch(val -> type.isAssignableFrom(val.getClass()));
  }
}
