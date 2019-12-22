package com.github.opennano.valuegen.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class AutoboxingUtil {

  private static final Map<Class<?>, Class<?>> BOXED_TYPES_BY_PRIMITIVE_TYPE;

  static {
    Map<Class<?>, Class<?>> typeMap = new HashMap<>();
    typeMap.put(boolean.class, Boolean.class);
    typeMap.put(char.class, Character.class);
    typeMap.put(byte.class, Byte.class);
    typeMap.put(short.class, Short.class);
    typeMap.put(int.class, Integer.class);
    typeMap.put(long.class, Long.class);
    typeMap.put(float.class, Float.class);
    typeMap.put(double.class, Double.class);

    BOXED_TYPES_BY_PRIMITIVE_TYPE = Collections.unmodifiableMap(typeMap);
  }

  private AutoboxingUtil() {
    // no-op: no instantiation
  }

  public static Class<?> boxedEquivalent(Class<?> type) {
    return BOXED_TYPES_BY_PRIMITIVE_TYPE.get(type);
  }
}
