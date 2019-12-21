package com.github.opennano.valuegen.generator.strategies;

import java.util.Optional;

import com.github.opennano.valuegen.ValueGenerationException;

public interface MapKeyStrategy {

  /**
   * Attempts to interpret a map key type as a String if possible (e.g. if Object was specified),
   * otherwise uses whatever type was specified
   */
  static MapKeyStrategy ANY_KEY_TYPE = type -> stringIfPossible(type).orElse(type);

  static MapKeyStrategy STRING_KEYS_ONLY =
      type ->
          stringIfPossible(type)
              .orElseThrow(() -> new ValueGenerationException("unsupported key type " + type));

  static Optional<Class<?>> stringIfPossible(Class<?> type) {
    return Optional.of(type)
        .filter(cls -> cls.isAssignableFrom(String.class))
        .map(cls -> String.class);
  }

  Class<?> apply(Class<?> resolvedKeyClass);
}
