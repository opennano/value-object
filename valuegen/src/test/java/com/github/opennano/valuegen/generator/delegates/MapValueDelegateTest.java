package com.github.opennano.valuegen.generator.delegates;

import static com.github.opennano.reflectionassert.hamcrest.ReflectionEqualsMatcher.reflectionEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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

import com.github.opennano.valuegen.GeneratorConfig;
import com.github.opennano.valuegen.generator.TypeInfo;
import com.github.opennano.valuegen.generator.ValueObjectGenerator;
import com.github.opennano.valuegen.generator.strategies.MapKeyStrategy;

@ExtendWith(MockitoExtension.class)
public class MapValueDelegateTest {

  @InjectMocks private MapValueDelegate delegate;

  @Mock private ValueObjectGenerator mockValueObjectGenerator;
  @Mock private GeneratorConfig mockGeneratorConfig;

  @Captor private ArgumentCaptor<TypeInfo> captorTypeInfo;
  @Captor private ArgumentCaptor<String> captorString;

  @BeforeEach
  public void createDelegate() {
    when(mockGeneratorConfig.getMapKeyStrategy()).thenReturn(MapKeyStrategy.ANY_KEY_TYPE);
    delegate = new MapValueDelegate(mockGeneratorConfig);
  }

  @ParameterizedTest
  @ValueSource(classes = {Map.class, HashMap.class, LinkedHashMap.class, TreeMap.class})
  public void handlesClass_handledTypes(Class<?> type) {
    assertThat(delegate.handlesClass(type), is(true));
  }

  @ParameterizedTest
  @ValueSource(classes = {int.class, List.class, Collection.class, Object.class})
  public void handlesClass_notHandledTypes(Class<?> type) {
    assertThat(delegate.handlesClass(type), is(false));
  }

  @Test
  public void generateValue_simpleMap() {
    TypeInfo mockTypeInfo = new TypeInfo(Map.class, new Type[] {String.class, Integer.class});

    when(mockValueObjectGenerator.valueFor(
            captorTypeInfo.capture(), eq(null), captorString.capture(), eq(null)))
        .thenReturn("mockKey")
        .thenReturn(1);

    Object mapValue =
        delegate.generateValue(mockTypeInfo, null, "mockNameHint", null, mockValueObjectGenerator);

    TypeInfo expectedKeyType = new TypeInfo(String.class);
    TypeInfo expectedValueType = new TypeInfo(Integer.class);
    List<TypeInfo> allTypeValues = captorTypeInfo.getAllValues();
    assertThat(allTypeValues.size(), is(2));
    assertThat(allTypeValues.get(0), reflectionEquals(expectedKeyType));
    assertThat(allTypeValues.get(1), reflectionEquals(expectedValueType));

    List<String> allStringValues = captorString.getAllValues();
    assertThat(allStringValues.size(), is(2));
    assertThat(allStringValues.get(0), is("mockNameHintKey"));
    assertThat(allStringValues.get(1), is("mockNameHintValue"));

    HashMap<Object, Object> expectedMap = new HashMap<>();
    expectedMap.put("mockKey", 1);
    assertThat(mapValue, reflectionEquals(expectedMap));
  }

  @Test
  public void generateValue_mapUsesKeyStrategyToConvertObjectToString() {
    TypeInfo mockTypeInfo = new TypeInfo(Map.class, new Type[] {String.class, Integer.class});

    when(mockValueObjectGenerator.valueFor(any(), any(), captorString.capture(), any()))
        .thenReturn("mockKey")
        .thenReturn(1);

    Object mapValue =
        delegate.generateValue(mockTypeInfo, null, "mockNameHint", null, mockValueObjectGenerator);

    List<String> allStringValues = captorString.getAllValues();
    assertThat(allStringValues.size(), is(2));
    assertThat(allStringValues.get(0), is("mockNameHintKey"));
    assertThat(allStringValues.get(1), is("mockNameHintValue"));

    HashMap<Object, Object> expectedMap = new HashMap<>();
    expectedMap.put("mockKey", 1);
    assertThat(mapValue, reflectionEquals(expectedMap));
  }

  @Test
  public void generateValue_mapTruncatesSuggestedNameWhenNeeded1() {
    TypeInfo mockTypeInfo = new TypeInfo(Map.class, new Type[] {String.class, Integer.class});

    when(mockValueObjectGenerator.valueFor(any(), any(), captorString.capture(), any()))
        .thenReturn("mockKey")
        .thenReturn(1);

    Object mapValue =
        delegate.generateValue(
            mockTypeInfo, null, "mockNameHintValue", null, mockValueObjectGenerator);

    List<String> allStringValues = captorString.getAllValues();
    assertThat(allStringValues.size(), is(2));
    assertThat(allStringValues.get(0), is("mockNameHintKey"));
    assertThat(allStringValues.get(1), is("mockNameHintValue"));

    HashMap<Object, Object> expectedMap = new HashMap<>();
    expectedMap.put("mockKey", 1);
    assertThat(mapValue, reflectionEquals(expectedMap));
  }

  @Test
  public void generateValue_mapTruncatesSuggestedNameWhenNeeded2() {
    TypeInfo mockTypeInfo = new TypeInfo(Map.class, new Type[] {String.class, Integer.class});

    when(mockValueObjectGenerator.valueFor(any(), any(), captorString.capture(), any()))
        .thenReturn("mockKey")
        .thenReturn(1);

    Object mapValue =
        delegate.generateValue(
            mockTypeInfo,
            null,
            "mockNameHintValueSpamValueValueSpamSpam",
            null,
            mockValueObjectGenerator);

    List<String> allStringValues = captorString.getAllValues();
    assertThat(allStringValues.size(), is(2));
    assertThat(allStringValues.get(0), is("mockNameHintKey"));
    assertThat(allStringValues.get(1), is("mockNameHintValue"));

    HashMap<Object, Object> expectedMap = new HashMap<>();
    expectedMap.put("mockKey", 1);
    assertThat(mapValue, reflectionEquals(expectedMap));
  }

  @Test
  public void generateValue_useLinkedHashMapByDefault() {
    TypeInfo mockTypeInfo = new TypeInfo(Map.class, new Type[] {String.class, Integer.class});

    when(mockValueObjectGenerator.valueFor(any(), any(), any(), any()))
        .thenReturn("mockKey")
        .thenReturn(1);

    Object mapValue =
        delegate.generateValue(mockTypeInfo, null, "mockNameHint", null, mockValueObjectGenerator);

    HashMap<Object, Object> expectedMap = new HashMap<>();
    expectedMap.put("mockKey", 1);
    assertThat(mapValue, reflectionEquals(expectedMap));
    assertThat(mapValue, instanceOf(LinkedHashMap.class));
  }

  @Test
  public void generateValue_useSpecificMapIfConcreteTypee() {
    TypeInfo mockTypeInfo = new TypeInfo(HashMap.class, new Type[] {String.class, Integer.class});

    when(mockValueObjectGenerator.valueFor(any(), any(), any(), any()))
        .thenReturn("mockKey")
        .thenReturn(1);

    Object mapValue =
        delegate.generateValue(mockTypeInfo, null, "mockNameHint", null, mockValueObjectGenerator);

    HashMap<Object, Object> expectedMap = new HashMap<>();
    expectedMap.put("mockKey", 1);
    assertThat(mapValue, reflectionEquals(expectedMap));
    assertThat(mapValue, instanceOf(HashMap.class));
  }

  @Test
  public void generateValue_emptyOnException() {
    TypeInfo mockTypeInfo = new TypeInfo(Map.class, new Type[] {String.class, Integer.class});

    when(mockValueObjectGenerator.valueFor(
            captorTypeInfo.capture(), eq(null), captorString.capture(), eq(null)))
        .thenThrow(new IllegalArgumentException());

    Object mapValue =
        delegate.generateValue(mockTypeInfo, null, "mockNameHint", null, mockValueObjectGenerator);

    assertThat(mapValue, reflectionEquals(new HashMap<>()));
  }

  @Test
  public void generateValue_missingValueType() {
    TypeInfo mockTypeInfo = new TypeInfo(Map.class, new Type[] {String.class});

    when(mockValueObjectGenerator.valueFor(captorTypeInfo.capture(), any(), any(), any()))
        .thenReturn("mockKey")
        .thenReturn(1);

    Object mapValue =
        delegate.generateValue(mockTypeInfo, null, "mockNameHint", null, mockValueObjectGenerator);

    TypeInfo expectedKeyType = new TypeInfo(String.class);
    TypeInfo expectedValueType = new TypeInfo(Object.class);
    List<TypeInfo> allTypeValues = captorTypeInfo.getAllValues();
    assertThat(allTypeValues.size(), is(2));
    assertThat(allTypeValues.get(0), reflectionEquals(expectedKeyType));
    assertThat(allTypeValues.get(1), reflectionEquals(expectedValueType));

    HashMap<Object, Object> expectedMap = new HashMap<>();
    expectedMap.put("mockKey", 1);
    assertThat(mapValue, reflectionEquals(expectedMap));
  }

  @Test
  public void generateValue_missingBothGenericTypes() {
    TypeInfo mockTypeInfo = new TypeInfo(Map.class);

    when(mockValueObjectGenerator.valueFor(captorTypeInfo.capture(), any(), any(), any()))
        .thenReturn("mockKey")
        .thenReturn(1);

    Object mapValue =
        delegate.generateValue(mockTypeInfo, null, "mockNameHint", null, mockValueObjectGenerator);

    // key type will be object, but the map key strategy will convert that to a string anyway
    TypeInfo expectedKeyType = new TypeInfo(String.class); 
    TypeInfo expectedValueType = new TypeInfo(Object.class);
    List<TypeInfo> allTypeValues = captorTypeInfo.getAllValues();
    assertThat(allTypeValues.size(), is(2));
    assertThat(allTypeValues.get(0), reflectionEquals(expectedKeyType));
    assertThat(allTypeValues.get(1), reflectionEquals(expectedValueType));

    HashMap<Object, Object> expectedMap = new HashMap<>();
    expectedMap.put("mockKey", 1);
    assertThat(mapValue, reflectionEquals(expectedMap));
  }
}
