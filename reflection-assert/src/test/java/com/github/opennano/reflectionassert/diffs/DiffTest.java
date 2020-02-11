package com.github.opennano.reflectionassert.diffs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.opennano.reflectionassert.diffs.Diff.DiffType;
import com.github.opennano.reflectionassert.exceptions.ReflectionAssertionInternalException;
import com.github.opennano.reflectionassert.report.DiffView;
import com.github.opennano.reflectionassert.report.DiffVisitor;
import com.github.opennano.testutils.EnumTestUtil;

@ExtendWith(MockitoExtension.class)
public class DiffTest {

  private static class StubDiff extends Diff {
    public StubDiff() {
      super(null, null, null);
    }

    public StubDiff(String path, Object expectedValue, Object actualValue) {
      super(path, expectedValue, actualValue);
    }

    @Override
    public DiffType getType() {
      return null;
    }
  }

  private static class UncloneableDiff extends StubDiff {
    public UncloneableDiff() {
      super(null, null, null);
    }

    @Override
    protected Diff clone() throws CloneNotSupportedException {
      throw new CloneNotSupportedException();
    }
  }

  @Mock private Object mockValue1;
  @Mock private Object mockValue2;
  @Mock private Diff mockDiff;
  @Mock private DiffView mockDiffView;
  @Mock private DiffVisitor mockDiffVisitor;

  @Test
  public void assertConstructorAndGettersAreValid() {
    Diff diff = new StubDiff("mockPath", mockValue1, mockValue2);
    assertEquals("mockPath", diff.getPath());
    assertSame(mockValue1, diff.getExpectedValue());
    assertSame(mockValue2, diff.getActualValue());
  }

  @Test
  public void assertInnerEnumIsValid() {
    EnumTestUtil.assertEnumValid(DiffType.class);
  }

  @Test
  public void accept() {
    StubDiff diff = new StubDiff();
    diff.accept(mockDiffView, mockDiffVisitor);

    verify(mockDiffView).formatDiff(diff);
  }

  @Test
  public void cloneAndRepath_ok() throws Exception {
    StubDiff diff = new StubDiff("mockPath.mockValue", mockValue1, mockValue2);

    Diff clone = diff.cloneAndRepath("mockPath", "newPath");
    assertEquals("newPath.mockValue", clone.getPath());
    assertSame(mockValue1, clone.getExpectedValue());
    assertSame(mockValue2, clone.getActualValue());
  }

  @Test
  public void cloneAndRepath_cloneNotSupported() {
    UncloneableDiff diff = new UncloneableDiff();
    assertThrows(ReflectionAssertionInternalException.class, () -> diff.cloneAndRepath(null, null));
  }

  @Test
  public void clone_isShallow() throws Exception {
    Diff diff = new StubDiff("mockPath", mockValue1, mockValue2);
    Diff clone = diff.clone();

    assertEquals(StubDiff.class, clone.getClass());
    assertEquals("mockPath", clone.getPath());
    assertSame(mockValue1, clone.getExpectedValue());
    assertSame(mockValue2, clone.getActualValue());
  }
}
