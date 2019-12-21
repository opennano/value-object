package com.github.opennano.reflectionassert.exceptions;

import org.junit.jupiter.api.Test;

import com.github.opennano.reflectionassert.exceptions.ReflectionAssertionInternalException;
import com.github.opennano.reflectionassert.testutils.ExceptionTestUtil;

public class ReflectionAssertionInternalExceptionTest {

  @Test
  public void validateException() {
    ExceptionTestUtil.assertValidException(ReflectionAssertionInternalException.class);
  }
}
