package com.github.opennano.reflectionassert.report;

import static com.github.opennano.reflectionassert.diffs.Diff.DiffType.DIFFERENT;
import static com.github.opennano.reflectionassert.diffs.Diff.DiffType.MISSING;
import static com.github.opennano.reflectionassert.diffs.Diff.DiffType.UNEXPECTED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.ReflectionTestUtils.getField;
import static org.springframework.test.util.ReflectionTestUtils.setField;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.opennano.reflectionassert.diffs.Diff;
import com.github.opennano.reflectionassert.exceptions.ReflectionAssertionInternalException;
import com.github.opennano.reflectionassert.report.DiffView;
import com.github.opennano.reflectionassert.report.ValueFormatter;

@ExtendWith(MockitoExtension.class)
public class DiffViewTest {

  @InjectMocks private DiffView view;
  @Mock private ValueFormatter mockValueFormatter;
  @Mock private Diff mockDiff;

  @Test
  public void ctor_createsDefaultFormatter() {
    assertNotNull(getField(new DiffView(), "formatter"));
  }

  @Test
  public void describeDiff_different() {
    when(mockDiff.getPath()).thenReturn("mockPath");
    when(mockDiff.getType()).thenReturn(DIFFERENT);
    when(mockDiff.getExpectedValue()).thenReturn(1);
    when(mockValueFormatter.format(any())).thenReturn("x", "y");
    when(mockDiff.getActualValue()).thenReturn(2);

    String expected =
        String.join(
            "\n", // comment forces a line break
            "Path:       mockPath",
            "Expected:   x",
            "Actual:     y");
    assertEquals(expected, view.describeDiff(mockDiff));
  }

  @Test
  public void describeDiff_missing() {
    when(mockDiff.getPath()).thenReturn("mockPath");
    when(mockDiff.getType()).thenReturn(MISSING);
    when(mockDiff.getExpectedValue()).thenReturn(1);
    when(mockValueFormatter.format(1)).thenReturn("x");

    String expected =
        String.join(
            "\n", // comment forces a line break
            "Path:       mockPath",
            "Missing:    x");
    assertEquals(expected, view.describeDiff(mockDiff));
  }

  @Test
  public void describeDiff_unexpected() {
    when(mockDiff.getPath()).thenReturn("mockPath");
    when(mockDiff.getType()).thenReturn(UNEXPECTED);
    when(mockDiff.getActualValue()).thenReturn(2);
    when(mockValueFormatter.format(2)).thenReturn("y");

    String expected =
        String.join(
            "\n", // comment forces a line break
            "Path:       mockPath",
            "Unexpected: y");
    assertEquals(expected, view.describeDiff(mockDiff));
  }

  @ParameterizedTest
  @CsvSource(value = {"1,1,0", "2,2,0", "5,5,0", "6,5,1", "20,5,15", "21,5,15"})
  public void formatDiff_variousDiffCounts(int total, int expectedFull, int expectedShort) {
    when(mockDiff.getType()).thenReturn(DIFFERENT);

    // after 5 diffs we start summarizing, up to a max of 20
    for (int i = 0; i < total; i++) {
      view.formatDiff(mockDiff);
    }

    List<?> fullDiffs = (List<?>) getField(view, "fullDiffs");
    List<?> shortDiffs = (List<?>) getField(view, "shortDiffs");
    int totalDiffs = (int) getField(view, "numDiffs");

    assertEquals(expectedFull, fullDiffs.size());
    assertEquals(expectedShort, shortDiffs.size());
    assertEquals(total, totalDiffs);
  }

  @Test
  public void generateReport_oneFullDiff() {
    setField(view, "fullDiffs", Arrays.asList("full diff"));
    setField(view, "numDiffs", 1);

    assertEquals("mockMessage:\n\nfull diff\n", view.generateReport("mockMessage"));
  }

  @Test
  public void generateReport_oneOfEach() {
    setField(view, "fullDiffs", Arrays.asList("full diff"));
    setField(view, "shortDiffs", Arrays.asList("short diff"));
    setField(view, "numDiffs", 2);

    assertEquals(
        "mockMessage:\n\nfull diff\n\nAdditional differences:\n\nshort diff\n",
        view.generateReport("mockMessage"));
  }

  @Test
  public void generateReport_max20() {
    String[] fullDiffs = new String[5];
    Arrays.fill(fullDiffs, "full diff");
    String[] shortDiffs = new String[15];
    Arrays.fill(shortDiffs, "short diff");

    setField(view, "fullDiffs", Arrays.asList(fullDiffs));
    setField(view, "shortDiffs", Arrays.asList(shortDiffs));
    setField(view, "numDiffs", 20);

    String expected =
        cat(
            "mockMessage:\n",
            "full diff\n",
            "full diff\n",
            "full diff\n",
            "full diff\n",
            "full diff\n",
            "Additional differences:\n",
            "short diff",
            "short diff",
            "short diff",
            "short diff",
            "short diff",
            "short diff",
            "short diff",
            "short diff",
            "short diff",
            "short diff",
            "short diff",
            "short diff",
            "short diff",
            "short diff",
            "short diff\n");

    assertEquals(expected, view.generateReport("mockMessage"));
  }

  @Test
  public void generateReport_extrasSuppressed() {
    String[] fullDiffs = new String[5];
    Arrays.fill(fullDiffs, "full diff");
    String[] shortDiffs = new String[15];
    Arrays.fill(shortDiffs, "short diff");

    setField(view, "fullDiffs", Arrays.asList(fullDiffs));
    setField(view, "shortDiffs", Arrays.asList(shortDiffs));
    setField(view, "numDiffs", 21);

    String expected =
        cat(
            "mockMessage:\n",
            "full diff\n",
            "full diff\n",
            "full diff\n",
            "full diff\n",
            "full diff\n",
            "Additional differences:\n",
            "short diff",
            "short diff",
            "short diff",
            "short diff",
            "short diff",
            "short diff",
            "short diff",
            "short diff",
            "short diff",
            "short diff",
            "short diff",
            "short diff",
            "short diff",
            "short diff",
            "(2 more not shown)\n");

    assertEquals(expected, view.generateReport("mockMessage"));
  }

  @Test
  public void generateReport_defaultMessage() {
    setField(view, "fullDiffs", Arrays.asList("full diff"));
    setField(view, "numDiffs", 1);

    assertEquals("The following differences were found:\n\nfull diff\n", view.generateReport(null));
  }

  @Test
  public void generateReport_invalidState() {
    setField(view, "numDiffs", 0);
    assertThrows(ReflectionAssertionInternalException.class, () -> view.generateReport(null));
  }

  private String cat(String... lines) {
    return String.join("\n", lines);
  }
}
