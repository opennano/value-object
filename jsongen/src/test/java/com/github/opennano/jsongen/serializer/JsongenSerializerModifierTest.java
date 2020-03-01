package com.github.opennano.jsongen.serializer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JsonSerializer;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.FixedValue;

@ExtendWith(MockitoExtension.class)
public class JsongenSerializerModifierTest {

  @Mock private JsonSerializer<?> mockJsonSerializer;
  @Mock private BeanDescription mockBeanDescription;
  @InjectMocks private JsongenSerializerModifier modifier;

  @Test
  public void isProxy_falseForRegularClass() {
    assertThat(JsongenSerializerModifier.isProxy(Object.class), is(false));
  }

  @Test
  public void isProxy_trueForCglibProxy() {
    Enhancer enhancer = new Enhancer();
    enhancer.setSuperclass(Object.class);
    enhancer.setCallback((FixedValue) () -> "Hello World");
    Object proxy = enhancer.create();

    assertThat(JsongenSerializerModifier.isProxy(proxy.getClass()), is(true));
  }

  @Test
  @SuppressWarnings({"rawtypes", "unchecked"}) // getBeanClass generic is not mock friendly
  public void modifyArraySerializer_modifiedForByteArray() {
    when(mockBeanDescription.getBeanClass()).thenReturn((Class) byte[].class);

    JsonSerializer<?> serializer =
        modifier.modifyArraySerializer(null, null, mockBeanDescription, mockJsonSerializer);

    assertThat(serializer.getClass(), equalTo(ByteArrayAsIntArraySerializer.class));
  }

  @Test
  @SuppressWarnings({"rawtypes", "unchecked"}) // getBeanClass generic is not mock friendly
  public void modifyArraySerializer_notModifiedForOtherTypes() {
    when(mockBeanDescription.getBeanClass()).thenReturn((Class) Byte[].class);

    JsonSerializer<?> serializer =
        modifier.modifyArraySerializer(null, null, mockBeanDescription, mockJsonSerializer);

    assertThat(serializer, sameInstance(mockJsonSerializer));
  }

  @Test
  public void modifyMapSerializer_returnsCustom() {
    JsonSerializer<?> serializer =
        modifier.modifyMapSerializer(null, null, mockBeanDescription, mockJsonSerializer);

    assertThat(serializer.getClass(), equalTo(CustomMapSerializer.class));
  }

  @Test
  @SuppressWarnings({"rawtypes", "unchecked"}) // getBeanClass generic is not mock friendly
  public void modifySerializer_nonProxy() {
    when(mockBeanDescription.getBeanClass()).thenReturn((Class) Object.class);

    JsonSerializer<?> serializer =
        modifier.modifySerializer(null, mockBeanDescription, mockJsonSerializer);

    assertThat(serializer, sameInstance(mockJsonSerializer));
  }

  @Test
  @SuppressWarnings({"rawtypes", "unchecked"}) // getBeanClass generic is not mock friendly
  public void modifySerializer_proxy() {
    Enhancer enhancer = new Enhancer();
    enhancer.setSuperclass(Object.class);
    enhancer.setCallback((FixedValue) () -> "Hello World");
    Object proxy = enhancer.create();

    when(mockBeanDescription.getBeanClass()).thenReturn((Class) proxy.getClass());

    JsonSerializer<?> serializer =
        modifier.modifySerializer(null, mockBeanDescription, mockJsonSerializer);

    assertThat(serializer.getClass(), equalTo(EmptyObjectSerializer.class));
  }
}
