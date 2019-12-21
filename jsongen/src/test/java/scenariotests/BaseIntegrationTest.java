package scenariotests;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

public abstract class BaseIntegrationTest {

  protected static final String TYPE_PROPERTY = "@type";

  private static final String KEY_VALUE_TEMPLATE = " \"%s\": %s";

  // UTILITY METHODS

  /**
   * create a simple json string using the provided keys and values, where all values are simple
   * types like number, null, bool, string, etc. (not arrays or objects). For more complicated json
   * externalize to a file and use readFile
   */
  protected String simpleJson(Object... keysAndScalarValues) {
    StringBuilder sb = new StringBuilder("{");
    List<String> keyVals = new ArrayList<>();
    for (int i = 0; i < keysAndScalarValues.length; i += 2) {
      String value = formatValue(keysAndScalarValues[i + 1]);
      keyVals.add(String.format(KEY_VALUE_TEMPLATE, keysAndScalarValues[i], value));
    }
    return sb.append(String.join(",", keyVals)).append("}").toString();
  }

  protected String fileContents(String path) {
    File file = new File("src/test/resources/" + path);
    try {
      return IOUtils.readLines(new FileReader(file)).stream().collect(Collectors.joining("\n"));
    } catch (IOException e) {
      throw new IllegalStateException("failed to open file: " + file.getAbsolutePath());
    }
  }

  protected String formatValue(Object object) {
    if (object == null) {
      return "null";
    }
    if (object instanceof Boolean || object instanceof Number) {
      return object.toString();
    }
    return String.format("\"%s\"", object);
  }

  protected Matcher<String> jsonEquals(String json) {
    return new BaseMatcher<String>() {

      private String message;

      @Override
      public boolean matches(Object item) {
        try {
          JSONAssert.assertEquals(json, item.toString(), JSONCompareMode.STRICT);
          return true;
        } catch (JSONException e) {
          message = e.getMessage();
          return false;
        }
      }

      @Override
      public void describeTo(Description description) {
        description.appendText(message);
      }
    };
  }
}
