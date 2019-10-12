package com.github.opennano.reflectionassert.report;

import com.github.opennano.reflectionassert.diffs.Diff;
import com.github.opennano.reflectionassert.worker.ComparerManager;

/**
 * This class visits the tree of {@link Diff} objects produced by a {@link ComparerManager}.
 * While visiting each diff the full property path to that diff is provided, so that a flattened
 * list of diffs can be provided to the end user. Also provided is a {@link DiffView} used by the
 * visited diffs to aggregate text results suitable for display to the end user.
 *
 * <p>An instance of this class is passed to the accept method so that internal nodes in the diff
 * tree can themselves determine how to visit their child diffs.
 */
public class DiffVisitor {

  private DiffView view;

  public DiffVisitor(DiffView view) {
    this.view = view;
  }

  public void visit(Diff diffNode) {
    diffNode.accept(view, this);
  }
}
