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
import com.github.opennano.reflectionassert.diffs.SimpleDiff;
import com.github.opennano.reflectionassert.exceptions.ReflectionAssertionInternalException;
import com.github.opennano.reflectionassert.worker.ComparerManager;
import com.github.opennano.reflectionassert.worker.ValueComparer;

/** reflectively compares all field values between two objects */
public class ObjectComparer extends ValueComparer {

  private static final String DOT = ".";
  private static final String EMPTY = "";

  /** can compare any non-null objects, should be last in the chain */
  @Override
  public boolean canCompare(Object left, Object right) {
    return left != null && right != null;
  }

  /**
   * Compares two non-null objects of the same type by iterating over all their fields--including
   * superclass fields--comparing their values. Static and transient fields are ignored. Returns
   * null if both objects are equal.
   */
  @Override
  public Diff compare(
      String path, Object left, Object right, ComparerManager comparer, boolean fullDiff) {

    // first check for different class types
    Class<?> leftType = left.getClass();
    Class<?> rightType = right.getClass();
    if (!leftType.isAssignableFrom(rightType)) {
      return new SimpleDiff(path, leftType, rightType);
    }

    // compare all fields of the object using reflection
    List<Diff> fieldDiffs = compareFields(path, left, right, leftType, comparer, fullDiff);
    return createDiff(path, fieldDiffs, fullDiff);
  }

  /** reflectively compares the values of all fields in the given objects */
  private List<Diff> compareFields(
      String path,
      Object left,
      Object right,
      Class<?> type,
      ComparerManager comparer,
      boolean fullDiff) {

    return listFields(type)
        .stream()
        .map(field -> getFieldDiff(path, field, left, right, comparer, fullDiff))
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
      Object left,
      Object right,
      ComparerManager comparer,
      boolean fullDiff) {

    String fieldName = field.getName();
    String path = String.join(parentPath.length() > 1 ? DOT : EMPTY, parentPath, fieldName);
    try {
      return comparer.getDiff(path, field.get(left), field.get(right), fullDiff);
    } catch (IllegalArgumentException | IllegalAccessException e) {
      throw new ReflectionAssertionInternalException(
          "error reflectively comparing field: " + field, e);
    }
  }
}
