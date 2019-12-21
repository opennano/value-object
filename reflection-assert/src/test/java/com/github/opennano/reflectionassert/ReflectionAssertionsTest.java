package com.github.opennano.reflectionassert;

import static com.github.opennano.reflectionassert.ReflectionAssertions.assertLenientEquals;
import static com.github.opennano.reflectionassert.ReflectionAssertions.assertReflectionEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.sql.Date;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import com.github.opennano.reflectionassert.testutils.SingletonTestUtil;

/** a few quick sanity tests--end-to-end tests do a far more thorough job */
public class ReflectionAssertionsTest {

  @Test
  public void assertUninstantiable() {
    SingletonTestUtil.assertIsNotInstantiable(ReflectionAssertions.class);
  }

  @Test
  public void assertReflectionEquals_simplePass() {
    assertReflectionEquals(1, 1);
  }

  @Test
  public void assertLenientEquals_simplePassLenient() {
    assertLenientEquals(1, 1);
  }

  @Test
  public void assertReflectionEquals_nullExpectedStrict() {
    assertThrows(Exception.class, () -> assertReflectionEquals(null, 1));
  }

  @Test
  public void assertReflectionEquals_nullExpectedLenient() {
    assertReflectionEquals(new Date(1), new Date(0), LeniencyMode.LENIENT_DATES);
  }

  @Test
  public void assertReflectionEquals_unorderedCollectionStrict() {
    assertThrows(
        Exception.class, () -> assertReflectionEquals(Arrays.asList(1, 2), Arrays.asList(2, 1)));
  }

  @Test
  public void assertReflectionEquals_unorderedCollectionLenient() {
    assertLenientEquals(Arrays.asList(1, 2), Arrays.asList(2, 1));
  }
}
