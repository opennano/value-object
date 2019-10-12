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

  /** true if both objects are instances of a date-like type, also ok if one is null */
  @Override
  public boolean canCompare(Object left, Object right) {
    if (left == null) {
      return right != null && isAnyType(right, Date.class, Calendar.class);
    } else if (right == null) {
      return isAnyType(left, Date.class, Calendar.class);
    }
    return areBothOneOfTheseTypes(left, right, Date.class, Calendar.class);
  }

  private boolean isAnyType(Object value, Class<?>... types) {
    return Stream.of(types).anyMatch(type -> type.isAssignableFrom(value.getClass()));
  }

  /** compares the given dates, ignoring any diffs */
  @Override
  public Diff compare(
      String path, Object left, Object right, ComparerManager comparer, boolean fullDiff) {

    return (left == null || right == null) ? new SimpleDiff(path, left, right) : NULL_TOKEN;
  }
}
