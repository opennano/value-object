package com.github.opennano.valuegen.generator.delegates;

import java.lang.reflect.Array;
import java.lang.reflect.Field;

import com.github.opennano.valuegen.generator.TypeInfo;
import com.github.opennano.valuegen.generator.ValueGeneratorDelegate;
import com.github.opennano.valuegen.generator.ValueObjectGenerator;

public class ArrayValueDelegate implements ValueGeneratorDelegate {

  @Override
  public boolean handlesClass(Class<?> type) {
    return type.isArray();
  }

  @Override
  public Object generateValue(
      TypeInfo typeInfo,
      Field declaringField,
      String nameHint,
      Class<?> owningClass,
      ValueObjectGenerator valueGenerator) {

    // generate an array with a single item in it for the given generic type */
    Class<?> elementClass = typeInfo.getResolvedClass().getComponentType();
    TypeInfo elementInfo = new TypeInfo(elementClass, typeInfo.getGenericParameterTypes());
    Object element = valueGenerator.valueFor(elementInfo, declaringField, nameHint, owningClass);

    Object values = Array.newInstance(elementClass, 1);
    Array.set(values, 0, element);
    return values;
  }
}
