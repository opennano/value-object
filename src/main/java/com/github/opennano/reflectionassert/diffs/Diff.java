package com.github.opennano.reflectionassert.diffs;

import com.github.opennano.reflectionassert.exceptions.ReflectionAssertionInternalException;
import com.github.opennano.reflectionassert.report.DiffView;
import com.github.opennano.reflectionassert.report.DiffVisitor;

/** instances of this class represent a difference between two values */
public abstract class Diff implements Cloneable {

  public static enum DiffType {
    DIFFERENT,
    MISSING,
    UNEXPECTED,
    PARTIAL
  }

  /**
   * the part of the full property path corresponding to just this node, e.g. 'myInt' in
   * '[0].myValue.myInt'
   */
  private String path;

  private Object leftValue;
  private Object rightValue;

  public Diff(String path, Object leftValue, Object rightValue) {
    this.path = path;
    this.leftValue = leftValue;
    this.rightValue = rightValue;
  }

  public String getPath() {
    return path;
  }

  public Object getLeftValue() {
    return leftValue;
  }

  public Object getRightValue() {
    return rightValue;
  }

  public abstract DiffType getType();

  /**
   * The provided default behavior is for "terminal" diff nodes that represent a single diff to
   * report to the user. For "internal" diffs in the tree this method should be overridden to
   * provide the logic for visiting each child.
   */
  public void accept(DiffView view, DiffVisitor diffVisitor) {
    view.formatDiff(this);
  }

  public Diff cloneAndRepath(String originalRootPath, String newRootPath) {
    try {
      Diff clone = clone();
      clone.path = clone.path.replace(originalRootPath, newRootPath);
      return clone;
    } catch (CloneNotSupportedException e) {
      throw new ReflectionAssertionInternalException("unexpected error cloning diff", e);
    }
  }

  @Override
  protected Diff clone() throws CloneNotSupportedException {
    return (Diff) super.clone();
  }
}
