package com.github.opennano.reflectionassert.diffs;

import static com.github.opennano.reflectionassert.diffs.Diff.DiffType.PARTIAL;
import static com.github.opennano.reflectionassert.diffs.PartialDiff.PARTIAL_DIFF_TOKEN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.opennano.reflectionassert.diffs.PartialDiff;
import com.github.opennano.reflectionassert.exceptions.ReflectionAssertionInternalException;
import com.github.opennano.reflectionassert.testutils.SingletonTestUtil;

@ExtendWith(MockitoExtension.class)
public class PartialDiffTest {

  @Test
  public void assertValidSingleton() {
    SingletonTestUtil.assertIsNotInstantiable(PartialDiff.class);
  }

  @Test
  public void getType_returnsNull() {
    assertEquals(PARTIAL, PARTIAL_DIFF_TOKEN.getType());
  }

  @Test
  public void accept_throwsException() {
    assertThrows(
        ReflectionAssertionInternalException.class, () -> PARTIAL_DIFF_TOKEN.accept(null, null));
  }

  @Test
  public void cloneAndRepath_returnsSelf() {
    assertSame(PARTIAL_DIFF_TOKEN, PARTIAL_DIFF_TOKEN.cloneAndRepath(null, null));
  }
}
