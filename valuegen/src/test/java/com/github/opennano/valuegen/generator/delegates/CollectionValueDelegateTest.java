package com.github.opennano.valuegen.generator.delegates;

import static com.github.opennano.reflectionassert.hamcrest.ReflectionEqualsMatcher.reflectionEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.lang.reflect.Type;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;
import java.util.concurrent.ArrayBlockingQueue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.opennano.valuegen.generator.TypeInfo;
import com.github.opennano.valuegen.generator.ValueObjectGenerator;

@ExtendWith(MockitoExtension.class)
public class CollectionValueDelegateTest {

  @InjectMocks private CollectionValueDelegate delegate;

  @Mock private ValueObjectGenerator mockValueObjectGenerator;

  @Captor ArgumentCaptor<TypeInfo> captorTypeInfo;

  @ParameterizedTest
  @ValueSource(classes = {Collection.class, List.class, Queue.class})
  public void handlesClass_handledTypes(Class<?> type) {
    assertThat(delegate.handlesClass(type), is(true));
  }

  @ParameterizedTest
  @ValueSource(classes = {int[].class, Integer[].class, Object.class, Map.class})
  public void handlesClass_notHandledTypes(Class<?> type) {
    assertThat(delegate.handlesClass(type), is(false));
  }

  @ParameterizedTest
  @ValueSource(
      classes = {
        Collection.class,
        List.class,
        ArrayList.class,
        AbstractList.class,
        Vector.class,
        Set.class,
        HashSet.class,
        LinkedHashSet.class,
        TreeSet.class,
        Queue.class,
        ArrayBlockingQueue.class
      })
  public void generateValue_variousCollectionsOfInteger(Class<?> collectionType) {
    TypeInfo mockTypeInfo = new TypeInfo(collectionType, new Type[] {Integer.class});

    when(mockValueObjectGenerator.valueFor(
            captorTypeInfo.capture(), eq(null), eq("mockNameHint"), eq(null)))
        .thenReturn(1);

    Object collectionValue =
        delegate.generateValue(mockTypeInfo, null, "mockNameHint", null, mockValueObjectGenerator);

    TypeInfo expectedElementType = new TypeInfo(Integer.class);
    assertThat(captorTypeInfo.getValue(), reflectionEquals(expectedElementType));
    assertThat(collectionValue, reflectionEquals(new Integer[] {1}));
  }

  @Test
  public void generateValue_immutableCollection() {
    TypeInfo mockTypeInfo = new TypeInfo(ImmutableList.class, new Type[] {Integer.class});

    when(mockValueObjectGenerator.valueFor(
            captorTypeInfo.capture(), eq(null), eq("mockNameHint"), eq(null)))
        .thenReturn(1);

    Object collectionValue =
        delegate.generateValue(mockTypeInfo, null, "mockNameHint", null, mockValueObjectGenerator);

    TypeInfo expectedElementType = new TypeInfo(Integer.class);
    assertThat(captorTypeInfo.getValue(), reflectionEquals(expectedElementType));
    assertThat(collectionValue, reflectionEquals(new Integer[0]));
  }

  private class ImmutableList<T> extends AbstractList<T> {

    @Override
    public boolean add(T e) {
      throw new IllegalStateException("mock immutable list");
    }

    @Override
    public T get(int index) {
      return null;
    }

    @Override
    public int size() {
      return 0;
    }
  }
}
