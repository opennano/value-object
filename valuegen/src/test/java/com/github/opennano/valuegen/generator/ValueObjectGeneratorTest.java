package com.github.opennano.valuegen.generator;

import static com.github.opennano.reflectionassert.hamcrest.ReflectionEqualsMatcher.lenientEquals;
import static com.github.opennano.reflectionassert.hamcrest.ReflectionEqualsMatcher.reflectionEquals;
import static com.github.opennano.valuegen.generator.ValueObjectGenerator.createDefaultDelegateChain;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.opennano.valuegen.GeneratorConfig;
import com.github.opennano.valuegen.generator.delegates.ArrayValueDelegate;
import com.github.opennano.valuegen.generator.delegates.BooleanValueDelegate;
import com.github.opennano.valuegen.generator.delegates.CharacterValueDelegate;
import com.github.opennano.valuegen.generator.delegates.CollectionValueDelegate;
import com.github.opennano.valuegen.generator.delegates.DateValueDelegate;
import com.github.opennano.valuegen.generator.delegates.EnumValueDelegate;
import com.github.opennano.valuegen.generator.delegates.FileValueDelegate;
import com.github.opennano.valuegen.generator.delegates.MapValueDelegate;
import com.github.opennano.valuegen.generator.delegates.NumberValueDelegate;
import com.github.opennano.valuegen.generator.delegates.ObjectValueDelegate;
import com.github.opennano.valuegen.generator.delegates.StringValueDelegate;
import com.github.opennano.valuegen.generator.delegates.UrlValueDelegate;

@ExtendWith(MockitoExtension.class)
public class ValueObjectGeneratorTest {

  private final class MockValueDelegate implements ValueGeneratorDelegate {

    @Override
    public boolean handlesClass(Class<?> type) {
      return false;
    }

    @Override
    public Object generateValue(
        TypeInfo typeInfo,
        Field declaringField,
        String nameHint,
        Class<?> owningClass,
        ValueObjectGenerator valueObjectGenerator) {

      return null;
    }
  }

  private static final Object DUMMY_OBJECT = new Object();
  private static final GeneratorConfig DEFAULT_CONFIG = new GeneratorConfig();
  private static final List<ValueGeneratorDelegate> DEFAULT_CHAIN =
      Arrays.asList(
          new StringValueDelegate(),
          new NumberValueDelegate(),
          new BooleanValueDelegate(),
          new DateValueDelegate(),
          new EnumValueDelegate(),
          new FileValueDelegate(),
          new CharacterValueDelegate(),
          new UrlValueDelegate(),
          new CollectionValueDelegate(),
          new ArrayValueDelegate(),
          new MapValueDelegate(DEFAULT_CONFIG),
          new ObjectValueDelegate(DEFAULT_CONFIG));

  @Mock private ValueGeneratorDelegate mockDelegate;

  @Captor private ArgumentCaptor<TypeInfo> captorTypeInfo;

  private Field sampleField;

  {
    try {
      sampleField = ValueObjectGeneratorTest.class.getDeclaredField("sampleField");
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  public void createDefaultDelegateChain_defaultChain() {
    assertThat(DEFAULT_CHAIN, reflectionEquals(createDefaultDelegateChain(DEFAULT_CONFIG)));
  }

  @Test
  public void ctorWithConfig_noAdditionalGenerators() {
    assertThat(
        new ValueObjectGenerator(DEFAULT_CONFIG),
        reflectionEquals(new ValueObjectGenerator(DEFAULT_CHAIN)));
  }

  @Test
  public void ctorWithConfig_additionalGenerators() {
    ValueGeneratorDelegate extraDelegate = new MockValueDelegate();

    List<ValueGeneratorDelegate> customChain = new ArrayList<>(13);
    customChain.add(extraDelegate);
    customChain.addAll(DEFAULT_CHAIN);

    assertThat(
        new ValueObjectGenerator(DEFAULT_CONFIG, extraDelegate),
        reflectionEquals(new ValueObjectGenerator(customChain)));
  }

  @Test
  public void valueFor_delegateFound() {
    TypeInfo typeInfo = new TypeInfo(Integer.class);
    ValueObjectGenerator generator = new ValueObjectGenerator(Arrays.asList(mockDelegate));

    when(mockDelegate.handlesClass(Integer.class)).thenReturn(true);
    when(mockDelegate.generateValue(
            typeInfo, sampleField, "mockNameHint", Integer.class, generator))
        .thenReturn(DUMMY_OBJECT);

    assertThat(
        generator.valueFor(typeInfo, sampleField, "mockNameHint", Integer.class),
        equalTo(DUMMY_OBJECT));
  }

  @Test
  public void valueFor_delegateNotFound() {
    TypeInfo typeInfo = new TypeInfo(Integer.class);
    ValueObjectGenerator generator = new ValueObjectGenerator(Arrays.asList(mockDelegate));

    when(mockDelegate.handlesClass(Integer.class)).thenReturn(false);

    assertThrows(
        IllegalStateException.class,
        () -> generator.valueFor(typeInfo, sampleField, "mockNameHint", Integer.class));
  }

  @Test
  public void valueForWithField_delegateFound() {
    TypeInfo expectedTypeInfo = new TypeInfo(Field.class);
    ValueObjectGenerator generator = new ValueObjectGenerator(Arrays.asList(mockDelegate));

    when(mockDelegate.handlesClass(Field.class)).thenReturn(true);
    when(mockDelegate.generateValue(
            captorTypeInfo.capture(),
            eq(sampleField),
            eq("sampleField"),
            eq(ValueObjectGeneratorTest.class),
            eq(generator)))
        .thenReturn(DUMMY_OBJECT);

    Object valueObject = generator.valueFor(sampleField, ValueObjectGeneratorTest.class);
    assertThat(valueObject, equalTo(DUMMY_OBJECT));
    assertThat(captorTypeInfo.getValue(), lenientEquals(expectedTypeInfo));
  }
}
