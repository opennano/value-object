package com.github.opennano.reflectionassert.comparers;

import static com.github.opennano.reflectionassert.diffs.NullDiff.NULL_TOKEN;

import java.util.Calendar;
import java.util.Date;
import java.util.stream.Stream;

import com.github.opennano.reflectionassert.LeniencyMode;
import com.github.opennano.reflectionassert.diffs.Diff;
import com.github.opennano.reflectionassert.diffs.SimpleDiff;
import com.github.opennano.reflectionassert.worker.ComparerManager;
import com.github.opennano.reflectionassert.worker.ValueComparer;

/**
 * A comparer that only checks whether two dates or calendars are both null or not null--the value
 * is not compared.
 *
 * <p>See {@link LeniencyMode#LENIENT_DATES}.
 */
public class LenientDateComparer extends ValueComparer {

  /**
   * Return true if both objects are instances of a date-like type, also ok if one is null.
   *
   * @param expected the expected object
   * @param actual the actual object
   */
  @Override
  public boolean canCompare(Object expected, Object actual) {
    if (expected == null) {
      return actual != null && isAnyType(actual, Date.class, Calendar.class);
    } else if (actual == null) {
      return isAnyType(expected, Date.class, Calendar.class);
    }
    return areBothOneOfTheseTypes(expected, actual, Date.class, Calendar.class);
  }

  private boolean isAnyType(Object value, Class<?>... types) {
    return Stream.of(types).anyMatch(type -> type.isAssignableFrom(value.getClass()));
  }

  /**
   * Compare the given dates, ignoring any diffs.
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

    return (expected == null || actual == null)
        ? new SimpleDiff(path, expected, actual)
        : NULL_TOKEN;
  }
}
