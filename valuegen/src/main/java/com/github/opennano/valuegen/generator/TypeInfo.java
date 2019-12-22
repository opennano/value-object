package com.github.opennano.valuegen.generator;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.stream.Stream;

public class TypeInfo {

  public static final Class<?>[] EMPTY_TYPES = new Class<?>[0];

  private Class<?> resolvedClass;
  private Type[] genericParameterTypes;
  private Class<?>[] additionalInterfaces;

  public TypeInfo() {
    this(Object.class);
  }

  public TypeInfo(Class<?> resolvedClass) {
    this(resolvedClass, EMPTY_TYPES);
  }

  public TypeInfo(Class<?> resolvedClass, Type[] genericParameterTypes) {
    this(resolvedClass, genericParameterTypes, EMPTY_TYPES);
  }

  public TypeInfo(
      Class<?> resolvedClass, Type[] genericParameterTypes, Class<?>[] additionalInterfaces) {

    this.resolvedClass = resolvedClass;
    this.genericParameterTypes = genericParameterTypes;
    this.additionalInterfaces = additionalInterfaces;
  }

  public Class<?> getResolvedClass() {
    return resolvedClass;
  }

  public Type[] getGenericParameterTypes() {
    return genericParameterTypes;
  }

  public Class<?>[] getAdditionalInterfaces() {
    return additionalInterfaces;
  }

  public void setResolvedClass(Class<?> resolvedClass) {
    this.resolvedClass = resolvedClass;
  }

  public void setGenericParameterTypes(Type[] genericParameterTypes) {
    this.genericParameterTypes = genericParameterTypes;
  }

  public void setAdditionalInterfaces(Class<?>[] additionalInterfaces) {
    this.additionalInterfaces = additionalInterfaces;
  }

  public Class<?> getSuperclass() {
    return hasNoSuperclass() ? null : resolvedClass;
  }

  public Class<?>[] getInterfaces() {
    if (hasNoSuperclass() && resolvedClass != Object.class) {
      return Stream.concat(Stream.of(resolvedClass), Stream.of(additionalInterfaces))
          .toArray(Class<?>[]::new);
    }
    return additionalInterfaces;
  }

  public boolean isAbstract() {
    // like hasNoSuperclass but stricter--also returning false if the superclass is abstract
    return hasNoSuperclass() || Modifier.isAbstract(resolvedClass.getModifiers());
  }

  public boolean hasNoSuperclass() {
    // determines if there is a superclass for this type after resolution
    // there are a few scenarios to consider:
    // (1) the primary type resolved to an interface
    // (2) resolution uncovered multiple interface bounds and resolved defaulted to Object
    // (3) the primary type resolved to Object
    // we would want to return true only for scenarios one and two

    return resolvedClass.isInterface() || isMultipleInterfacesOnly();
  }

  private boolean isMultipleInterfacesOnly() {
    return resolvedClass == Object.class && additionalInterfaces.length > 0;
  }

  public void copy(TypeInfo fromType) {
    resolvedClass = fromType.resolvedClass;
    genericParameterTypes = fromType.genericParameterTypes;
    additionalInterfaces = fromType.additionalInterfaces;
  }
}
