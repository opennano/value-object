package com.github.opennano.valuegen.utils;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AssignableTypeFilter;

import com.github.opennano.valuegen.ValueGenerationException;
import com.github.opennano.valuegen.generator.strategies.SubtypeStrategy;

public class SubtypeUtil {

  public static class ConcreteImplementationsFilter extends AssignableTypeFilter {

    private List<AssignableTypeFilter> typeFilters;

    public ConcreteImplementationsFilter(Collection<Class<?>> types) {
      super(null);
      this.typeFilters = types.stream().map(AssignableTypeFilter::new).collect(Collectors.toList());
    }

    @Override
    public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory)
        throws IOException {

      ClassMetadata metadata = metadataReader.getClassMetadata();
      if (!metadata.isConcrete()) {
        return false;
      }

      return typeFilters
          .stream()
          .allMatch(
              filter -> {
                try {
                  return filter.match(metadataReader, metadataReaderFactory);
                } catch (IOException e) {
                  // TODO Auto-generated catch block
                  e.printStackTrace();
                }
                return false;
              });
    }
  }

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
    Set<Class<?>> types = new HashSet<>(Arrays.asList(interfaces));
    if (superclass != null) {
      types.add(superclass);
    }

    // find all subtypes of the primary type
    // filter out abstract classes and interfaces
    // also remove any that don't implement all additional interfaces
    // if we get more than one, then we can't resolve to a single type
    // in which case we just pass back the input for convenience

    ClassPathScanningCandidateComponentProvider provider =
        new ClassPathScanningCandidateComponentProvider(false);
    provider.addIncludeFilter(new ConcreteImplementationsFilter(types));

    List<Class<?>> candidates =
        provider
            .findCandidateComponents("")
            .stream()
            .map(
                defn -> {
                  try {
                    return Class.forName(defn.getBeanClassName());
                  } catch (ClassNotFoundException e) {
                    // no-op: just ignore this type then
                  }
                  return null;
                })
            .filter(Objects::nonNull)
            .limit(2)
            .collect(Collectors.toList());

    return candidates.size() == 1 ? candidates.get(0) : superclass;
  }

  private static boolean isConcreteType(Class<?> type) {
    return !type.isInterface() && !Modifier.isAbstract(type.getModifiers());
  }

  private SubtypeUtil() {
    // no-op: singleton
  }
}
