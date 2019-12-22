package com.github.opennano.valuegen.generator.delegates;

import static com.github.opennano.valuegen.generator.ValueGeneratorDelegate.isInstanceOfAny;

import java.io.File;
import java.lang.reflect.Field;

import com.github.opennano.valuegen.generator.TypeInfo;
import com.github.opennano.valuegen.generator.ValueObjectGenerator;

public class FileValueDelegate extends StringValueDelegate {

  private static final String DEFAULT_VALUE = "file";
  protected static final String VALUE_PREFIX = "";

  public FileValueDelegate() {}

  @Override
  public boolean handlesClass(Class<?> type) {
    return isInstanceOfAny(type, File.class);
  }

  @Override
  public Object generateValue(
      TypeInfo typeInfo,
      Field declaringField,
      String nameHint,
      Class<?> owningClass,
      ValueObjectGenerator valueGenerator) {

    return new File("/mock/path/" + constructName(nameHint, DEFAULT_VALUE, VALUE_PREFIX, false));
  }
}
