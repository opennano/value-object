package com.github.opennano.reflectionassert.report;

import java.io.File;

/** formats any object into a user-friendly string, for example wrapping a string in quotes */
public class ValueFormatter {

  private static final String NULL_STRING = "null";
  private static final String SINGLE_QUOTED = "'%s'";
  private static final String DOUBLE_QUOTED = "\"%s\"";
  private static final String CLASS_TEMPLATE = "object of type '%s'";
  private static final String FILE_TEMPLATE = "File<%s>";

  public String format(Object value) {
    if (value == null) {
      return NULL_STRING;
    }
    if (value instanceof CharSequence) {
      return String.format(DOUBLE_QUOTED, value);
    }
    if (value instanceof Character) {
      return String.format(SINGLE_QUOTED, value);
    }
    if (value instanceof Class) {
      return String.format(CLASS_TEMPLATE, ((Class<?>) value).getName());
    }
    if (value instanceof File) {
      return String.format(FILE_TEMPLATE, ((File) value).getPath());
    }
    return value.toString();
  }
}
