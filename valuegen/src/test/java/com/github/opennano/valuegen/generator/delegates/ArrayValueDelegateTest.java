package com.github.opennano.valuegen.generator.delegates;

import static com.github.opennano.reflectionassert.hamcrest.ReflectionEqualsMatcher.reflectionEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
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
public class ArrayValueDelegateTest {

  @InjectMocks private ArrayValueDelegate delegate;

  @Mock private ValueObjectGenerator mockValueObjectGenerator;
  @Mock private TypeInfo mockTypeInfo;

  @Captor ArgumentCaptor<TypeInfo> captorTypeInfo;

  private Field sampleField;

  @BeforeEach
  public void createMockField() throws Exception {
    sampleField = ArrayValueDelegateTest.class.getDeclaredField("sampleField");
  }

  @ParameterizedTest
  @ValueSource(classes = {byte[].class, Byte[].class, Object[].class})
  public void handlesClass_handledTypes(Class<?> type) {
    assertThat(delegate.handlesClass(type), is(true));
  }

  @ParameterizedTest
  @ValueSource(classes = {byte.class, Byte.class, Object.class, Collection.class, Map.class})
  public void handlesClass_notHandledTypes(Class<?> type) {
    assertThat(delegate.handlesClass(type), is(false));
  }

  @Test
  @SuppressWarnings({"rawtypes", "unchecked"}) // Class API seems to require unsafe conversions
  public void generateValue_success() {
    when(mockTypeInfo.getResolvedClass()).thenReturn((Class) Integer[].class);
    when(mockValueObjectGenerator.valueFor(
            captorTypeInfo.capture(),
            eq(sampleField),
            eq("mockNameHint"),
            eq(ArrayValueDelegateTest.class)))
        .thenReturn(1);

    Object arrayValue =
        delegate.generateValue(
            mockTypeInfo,
            sampleField,
            "mockNameHint",
            ArrayValueDelegateTest.class,
            mockValueObjectGenerator);

    TypeInfo expectedElementType = new TypeInfo(Integer.class, null, new Class<?>[0]);
    assertThat(captorTypeInfo.getValue(), reflectionEquals(expectedElementType));
    assertThat(arrayValue, reflectionEquals(new Integer[] {1}));
  }
}
