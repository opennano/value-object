package com.github.opennano.reflectionassert.diffs;

import static com.github.opennano.reflectionassert.diffs.NullDiff.NULL_TOKEN;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.opennano.reflectionassert.exceptions.ReflectionAssertionInternalException;
import com.github.opennano.testutils.SingletonTestUtil;

@ExtendWith(MockitoExtension.class)
public class NullDiffTest {

  @Test
  public void assertValidSingleton() {
    SingletonTestUtil.assertIsNotInstantiable(NullDiff.class);
  }

  @Test
  public void getType_returnsNull() {
    assertNull(NULL_TOKEN.getType());
  }

  @Test
  public void accept_throwsException() {
    assertThrows(ReflectionAssertionInternalException.class, () -> NULL_TOKEN.accept(null, null));
  }

  @Test
  public void cloneAndRepath_returnsSelf() {
    assertSame(NULL_TOKEN, NULL_TOKEN.cloneAndRepath(null, null));
  }
}
