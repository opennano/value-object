package com.github.opennano.reflectionassert.exceptions;

import org.junit.jupiter.api.Test;

import com.github.opennano.reflectionassert.exceptions.ReflectionAssertionInputException;
import com.github.opennano.reflectionassert.testutils.ExceptionTestUtil;

public class ReflectionAssertionInputExceptionTest {

  @Test
  public void validateException() {
    ExceptionTestUtil.assertValidException(ReflectionAssertionInputException.class);
  }
}
