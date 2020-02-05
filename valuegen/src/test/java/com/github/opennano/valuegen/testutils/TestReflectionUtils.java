package com.github.opennano.valuegen.testutils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.FieldCallback;

public class TestReflectionUtils {
  private TestReflectionUtils() {
    // no-op: no instantiation
  }

  /**
   * Create a new instance of the specified type and set all it's fields to java defaults (i.e.
   * null, 0, or false depending on type). This convenience method assumes there is an available
   * no-args constructor on the target type, otherwise create your own instance and call {@link
   * #defaultObjectFill(Object)}.
   */
  public static <T> T defaultObjectFill(Class<T> type) {
    return defaultObjectFill(newInstance(type));
  }

  /**
   * Set all fields in the provided object to java defaults (i.e. null, 0, or false depending on
   * type).
   */
  public static <T> T defaultObjectFill(T target) {
    ReflectionUtils.doWithFields(target.getClass(), new DefaultSettingFieldVisitor(target));
    return target;
  }

  /**
   * Convenience method to easily create a new instance of the specified type.
   *
   * <p>The target class must define a no-arg constructor, though it may be private.
   */
  public static <T> T newInstance(Class<T> type) {
    try {
      Constructor<T> ctor = type.getDeclaredConstructor();
      ctor.setAccessible(true);
      return ctor.newInstance();
    } catch (ReflectiveOperationException e) {
      String msg = "failed to instantiate type '%s' via its no-arg constructor";
      throw new IllegalArgumentException(String.format(msg, type.getSimpleName()), e);
    }
  }

  /** Sets every field in the target object to a java default value (false, 0, or null) */
  private static final class DefaultSettingFieldVisitor implements FieldCallback {

    private Object target;

    private DefaultSettingFieldVisitor(Object target) {
      this.target = target;
    }

    @Override
    public void doWith(Field field) {
      if (field.isSynthetic()
          || Modifier.isFinal(field.getModifiers())
          || Modifier.isStatic(field.getModifiers())) {
        return;
      }

      Class<?> type = field.getType();
      ReflectionUtils.makeAccessible(field);
      if (type == boolean.class) {
        ReflectionUtils.setField(field, target, false);
      } else if (type == char.class) {
        ReflectionUtils.setField(field, target, Character.MIN_VALUE);
      } else if (type.isPrimitive()) {
        ReflectionUtils.setField(field, target, (byte) 0);
      } else {
        ReflectionUtils.setField(field, target, null);
      }
    }
  }
}
