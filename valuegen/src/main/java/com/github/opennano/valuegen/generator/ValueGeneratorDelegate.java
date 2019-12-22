package com.github.opennano.valuegen.generator;

import java.lang.reflect.Field;
import java.util.stream.Stream;

import org.objenesis.ObjenesisStd;

public interface ValueGeneratorDelegate {

  static final ObjenesisStd objenesis = new ObjenesisStd();

  static boolean isInstanceOfAny(Class<?> type, Class<?>... acceptableTypes) {
    return Stream.of(acceptableTypes).anyMatch(okType -> okType.isAssignableFrom(type));
  }

  boolean handlesClass(Class<?> type);

  Object generateValue(
      TypeInfo typeInfo,
      Field declaringField,
      String nameHint,
      Class<?> owningClass,
      ValueObjectGenerator valueObjectGenerator);
}
