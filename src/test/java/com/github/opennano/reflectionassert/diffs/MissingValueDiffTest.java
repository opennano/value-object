package com.github.opennano.reflectionassert.diffs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.opennano.reflectionassert.diffs.Diff;
import com.github.opennano.reflectionassert.diffs.MissingValueDiff;
import com.github.opennano.reflectionassert.diffs.Diff.DiffType;

@ExtendWith(MockitoExtension.class)
public class MissingValueDiffTest {

  @Mock private Object mockValue;

  @Test
  public void assertConstructorAndGettersAreValid() {
    Diff diff = new MissingValueDiff("mockPath", mockValue);
    assertEquals("mockPath", diff.getPath());
    assertSame(mockValue, diff.getExpectedValue());
    assertNull(diff.getActualValue());
  }

  @Test
  public void getType() {
    assertEquals(DiffType.MISSING, new MissingValueDiff("mockPath", mockValue).getType());
  }
}
