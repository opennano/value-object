package com.github.opennano.valuegen.generator.strategies;

import static com.github.opennano.valuegen.utils.SubtypeUtil.*;

import com.github.opennano.valuegen.generator.TypeInfo;

public interface SubtypeStrategy {

  /**
   * This strategy is a no-op: no attempt is made to find a suitable instantiable type, saving a
   * potentially expensive full classpath scan.
   */
  static SubtypeStrategy SKIP_TYPES = TypeInfo::getResolvedClass;

  /**
   * This strategy tries to find a unique subtype of the given abstract class and interface(s) by
   * scanning the entire classpath. If exactly one (non-abstract, non-interface) matching subtype is
   * found it is used as a value object, otherwise the abstract or interface type will be proxied.
   */
  static SubtypeStrategy UNIQUE_SUBTYPE =
      info -> findUniqueSubType(info.getSuperclass(), info.getInterfaces());

  Class<?> apply(TypeInfo typeInfo);
}
