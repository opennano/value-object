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
  public boolean canCompare(Object expected, Object actual) {
    return expected == actual
        || expected == null
        || actual == null
        || areEitherJavaLang(expected, actual)
        || areBothOneOfTheseTypes(
            expected, actual, Enum.class, Date.class, Calendar.class, File.class);
  }

  @Override
  public Diff compare(
      String path, Object expected, Object actual, ComparerManager comparer, boolean fullDiff) {

    // if they're the same instance (or both null) there can't be a diff
    if (expected == actual) {
      return NULL_TOKEN;
    }

    if (expected == null || actual == null) {
      return new SimpleDiff(path, expected, actual);
    }

    // if they're both not null but their classes are incompatible it's a class diff
    if (!(expected.getClass().isAssignableFrom(actual.getClass()))) {
      return new SimpleDiff(path, expected.getClass(), actual.getClass());
    }

    // handle comparing any two values using equals if we can
    // comparable values may be null
    return compare(
        path, getComparableValue(expected), getComparableValue(actual), expected, actual);
  }

  private Diff compare(
      String path, Object expected, Object actual, Object originalExpected, Object originalActual) {

    return expected.equals(actual)
        ? NULL_TOKEN
        : new SimpleDiff(path, originalExpected, originalActual);
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

  private boolean areEitherJavaLang(Object expected, Object actual) {
    return Stream.of(expected, actual).anyMatch(this::isJavaLang);
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
