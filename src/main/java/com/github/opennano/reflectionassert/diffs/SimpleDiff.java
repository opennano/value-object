package com.github.opennano.reflectionassert.diffs;

/** represents a difference between two values */
public final class SimpleDiff extends Diff {

  public SimpleDiff(String path, Object expected, Object actual) {
    super(path, expected, actual);
  }

  @Override
  public DiffType getType() {
    return DiffType.DIFFERENT;
  }
}
