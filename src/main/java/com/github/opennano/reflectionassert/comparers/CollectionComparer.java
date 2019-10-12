package com.github.opennano.reflectionassert.comparers;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import com.github.opennano.reflectionassert.exceptions.ReflectionAssertionInternalException;
import com.github.opennano.reflectionassert.worker.ValueComparer;

/** base class for comparing collections and arrays */
public abstract class CollectionComparer extends ValueComparer {

  /** returns true when both objects are arrays or collections. */
  @Override
  public boolean canCompare(Object left, Object right) {
    return isBothArrayOrCollection(left, right);
  }

  /** converts the given (possibly primitive) array or collection object to a list of objects */
  protected List<?> asNewList(Object object) {
    if (object instanceof Collection) {
      return new ArrayList<>((Collection<?>) object);
    }
    if (object instanceof Object[]) {
      return new ArrayList<>(Arrays.asList((Object[]) object));
    }
    if (!object.getClass().isArray()) {
      throw new ReflectionAssertionInternalException(
          "can't convert an object of this type to an array: " + object.getClass());
    }

    // it's some kind of primitive array--box each element so it can be put in a list of objects
    int length = Array.getLength(object);
    List<Object> boxedArray = new ArrayList<>(length);
    for (int i = 0; i < length; i++) {
      boxedArray.add(Array.get(object, i));
    }
    return boxedArray;
  }

  private boolean isBothArrayOrCollection(Object left, Object right) {
    if (left == null || right == null) {
      return false;
    }
    return Stream.of(left, right)
        .allMatch(val -> val instanceof Collection || val.getClass().isArray());
  }
}
