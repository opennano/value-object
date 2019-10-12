package com.github.opennano.reflectionassert.comparers;

import static com.github.opennano.reflectionassert.diffs.NullDiff.NULL_TOKEN;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Stream;

import com.github.opennano.reflectionassert.diffs.Diff;
import com.github.opennano.reflectionassert.diffs.SimpleDiff;
import com.github.opennano.reflectionassert.exceptions.ReflectionAssertionInternalException;
import com.github.opennano.reflectionassert.worker.ComparerManager;
import com.github.opennano.reflectionassert.worker.ValueComparer;

/** compares simple value types using standard {@link Object#equals(Object)} semantics */
public class SimpleComparer extends ValueComparer {

  private static final String JAVA_LANG = Object.class.getPackage().getName();

  @Override
  public boolean canCompare(Object left, Object right) {
    return left == right
        || left == null
        || right == null
        || areEitherJavaLang(left, right)
        || areBothOneOfTheseTypes(left, right, Enum.class, Date.class, Calendar.class, File.class);
  }

  @Override
  public Diff compare(
      String path, Object left, Object right, ComparerManager comparer, boolean fullDiff) {

    // if they're the same instance (or both null) there can't be a diff
    if (left == right) {
      return NULL_TOKEN;
    }

    if (left == null || right == null) {
      return new SimpleDiff(path, left, right);
    }

    // if they're both not null but their classes are incompatible it's a class diff
    if (!(left.getClass().isAssignableFrom(right.getClass()))) {
      return new SimpleDiff(path, left.getClass(), right.getClass());
    }

    // handle comparing any two values using equals if we can
    // comparable values may be null
    return compare(path, getComparableValue(left), getComparableValue(right), left, right);
  }

  private Diff compare(
      String path, Object left, Object right, Object originalLeft, Object originalRight) {

    return left.equals(right) ? NULL_TOKEN : new SimpleDiff(path, originalLeft, originalRight);
  }

  private Object getComparableValue(Object value) {
    if (value instanceof Number) {
      return ((Number) value).doubleValue();
    }
    if (value instanceof CharSequence
        || value instanceof Enum
        || value instanceof Date
        || value instanceof Calendar
        || value instanceof File
        || isJavaLang(value)) {
      return value;
    }
    throw new ReflectionAssertionInternalException(
        "this comparer doesn't support type: " + value.getClass());
  }

  private boolean areEitherJavaLang(Object left, Object right) {
    return Stream.of(left, right).anyMatch(this::isJavaLang);
  }

  private boolean isJavaLang(Object value) {
    return Optional.ofNullable(value)
        .map(Object::getClass)
        .map(Class::getPackage)
        .map(Package::getName)
        .map(name -> name.startsWith(JAVA_LANG))
        .orElse(false);
  }
}
