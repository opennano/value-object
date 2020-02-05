package com.github.opennano.jsongen.serializer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;

@ExtendWith(MockitoExtension.class)
public class ByteArrayAsIntArraySerializerTest {

  @Mock private JsonGenerator mockJsonGenerator;
  @Mock private SerializerProvider mockSerializerProvider;
  @InjectMocks private ByteArrayAsIntArraySerializer serializer;

  @Test
  public void isEmpty_yesWhenNull() {
    assertThat(serializer.isEmpty(mockSerializerProvider, null), is(true));
  }

  @Test
  public void isEmpty_yesWhenEmpty() {
    assertThat(serializer.isEmpty(mockSerializerProvider, new byte[0]), is(true));
  }

  @Test
  public void isEmpty_noWhenLengthGreaterThanZero() {
    assertThat(serializer.isEmpty(mockSerializerProvider, new byte[] {0}), is(false));
  }

  @Test
  public void serializer_oneByte() throws Exception {
    byte[] bytes = new byte[] {1};
    serializer.serialize(bytes, mockJsonGenerator, mockSerializerProvider);

    verify(mockJsonGenerator, times(1)).writeStartArray(bytes, 1);
    verify(mockJsonGenerator, times(1)).writeNumber(1);
    verify(mockJsonGenerator, times(1)).writeEndArray();
  }

  @Test
  public void serializer_manyBytes() throws Exception {
    byte[] bytes = new byte[] {1, 2, 1};
    serializer.serialize(bytes, mockJsonGenerator, mockSerializerProvider);

    verify(mockJsonGenerator, times(1)).writeStartArray(bytes, 3);
    verify(mockJsonGenerator, times(2)).writeNumber(1);
    verify(mockJsonGenerator, times(1)).writeNumber(2);
    verify(mockJsonGenerator, times(1)).writeEndArray();
  }
}
