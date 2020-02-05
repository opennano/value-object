package com.github.opennano.valuegen.utils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.opennano.valuegen.testutils.SingletonTestUtil;

@ExtendWith(MockitoExtension.class)
public class AutoboxingUtilTest {

  @Test
  public void boxedEquivalent_convertAllUnboxedTypes() {
    assertThat(AutoboxingUtil.boxedEquivalent(boolean.class), equalTo(Boolean.class));
    assertThat(AutoboxingUtil.boxedEquivalent(char.class), equalTo(Character.class));
    assertThat(AutoboxingUtil.boxedEquivalent(byte.class), equalTo(Byte.class));
    assertThat(AutoboxingUtil.boxedEquivalent(short.class), equalTo(Short.class));
    assertThat(AutoboxingUtil.boxedEquivalent(int.class), equalTo(Integer.class));
    assertThat(AutoboxingUtil.boxedEquivalent(long.class), equalTo(Long.class));
    assertThat(AutoboxingUtil.boxedEquivalent(float.class), equalTo(Float.class));
    assertThat(AutoboxingUtil.boxedEquivalent(double.class), equalTo(Double.class));
  }

  @Test
  public void assertSingleton() {
    SingletonTestUtil.assertIsNotInstantiable(AutoboxingUtil.class);
  }
}
