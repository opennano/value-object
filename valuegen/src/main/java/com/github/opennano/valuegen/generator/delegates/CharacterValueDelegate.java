package com.github.opennano.valuegen.generator.delegates;

import static com.github.opennano.valuegen.generator.ValueGeneratorDelegate.isInstanceOfAny;

import java.lang.reflect.Field;

import com.github.opennano.valuegen.generator.TypeInfo;
import com.github.opennano.valuegen.generator.ValueGeneratorDelegate;
import com.github.opennano.valuegen.generator.ValueObjectGenerator;

public class CharacterValueDelegate implements ValueGeneratorDelegate {

  private static int A = (int) 'a';

  private int currentIndex = 0;

  @Override
  public boolean handlesClass(Class<?> type) {
    return isInstanceOfAny(type, char.class, Character.class);
  }

  @Override
  public Object generateValue(
      TypeInfo typeInfo,
      Field declaringField,
      String nameHint,
      Class<?> owningClass,
      ValueObjectGenerator valueGenerator) {

    // generate unique lower-case letters, starting at 'a'
    return (char) (A + (currentIndex++ % 26));
  }
}
