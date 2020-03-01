package com.github.opennano.valueobject.junit;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ObjectMarshallerTest {

  @InjectMocks private ObjectMarshaller marshaller;

  @Mock private ValueObject mockAnnotation;

  @Test
  public void marshalJson_missingFile() {
    when(mockAnnotation.value()).thenReturn("missing.json");

    assertThrows(
        ValueObjectDeserializationException.class,
        () ->
            marshaller.marshalJson(
                mockAnnotation, "ignored", Object.class, "src/test/resources/value-objects"));
  }

  @Test
  public void marshalJson_invalidJson() {
    when(mockAnnotation.value()).thenReturn("invalid.json");

    assertThrows(
        ValueObjectDeserializationException.class,
        () ->
            marshaller.marshalJson(
                mockAnnotation, "ignored", Object.class, "src/test/resources/value-objects"));
  }
}
