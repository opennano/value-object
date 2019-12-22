package com.github.opennano.valuegen.generator.delegates;

import static com.github.opennano.valuegen.generator.ValueGeneratorDelegate.isInstanceOfAny;
import static com.github.opennano.valuegen.utils.ReflectionUtil.getTypeInfo;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.stream.Stream;

import com.github.opennano.valuegen.generator.TypeInfo;
import com.github.opennano.valuegen.generator.ValueGeneratorDelegate;
import com.github.opennano.valuegen.generator.ValueObjectGenerator;

public class CollectionValueDelegate implements ValueGeneratorDelegate {

  @Override
  public boolean handlesClass(Class<?> type) {
    return isInstanceOfAny(type, Collection.class);
  }

  @Override
  public Object generateValue(
      TypeInfo typeInfo,
      Field declaringField,
      String nameHint,
      Class<?> owningClass,
      ValueObjectGenerator valueGenerator) {

    // generate a collection with a single item in it for the given generic type */

    Collection<Object> collection = instantiateCollection(typeInfo.getResolvedClass());

    // generate a single element value for the collection
    try {
      // figure out the type of the first element
      // it's possible that the generic wasn't present (i.e. the user ignored the warning)
      // in this case we should assume Object
      Type[] genericParameterTypes = typeInfo.getGenericParameterTypes();
      Type elementType = Stream.of(genericParameterTypes).findFirst().orElse(Object.class);
      TypeInfo elementInfo = getTypeInfo(elementType, owningClass, declaringField);
      Object element = valueGenerator.valueFor(elementInfo, declaringField, nameHint, owningClass);
      collection.add(element);
    } catch (Exception e) {
      // TODO log
      // couldn't add an item to the collection
      // this can happen e.g. if the collection is a TreeSet and we add an Object
      // the default TreeSet does not have a Comparable and Object isn't Comparable
    }
    return collection;
  }

  @SuppressWarnings("unchecked") // safe to cast when empty
  private Collection<Object> instantiateCollection(Class<?> collectionClass) {
    if (collectionClass.isInterface() || Modifier.isAbstract(collectionClass.getModifiers())) {
      return instantiateSubtype(collectionClass);
    }

    // this is a concrete type--try to create this instantiate type
    // Objenesis sometimes creates unusable collections--try to create via reflection if we can
    try {
      Constructor<?> noArgCtor = collectionClass.getDeclaredConstructor();
      return (Collection<Object>) noArgCtor.newInstance();
    } catch (ReflectiveOperationException | IllegalArgumentException | SecurityException e1) {
      try {
        // no luck with no-arg, maybe it has a size based ctor?
        Constructor<?> intCtor = collectionClass.getDeclaredConstructor(int.class);
        return (Collection<Object>) intCtor.newInstance(2);
      } catch (ReflectiveOperationException | IllegalArgumentException | SecurityException e2) {
        // no-op: just try Objenesis then
      }
    }
    return (Collection<Object>) objenesis.newInstance(collectionClass);
  }

  private Collection<Object> instantiateSubtype(Class<?> collectionClass) {
    // handle interfaces and abstract classes by choosing the most common implementation of them
    if (collectionClass.isAssignableFrom(List.class)) {
      return new ArrayList<>(1);
    } else if (collectionClass.isAssignableFrom(Queue.class)) {
      return new ArrayBlockingQueue<>(1);
    }
    return new LinkedHashSet<>(2);
  }
}
