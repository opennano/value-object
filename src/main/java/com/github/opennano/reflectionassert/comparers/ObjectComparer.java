package com.github.opennano.reflectionassert.comparers;

import static com.github.opennano.reflectionassert.diffs.NullDiff.NULL_TOKEN;
import static java.lang.reflect.Modifier.isStatic;
import static java.lang.reflect.Modifier.isTransient;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.opennano.reflectionassert.diffs.Diff;
import com.github.opennano.reflectionassert.diffs.PartialDiff;
import com.github.opennano.reflectionassert.diffs.SimpleDiff;
import com.github.opennano.reflectionassert.exceptions.ReflectionAssertionInternalException;
import com.github.opennano.reflectionassert.worker.ComparerManager;
import com.github.opennano.reflectionassert.worker.ValueComparer;

/** reflectively compares all field values between two objects */
public class ObjectComparer extends ValueComparer {

  private static final String DOT = ".";
  private static final String EMPTY = "";

  /**
   * @param expected the expected object
   * @param actual the actual object
   * @return true for any two (non-null) objects
   */
  @Override
  public boolean canCompare(Object expected, Object actual) {
    return expected != null && actual != null;
  }

  /**
   * Compare two non-null objects of the same type by iterating over all their fields--including
   * superclass fields--comparing their values. Static and transient fields are ignored. Returns
   * null if both objects are equal.
   *
   * @param path the path so far (from root down to the objects being compared)
   * @param expected the expected object
   * @param actual the actual object
   * @param comparer used when recursion is necessary on child objects
   * @param fullDiff when false comparison should end at the first found difference, in which case a
   *     {@link PartialDiff#PARTIAL_DIFF_TOKEN} should be returned.
   */
  @Override
  public Diff compare(
      String path, Object expected, Object actual, ComparerManager comparer, boolean fullDiff) {

    // first check for different class types
    Class<?> expectedType = expected.getClass();
    Class<?> actualType = actual.getClass();
    if (!expectedType.isAssignableFrom(actualType)) {
      return new SimpleDiff(path, expectedType, actualType);
    }

    // compare all fields of the object using reflection
    List<Diff> fieldDiffs = compareFields(path, expected, actual, expectedType, comparer, fullDiff);
    return createDiff(path, fieldDiffs, fullDiff);
  }

  /**
   * Reflectively compare the values of all fields in the given objects.
   *
   * @param path the path so far (from root down to the objects being compared)
   * @param expected the expected object
   * @param actual the actual object
   * @param type the parent object's class
   * @param comparer used when recursion is necessary on child objects
   * @param fullDiff when false comparison should end at the first found difference, in which case a
   *     {@link PartialDiff#PARTIAL_DIFF_TOKEN} should be returned.
   */
  private List<Diff> compareFields(
      String path,
      Object expected,
      Object actual,
      Class<?> type,
      ComparerManager comparer,
      boolean fullDiff) {

    return listFields(type)
        .stream()
        .map(field -> getFieldDiff(path, field, expected, actual, comparer, fullDiff))
        .filter(diff -> diff != NULL_TOKEN)
        .collect(Collectors.toList());
  }

  private List<Field> listFields(Class<?> baseType) {
    List<Field> allFields = new ArrayList<>();
    Class<?> type = baseType;
    do {
      allFields.addAll(
          Stream.of(type.getDeclaredFields())
              .filter(field -> !isIgnoredType(field))
              .collect(Collectors.toList()));
      type = type.getSuperclass();
    } while (type != null && type != Object.class);

    // make any private fields public (in one call for best performance)
    AccessibleObject.setAccessible(allFields.stream().toArray(Field[]::new), true);
    return allFields;
  }

  private boolean isIgnoredType(Field field) {
    int modifiers = field.getModifiers();
    return isTransient(modifiers) || isStatic(modifiers) || field.isSynthetic();
  }

  private Diff getFieldDiff(
      String parentPath,
      Field field,
      Object expected,
      Object actual,
      ComparerManager comparer,
      boolean fullDiff) {

    String fieldName = field.getName();
    String path = String.join(parentPath.length() > 1 ? DOT : EMPTY, parentPath, fieldName);
    try {
      return comparer.getDiff(path, field.get(expected), field.get(actual), fullDiff);
    } catch (IllegalArgumentException | IllegalAccessException e) {
      throw new ReflectionAssertionInternalException(
          "error reflectively comparing field: " + field, e);
    }
  }
}
