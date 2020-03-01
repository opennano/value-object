package com.github.opennano.valuegen.generator;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import static com.github.opennano.valuegen.generator.ValueGeneratorDelegate.*;

import org.junit.jupiter.api.Test;

public class ValueGeneratorDelegateTest {

  @Test
  public void isInstanceOfAny_yesForSameInstance() {
    assertThat(isInstanceOfAny(String.class, String.class), is(true));
  }

  @Test
  public void isInstanceOfAny_yesForSubtype() {
    assertThat(isInstanceOfAny(String.class, CharSequence.class), is(true));
  }

  @Test
  public void isInstanceOfAny_noForSupertype() {
    assertThat(isInstanceOfAny(CharSequence.class, String.class), is(false));
  }

  @Test
  public void isInstanceOfAny_noMatches() {
    assertThat(isInstanceOfAny(CharSequence.class, Integer.class, Cloneable.class), is(false));
  }

  @Test
  public void isInstanceOfAny_allMatch() {
    assertThat(isInstanceOfAny(CharSequence.class, Object.class, CharSequence.class), is(true));
  }

  @Test
  public void isInstanceOfAny_oneMatches() {
    assertThat(isInstanceOfAny(String.class, Cloneable.class, CharSequence.class), is(true));
  }
}
