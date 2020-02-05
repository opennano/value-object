package com.github.opennano.valuegen.utils;

import static com.github.opennano.reflectionassert.hamcrest.ReflectionEqualsMatcher.reflectionEquals;
import static com.github.opennano.valuegen.utils.ReflectionUtil.getTypeInfo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.opennano.valuegen.generator.TypeInfo;
import com.github.opennano.valuegen.utils.ReflectionUtilTest.BoundSetOnSuperTypeVariableParent.BoundSetOnSuperTypeVariable;
import com.github.opennano.valuegen.utils.ReflectionUtilTest.NonGenericSuperTypeVariableParent.NonGenericSuperTypeVariable;
import com.github.opennano.valuegen.utils.ReflectionUtilTest.TypeResolvesToClassParent.TypeResolvesToClass;

@ExtendWith(MockitoExtension.class)
public class ReflectionUtilTest {

  @Mock private ParameterizedType mockParameterizedType;
  @Mock private TypeVariable<?> mockTypeVariable;

  @Test
  public void getTypeInfo_simpleClass() {
    TypeInfo typeInfo = getTypeInfo(Object.class, null, null);
    assertThat(typeInfo, reflectionEquals(new TypeInfo(Object.class)));
  }

  @Test
  public void getTypeInfo_simpleParameterizedType() {
    when(mockParameterizedType.getRawType()).thenReturn(List.class);
    when(mockParameterizedType.getActualTypeArguments()).thenReturn(new Type[] {Integer.class});
    TypeInfo typeInfo = getTypeInfo(mockParameterizedType, null, null);

    assertThat(typeInfo, reflectionEquals(new TypeInfo(List.class, new Type[] {Integer.class})));
  }

  @Test
  public void getTypeInfo_simpleTypeVariable() {
    Field typeVarField = getTestedField(SimpleTypeVariable.class);
    Type type = typeVarField.getGenericType();

    TypeInfo typeInfo = getTypeInfo(type, SimpleTypeVariable.class, typeVarField);

    assertThat(typeInfo, reflectionEquals(new TypeInfo(Number.class)));
  }

  @Test
  public void getTypeInfo_typeResolvesToClass() {
    Field typeVarField = getTestedField(TypeResolvesToClass.class);
    Type type = typeVarField.getGenericType();

    TypeInfo typeInfo = getTypeInfo(type, TypeResolvesToClass.class, typeVarField);

    assertThat(typeInfo, reflectionEquals(new TypeInfo(Number.class)));
  }

  @Test
  public void getTypeInfo_boundSetOnSuperTypeVariable() {
    Field typeVarField = getTestedField(BoundSetOnSuperTypeVariable.class);
    Type type = typeVarField.getGenericType();

    TypeInfo typeInfo = getTypeInfo(type, BoundSetOnSuperTypeVariable.class, typeVarField);

    assertThat(typeInfo, reflectionEquals(new TypeInfo(Number.class)));
  }

  @Test
  public void getTypeInfo_multipleInterfaceBoundTypeVariable() {
    Field typeVarField = getTestedField(MultipleInterfaceBoundTypeVariable.class);
    Type type = typeVarField.getGenericType();

    TypeInfo typeInfo = getTypeInfo(type, MultipleInterfaceBoundTypeVariable.class, typeVarField);

    assertThat(
        typeInfo,
        reflectionEquals(
            new TypeInfo(
                Object.class, new Type[0], new Class<?>[] {Serializable.class, Cloneable.class})));
  }

  @Test
  public void getTypeInfo_classAndInterfaceBoundTypeVariable() {
    Field typeVarField = getTestedField(ClassAndInterfaceBoundTypeVariable.class);
    Type type = typeVarField.getGenericType();

    TypeInfo typeInfo = getTypeInfo(type, ClassAndInterfaceBoundTypeVariable.class, typeVarField);

    assertThat(
        typeInfo,
        reflectionEquals(
            new TypeInfo(Number.class, new Type[0], new Class<?>[] {Cloneable.class})));
  }

  @Test
  public void getTypeInfo_redundantBoundTypeVariable() {
    Field typeVarField = getTestedField(RedundantBoundTypeVariable.class);
    Type type = typeVarField.getGenericType();

    TypeInfo typeInfo = getTypeInfo(type, RedundantBoundTypeVariable.class, typeVarField);

    assertThat(typeInfo, reflectionEquals(new TypeInfo(Number.class)));
  }

  @Test
  public void getTypeInfo_nonGenericSuperTypeVariable() {
    Field typeVarField = getTestedField(NonGenericSuperTypeVariable.class);
    Type type = typeVarField.getGenericType();

    TypeInfo typeInfo = getTypeInfo(type, NonGenericSuperTypeVariable.class, typeVarField);

    assertThat(typeInfo, reflectionEquals(new TypeInfo()));
  }

  @Test
  public void getTypeInfo_simpleGenericArray() {
    Field typeVarField = getTestedField(SimpleGenericArray.class);
    Type type = typeVarField.getGenericType();

    TypeInfo typeInfo = getTypeInfo(type, SimpleGenericArray.class, typeVarField);

    assertThat(typeInfo, reflectionEquals(new TypeInfo(Number[].class)));
  }

  @Test
  public void getTypeInfo_nestedGenericArray() {
    Field typeVarField = getTestedField(NestedGenericArray.class);
    Type type = typeVarField.getGenericType();

    TypeInfo typeInfo = getTypeInfo(type, NestedGenericArray.class, typeVarField);

    assertThat(typeInfo, reflectionEquals(new TypeInfo(Number[][].class)));
  }

  @Test
  public void populateArrayTypeInfoRecursivelyInfiniteLoopTest() throws Exception {
    Method method =
        ReflectionUtil.class.getDeclaredMethod(
            "populateArrayTypeInfoRecursively",
            TypeInfo.class,
            GenericArrayType.class,
            Class.class,
            Class.class,
            int.class);

    method.setAccessible(true);
    method.invoke(null, null, null, null, null, 256);
  }

  @Test
  public void getTypeInfo_simpleUpperBound() {
    Field typeVarField = getTestedField(SimpleUpperBound.class);
    Type type = ((ParameterizedType) typeVarField.getGenericType()).getActualTypeArguments()[0];

    TypeInfo typeInfo = getTypeInfo(type, SimpleUpperBound.class, typeVarField);

    assertThat(typeInfo, reflectionEquals(new TypeInfo(Number.class)));
  }

  @Test
  public void getTypeInfo_simpleLowerBound() {
    Field typeVarField = getTestedField(SimpleLowerBound.class);
    Type type = ((ParameterizedType) typeVarField.getGenericType()).getActualTypeArguments()[0];

    TypeInfo typeInfo = getTypeInfo(type, SimpleLowerBound.class, typeVarField);

    assertThat(typeInfo, reflectionEquals(new TypeInfo(Number.class)));
  }

  @Test
  public void getTypeInfo_unknownType() {
    Type unknownType = new Type() {};
    assertThrows(IllegalArgumentException.class, () -> getTypeInfo(unknownType, null, null));
  }

  ////////// UTILITY METHODS //////////

  private Field getTestedField(Class<?> type) {
    try {
      Field field = type.getField("testedField");
      field.setAccessible(true);
      return field;
    } catch (NoSuchFieldException | SecurityException e) {
      throw new IllegalStateException("reflection error", e);
    }
  }

  ////////// CLASSES THAT PROVIDE VARIOUS TYPES AS FIELDS //////////

  protected static class SimpleTypeVariable<T extends Number> {
    public T testedField;
  }

  protected static class TypeResolvesToClassParent<T> {

    public T testedField;

    protected static class TypeResolvesToClass extends TypeResolvesToClassParent<Number> {}
  }

  protected static class BoundSetOnSuperTypeVariableParent<T> {

    public T testedField;

    protected static class BoundSetOnSuperTypeVariable<S extends Number>
        extends BoundSetOnSuperTypeVariableParent<S> {}
  }

  protected static class MultipleInterfaceBoundTypeVariable<T extends Serializable & Cloneable> {
    public T testedField;
  }

  protected static class ClassAndInterfaceBoundTypeVariable<T extends Number & Cloneable> {
    public T testedField;
  }

  protected static class RedundantBoundTypeVariable<T extends Number & Serializable> {
    public T testedField;
  }

  protected static class NonGenericSuperTypeVariableParent<T> {

    public T testedField;

    @SuppressWarnings("rawtypes") // this is a valid test case we want to cover
    protected static class NonGenericSuperTypeVariable extends NonGenericSuperTypeVariableParent {}
  }

  protected static class SimpleGenericArray<T extends Number> {
    public T[] testedField;
  }

  protected static class NestedGenericArray<T extends Number> {
    public T[][] testedField;
  }

  protected static class SimpleUpperBound {
    public List<? extends Number> testedField;
  }

  protected static class SimpleLowerBound {
    public List<? super Number> testedField;
  }
}
