package com.github.opennano.reflectionassert.diffs;

/**
 * represents a difference between two items in a collection, map, or array where an unexpected
 * value was present
 */
public final class UnexpectedValueDiff extends Diff {

  public UnexpectedValueDiff(String path, Object right) {
    super(path, null, right);
  }

  @Override
  public DiffType getType() {
    return DiffType.UNEXPECTED;
  }
}
