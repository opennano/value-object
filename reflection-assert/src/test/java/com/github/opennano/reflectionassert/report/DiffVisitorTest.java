package com.github.opennano.reflectionassert.report;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.springframework.test.util.ReflectionTestUtils.getField;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.opennano.reflectionassert.diffs.Diff;
import com.github.opennano.reflectionassert.report.DiffView;
import com.github.opennano.reflectionassert.report.DiffVisitor;

@ExtendWith(MockitoExtension.class)
public class DiffVisitorTest {

  @InjectMocks private DiffVisitor visitor;
  @Mock private DiffView mockDiffView;
  @Mock private Diff mockDiff;

  @Test
  public void ctor() {
    DiffVisitor visitor = new DiffVisitor(mockDiffView);
    assertSame(mockDiffView, getField(visitor, "view"));
  }

  @Test
  public void visit() {
    visitor.visit(mockDiff);

    verify(mockDiff).accept(mockDiffView, visitor);
  }
}
