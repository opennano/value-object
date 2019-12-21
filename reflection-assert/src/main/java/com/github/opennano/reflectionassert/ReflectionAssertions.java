package com.github.opennano.reflectionassert;

import static com.github.opennano.reflectionassert.LeniencyMode.IGNORE_DEFAULTS;
import static com.github.opennano.reflectionassert.LeniencyMode.LENIENT_ORDER;
import static com.github.opennano.reflectionassert.diffs.NullDiff.NULL_TOKEN;

import com.github.opennano.reflectionassert.diffs.Diff;
import com.github.opennano.reflectionassert.exceptions.ReflectionAssertionException;
import com.github.opennano.reflectionassert.report.DiffView;
import com.github.opennano.reflectionassert.report.DiffVisitor;
import com.github.opennano.reflectionassert.worker.ComparerManager;

/**
 * This class contains utility methods for asserting that two objects are "equal" according to a
 * deep reflection-based comparison of each object's fields.
 *
 * <p>This class makes it easy to do the most typically needed comparisons: "strict" comparison
 * where all fields must be equal, and "lenient" comparison where collection order is ignored and
 * default values (null, 0, false, etc.) in the expected object indicate comparison of that field
 * should be skipped. For more fine-grained control, including the ability to ignore date diffs, you
 * can call {@link #assertReflectionEquals(Object, Object, LeniencyMode...)} with any combination of
 * {@link LeniencyMode} values.
 */
public class ReflectionAssertions {

  private static final String ROOT_PATH = "$";

  private ReflectionAssertions() {
    // no-op: singleton
  }

  /**
   * Asserts that two objects are "leniently equal" using reflection to do a deep comparison of
   * fields.
   *
   * <p>This method is identical to {@link #assertReflectionEquals(Object, Object, LeniencyMode...)}
   * with leniency modes {@link LeniencyMode#IGNORE_DEFAULTS} and {@link LeniencyMode#LENIENT_ORDER}
   * specified.
   *
   * @param expected the expected object
   * @param actual the actual object
   */
  public static void assertLenientEquals(Object expected, Object actual) {
    assertLenientEquals(null, expected, actual);
  }

  /**
   * Same as {@link #assertLenientEquals(Object, Object)} but specifies a custom message to display
   * when comparison fails.
   *
   * @param message a custom message that will be displayed before any differences found
   * @param expected the expected object
   * @param actual the actual object
   */
  public static void assertLenientEquals(String message, Object expected, Object actual) {
    assertReflectionEquals(message, expected, actual, LENIENT_ORDER, IGNORE_DEFAULTS);
  }

  /**
   * Asserts that two objects are "equal" using reflection to do a deep comparison of fields.
   *
   * <p>This method is identical to {@link #assertReflectionEquals(Object, Object, LeniencyMode...)}
   * with leniency modes {@link LeniencyMode#IGNORE_DEFAULTS} and {@link
   * LeniencyMode#LENIENT_ORDER}.
   *
   * @param expected the expected object
   * @param actual the actual object
   * @param modes extra leniency modes to apply
   */
  public static void assertReflectionEquals(Object expected, Object actual, LeniencyMode... modes) {
    assertReflectionEquals(null, expected, actual, modes);
  }

  /**
   * Same as {@link #assertReflectionEquals(Object, Object, LeniencyMode...)} but specifies a custom
   * message to display when comparison fails.
   *
   * @param message a custom message that will be displayed before any differences found
   * @param expected the expected object
   * @param actual the actual object
   * @param modes extra leniency modes to apply
   */
  public static void assertReflectionEquals(
      String message, Object expected, Object actual, LeniencyMode... modes) {

    ComparerManager comparer = new ComparerManager(modes);
    Diff diff = comparer.getDiff(ROOT_PATH, expected, actual, true);
    if (diff != NULL_TOKEN) {
      throw new ReflectionAssertionException(describeDiffs(message, diff));
    }
  }

  private static String describeDiffs(String customMessage, Diff diff) {
    DiffView view = new DiffView();
    new DiffVisitor(view).visit(diff);
    return view.generateReport(customMessage);
  }
}
