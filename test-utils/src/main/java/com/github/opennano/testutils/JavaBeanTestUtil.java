package com.github.opennano.testutils;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Arrays;
import java.util.Collection;

// TODO this is an unnecessary functionality-limiting wrapper when using Hamcrest
public class JavaBeanTestUtil {

  /**
   * Tests all the specified java beans. This convenience method is equivalent to calling {@link
   * #assertValidJavaBeans(Collection)} with a collection of the provided elements.
   *
   * @param types a vararg array of types to validate
   */
  public static void assertValidJavaBeans(Class<?>... types) {
    assertValidJavaBeans(Arrays.asList(types));
  }

  /**
   * Tests all the specified java beans. The goal of this method is to assert that all java beans
   * are behaving according to the java bean contract, but avoid the boilerplate test code that
   * would be required for testing vanilla constructors and getters and setters.
   *
   * <p>Java beans may have additional member methods, which will be ignored, unless they look like
   * getters and setters. In this case, it's best not to use this convenience method.
   *
   * <p>This test will assert that there is a valid no-arg constructor and working getters and
   * setters in each pojo. Anything beyond that, including custom or mismatched getters/setters,
   * custom constructors, customized hashcode, equals, toString, or any extra methods will not be
   * tested. Such methods should be tested as proper unit tests.
   *
   * @param types a collection of types to validate
   */
  public static void assertValidJavaBeans(Collection<Class<?>> types) {
    types.forEach(JavaBeanTestUtil::assertValidJavaBean);
  }

  private static void assertValidJavaBean(Class<?> type) {
    assertThat(type, allOf(hasValidBeanConstructor(), hasValidGettersAndSetters()));
  }

  private JavaBeanTestUtil() {
    // no-op: singleton
  }
}
