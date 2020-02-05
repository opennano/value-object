package com.github.opennano.valuegen.utils;

import static com.github.opennano.valuegen.utils.SubtypeUtil.findUniqueSubType;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.opennano.valuegen.ValueGenerationException;
import com.github.opennano.valuegen.testutils.SingletonTestUtil;

@SuppressWarnings("unused") // used by a classpath scanning library
@ExtendWith(MockitoExtension.class)
public class SubtypeUtilTest {

  @Test
  public void findUniqueSubType_noSubtypes() {
    assertThat(
        findUniqueSubType(UnimplementedInterface.class), equalTo(UnimplementedInterface.class));
  }

  @Test
  public void findUniqueSubType_oneSubtype() {
    assertThat(
        findUniqueSubType(SingleImplementationInterface.class),
        equalTo(SingleImplementationClass.class));
  }

  @Test
  public void findUniqueSubType_multipleSubtypes() {
    assertThat(
        findUniqueSubType(MultipleImplementationInterface.class),
        equalTo(MultipleImplementationInterface.class));
  }

  @Test
  public void findUniqueSubType_onlyConcreteTypesConsidered() {
    assertThat(
        findUniqueSubType(null, VariousImplementationsInterface.class),
        equalTo(ConcreteImplementationClass.class));
  }

  @Test
  public void findUniqueSubType_onlyInterfaces() {
    assertThat(
        findUniqueSubType(null, VariousImplementationsInterface.class, Cloneable.class),
        equalTo(ConcreteImplementationClass.class));
  }

  @Test
  public void findUniqueSubType_classIncludedInInterfaces() {
    assertThrows(
        ValueGenerationException.class,
        () -> findUniqueSubType(null, VariousImplementationsInterface.class, Object.class));
  }

  @Test
  public void findUniqueSubType_superClassIsConcrete() {
    assertThrows(ValueGenerationException.class, () -> findUniqueSubType(Object.class));
  }

  @Test
  public void findUniqueSubType_noUsefulArgs() {
    assertThrows(ValueGenerationException.class, () -> findUniqueSubType(null));
  }

  @Test
  public void assertSingleton() {
    SingletonTestUtil.assertIsNotInstantiable(AutoboxingUtil.class);
  }

  private static interface UnimplementedInterface {}

  private static interface SingleImplementationInterface {}

  private static interface MultipleImplementationInterface {}

  private static interface VariousImplementationsInterface {}

  private static class SingleImplementationClass implements SingleImplementationInterface {}

  private static class MultipleImplementationClass1 implements MultipleImplementationInterface {}

  private static class MultipleImplementationClass2 implements MultipleImplementationInterface {}

  private static interface MultipleExtensionsInterface
      extends VariousImplementationsInterface, Cloneable {}

  private abstract static class AbstractImplementationClass
      implements VariousImplementationsInterface {}

  private static class ConcreteImplementationClass
      implements Cloneable, VariousImplementationsInterface {}
}
