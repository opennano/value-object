package com.github.opennano.valuegen;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.lang.reflect.Type;

import org.junit.jupiter.api.Test;

import com.github.opennano.valuegen.Valuegen;

/**
 * Since most of the methods here are static, coverage largely depends on scenario tests. What is
 * left here are just paths not easily covered by a scenario.
 */
public class ValuegenTest {

  @Test
  public void createValueObject_worksForNonClassTypes() throws Exception {
    Type type = ParameterizedField.class.getDeclaredField("testedField").getGenericType();
    Object valueObject = Valuegen.createValueObject(type);
    assertThat(valueObject.getClass(), equalTo(Object.class));
  }

  private static class ParameterizedField<T> {
    @SuppressWarnings("unused") // used by reflection
    private T testedField;
  }
}
