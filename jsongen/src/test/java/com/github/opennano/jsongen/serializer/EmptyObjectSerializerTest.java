package com.github.opennano.jsongen.serializer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;

@ExtendWith(MockitoExtension.class)
public class EmptyObjectSerializerTest {

  @Mock private JsonGenerator mockJsonGenerator;
  @Mock private SerializerProvider mockSerializerProvider;
  @Mock private TypeSerializer mockTypeSerializer;
  @InjectMocks private EmptyObjectSerializer serializer;

  @Test
  public void handledType_isObject() {
    assertThat(serializer.handledType(), sameInstance(Object.class));
  }

  @Test
  public void serializeWithType_valueDoesntMatter1() throws Exception {
    serializer.serializeWithType(
        null, mockJsonGenerator, mockSerializerProvider, mockTypeSerializer);

    verify(mockJsonGenerator, times(1)).writeStartObject();
    verify(mockJsonGenerator, times(1)).writeEndObject();
  }

  @Test
  public void serializeWithType_valueDoesntMatter2() throws Exception {
    serializer.serializeWithType(
        new Object(), mockJsonGenerator, mockSerializerProvider, mockTypeSerializer);

    verify(mockJsonGenerator, times(1)).writeStartObject();
    verify(mockJsonGenerator, times(1)).writeEndObject();
  }

  @Test
  public void serialize_sameAsSerializeWithType() throws Exception {
    serializer.serialize(null, mockJsonGenerator, mockSerializerProvider);

    verify(mockJsonGenerator, times(1)).writeStartObject();
    verify(mockJsonGenerator, times(1)).writeEndObject();
  }
}
