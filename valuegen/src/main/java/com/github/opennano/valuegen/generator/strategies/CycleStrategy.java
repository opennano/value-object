package com.github.opennano.valuegen.generator.strategies;

public interface CycleStrategy {

  static CycleStrategy NULL_DESCENDENT = (type, val) -> null;
  static CycleStrategy REUSE_ANCESTOR_VALUE = (type, val) -> val;

  Object apply(Class<?> valueClass, Object parentValue);
}
