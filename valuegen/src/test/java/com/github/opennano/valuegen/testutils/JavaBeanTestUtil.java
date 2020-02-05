package com.github.opennano.valuegen.testutils;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Arrays;
import java.util.Collection;

import org.junit.jupiter.api.Test;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.AnnotationMetadataReadingVisitor;
import org.springframework.core.type.filter.AbstractClassTestingTypeFilter;

public class JavaBeanTestUtil {

  /**
   * Tests all the specified java beans. This convenience method is equivalent to calling {@link
   * #assertValidJavaBeans(Collection)} with a collection of the provided elements.
   */
  public static void assertValidJavaBeans(Class<?>... types) {
    assertValidJavaBeans(Arrays.asList(types));
  }

  /**
   * Tests all the specified java beans. The goal of this method is to assert that all java beans
   * are behaving according to the java bean contract, but avoid the boilerplate that would be
   * required for testing getters and setters.
   *
   * <p>This test will assert that there is a valid no-arg constructor and working getters and
   * setters in each pojo. Anything beyond that, including custom or mismatched getters/setters,
   * custom constructors, customized hashcode, equals, toString, or any extra methods will not be
   * tested. Such methods should be tested as proper unit tests.
   */
  public static void assertValidJavaBeans(Collection<Class<?>> types) {
    types.forEach(JavaBeanTestUtil::assertValidJavaBean);
  }

  private static void assertValidJavaBean(Class<?> type) {
    assertThat(type, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
    try {
      assertToStringHandlesNulls(type.getDeclaredConstructor().newInstance());
    } catch (Exception e) {
      throw new IllegalArgumentException("java bean validation failed", e);
    }
  }

  /** tests that toString method handles null values properly */
  public static void assertToStringHandlesNulls(Class<?> type) {
    TestReflectionUtils.defaultObjectFill(type).toString(); // doesn't throw
  }

  /** tests that toString method handles null values properly */
  public static void assertToStringHandlesNulls(Object target) {
    TestReflectionUtils.defaultObjectFill(target).toString(); // doesn't throw
  }

  public static class InstantiableBeanFilter extends AbstractClassTestingTypeFilter {

    @Override
    protected boolean match(ClassMetadata metadata) {
      return metadata.isConcrete()
          && ((metadata instanceof AnnotationMetadataReadingVisitor)
              && !((AnnotationMetadataReadingVisitor) metadata)
                  .hasAnnotatedMethods(Test.class.getCanonicalName()))
          && metadata.isIndependent()
          && !metadata.getSuperClassName().equals(Enum.class.getName());
    }
  }

  private JavaBeanTestUtil() {
    // no-op: singleton
  }
}
