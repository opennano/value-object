package com.github.opennano.reflectionassert.diffs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.opennano.reflectionassert.diffs.Diff;
import com.github.opennano.reflectionassert.diffs.SimpleDiff;
import com.github.opennano.reflectionassert.diffs.Diff.DiffType;

@ExtendWith(MockitoExtension.class)
public class SimpleDiffTest {

  @Mock private Object mockValue1;
  @Mock private Object mockValue2;

  @Test
  public void assertConstructorAndGettersAreValid() {
    Diff diff = new SimpleDiff("mockPath", mockValue1, mockValue2);
    assertEquals("mockPath", diff.getPath());
    assertSame(mockValue1, diff.getLeftValue());
    assertSame(mockValue2, diff.getRightValue());
  }

  @Test
  public void getType() {
    assertEquals(DiffType.DIFFERENT, new SimpleDiff("mockPath", mockValue1, mockValue2).getType());
  }
}
