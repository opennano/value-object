package com.github.opennano.reflectionassert.diffs;

/**
 * represents a difference between two items in a collection, map, or array where an expected value
 * was missing
 */
public final class MissingValueDiff extends Diff {

  public MissingValueDiff(String path, Object left) {
    super(path, left, null);
  }
  
  public DiffType getType() {
	  return DiffType.MISSING;
  }
}
