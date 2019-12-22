package com.github.opennano.valuegen.generator.delegates;

import java.lang.reflect.Field;

import com.github.opennano.valuegen.generator.TypeInfo;
import com.github.opennano.valuegen.generator.ValueGeneratorDelegate;
import com.github.opennano.valuegen.generator.ValueObjectGenerator;

public class EnumValueDelegate implements ValueGeneratorDelegate {

  private int indexOffset = 0;

  @Override
  public boolean handlesClass(Class<?> type) {
    return type.isEnum();
  }

  @Override
  public Object generateValue(
      TypeInfo typeInfo,
      Field declaringField,
      String nameHint,
      Class<?> owningClass,
      ValueObjectGenerator valueGenerator) {

    Object[] values = typeInfo.getResolvedClass().getEnumConstants();
    return (Enum<?>) values[indexOffset++ % values.length];
  }
}
