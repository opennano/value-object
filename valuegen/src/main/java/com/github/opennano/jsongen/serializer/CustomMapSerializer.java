package com.github.opennano.jsongen.serializer;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class CustomMapSerializer extends StdSerializer<Map<?, ?>> {

  private static final long serialVersionUID = 1L;

  public CustomMapSerializer() {
    super(Map.class, false);
  }

  @Override
  public void serialize(Map<?, ?> value, JsonGenerator gen, SerializerProvider serializers)
      throws IOException {

    if (value.keySet().stream().allMatch(key -> key instanceof String)) {
      // use string keys if at all possible
      for (Entry<?, ?> entry : value.entrySet()) {
        gen.writeStartObject();
        gen.writeObjectField(entry.getKey().toString(), entry.getValue());
        gen.writeEndObject();
      }
    } else {
      // represent as an array of key-value pairs otherwise
      gen.writeStartArray();
      for (Entry<?, ?> entry : value.entrySet()) {
        gen.writeStartArray();
        gen.writeObject(entry.getKey());
        gen.writeObject(entry.getValue());
        gen.writeEndArray();
      }
      gen.writeEndArray();
    }
  }
}
