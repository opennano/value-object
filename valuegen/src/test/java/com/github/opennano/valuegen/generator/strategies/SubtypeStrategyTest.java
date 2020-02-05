package com.github.opennano.valuegen.generator.strategies;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.util.Collection;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.opennano.valuegen.generator.TypeInfo;

@ExtendWith(MockitoExtension.class)
public class SubtypeStrategyTest {

  @ParameterizedTest
  @ValueSource(classes = {int.class, String.class, Object.class, Collection.class})
  public void skipTypesTest(Class<?> type) {
    assertThat(SubtypeStrategy.SKIP_TYPES.apply(new TypeInfo(type)), equalTo(type));
  }

  @Test
  public void uniqueSubtypesTest() {
    assertThat(
        SubtypeStrategy.UNIQUE_SUBTYPE.apply(new TypeInfo(MyInterface.class)),
        equalTo(OnlySubType.class));
  }

  private interface MyInterface {}

  private class OnlySubType implements MyInterface {}
}
