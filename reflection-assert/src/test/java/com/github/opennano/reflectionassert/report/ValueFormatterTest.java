package com.github.opennano.reflectionassert.report;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import com.github.opennano.reflectionassert.report.ValueFormatter;

public class ValueFormatterTest {

  @Test
  public void format_null() {
    assertEquals("null", new ValueFormatter().format(null));
  }

  @Test
  public void format_string() {
    assertEquals("\"mockString\"", new ValueFormatter().format("mockString"));
  }

  @Test
  public void format_char() {
    assertEquals("'c'", new ValueFormatter().format('c'));
  }

  @Test
  public void format_class() {
    assertEquals("object of type 'java.lang.Object'", new ValueFormatter().format(Object.class));
  }

  @Test
  public void format_file() {
    assertEquals("File<mock/path>", new ValueFormatter().format(new File("mock/path")));
  }

  @Test
  public void format_object() {
    assertEquals("[1, 2, 3]", new ValueFormatter().format(Arrays.asList(1, 2, 3)));
  }
}
