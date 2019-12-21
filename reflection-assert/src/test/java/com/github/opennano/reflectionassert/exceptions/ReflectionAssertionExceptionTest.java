package com.github.opennano.reflectionassert.exceptions;

import org.junit.jupiter.api.Test;

import com.github.opennano.reflectionassert.exceptions.ReflectionAssertionException;
import com.github.opennano.reflectionassert.testutils.ExceptionTestUtil;

public class ReflectionAssertionExceptionTest {

  @Test
  public void validateException() {
    ExceptionTestUtil.assertValidException(ReflectionAssertionException.class);
  }
}
