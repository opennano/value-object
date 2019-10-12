package com.github.opennano.reflectionassert.diffs;

import java.util.List;
import java.util.stream.Collectors;

import com.github.opennano.reflectionassert.report.DiffView;
import com.github.opennano.reflectionassert.report.DiffVisitor;

/** represents an internal node in a diff tree */
public final class ParentDiff extends Diff {

  private List<Diff> childDiffs;

  public ParentDiff(String path, List<Diff> childDiffs) {
    super(path, null, null);
    this.childDiffs = childDiffs;
  }

  @Override
  public DiffType getType() {
    return null;
  }

  public ParentDiff cloneAndRepath(String originalRootPath, String newRootPath) {
    // recursively clones all nodes under this one, updating the base path along the way
    ParentDiff clone = (ParentDiff) super.cloneAndRepath(originalRootPath, newRootPath);
    clone.childDiffs =
        childDiffs
            .stream()
            .map(diff -> diff.cloneAndRepath(originalRootPath, newRootPath))
            .collect(Collectors.toList());
    return clone;
  }

  @Override
  public void accept(DiffView view, DiffVisitor visitor) {
    childDiffs.forEach(diff -> visitor.visit(diff));
  }
}
