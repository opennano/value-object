package com.github.opennano.jsongen;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.github.opennano.jsongen.Jsongen.IdAndTypeMixInResolver;
import com.github.opennano.jsongen.serializer.CustomMapSerializer;
import com.github.opennano.valuegen.ValueGenerationException;

/**
 * Since most of the methods here are static, coverage largely depends on scenario tests. What is
 * left here are just paths not easily covered by a scenario.
 */
@ExtendWith(MockitoExtension.class)
public class JsongenTest {

  @Mock private JsonGenerator mockJsonGenerator;
  @Mock private SerializerProvider mockSerializerProvider;
  @Mock private CharSequence mockCharSequence;
  @InjectMocks private CustomMapSerializer serializer;

  @Test
  public void printSample_logsToConsole() {
    PrintStream consoleOut = System.out;
    OutputStream out = new ByteArrayOutputStream();
    try (PrintStream testOut = new PrintStream(out, true)) {
      System.setOut(testOut);
      Jsongen.printSample(String.class);
      assertThat(out.toString().trim(), is("\"mockString\""));
    } finally {
      System.setOut(consoleOut);
    }
  }

  @Test
  public void toJson_errorsAreWrappedAndPropagated() {
    when(mockCharSequence.toString()).thenReturn("oops"); // makes internal state of char sequence invalid
    assertThrows(ValueGenerationException.class, () -> Jsongen.toJson(mockCharSequence));
  }

  @Test
  public void copy_returnsSelf() {
    IdAndTypeMixInResolver resolver = new IdAndTypeMixInResolver();
    assertThat(resolver.copy(), is(resolver));
  }
}
