package com.github.opennano.testutils;

import static com.github.opennano.testutils.JavaBeanTestUtil.assertValidJavaBeans;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.github.opennano.testutils.beans.BeanWithBrokenGetter;
import com.github.opennano.testutils.beans.invalid.BeanWithBrokenSetter;
import com.github.opennano.testutils.beans.invalid.BeanWithoutZeroArgCtor;
import com.github.opennano.testutils.beans.valid.BeanWithExtraCtor;
import com.github.opennano.testutils.beans.valid.BeanWithExtraMethods;
import com.github.opennano.testutils.beans.valid.BeanWithOverrides;
import com.github.opennano.testutils.beans.valid.BeanWithVariousProperties;
import com.github.opennano.testutils.beans.valid.BeanWithWeirdFieldAccessors;
import com.github.opennano.testutils.beans.valid.BeanWithWeirdMethodAccessors;

public class JavaBeanTestUtilTest {

  ////////// single bean tests //////////

  @Test
  public void assertValidJavaBeans_simpleBean() {
    assertValidJavaBeans(BeanWithVariousProperties.class);
  }

  /** setter doesn't set the right value */
  @Test
  public void assertValidJavaBeans_brokenSetter() {
    assertThrows(AssertionError.class, () -> assertValidJavaBeans(BeanWithBrokenSetter.class));
  }

  /** getter doesn't return the right value */
  @Test
  public void assertValidJavaBeans_brokenGetter() {
    assertThrows(AssertionError.class, () -> assertValidJavaBeans(BeanWithBrokenGetter.class));
  }

  /** extra constructors are ignored */
  @Test
  public void assertValidJavaBeans_extraCtor() {
    assertValidJavaBeans(BeanWithExtraCtor.class);
  }

  /** fail if there's no zero-arg constructor */
  @Test
  public void assertValidJavaBeans_noCtor() {
    assertThrows(AssertionError.class, () -> assertValidJavaBeans(BeanWithoutZeroArgCtor.class));
  }

  /** toString, equals, hashcode, etc. are ignored */
  @Test
  public void assertValidJavaBeans_objectOverrides() {
    assertValidJavaBeans(BeanWithOverrides.class);
  }

  /** extra methods are ignored */
  @Test
  public void assertValidJavaBeans_extraMethods() {
    assertValidJavaBeans(BeanWithExtraMethods.class);
  }

  /** extra methods are ignored */
  @Test
  public void assertValidJavaBeans_weirdFields() {
    assertValidJavaBeans(BeanWithWeirdFieldAccessors.class);
  }

  /** extra methods are ignored */
  @Test
  public void assertValidJavaBeans_weirdMethods() {
    assertValidJavaBeans(BeanWithWeirdMethodAccessors.class);
  }

  ////////// bean collection tests //////////

  /** multiple beans at once works */
  @Test
  public void assertValidJavaBeans_manyBeans() {
    assertValidJavaBeans(BeanWithVariousProperties.class, BeanWithExtraCtor.class);
  }

  /** if any bean fails the whole test fails */
  @Test
  public void assertValidJavaBeans_someInvalidBeans() {
    assertThrows(
        AssertionError.class,
        () -> assertValidJavaBeans(BeanWithVariousProperties.class, BeanWithoutZeroArgCtor.class));
  }

  @Test // asinine test for 100% coverage
  public void assertIsNotInstantiable() {
    SingletonTestUtil.assertIsNotInstantiable(JavaBeanTestUtil.class);
  }
}
