package com.github.opennano.reflectionassert;

/**
 * Leniency modes specify how to compare two values. No mode means strict comparison. Modes can be
 * combined.
 */
public enum LeniencyMode {

  /**
   * Ignore mismatches when the field value in the expected object is the Java default (null, 0,
   * false, etc.)
   */
  IGNORE_DEFAULTS,

  /**
   * Ignore mismatches in dates and other time-based objects, asserting only that values are both
   * null or both not null.
   */
  LENIENT_DATES,

  /** Ignore element order of collections and arrays */
  LENIENT_ORDER,
}
