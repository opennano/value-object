package com.github.opennano.reflectionassert.report;

import java.util.ArrayList;
import java.util.List;

import com.github.opennano.reflectionassert.diffs.Diff;
import com.github.opennano.reflectionassert.exceptions.ReflectionAssertionException;
import com.github.opennano.reflectionassert.exceptions.ReflectionAssertionInternalException;

/**
 * This class aggregates diffs visited by a {@link DiffVisitor} into a user-friendly format that
 * will ultimately end up as the message within a {@link ReflectionAssertionException}. This class
 * should only be used when there are diffs to report (i.e. the assertion failed with diffs).
 */
public class DiffView {

  private static final Integer MAX_DIFFERENCES_TO_FULLY_DESCRIBE = 5;
  private static final Integer MAX_SHORT_DIFFERENCES_TO_SHOW = 15;
  private static final Integer MAX_DIFFERENCES_TO_SHOW =
      MAX_DIFFERENCES_TO_FULLY_DESCRIBE + MAX_SHORT_DIFFERENCES_TO_SHOW;

  private static final String SUPPRESSED_DIFF_COUNT_TEMPLATE = "(%s more not shown)";
  private static final String HEADER = "The following differences were found:";
  private static final String ADDITIONAL = "Additional differences:";
  private static final String DIFFERENT_VALUE_TEMPLATE;
  private static final String MISSING_VALUE_TEMPLATE;
  private static final String UNEXPECTED_VALUE_TEMPLATE;
  private static final String PATH_ONLY_TEMPLATE;

  static {
    StringBuilder sb = new StringBuilder();
    sb.append("Path:       %s\n");
    sb.append("Expected:   %s\n");
    sb.append("Actual:     %s");
    DIFFERENT_VALUE_TEMPLATE = sb.toString();

    sb = new StringBuilder();
    sb.append("Path:       %s\n");
    sb.append("Missing:    %s");
    MISSING_VALUE_TEMPLATE = sb.toString();

    sb = new StringBuilder();
    sb.append("Path:       %s\n");
    sb.append("Unexpected: %s");
    UNEXPECTED_VALUE_TEMPLATE = sb.toString();

    sb = new StringBuilder();
    sb.append("Path:       %s");
    PATH_ONLY_TEMPLATE = sb.toString();
  }

  private ValueFormatter formatter;

  private int numDiffs;
  private List<String> fullDiffs = new ArrayList<>();
  private List<String> shortDiffs = new ArrayList<>();

  public DiffView() {
    this(new ValueFormatter());
  }

  public DiffView(ValueFormatter formatter) {
    this.formatter = formatter;
  }

  public void formatDiff(Diff diff) {
    numDiffs++;
    if (numDiffs <= MAX_DIFFERENCES_TO_FULLY_DESCRIBE) {
      // fully describe the first few issues
      fullDiffs.add(describeDiff(diff));
    } else if (numDiffs <= MAX_DIFFERENCES_TO_SHOW) {
      // full descriptions become annoying when there are many
      // if there are a lot of diffs after a few descriptions just show paths to problems
      shortDiffs.add(summarizeDiff(diff));
    }
    // if there are very many diffs even paths become annoying, just keep a count instead
  }

  public String generateReport(String customMessage) {
    if (numDiffs < 1) {
      // this would only happen if there were a bug in this library
      // either there really are no diffs in which case we shouldn't be "viewing" them
      // or there are diffs but we failed to record them for some reason (e.g. visitor wasn't used)
      throw new ReflectionAssertionInternalException("no diffs to view!");
    }

    StringBuilder sb = new StringBuilder(customMessage == null ? HEADER : customMessage + ":");
    String newline = "\n";
    for (String fullDiff : fullDiffs) {
      sb.append(newline).append(newline).append(fullDiff);
    }
    if (!shortDiffs.isEmpty()) {
      sb.append(newline).append(newline).append(ADDITIONAL).append(newline);
    }
    int shortDiffsShown = 0;
    for (String shortDiff : shortDiffs) {
      shortDiffsShown++;
      if (shortDiffsShown >= MAX_SHORT_DIFFERENCES_TO_SHOW && numDiffs > MAX_DIFFERENCES_TO_SHOW) {
        // replace the last diff with a message that more diffs were suppressed
        // this is a little more complicated than just saying "n more suppressed"
        // but it avoids the annoying case where there are exactly n+1 diffs
        // i.e., why not just show me the last diff instead of saying "1 more"?)
        int numSuppressed = numDiffs - MAX_DIFFERENCES_TO_SHOW + 1;
        sb.append(newline);
        sb.append(String.format(SUPPRESSED_DIFF_COUNT_TEMPLATE, numSuppressed));
        break;
      }
      sb.append(newline).append(shortDiff);
    }
    return sb.append(newline).toString();
  }

  public String describeDiff(Diff diff) {
    String description;
    String expected;
    String actual;
    String path = diff.getPath();
    switch (diff.getType()) {
      case MISSING:
        expected = formatter.format(diff.getLeftValue());
        description = String.format(MISSING_VALUE_TEMPLATE, path, expected);
        break;
      case UNEXPECTED:
        actual = formatter.format(diff.getRightValue());
        description = String.format(UNEXPECTED_VALUE_TEMPLATE, path, actual);
        break;
      case DIFFERENT:
      default:
        expected = formatter.format(diff.getLeftValue());
        actual = formatter.format(diff.getRightValue());
        description = String.format(DIFFERENT_VALUE_TEMPLATE, path, expected, actual);
    }
    return description;
  }

  /** returns just the path to a diff */
  private String summarizeDiff(Diff diff) {
    return String.format(PATH_ONLY_TEMPLATE, diff.getPath());
  }
}
