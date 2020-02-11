package com.github.opennano.valueobject.junit;

import org.junit.jupiter.api.Test;

import com.github.opennano.testutils.ExceptionTestUtil;

public class FieldInjectionExceptionTest {

  @Test
  public void assertValidException() {
    ExceptionTestUtil.assertValidException(FieldInjectionException.class);
  }
}
