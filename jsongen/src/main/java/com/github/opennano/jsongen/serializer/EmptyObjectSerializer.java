package com.github.opennano.jsongen.serializer;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;

public class EmptyObjectSerializer extends JsonSerializer<Object> {

  @Override
  public Class<Object> handledType() {
    return Object.class;
  }

  public void serializeWithType(
      Object value, JsonGenerator gen, SerializerProvider serializers, TypeSerializer typeSer)
      throws IOException {

    serialize(value, gen, serializers);
  }

  @Override
  public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers)
      throws IOException {

    gen.writeStartObject();
    gen.writeEndObject();
  }
}
