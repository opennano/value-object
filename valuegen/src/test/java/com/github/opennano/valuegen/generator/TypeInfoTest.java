package com.github.opennano.valuegen.generator;

import static com.github.opennano.reflectionassert.hamcrest.ReflectionEqualsMatcher.reflectionEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.List;

import org.junit.jupiter.api.Test;

public class TypeInfoTest {

  @Test
  public void hasValidResolvedClassProperty() {
    TypeInfo typeInfo = new TypeInfo();
    typeInfo.setResolvedClass(String.class);
    assertThat(typeInfo.getResolvedClass(), equalTo(String.class));
  }

  @Test
  public void hasValidGenericParameterTypesProperty() {
    TypeInfo typeInfo = new TypeInfo();
    typeInfo.setGenericParameterTypes(new Type[] {String.class});
    assertThat(typeInfo.getGenericParameterTypes(), equalTo(new Type[] {String.class}));
  }

  @Test
  public void hasValidAdditionalInterfacesProperty() {
    TypeInfo typeInfo = new TypeInfo();
    typeInfo.setAdditionalInterfaces(new Class<?>[] {String.class});
    assertThat(typeInfo.getAdditionalInterfaces(), equalTo(new Class<?>[] {String.class}));
  }

  @Test
  public void noArgCtor() {
    TypeInfo typeInfo = new TypeInfo();

    assertThat(typeInfo.getResolvedClass(), equalTo(Object.class));
    assertThat(typeInfo.getGenericParameterTypes(), equalTo(new Class<?>[0]));
    assertThat(typeInfo.getAdditionalInterfaces(), equalTo(new Class<?>[0]));
  }

  @Test
  public void classArgCtor() {
    TypeInfo typeInfo = new TypeInfo(String.class);

    assertThat(typeInfo.getResolvedClass(), equalTo(String.class));
    assertThat(typeInfo.getGenericParameterTypes(), equalTo(new Class<?>[0]));
    assertThat(typeInfo.getAdditionalInterfaces(), equalTo(new Class<?>[0]));
  }

  @Test
  public void classAndTypeCtor() {
    TypeInfo typeInfo = new TypeInfo(String.class, new Type[] {Integer.class});

    assertThat(typeInfo.getResolvedClass(), equalTo(String.class));
    assertThat(typeInfo.getGenericParameterTypes(), equalTo(new Type[] {Integer.class}));
    assertThat(typeInfo.getAdditionalInterfaces(), equalTo(new Class<?>[0]));
  }

  @Test
  public void fullCtor() {
    TypeInfo typeInfo =
        new TypeInfo(String.class, new Type[] {String.class}, new Class<?>[] {Integer.class});

    assertThat(typeInfo.getResolvedClass(), equalTo(String.class));
    assertThat(typeInfo.getGenericParameterTypes(), equalTo(new Type[] {String.class}));
    assertThat(typeInfo.getAdditionalInterfaces(), equalTo(new Class<?>[] {Integer.class}));
  }

  @Test
  public void getSuperclass_concreteType() {
    assertThat(new TypeInfo(String.class).getSuperclass(), equalTo(String.class));
  }

  @Test
  public void getSuperclass_interfaceType() {
    assertThat(new TypeInfo(List.class).getSuperclass(), nullValue());
  }

  @Test
  public void getSuperclass_multipleInterfaceTypes() {
    assertThat(
        new TypeInfo(Object.class, new Type[0], new Class<?>[] {List.class, Serializable.class})
            .getSuperclass(),
        nullValue());
  }

  @Test
  public void getSuperclass_objectWithoutInterfaceTypes() {
    assertThat(
        new TypeInfo(Object.class, new Type[0], new Class<?>[0]).getSuperclass(),
        equalTo(Object.class));
  }

  @Test
  public void getInterfaces_resolvedTypeIsObject() {
    TypeInfo typeInfo = new TypeInfo(Object.class);

    assertThat(typeInfo.getInterfaces(), equalTo(new Class<?>[0]));
  }

  @Test
  public void getInterfaces_resolvedTypeIsString() {
    TypeInfo typeInfo = new TypeInfo(String.class);

    assertThat(typeInfo.getInterfaces(), equalTo(new Class<?>[0]));
  }

  @Test
  public void getInterfaces_resolvedTypeIsList() {
    TypeInfo typeInfo = new TypeInfo(List.class);

    assertThat(typeInfo.getInterfaces(), equalTo(new Class<?>[] {List.class}));
  }

  @Test
  public void getInterfaces_resolvedTypeAndAdditionals() {
    TypeInfo typeInfo = new TypeInfo(List.class, new Type[0], new Class<?>[] {Cloneable.class});

    assertThat(typeInfo.getInterfaces(), equalTo(new Class<?>[] {List.class, Cloneable.class}));
  }

  @Test
  public void getInterfaces_additionalsOnly() {
    TypeInfo typeInfo = new TypeInfo(Object.class, new Type[0], new Class<?>[] {Cloneable.class});

    assertThat(typeInfo.getInterfaces(), equalTo(new Class<?>[] {Cloneable.class}));
  }

  @Test
  public void isAbstract_noForObject() {
    TypeInfo typeInfo = new TypeInfo(Object.class);

    assertThat(typeInfo.isAbstract(), is(false));
  }

  @Test
  public void isAbstract_yesForInterface() {
    TypeInfo typeInfo = new TypeInfo(List.class);

    assertThat(typeInfo.isAbstract(), is(true));
  }

  @Test
  public void isAbstract_yesForAbstractClass() {
    TypeInfo typeInfo = new TypeInfo(Number.class);

    assertThat(typeInfo.isAbstract(), is(true));
  }

  @Test
  public void copy_producesSimilarObject() {
    TypeInfo typeInfo =
        new TypeInfo(String.class, new Type[] {Object.class}, new Class<?>[] {Integer.class});

    TypeInfo expected = new TypeInfo();
    expected.copy(typeInfo);
    assertThat(expected, reflectionEquals(typeInfo));
  }
}
