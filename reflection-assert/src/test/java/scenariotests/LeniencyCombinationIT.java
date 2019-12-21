package scenariotests;

import static com.github.opennano.reflectionassert.ReflectionAssertions.assertReflectionEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import com.github.opennano.reflectionassert.LeniencyMode;

import scenariotests.samplepojos.StringObject;

public class LeniencyCombinationIT extends BaseIntegrationTest {

  // used for parameterized tests where we want to test every possible combination of leniency modes
  private static List<List<LeniencyMode>> ALL_LENIENCY_COMBINATIONS;

  static {
    if (LeniencyMode.values().length > 7) {
      throw new IllegalStateException(
          "too many combinations (>128), failing to avoid possible performance issues when running tests on this data set");
    }

    List<List<LeniencyMode>> combos = new ArrayList<>();
    // Grow the list of combinations as follows:
    // at each position we need to either include the item or not
    // so if we had a list of all combinations up to that point
    // we could determine the new list of all combinations by splitting each existing combination
    // into two:
    // both containing all existing elements, but one including the new element, and the other
    // excluding it.
    // Each pass through the loop then will increase (double) the number of total combinations
    // as well as increase the size of the longest combination by one.
    // The total number of combinations should always be 2^n, where n is the number of leniency
    // modes
    combos.add(new ArrayList<>(0)); // always include the empty set
    for (LeniencyMode mode : LeniencyMode.values()) {
      List<List<LeniencyMode>> newCombinations = new ArrayList<>(combos.size());
      for (List<LeniencyMode> combination : combos) {
        // at every position we need to either include the item or not
        // if we don't include it, we already have that combination
        // if
        List<LeniencyMode> includingNext = new ArrayList<>(combination.size() + 1);
        includingNext.addAll(combination);
        includingNext.add(mode);
        newCombinations.add(includingNext);
      }
      combos.addAll(newCombinations);
    }
    ALL_LENIENCY_COMBINATIONS = Collections.unmodifiableList(combos);
  }

  private static Stream<List<LeniencyMode>> allLeniencyCombinations() {
    return ALL_LENIENCY_COMBINATIONS.stream();
  }

  // SCENARIOS THAT SHOULD ALWAYS PASS OR ALWAYS FAIL NO MATTER WHAT MODES ARE PASSED IN

  @ParameterizedTest
  @MethodSource("allLeniencyCombinations")
  public void assertReflectionEquals_passesInAllModes(List<LeniencyMode> modes) {
    assertReflectionEquals(new StringObject("x"), new StringObject("x"), toArray(modes));
  }

  @ParameterizedTest
  @MethodSource("allLeniencyCombinations")
  public void assertReflectionEquals_diffsFailInAllModes(List<LeniencyMode> modes) {
    assertComparisonThrowsWithMessage(
        new StringObject("x"),
        new StringObject("y"),
        "$.testedField",
        "\"x\"",
        "\"y\"",
        toArray(modes));
  }

  @ParameterizedTest
  @MethodSource("allLeniencyCombinations")
  public void assertReflectionEquals_nullActualFailsInAllModes(List<LeniencyMode> modes) {
    assertComparisonThrowsWithMessage(
        new StringObject("x"),
        new StringObject(null),
        "$.testedField",
        "\"x\"",
        "null",
        toArray(modes));
  }

  // wasn't able to get Stream<LeniencyMode[]> to work as a parameterized test input, so we do the
  // conversion in each test instead :(
  private LeniencyMode[] toArray(List<LeniencyMode> modes) {
    return modes.stream().toArray(LeniencyMode[]::new);
  }
}
