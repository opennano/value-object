package com.github.opennano.valuegen.generator.delegates;

import static com.github.opennano.reflectionassert.hamcrest.ReflectionEqualsMatcher.reflectionEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.opennano.valuegen.GeneratorConfig;
import com.github.opennano.valuegen.ValueGenerationException;
import com.github.opennano.valuegen.generator.TypeInfo;
import com.github.opennano.valuegen.generator.ValueObjectGenerator;
import com.github.opennano.valuegen.generator.strategies.CycleStrategy;
import com.github.opennano.valuegen.generator.strategies.SubtypeStrategy;

@ExtendWith(MockitoExtension.class)
public class ObjectValueDelegateTest {

  @InjectMocks private ObjectValueDelegate delegate;

  @Mock private ValueObjectGenerator mockValueObjectGenerator;
  @Mock private GeneratorConfig mockGeneratorConfig;

  @Captor private ArgumentCaptor<TypeInfo> captorTypeInfo;
  @Captor private ArgumentCaptor<String> captorString;

  @BeforeEach
  public void createDelegate() {
    when(mockGeneratorConfig.getCycleStrategy()).thenReturn(CycleStrategy.REUSE_ANCESTOR_VALUE);
    when(mockGeneratorConfig.getSubTypeStrategy()).thenReturn(SubtypeStrategy.SKIP_TYPES);
    delegate = new ObjectValueDelegate(mockGeneratorConfig);
  }

  @ParameterizedTest
  @ValueSource(classes = {int.class, Object.class}) // handles anything
  public void handlesClass_handledTypes(Class<?> type) {
    assertThat(delegate.handlesClass(type), is(true));
  }

  @Test
  public void generateValue_emptyObject() {
    TypeInfo mockTypeInfo = new TypeInfo(EmptyObject.class);
    Object value = delegate.generateValue(mockTypeInfo, null, null, null, null);

    assertThat(value, reflectionEquals(new EmptyObject()));
  }

  @Test
  public void generateValue_simpleObject() throws Exception {
    TypeInfo mockTypeInfo = new TypeInfo(SimpleObject.class);

    Field stringField = SimpleObject.class.getDeclaredField("testField");
    when(mockValueObjectGenerator.valueFor(stringField, SimpleObject.class))
        .thenReturn("mockValue");

    Object value = delegate.generateValue(mockTypeInfo, null, null, null, mockValueObjectGenerator);

    SimpleObject expectedObject = new SimpleObject();
    expectedObject.testField = "mockValue";
    assertThat(value, reflectionEquals(expectedObject));
  }

  @Test
  public void generateValue_skipStaticFields() throws Exception {
    TypeInfo mockTypeInfo = new TypeInfo(StaticFieldObject.class);

    Object value = delegate.generateValue(mockTypeInfo, null, null, null, null);

    StaticFieldObject expectedObject = new StaticFieldObject();
    assertThat(value, reflectionEquals(expectedObject));
  }

  @Test
  public void generateValue_skipTransientFields() throws Exception {
    TypeInfo mockTypeInfo = new TypeInfo(TransientFieldObject.class);

    Object value = delegate.generateValue(mockTypeInfo, null, null, null, null);

    TransientFieldObject expectedObject = new TransientFieldObject();
    assertThat(value, reflectionEquals(expectedObject));
  }

  @Test
  public void generateValue_skipFieldsThatFailToSerialize() throws Exception {
    TypeInfo mockTypeInfo = new TypeInfo(SimpleObject.class);

    Field stringField = SimpleObject.class.getDeclaredField("testField");
    when(mockValueObjectGenerator.valueFor(stringField, SimpleObject.class))
        .thenThrow(RuntimeException.class);

    Object value = delegate.generateValue(mockTypeInfo, null, null, null, mockValueObjectGenerator);

    SimpleObject expectedObject = new SimpleObject();
    assertThat(value, reflectionEquals(expectedObject));
  }

  @Test
  public void generateValue_cyclicObject() throws Exception {
    TypeInfo mockTypeInfo = new TypeInfo(SimpleObject.class);

    Field cacheField = delegate.getClass().getDeclaredField("valueCache");
    cacheField.setAccessible(true);
    Map<Class<?>, Object> cache = new HashMap<>();

    SimpleObject cachedValue = new SimpleObject();
    cache.put(SimpleObject.class, cachedValue);
    cacheField.set(delegate, cache);

    Object value = delegate.generateValue(mockTypeInfo, null, null, null, null);

    assertThat(value, sameInstance(value));
  }

  @Test
  public void generateValue_abstractObjectsUseTypeResolver() throws Exception {
    TypeInfo mockTypeInfo = new TypeInfo(AbstractObject.class);

    Field strategyField = delegate.getClass().getDeclaredField("subtypeStrategy");
    strategyField.setAccessible(true);
    strategyField.set(delegate, (SubtypeStrategy) info -> SimpleObject.class);

    Field stringField = SimpleObject.class.getDeclaredField("testField");
    when(mockValueObjectGenerator.valueFor(stringField, SimpleObject.class))
        .thenReturn("mockValue");

    Object value = delegate.generateValue(mockTypeInfo, null, null, null, mockValueObjectGenerator);

    SimpleObject expectedObject = new SimpleObject();
    expectedObject.testField = "mockValue";
    assertThat(value, reflectionEquals(expectedObject));
  }

  @Test
  public void generateValue_subtypeStrategyThrows() throws Exception {
    TypeInfo mockTypeInfo = new TypeInfo(AbstractObject.class);

    Field strategyField = delegate.getClass().getDeclaredField("subtypeStrategy");
    strategyField.setAccessible(true);
    strategyField.set(
        delegate,
        (SubtypeStrategy)
            info -> {
              throw new RuntimeException();
            });

    Object value = delegate.generateValue(mockTypeInfo, null, null, null, null);
    assertThat(value, nullValue());
  }

  @Test
  public void generateValue_subtypeStrategyReturnsNull() throws Exception {
    TypeInfo mockTypeInfo = new TypeInfo(AbstractObject.class);

    Field strategyField = delegate.getClass().getDeclaredField("subtypeStrategy");
    strategyField.setAccessible(true);
    strategyField.set(delegate, (SubtypeStrategy) info -> null);

    Object value = delegate.generateValue(mockTypeInfo, null, null, null, null);
    assertThat(value, nullValue());
  }

  @Test
  public void generateValue_object() {
    TypeInfo mockTypeInfo = new TypeInfo(Object.class);
    Object value = delegate.generateValue(mockTypeInfo, null, null, null, null);

    assertThat(value, reflectionEquals(new Object()));
  }

  @Test
  public void generateValue_proxiedInterface() throws Exception {
    TypeInfo mockTypeInfo = new TypeInfo(Cloneable.class);

    Object value = delegate.generateValue(mockTypeInfo, null, null, null, null);
    assertThat(value.getClass().getSimpleName(), startsWith("Cloneable$$EnhancerByCGLIB$$"));
    assertThat(value, instanceOf(Cloneable.class));
  }

  @Test
  public void generateValue_canCallObjectMethodsOnProxy() throws Exception {
    TypeInfo mockTypeInfo = new TypeInfo(Cloneable.class);

    Object value = delegate.generateValue(mockTypeInfo, null, null, null, null);
    value.toString(); // doesn't throw
  }

  @Test
  public void generateValue_cantCallSubtypeMethodsOnProxy() throws Exception {
    TypeInfo mockTypeInfo = new TypeInfo(Comparable.class);

    Object value = delegate.generateValue(mockTypeInfo, null, null, null, null);
    assertThrows(ValueGenerationException.class, () -> ((Comparable<?>) value).compareTo(null));
  }

  @Test
  public void generateValue_proxiedClassWithInterfaces() throws Exception {
    TypeInfo mockTypeInfo =
        new TypeInfo(AbstractObject.class, new Type[0], new Class[] {Cloneable.class});

    Field strategyField = delegate.getClass().getDeclaredField("subtypeStrategy");
    strategyField.setAccessible(true);
    strategyField.set(delegate, (SubtypeStrategy) info -> AbstractObject.class);

    Object value = delegate.generateValue(mockTypeInfo, null, null, null, null);
    assertThat(value.getClass().getSimpleName(), containsString("$$EnhancerByCGLIB$$"));
    assertThat(value, instanceOf(Cloneable.class));
    assertThat(value, instanceOf(AbstractObject.class));
  }

  @Test
  public void generateValue_subtypeStrategyReturnsNullButHasInterfaces() throws Exception {
    TypeInfo mockTypeInfo =
        new TypeInfo(AbstractObject.class, new Type[0], new Class[] {Cloneable.class});

    Field strategyField = delegate.getClass().getDeclaredField("subtypeStrategy");
    strategyField.setAccessible(true);
    strategyField.set(delegate, (SubtypeStrategy) info -> null);

    Object value = delegate.generateValue(mockTypeInfo, null, null, null, null);
    assertThat(value.getClass().getSimpleName(), containsString("$$EnhancerByCGLIB$$"));
    assertThat(value, instanceOf(Cloneable.class));
    assertThat(value, instanceOf(AbstractObject.class));
  }

  @Test
  public void generateValue_handlesSupertypeFields() throws Exception {
    TypeInfo mockTypeInfo = new TypeInfo(SubtypeObject.class);

    when(mockValueObjectGenerator.valueFor(any(), eq(SubtypeObject.class)))
        .thenReturn("mockValue1")
        .thenReturn("mockValue2");

    Object value = delegate.generateValue(mockTypeInfo, null, null, null, mockValueObjectGenerator);

    SubtypeObject expectedObject = new SubtypeObject();
    expectedObject.testField = "mockValue1";
    expectedObject.subtypeField = "mockValue2";
    assertThat(value, reflectionEquals(expectedObject));
  }

  ////////// TEST CLASSES //////////

  private static class EmptyObject {}

  @SuppressWarnings("unused") // used by reflection
  private static class SimpleObject {
    protected String testField;
  }

  @SuppressWarnings("unused") // used by reflection
  private static class StaticFieldObject {
    private static String testField;
  }

  @SuppressWarnings("unused") // used by reflection
  private static class TransientFieldObject {
    private transient String testField;
  }

  @SuppressWarnings("unused") // used by reflection
  private static class CyclicObject {
    private CyclicObject testField;
  }

  private abstract static class AbstractObject extends SimpleObject {
    @SuppressWarnings("unused") // used by cglib
    public AbstractObject() {
      // no-op: for cglib only
    }
  }

  @SuppressWarnings("unused") // used by reflection
  private static class SubtypeObject extends SimpleObject {
    private String subtypeField;
  }
}
