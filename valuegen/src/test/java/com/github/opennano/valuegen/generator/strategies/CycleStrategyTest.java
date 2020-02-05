package com.github.opennano.valuegen.generator.strategies;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import java.util.Collection;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CycleStrategyTest {

  @ParameterizedTest
  @ValueSource(classes = {int.class, String.class, Object.class, Collection.class})
  public void nullDescendentTest(Class<?> type) {
    assertThat(CycleStrategy.NULL_DESCENDENT.apply(type, null), is(nullValue()));
  }

  @ParameterizedTest
  @ValueSource(classes = {int.class, String.class, Object.class, Collection.class})
  public void reuseAncestorValueTest(Class<?> type) {
    Object mockParent = new Object();
    assertThat(CycleStrategy.REUSE_ANCESTOR_VALUE.apply(type, mockParent), is(mockParent));
  }
}
