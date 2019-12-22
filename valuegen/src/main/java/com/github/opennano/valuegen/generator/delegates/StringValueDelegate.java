package com.github.opennano.valuegen.generator.delegates;

import static com.github.opennano.valuegen.generator.ValueGeneratorDelegate.isInstanceOfAny;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import com.github.opennano.valuegen.generator.TypeInfo;
import com.github.opennano.valuegen.generator.ValueGeneratorDelegate;
import com.github.opennano.valuegen.generator.ValueObjectGenerator;

public class StringValueDelegate implements ValueGeneratorDelegate {

  protected static final String DEFAULT_VALUE = "string";
  protected static final String VALUE_PREFIX = "mock";

  /** make sure we don't duplicate values, even if the field names are the same */
  private Set<String> usedNames = new HashSet<>();

  @Override
  public boolean handlesClass(Class<?> type) {
    return isInstanceOfAny(type, CharSequence.class);
  }

  @Override
  public Object generateValue(
      TypeInfo typeInfo,
      Field declaringField,
      String nameHint,
      Class<?> owningClass,
      ValueObjectGenerator valueGenerator) {

    return constructName(nameHint, DEFAULT_VALUE, VALUE_PREFIX, true);
  }

  protected String constructName(
      String nameHint, String defaultName, String prefix, boolean capitalizeFirst) {

    String fieldName = Optional.ofNullable(nameHint).orElse(defaultName);
    String firstLetterUpper = fieldName.substring(0, 1);
    if (capitalizeFirst) {
      firstLetterUpper = firstLetterUpper.toUpperCase();
    }
    String preferredValue = prefix + firstLetterUpper + fieldName.substring(1);
    return getUniqueValue(preferredValue);
  }

  private String getUniqueValue(String preferredValue) {
    int uniqueSuffix = 1;
    String candidateValue = preferredValue;
    while (usedNames.contains(candidateValue)) {
      uniqueSuffix++;
      candidateValue = preferredValue + uniqueSuffix;
    }
    usedNames.add(candidateValue);
    return candidateValue;
  }
}
