package com.github.opennano.valuegen.generator.strategies;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.opennano.valuegen.ValueGenerationException;

@ExtendWith(MockitoExtension.class)
public class MapKeyStrategyTest {

  @ParameterizedTest
  @ValueSource(classes = {String.class, CharSequence.class, Object.class})
  public void anyKeyTypeTest_acceptableTypes(Class<?> type) {
    assertThat(MapKeyStrategy.ANY_KEY_TYPE.apply(type), equalTo(String.class));
  }

  @ParameterizedTest
  @ValueSource(classes = {char.class, Integer.class})
  public void anyKeyTypeTest_unacceptableTypes(Class<?> type) {
	    assertThat(MapKeyStrategy.ANY_KEY_TYPE.apply(type), equalTo(type));
  }

  @ParameterizedTest
  @ValueSource(classes = {String.class, CharSequence.class, Object.class})
  public void stringKeyTypeTest_acceptableTypes(Class<?> type) {
    assertThat(MapKeyStrategy.STRING_KEYS_ONLY.apply(type), equalTo(String.class));
  }

  @ParameterizedTest
  @ValueSource(classes = {char.class, Integer.class})
  public void stringKeyTypeTest_unacceptableTypes(Class<?> type) {
    assertThrows(ValueGenerationException.class, () -> MapKeyStrategy.STRING_KEYS_ONLY.apply(type));
  }
}
