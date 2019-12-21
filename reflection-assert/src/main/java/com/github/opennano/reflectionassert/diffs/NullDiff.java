package com.github.opennano.reflectionassert.diffs;

import com.github.opennano.reflectionassert.exceptions.ReflectionAssertionInternalException;
import com.github.opennano.reflectionassert.report.DiffView;
import com.github.opennano.reflectionassert.report.DiffVisitor;

public class NullDiff extends Diff {

  public static NullDiff NULL_TOKEN = newInstance();

  private static NullDiff newInstance() {
    return new NullDiff();
  }

  private NullDiff() { // singleton
    super(null, null, null);
  }

  @Override
  public DiffType getType() {
    return null;
  }

  public void accept(DiffView view, DiffVisitor diffVisitor) {
    throw new ReflectionAssertionInternalException("invalid attempt to visit a null token node");
  }

  public NullDiff cloneAndRepath(String originalRootPath, String newRootPath) {
    return this;
  }
}
