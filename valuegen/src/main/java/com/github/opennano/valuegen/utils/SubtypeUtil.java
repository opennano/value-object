package com.github.opennano.valuegen.utils;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.reflections.Reflections;

import com.github.opennano.valuegen.ValueGenerationException;
import com.github.opennano.valuegen.generator.strategies.SubtypeStrategy;

public class SubtypeUtil {

  /**
   * Gets the only non-abstract, non-interface class from the classpath for the provided abstract or
   * interface type. If no such class is found, or multiple candidates would match, the find failed
   * and the method will return back the superclass for convenience.
   *
   * <p>Scanning the classpath is an expensive and slow operation, depending on number of classes
   * present. If this is not desired behavior consider using the {@link SubtypeStrategy#SKIP_TYPES}
   * strategy or providing your own strategy for resolving subtypes.
   *
   * @param superclass the class object for which an instantiable subtype is requested
   * @param interfaces the class object for all interfaces the candidate must implement
   * @return the only instantiable type, or null
   */
  public static Class<?> findUniqueSubType(Class<?> superclass, Class<?>... interfaces) {

    // verify we have something to work with
    if (superclass == null && interfaces.length == 0) {
      throw new ValueGenerationException("no superclass or interfaces provided");
    }

    // make sure the superclass is an abstract class (or not set)
    if (superclass != null && isConcreteType(superclass)) {
      throw new ValueGenerationException(
          "only an abstract class or interfaces should be provided, but got type " + superclass);
    }

    // make sure all interfaces really are interfaces
    for (Class<?> superinterface : interfaces) {
      if (!superinterface.isInterface()) {
        throw new ValueGenerationException(
            "superinterface type is not an interface: " + superinterface);
      }
    }

    // figure out the primary class to use, generally the primary type
    // but we might have only gotten interfaces, in which case we just pick the first
    Class<?> primaryType = (superclass == null) ? interfaces[0] : superclass;
    List<Class<?>> additionalTypes =
        (superclass == null)
            ? Arrays.asList(interfaces).subList(1, interfaces.length)
            : Arrays.asList(interfaces);

    // find all subtypes of the primary type
    // filter out abstract classes and interfaces
    // also remove any that don't implement all additional interfaces
    // if we get more than one, then we can't resolve to a single type
    // in which case we just pass back the input for convenience
    List<Class<?>> candidates =
        new Reflections()
            .getSubTypesOf(primaryType)
            .stream()
            .filter(SubtypeUtil::isConcreteType)
            .filter(type -> implementsAll(type, additionalTypes))
            .limit(2)
            .collect(Collectors.toList());

    return candidates.size() == 1 ? candidates.get(0) : superclass;
  }

  private static boolean isConcreteType(Class<?> type) {
    return !type.isInterface() && !Modifier.isAbstract(type.getModifiers());
  }

  private static boolean implementsAll(Class<?> type, List<Class<?>> superinterfaces) {
    return superinterfaces
        .stream()
        .allMatch(superinterface -> superinterface.isAssignableFrom(type));
  }
  
  private SubtypeUtil() {
	  //no-op: singleton
  }
}
