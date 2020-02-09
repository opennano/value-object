package com.github.opennano.valueobject.junit;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import com.github.opennano.valuegen.Valuegen;

public class ValueObjectExtension
    implements BeforeAllCallback, BeforeEachCallback, ParameterResolver {

  private static final String PATH_PARAMETER_NAME = "value-object.base-path";
  private static final String PATH_PARAMETER_DEFAULT_VALUE = "src/test/resources/value-objects";

  private ObjectMarshaller marshaller = new ObjectMarshaller();

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
  public Object resolveParameter(ParameterContext parameterContext, ExtensionContext context)
      throws ParameterResolutionException {

    Optional<String> valueObjectBasePath = context.getConfigurationParameter(PATH_PARAMETER_NAME);
    ValueObject annotation = parameterContext.findAnnotation(ValueObject.class).get();
    Parameter parameter = parameterContext.getParameter();
    return readOrGenerate(
        annotation, parameter.getName(), parameter.getParameterizedType(), valueObjectBasePath);
  }

  private void injectFields(
      ExtensionContext context, Object testInstance, Predicate<Field> predicate) {

    Optional<String> valueObjectBasePath = context.getConfigurationParameter(PATH_PARAMETER_NAME);
    getTypeHierarchy(context.getRequiredTestClass())
        .stream()
        .map(Class::getDeclaredFields)
        .flatMap(Stream::of)
        .filter(this::hasAnnotation)
        .filter(predicate)
        .forEach(field -> injectField(field, testInstance, valueObjectBasePath));
  }

  private List<Class<?>> getTypeHierarchy(Class<?> valueClass) {
    List<Class<?>> typeHeirarchy = new ArrayList<>();
    Class<?> ancestorClass = valueClass;
    do {
      typeHeirarchy.add(ancestorClass);
      ancestorClass = ancestorClass.getSuperclass();
    } while (ancestorClass != Object.class);
    return typeHeirarchy;
  }

  private boolean hasAnnotation(Field field) {
    return field.isAnnotationPresent(ValueObject.class);
  }

  private void injectField(Field field, Object testInstance, Optional<String> valueObjectBasePath) {
    ValueObject annotation = field.getAnnotation(ValueObject.class);
    field.setAccessible(true);
    try {
      field.set(
          testInstance,
          readOrGenerate(annotation, field.getName(), field.getType(), valueObjectBasePath));
    } catch (IllegalAccessException e) {
      throw new FieldInjectionException("failed to inject value object into field: " + field, e);
    }
  }

  private Object readOrGenerate(
      ValueObject annotation,
      String parameterName,
      Type type,
      Optional<String> valueObjectBasePath) {

    return annotation.generate()
        ? Valuegen.createValueObject(type)
        : marshaller.marshalJson(
            annotation, parameterName, type, valueObjectBasePath.orElse(PATH_PARAMETER_DEFAULT_VALUE));
  }

  private boolean isNotStatic(Field field) {
    return !isStatic(field);
  }

  private boolean isStatic(Field field) {
    return Modifier.isStatic(field.getModifiers());
  }
}
