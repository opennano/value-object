package com.github.opennano.valuegen.generator.delegates;

import static com.github.opennano.valuegen.generator.ValueGeneratorDelegate.isInstanceOfAny;

import java.lang.reflect.Field;

import com.github.opennano.valuegen.generator.TypeInfo;
import com.github.opennano.valuegen.generator.ValueGeneratorDelegate;
import com.github.opennano.valuegen.generator.ValueObjectGenerator;

public class BooleanValueDelegate implements ValueGeneratorDelegate {

  @Override
  public boolean handlesClass(Class<?> type) {
    return isInstanceOfAny(type, boolean.class, Boolean.class);
  }

  @Override
  public Object generateValue(
      TypeInfo typeInfo,
      Field declaringField,
      String nameHint,
      Class<?> owningClass,
      ValueObjectGenerator valueGenerator) {

    return Boolean.TRUE; // avoid using false as it is a Java default
  }
}
