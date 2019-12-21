package com.github.opennano.valueobject.junit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.opennano.reflectionassert.annotations.ValueObject;
import com.github.opennano.reflectionassert.exceptions.ReflectionAssertionInternalException;
import com.github.opennano.valuegen.Valuegen;

public class ValueObjectExtension
    implements BeforeAllCallback, BeforeEachCallback, ParameterResolver {

  private static final String JSON_EXTENSION = ".json";
  private static final String DASH = "-";

  @JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
  private abstract static class IdentifiableMixin {}

  /** injects static fields that are annotated with {@link @ValueObject} */
  @Override
  public void beforeAll(ExtensionContext context) throws Exception {
    injectFields(context, null, this::isStatic);
  }

  /** injects non-static fields that are annotated with {@link @ValueObject} */
  @Override
  public void beforeEach(ExtensionContext context) throws Exception {
    injectFields(context, context.getRequiredTestInstance(), this::isNotStatic);
  }

  @Override
  public boolean supportsParameter(
      ParameterContext parameterContext, ExtensionContext extensionContext)
      throws ParameterResolutionException {

    return parameterContext.isAnnotated(ValueObject.class);
  }

  @Override
  public Object resolveParameter(
      ParameterContext parameterContext, ExtensionContext extensionContext)
      throws ParameterResolutionException {

    ValueObject annotation = parameterContext.findAnnotation(ValueObject.class).get();
    Parameter parameter = parameterContext.getParameter();
    return readOrGenerate(annotation, parameter.getName(), parameter.getParameterizedType());
  }

  private void injectFields(
      ExtensionContext context, Object testInstance, Predicate<Field> predicate) {

    getTypeHeirarchy(context.getRequiredTestClass())
        .stream()
        .map(Class::getDeclaredFields)
        .flatMap(Stream::of)
        .filter(this::hasAnnotation)
        .filter(predicate)
        .forEach(field -> injectField(field, testInstance));
  }

  private List<Class<?>> getTypeHeirarchy(Class<?> valueClass) {
    List<Class<?>> typeHeirarchy = new ArrayList<>();
    Class<?> ancestorClass = valueClass;
    do {
      typeHeirarchy.add(ancestorClass);
      ancestorClass = ancestorClass.getSuperclass();
    } while (ancestorClass != null && ancestorClass != Object.class);
    return typeHeirarchy;
  }

  private boolean hasAnnotation(Field field) {
    return field.isAnnotationPresent(ValueObject.class);
  }

  private void injectField(Field field, Object testInstance) {
    try {
      ValueObject annotation = field.getAnnotation(ValueObject.class);
      if (!field.isAccessible()) {
        field.setAccessible(true);
      }
      field.set(testInstance, readOrGenerate(annotation, field.getName(), field.getType()));
    } catch (Exception e) {
      throw new ReflectionAssertionInternalException(
          "failed to inject value object into field: " + field, e);
    }
  }

  private Object readOrGenerate(ValueObject annotation, String parameterName, Type type) {
    if (annotation.generate()) {
      return Valuegen.createValueObject(type);
    }

    File sourceFile = getSourceFile(annotation.value(), parameterName);
    return deserialize(sourceFile, type);
  }

  private File getSourceFile(String nameHint, String parameterName) {
    String fileName = nameHint.isEmpty() ? inferName(parameterName) : nameHint;
    return new File("src/test/resources/value-objects/" + fileName);
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
      throw new ParameterResolutionException(
          "failed to read from file: " + jsonFile.getAbsolutePath());
    }
  }

  /** convert the provided object to json via {@link ObjectMapper}. */
  private Object fromJson(String json, Type type) {
    ObjectMapper mapper = new ObjectMapper();
    mapper.setVisibility(PropertyAccessor.ALL, Visibility.NONE);
    mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
    //        mapper.setDateFormat(new SimpleDateFormat(DATE_FORMAT));
    //        mapper.setTimeZone(UTC);
    mapper.setLocale(Locale.US); // for consistency
    mapper.addMixIn(Object.class, IdentifiableMixin.class);

    try {
      return mapper.readValue(json, mapper.getTypeFactory().constructType(type));
    } catch (JsonProcessingException e) {
      throw new IllegalStateException("failed to marshal json to value object: " + type, e);
    }
  }

  private boolean isNotStatic(Field field) {
    return !isStatic(field);
  }

  private boolean isStatic(Field field) {
    return Modifier.isStatic(field.getModifiers());
  }
}
