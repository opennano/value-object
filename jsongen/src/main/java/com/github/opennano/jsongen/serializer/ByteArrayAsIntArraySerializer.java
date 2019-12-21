package com.github.opennano.jsongen.serializer;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class ByteArrayAsIntArraySerializer extends StdSerializer<byte[]> {

  private static final long serialVersionUID = 1L;

  public ByteArrayAsIntArraySerializer() {
    super(byte[].class);
  }

  @Override
  public boolean isEmpty(SerializerProvider prov, byte[] value) {
    return value.length == 0;
  }

  @Override
  public final void serialize(byte[] value, JsonGenerator gen, SerializerProvider provider)
      throws IOException {

    int len = value.length; // should always be one
    gen.writeStartArray(value, len);
    serializeContents(value, gen, provider);
    gen.writeEndArray();
  }

  public void serializeContents(byte[] value, JsonGenerator gen, SerializerProvider provider)
      throws IOException {

    for (int i = 0, len = value.length; i < len; ++i) {
      gen.writeNumber((int) value[i]);
    }
  }
}
