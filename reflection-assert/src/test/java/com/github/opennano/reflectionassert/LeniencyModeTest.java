package com.github.opennano.reflectionassert;

import org.junit.jupiter.api.Test;

import com.github.opennano.testutils.EnumTestUtil;

public class LeniencyModeTest {

  @Test
  public void enumCodeCoverage() {
    EnumTestUtil.assertEnumValid(LeniencyMode.class);
  }
}
