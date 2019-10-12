package com.github.opennano.reflectionassert.diffs;

import com.github.opennano.reflectionassert.exceptions.ReflectionAssertionInternalException;
import com.github.opennano.reflectionassert.report.DiffView;
import com.github.opennano.reflectionassert.report.DiffVisitor;

/** this kind of diff is just a caching hint and should never be used in a report */
public class PartialDiff extends Diff {

  public static PartialDiff PARTIAL_DIFF_TOKEN = newInstance();

  private static PartialDiff newInstance() {
    return new PartialDiff();
  }

  private PartialDiff() { // singleton
    super(null, null, null);
  }

  @Override
  public DiffType getType() {
    return DiffType.PARTIAL;
  }

  public void accept(DiffView view, DiffVisitor diffVisitor) {
    throw new ReflectionAssertionInternalException("invalid attempt to visit a partial diff node");
  }

  public PartialDiff cloneAndRepath(String originalRootPath, String newRootPath) {
    return this;
  }
}
