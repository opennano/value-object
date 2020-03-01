package com.github.opennano.valuegen.generator.delegates;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CharacterValueDelegateTest {

  @InjectMocks private CharacterValueDelegate delegate;

  @ParameterizedTest
  @ValueSource(classes = {char.class, Character.class})
  public void handlesClass_handledTypes(Class<?> type) {
    assertThat(delegate.handlesClass(type), is(true));
  }

  @ParameterizedTest
  @ValueSource(classes = {byte.class, String.class})
  public void handlesClass_notHandledTypes(Class<?> type) {
    assertThat(delegate.handlesClass(type), is(false));
  }

  @Test
  public void generateValue_success() {
    Object characterValue = delegate.generateValue(null, null, null, null, null);

    assertThat(characterValue, is('a'));
  }

  @Test
  public void generateValue_incrementsAndResets() {

	    // make sure we get 'a', 'b', 'c' all the way to 'z'
    char expectedCharacter = 'a';
    for (int i = 0; i < 26; i++) {
      Object characterValue = delegate.generateValue(null, null, null, null, null);
      assertThat(characterValue, is(expectedCharacter));
      expectedCharacter++;
    }
    
    // make sure we start over at 'a' after 'z'
    Object characterValue = delegate.generateValue(null, null, null, null, null);
    assertThat(characterValue, is('a'));
  }
}
