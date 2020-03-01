package com.github.opennano.valuegen.utils;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.opennano.valuegen.generator.TypeInfo;

public class ReflectionUtil {

  public static TypeInfo getTypeInfo(Type type, Class<?> owningClass, Field field) {
    return getTypeInfo(type, owningClass, (field == null) ? null : field.getDeclaringClass());
  }

  private static TypeInfo getTypeInfo(Type type, Class<?> owningClass, Class<?> declaringClass) {
    Class<?> nullSafeOwningClass = (owningClass == null) ? Object.class : owningClass;
    Class<?> nullSafeDeclaringClass = (declaringClass == null) ? Object.class : declaringClass;

    TypeInfo typeInfo = new TypeInfo();
    populateTypeInfo(typeInfo, type, nullSafeOwningClass, nullSafeDeclaringClass);
    return typeInfo;
  }

  private static void populateTypeInfo(
      TypeInfo typeInfo, Type type, Class<?> instanceClass, Class<?> declaringClass) {

    if (type instanceof Class) {
      // a vanilla no generics class type
      typeInfo.setResolvedClass((Class<?>) type);
    } else if (type instanceof ParameterizedType) {
      // something like List<Integer>
      // getRawType blind casted as it seems it can't return anything but a class
      typeInfo.setResolvedClass((Class<?>) ((ParameterizedType) type).getRawType());
      typeInfo.setGenericParameterTypes(((ParameterizedType) type).getActualTypeArguments());
    } else if (type instanceof TypeVariable) {
      // T, for example
      // resolve the type var and try again
      TypeVariable<?> typeVar = (TypeVariable<?>) type;
      Type resolvedType = resolveTypeArgument(typeVar, instanceClass, declaringClass);
      if (Object.class.equals(resolvedType) || resolvedType instanceof TypeVariable) {
        // can't resolve the type to a specific class
        // if we resolved to Object that means we found no binding
        // if we resolved to a type variable that means we found a more specific type var
        // in which case we'll get better results using that type's bounds
        TypeVariable<?> mostSpecificType =
            (resolvedType instanceof TypeVariable) ? (TypeVariable<?>) resolvedType : typeVar;

        Type[] upperBounds = mostSpecificType.getBounds();
        populateTypeInfoFromUpperBounds(typeInfo, upperBounds, instanceClass, declaringClass);
      } else {
        populateTypeInfo(typeInfo, resolvedType, instanceClass, declaringClass);
      }
    } else if (type instanceof GenericArrayType) {
      // T[] or T[][] or some such
      // recursion helps us drill down until we get to just T
      GenericArrayType arrayType = (GenericArrayType) type;
      populateArrayTypeInfoRecursively(typeInfo, arrayType, instanceClass, declaringClass, 0);
    } else if (type instanceof WildcardType) {
      // '? extends List & Cloneable' or '? super List', e.g.
      // either we have an upper bound or (exclusive or, to be precise) a lower bound

      WildcardType wildcardType = (WildcardType) type;
      Type[] lowerBounds = wildcardType.getLowerBounds();
      if (lowerBounds.length == 0) {
        // '? extends List & Cloneable', e.g.
        Type[] upperBounds = wildcardType.getUpperBounds();
        populateTypeInfoFromUpperBounds(typeInfo, upperBounds, instanceClass, declaringClass);
      } else {
        // '? super List', e.g.
        // the Java API allows for more than one lower bound
        // but in practice there can only ever be one
        populateTypeInfoFromLowerBound(typeInfo, lowerBounds[0], instanceClass, declaringClass);
      }
    } else {
    	throw new IllegalArgumentException("unknown type " + type.getClass());
    }
  }

  /**
   * Resolves the type variables of provided subclass by inspecting the type heirarchy up to
   * superclass.
   *
   * <p>For example, for the classes
   *
   * <pre>
   * class Foo&lt;U, V&gt; {}
   * class Bar&lt;W&gt; extends Foo&lt;String, W&gt; {}
   * class Baz extends Bar&lt;Long&gt;
   * </pre>
   *
   * and a <code>typedClass</code> argument of <code>Baz.class</code>, the method should return
   *
   * <p>
   *
   * <ul>
   *   <li><code>[String, Long]</code> for a <code>typedSuperclass</code> argument of <code>
   *       Foo.class</code>, and
   *   <li><code>[Long]</code> if <code>typedSuperclass</code> is <code>Bar.class</code>.
   * </ul>
   *
   * For type parameters that cannot be determined, <code>null</code> is returned.
   */
  private static Map<TypeVariable<?>, Type> getTypeMap(Class<?> subclass, Class<?> superclass) {
    Map<TypeVariable<?>, Type> typeMap = new HashMap<>(2 * superclass.getTypeParameters().length);

    // get the whole class hierarchy between subtype and supertype inclusive
    List<Class<?>> typeHeirarchy = new ArrayList<>();
    Class<?> supertype = subclass;
    while (!superclass.equals(supertype)) {
      typeHeirarchy.add(supertype);
      supertype = supertype.getSuperclass();
    }
    typeHeirarchy.add(superclass);

    for (int i = 0; i < typeHeirarchy.size() - 1; i++) {
      resolveTypeVariables(typeHeirarchy.get(i), typeHeirarchy.get(i + 1), typeMap);
    }

    // contains the best information we could get from the type hierarchy
    // if we couldn't resolve a variable at all this map would return null
    return typeMap;
  }

  private static void resolveTypeVariables(
      Class<?> type, Class<?> supertype, Map<TypeVariable<?>, Type> typeMap) {

    TypeVariable<?>[] typeParameters = supertype.getTypeParameters();
    Type genericSupertype = type.getGenericSuperclass();
    Type[] resolvedTypes =
        ((genericSupertype instanceof ParameterizedType)
            ? ((ParameterizedType) genericSupertype).getActualTypeArguments()
            : supertype.getTypeParameters());

    // matches up type parameters with their actual assignments
    for (int i = 0; i < resolvedTypes.length; i++) {
      typeMap.put(typeParameters[i], resolvedTypes[i]);
    }
  }

  private static Type resolveTypeArgument(
      TypeVariable<?> type, Class<?> subType, Class<?> superType) {

    Type resolvedType = getTypeMap(subType, superType).get(type);
    return resolvedType == null ? Object.class : resolvedType;
  }

  private static void populateArrayTypeInfoRecursively(
      TypeInfo typeInfo,
      GenericArrayType type,
      Class<?> owningClass,
      Class<?> declaringClass,
      int dimensions) {

    Type resolvedElementType = type;
    TypeInfo tempInfo = null;
    do {
      if (dimensions++ > 255) {
        // max number of dimensions for an array is 255
        // recursing beyond that is almost certainly an infinite loop bug
        // TODO log
        return;
      }

      resolvedElementType = ((GenericArrayType) resolvedElementType).getGenericComponentType();
      if (resolvedElementType instanceof TypeVariable) {
        TypeVariable<?> typeVar = (TypeVariable<?>) resolvedElementType;
        tempInfo = getTypeInfo(typeVar, owningClass, declaringClass);
        resolvedElementType = tempInfo.getResolvedClass();
      }
    } while (resolvedElementType instanceof GenericArrayType);

    // we've dug through all the array dimensions, resolving the underlying component type variable
    // now we can just go back to the main populate method with this resolved component type
    // (which could be anything but a TypeVar or GenericArrayType or we we'd still be looping)
    // the only catch is that we will get a raw type, when we really want an n-dimensional array
    populateTypeInfo(typeInfo, resolvedElementType, owningClass, declaringClass);
    Class<?> componentType = tempInfo.getResolvedClass();
    typeInfo.setResolvedClass(Array.newInstance(componentType, new int[dimensions]).getClass());
    typeInfo.setGenericParameterTypes(tempInfo.getGenericParameterTypes());
  }

  private static void populateTypeInfoFromLowerBound(
      TypeInfo typeInfo, Type lowerBound, Class<?> instanceClass, Class<?> declaringClass) {

    // '? super List', e.g.
    // it's valid for the parameterized type values to be set to the exact lower bound
    // for example, this compiles: List<? super String> x = new ArrayList<String>();
    populateTypeInfo(typeInfo, lowerBound, instanceClass, declaringClass);
  }

  private static void populateTypeInfoFromUpperBounds(
      TypeInfo typeInfo, Type[] upperBounds, Class<?> instanceClass, Class<?> declaringClass) {

    if (upperBounds.length == 1) {
      // '? extends List', e.g.
      // this is by far the most typical case and is the easy one to handle
      // what we extend could be any Type, not just a Class
      populateTypeInfo(typeInfo, upperBounds[0], instanceClass, declaringClass);
      return;
    }

    // more than one upper bound, this is quite a bit more complicated...
    // '? extends List & Cloneable', e.g.
    // the first step is to resolve the raw types of each bounding type
    List<TypeInfo> boundInfos =
        Stream.of(upperBounds)
            .map(type -> getTypeInfo(type, instanceClass, declaringClass))
            .collect(Collectors.toList());

    // now eliminate redundant types
    // if we got Number and Serializable e.g., it's the same as just Number
    // to figure this out first get the resolved type
    for (Iterator<TypeInfo> iter = boundInfos.iterator(); iter.hasNext(); ) {
      TypeInfo boundInfo = iter.next();
      for (TypeInfo otherBoundInfo : boundInfos) {
        if (boundInfo == otherBoundInfo) {
          continue;
        }
        if (boundInfo.getResolvedClass().isAssignableFrom(otherBoundInfo.getResolvedClass())) {
          iter.remove();
          break;
        }
      }
    }

    // did that get us down to just one upper bound? if so, this is easy
    if (boundInfos.size() == 1) {
      typeInfo.copy(boundInfos.get(0));
      return;
    }

    // still multiple bounds--find a class type that is not an interface
    // if we find just one of those, that is our type
    // note: Java does not allow more than one non-interface to be an upper bound
    TypeInfo classBound =
        boundInfos
            .stream()
            .filter(info -> !info.getResolvedClass().isInterface())
            .findFirst()
            .orElse(null);

    if (classBound != null) {
      typeInfo.copy(classBound);
      boundInfos.remove(classBound); // only interfaces left
    }

    // all remaining upper bounds must be interfaces
    // set all the resolved interface types on additionalInterfaces
    typeInfo.setAdditionalInterfaces(
        boundInfos.stream().map(TypeInfo::getResolvedClass).toArray(Class<?>[]::new));
  }
  
  private ReflectionUtil() {
	  //no-op: singleton
  }
}
