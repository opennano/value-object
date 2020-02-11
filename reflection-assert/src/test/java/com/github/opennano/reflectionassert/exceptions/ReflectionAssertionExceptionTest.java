package com.github.opennano.reflectionassert.exceptions;

import org.junit.jupiter.api.Test;

import com.github.opennano.testutils.ExceptionTestUtil;

public class ReflectionAssertionExceptionTest {

  @Test
  public void validateException() {
    ExceptionTestUtil.assertValidException(ReflectionAssertionException.class);
  }
}
