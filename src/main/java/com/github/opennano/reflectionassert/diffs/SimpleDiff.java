package com.github.opennano.reflectionassert.diffs;

/** represents a difference between two values */
public final class SimpleDiff extends Diff {

  public SimpleDiff(String path, Object left, Object right) {
    super(path, left, right);
  }

  @Override
  public DiffType getType() {
    return DiffType.DIFFERENT;
  }
}
