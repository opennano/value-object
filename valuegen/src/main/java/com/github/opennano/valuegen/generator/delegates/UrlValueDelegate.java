package com.github.opennano.valuegen.generator.delegates;

import static com.github.opennano.valuegen.generator.ValueGeneratorDelegate.isInstanceOfAny;

import java.lang.reflect.Field;
import java.net.URI;
import java.net.URL;

import com.github.opennano.valuegen.ValueGenerationException;
import com.github.opennano.valuegen.generator.TypeInfo;
import com.github.opennano.valuegen.generator.ValueObjectGenerator;

public class UrlValueDelegate extends StringValueDelegate {

  // always use an invalid domain so we don't spam a real site by mistake!
  private static final String DEFAULT_VALUE = "url";
  private static final String VALUE_PREFIX = "http://domain.mock/";

  @Override
  public boolean handlesClass(Class<?> type) {
    return isInstanceOfAny(type, URI.class, URL.class);
  }

  @Override
  public Object generateValue(
      TypeInfo typeInfo,
      Field declaringField,
      String nameHint,
      Class<?> owningClass,
      ValueObjectGenerator valueGenerator) {

    String url = constructName(nameHint, DEFAULT_VALUE, VALUE_PREFIX, false);
    return instantiate(typeInfo.getResolvedClass(), url);
  }

  private Object instantiate(Class<?> type, String url) {
    try {
      // both types have string constructors we can use
      return type.getConstructor(String.class).newInstance(url);
    } catch (ReflectiveOperationException | IllegalArgumentException | SecurityException e) {
      String message = "error invoking string arg constructor for type " + type.getSimpleName();
      throw new ValueGenerationException(message, e);
    }
  }
}
