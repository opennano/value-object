package com.github.opennano.reflectionassert.e2etests;

import static com.github.opennano.reflectionassert.ReflectionAssertions.assertReflectionEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.opennano.reflectionassert.LeniencyMode;
import com.github.opennano.reflectionassert.exceptions.ReflectionAssertionException;

public abstract class BaseIntegrationTest {

  private static final String PATH_MESSAGE_TEMPLATE = "\nPath:       $%s\n";
  private static final String EXPECTED_MESSAGE_TEMPLATE = "\nExpected:   %s\n";
  private static final String ACTUAL_MESSAGE_TEMPLATE = "\nActual:     %s\n";
  private static final String UNEXPECTED_MESSAGE_TEMPLATE = "Unexpected: %s\n";
  private static final String MISSING_MESSAGE_TEMPLATE = "Missing:    %s\n";

  protected void assertComparisonThrowsWithMessage(
      Object expected,
      Object actual,
      String propertyPathMessage,
      String expectedMessage,
      String actualMessage,
      LeniencyMode... modes) {

    Class<ReflectionAssertionException> type = ReflectionAssertionException.class;
    Throwable thrown = assertThrows(type, () -> assertReflectionEquals(expected, actual, modes));

    String message = thrown.getMessage();
    assertMessageDescribesDiff(message, propertyPathMessage, expectedMessage, actualMessage);
  }

  private void assertMessageDescribesDiff(
      String message, String path, String expected, String actual) {

    List<String> errors = new ArrayList<>(3);
    if (!message.contains(String.format(PATH_MESSAGE_TEMPLATE, path))) {
      errors.add("didn't find expected property path: $" + path);
    }

    if (expected != null && actual != null) {
      // standard mismatch--check that declared expected and actual values match
      if (!message.contains(String.format(EXPECTED_MESSAGE_TEMPLATE, expected))) {
        errors.add("didn't find expected value: " + expected);
      }
      if (!message.contains(String.format(ACTUAL_MESSAGE_TEMPLATE, actual))) {
        errors.add("didn't find actual value: " + actual);
      }
    } else if (actual == null) {
      // missing value message
      if (!message.contains(String.format(MISSING_MESSAGE_TEMPLATE, expected))) {
        errors.add("failed to report as missing value: " + expected);
      }
    } else if (expected == null) {
      // unexpected value message
      if (!message.contains(String.format(UNEXPECTED_MESSAGE_TEMPLATE, actual))) {
        errors.add("failed to report as unexpected value: " + actual);
      }
    }
    // note: if both values are null we only assert against the property path

    if (!errors.isEmpty()) {
      fail("\n\n" + String.join("\n", errors) + "\n\nactual error message: <" + message + ">");
    }
  }

  protected Map<?, ?> mapOf(Object... keysAndValues) {
    if (keysAndValues == null) {
      return Collections.EMPTY_MAP;
    }
    if (keysAndValues.length % 2 != 0) {
      throw new IllegalStateException("must have some number of keys and values");
    }

    Map<Object, Object> map = new HashMap<>(keysAndValues.length);
    for (int i = 0; i < keysAndValues.length; i += 2) {
      map.put(keysAndValues[i], keysAndValues[i + 1]);
    }
    return map;
  }
  
  protected List<?> listOf(Object... values) {
	  return Arrays.asList(values);
  }
}
