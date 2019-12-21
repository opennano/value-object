package com.github.opennano.reflectionassert.diffs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.github.opennano.reflectionassert.diffs.Diff;
import com.github.opennano.reflectionassert.diffs.ParentDiff;
import com.github.opennano.reflectionassert.report.DiffVisitor;

@ExtendWith(MockitoExtension.class)
public class ParentDiffTest {

  @Mock private Diff mockDiff1;
  @Mock private Diff mockDiff2;
  @Mock private DiffVisitor mockDiffVisitor;

  @Test
  public void getType() {
    assertNull(new ParentDiff("mockPath", null).getType());
  }

  @Test
  public void cloneAndRepath() {
    ParentDiff diff = new ParentDiff("mockPath", Arrays.asList(mockDiff1));

    when(mockDiff1.cloneAndRepath("mockPath", "newPath")).thenReturn(mockDiff2);

    ParentDiff clone = diff.cloneAndRepath("mockPath", "newPath");

    assertEquals(Arrays.asList(mockDiff2), ReflectionTestUtils.getField(clone, "childDiffs"));
  }

  @Test
  public void accept() {
    ParentDiff diff = new ParentDiff("mockPath", Arrays.asList(mockDiff1));
    diff.accept(null, mockDiffVisitor);

    verify(mockDiffVisitor).visit(mockDiff1);
  }
}
