package com.github.opennano.valueobject.junit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ObjectMarshaller {

  private static final String JSON_EXTENSION = ".json";
  private static final String DASH = "-";

  public Object marshalJson(
      ValueObject annotation, String parameterName, Type type, String valueObjectsBasePath) {
    File sourceFile = getSourceFile(annotation.value(), parameterName, valueObjectsBasePath);
    return deserialize(sourceFile, type);
  }

  private File getSourceFile(String nameHint, String parameterName, String valueObjectsBasePath) {
    String fileName = nameHint.isEmpty() ? inferName(parameterName) : nameHint;
    return new File(valueObjectsBasePath, fileName);
  }

  private String inferName(String parameterName) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < parameterName.length(); i++) {
      char letter = parameterName.charAt(i);
      if (Character.isUpperCase(letter)) {
        sb.append(DASH).append(Character.toLowerCase(letter));
      } else {
        sb.append(letter);
      }
    }
    return sb.append(JSON_EXTENSION).toString();
  }

  private Object deserialize(File jsonFile, Type type) {
    StringBuilder sb = new StringBuilder();
    try (BufferedReader reader = new BufferedReader(new FileReader(jsonFile))) {
      String line;
      while ((line = reader.readLine()) != null) {
        sb.append(line);
      }
      return fromJson(sb.toString(), type);
    } catch (IOException e) {
      throw new ValueObjectDeserializationException(
          "failed to read from file: " + jsonFile.getAbsolutePath());
    }
  }

  /** convert the provided object to json via {@link ObjectMapper}. */
  private Object fromJson(String json, Type type) {
    ObjectMapper mapper = new ObjectMapper();
    mapper.setVisibility(PropertyAccessor.ALL, Visibility.NONE);
    mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
    mapper.setLocale(Locale.US); // for consistency
    mapper.addMixIn(Object.class, IdentifiableMixin.class);

    try {
      return mapper.readValue(json, mapper.getTypeFactory().constructType(type));
    } catch (JsonProcessingException e) {
      throw new ValueObjectDeserializationException(
          "failed to marshal json to value object: " + type, e);
    }
  }

  @JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
  private abstract static class IdentifiableMixin {}
}
