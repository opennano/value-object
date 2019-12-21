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

  private Object expectedValue;
  private Object actualValue;

  public Diff(String path, Object expectedValue, Object actualValue) {
    this.path = path;
    this.expectedValue = expectedValue;
    this.actualValue = actualValue;
  }

  public String getPath() {
    return path;
  }

  public Object getExpectedValue() {
    return expectedValue;
  }

  public Object getActualValue() {
    return actualValue;
  }

  public abstract DiffType getType();

  /**
   * The provided default behavior is for "terminal" diff nodes that represent a single diff to
   * report to the user. For "internal" diffs in the tree this method should be overridden to
   * provide the logic for visiting each child.
   *
   * @param view an object that collects information about differences found
   * @param visitor an object implementing the visitor pattern that visits all diff nodes in a tree
   */
  public void accept(DiffView view, DiffVisitor visitor) {
    view.formatDiff(this);
  }

  /**
   * Used when a diff is found in a cache, this method will create a new subtree of diffs identical
   * to the other tree, but with a different root path.
   *
   * @param originalRootPath the part of the path that needs to be replaced on all child nodes
   * @param newRootPath the new base path to set on all child nodes
   * @return a new diff object with a new path but the same expected and actual values
   */
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
