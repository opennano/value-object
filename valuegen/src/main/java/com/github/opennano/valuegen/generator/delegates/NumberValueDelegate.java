package com.github.opennano.valuegen.generator.delegates;

import static com.github.opennano.valuegen.generator.ValueGeneratorDelegate.isInstanceOfAny;
import static com.github.opennano.valuegen.utils.AutoboxingUtil.boxedEquivalent;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import com.github.opennano.valuegen.generator.TypeInfo;
import com.github.opennano.valuegen.generator.ValueGeneratorDelegate;
import com.github.opennano.valuegen.generator.ValueObjectGenerator;

public class NumberValueDelegate implements ValueGeneratorDelegate {

  private int currentValue;

  @Override
  public boolean handlesClass(Class<?> type) {

    return isInstanceOfAny(
        type,
        Number.class,
        int.class,
        float.class,
        long.class,
        double.class,
        short.class,
        byte.class);
  }

  @Override
  public Number generateValue(
      TypeInfo typeInfo,
      Field declaringField,
      String nameHint,
      Class<?> owningClass,
      ValueObjectGenerator valueGenerator) {

    // use bytes as they work with any numeric type field
    // range is 1 to 126, after which it starts at 1 again
    // this avoids using zero as it is a Java default
    return widen(typeInfo.getResolvedClass(), (byte) (1 + currentValue++ % (Byte.MAX_VALUE - 1)));
  }

  /**
   * while autoboxing generally works when setting values by reflection, auto-widening does not.
   * This method attempts to widen the byte value and then box it up. The way this is done is
   * unpleasant, converting the byte to a string and reflectively calling the right constructor. Any
   * other approach will require a lot more boilerplate code though, to handle each scenario
   * separately. In the end, this method will produce a boxed primitive of the requested type
   * (whether requested was boxed or not). {@link Array#set(Object, int, Object)} for one fails
   * without this widening conversion, though it can box/unbox just fine.
   */
  private Number widen(Class<?> numberClass, byte value) {
    Class<?> boxedType =
        numberClass.isPrimitive()
            ? boxedEquivalent(numberClass)
            : numberClass; // boxed type should be instance of number

    if (Number.class.equals(numberClass)) {
      // use integers by default if we get an abstract type
      boxedType = Integer.class;
    }

    // all subclasses of number have a string constructor
    String byteAsString = Byte.toString(value);
    try {
      Constructor<?> stringCtor = boxedType.getConstructor(String.class);
      return (Number) stringCtor.newInstance(byteAsString);
    } catch (SecurityException | ReflectiveOperationException | IllegalArgumentException e) {
      throw new IllegalArgumentException("error using string constructor to create Number", e);
    }
  }
}
