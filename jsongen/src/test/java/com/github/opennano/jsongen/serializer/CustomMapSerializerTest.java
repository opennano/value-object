package com.github.opennano.jsongen.serializer;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;

@ExtendWith(MockitoExtension.class)
public class CustomMapSerializerTest {

  @Mock private JsonGenerator mockJsonGenerator;
  @Mock private SerializerProvider mockSerializerProvider;
  @InjectMocks private CustomMapSerializer serializer;

  @Test
  public void serializer_null() {
    assertThrows(
        NullPointerException.class,
        () -> serializer.serialize(null, mockJsonGenerator, mockSerializerProvider));
  }

  @Test
  public void serializer_empty() throws Exception {
    Map<?, ?> value = new HashMap<>(0);
    serializer.serialize(value, mockJsonGenerator, mockSerializerProvider);

    verifyNoMoreInteractions(mockJsonGenerator, mockSerializerProvider);
  }

  @Test
  public void serializer_stringKeysSingleValue() throws Exception {
    Map<String, Object> value = new HashMap<>(2);
    value.put("mockKey", "mockValue");
    serializer.serialize(value, mockJsonGenerator, mockSerializerProvider);

    verify(mockJsonGenerator, times(1)).writeStartObject();
    verify(mockJsonGenerator, times(1)).writeObjectField("mockKey", "mockValue");
    verify(mockJsonGenerator, times(1)).writeEndObject();
  }

  @Test
  public void serializer_stringKeysMultipleValues() throws Exception {
    Map<String, Object> value = new HashMap<>(2);
    value.put("mockKey1", "mockValue1");
    value.put("mockKey2", "mockValue2");
    serializer.serialize(value, mockJsonGenerator, mockSerializerProvider);

    verify(mockJsonGenerator, times(2)).writeStartObject();
    verify(mockJsonGenerator, times(1)).writeObjectField("mockKey1", "mockValue1");
    verify(mockJsonGenerator, times(1)).writeObjectField("mockKey2", "mockValue2");
    verify(mockJsonGenerator, times(2)).writeEndObject();
  }

  @Test
  public void serializer_nonStringKeysSingleValue() throws Exception {
    Map<Object, Object> value = new HashMap<>(2);
    value.put(1, "mockValue");
    serializer.serialize(value, mockJsonGenerator, mockSerializerProvider);

    verify(mockJsonGenerator, times(2)).writeStartArray();
    verify(mockJsonGenerator, times(1)).writeObject(1);
    verify(mockJsonGenerator, times(1)).writeObject("mockValue");
    verify(mockJsonGenerator, times(2)).writeEndArray();
  }

  @Test
  public void serializer_nonStringKeysMultipleValues() throws Exception {
    Map<Object, Object> value = new HashMap<>(2);
    value.put(1, "mockValue1");
    value.put(2, "mockValue2");
    serializer.serialize(value, mockJsonGenerator, mockSerializerProvider);

    verify(mockJsonGenerator, times(3)).writeStartArray();
    verify(mockJsonGenerator, times(1)).writeObject(1);
    verify(mockJsonGenerator, times(1)).writeObject("mockValue1");
    verify(mockJsonGenerator, times(1)).writeObject(2);
    verify(mockJsonGenerator, times(1)).writeObject("mockValue2");
    verify(mockJsonGenerator, times(3)).writeEndArray();
  }
}
