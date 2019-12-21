package scenariotests;

import static com.github.opennano.reflectionassert.LeniencyMode.IGNORE_DEFAULTS;
import static com.github.opennano.reflectionassert.LeniencyMode.LENIENT_ORDER;
import static com.github.opennano.reflectionassert.ReflectionAssertions.assertReflectionEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;

import org.junit.jupiter.api.Test;

import com.github.opennano.reflectionassert.LeniencyMode;
import com.github.opennano.reflectionassert.exceptions.ReflectionAssertionInputException;

public class CollectionIT extends BaseIntegrationTest {

  ////////// LIST AND ARRAY VALUE TESTS //////////

  @Test
  public void assertReflectionEquals_listValuesSame() {
    assertReflectionEquals(Arrays.asList(1), Arrays.asList(1));
  }

  @Test
  public void assertReflectionEquals_listValuesDifferent() {
    assertComparisonThrowsWithMessage(Arrays.asList(1), Arrays.asList(2), "$[0]", "1", "2");
  }

  @Test
  public void assertReflectionEquals_arrayValuesSame() {
    assertReflectionEquals(new int[] {1}, new int[] {1});
  }

  @Test
  public void assertReflectionEquals_arrayValuesDifferent() {
    assertComparisonThrowsWithMessage(new int[] {1}, new int[] {2}, "$[0]", "1", "2");
  }

  @Test
  public void assertReflectionEquals_arraysAndListsInterchangeableSame() {
    assertReflectionEquals(new int[] {1}, Arrays.asList(1));
  }

  @Test
  public void assertReflectionEquals_arraysAndListsInterchangeableDifferent() {
    assertComparisonThrowsWithMessage(new int[] {1}, Arrays.asList(2), "$[0]", "1", "2");
  }

  @Test
  public void assertReflectionEquals_multiValuelistValuesSame() {
    assertReflectionEquals(Arrays.asList(1, 2), Arrays.asList(1, 2));
  }

  @Test
  public void assertReflectionEquals_multiValuelistValuesDifferentStrict() {
    assertComparisonThrowsWithMessage(Arrays.asList(1, 2), Arrays.asList(1, 3), "$[1]", "2", "3");
  }

  @Test
  public void assertReflectionEquals_multiValuelistValuesDifferentLenientDefaultsIrrelevant() {
    assertComparisonThrowsWithMessage(
        Arrays.asList(1, 2), Arrays.asList(1, 3), "$[1]", "2", "3", IGNORE_DEFAULTS);
  }

  @Test
  public void assertReflectionEquals_multiValuelistValuesDifferentLenientOrder() {
    assertComparisonThrowsWithMessage(
        Arrays.asList(1, 2), Arrays.asList(1, 3), "$[1]", "2", "3", IGNORE_DEFAULTS);
  }

  @Test
  public void assertReflectionEquals_multiValuelistValuesReorderedStrict() {
    assertComparisonThrowsWithMessage(Arrays.asList(1, 2), Arrays.asList(2, 1), "$[0]", "1", "2");
  }

  @Test
  public void assertReflectionEquals_multiValuelistExtraValueStrict() {
    assertComparisonThrowsWithMessage(Arrays.asList(1, 2), Arrays.asList(1), "$[1]", "2", null);
  }

  @Test
  public void assertReflectionEquals_multiValuelistExtraValueLenientOrder() {
    assertComparisonThrowsWithMessage(
        Arrays.asList(1, 2), Arrays.asList(1), "$[*]", "2", null, LENIENT_ORDER);
  }

  @Test
  public void assertReflectionEquals_multiValuelistMissingValueStrict() {
    assertComparisonThrowsWithMessage(Arrays.asList(1), Arrays.asList(1, 2), "$[1]", null, "2");
  }

  @Test
  public void assertReflectionEquals_multiValuelistMissingValueLenientOrder() {
    assertComparisonThrowsWithMessage(
        Arrays.asList(1), Arrays.asList(1, 2), "$[*]", null, "2", LENIENT_ORDER);
  }

  @Test
  public void assertReflectionEquals_multiValuelistValuesReorderedLenientOrder() {
    assertReflectionEquals(Arrays.asList(1, 2), Arrays.asList(2, 1), LENIENT_ORDER);
  }

  @Test
  public void assertReflectionEquals_listsEmpty() {
    assertReflectionEquals(Arrays.asList(), Arrays.asList());
  }

  @Test
  public void assertReflectionEquals_listsNullExpectedStrict() {
    assertComparisonThrowsWithMessage(null, Arrays.asList(), "$", "null", "[]");
  }

  @Test
  public void assertReflectionEquals_listsNullExpectedLenient() {
    assertReflectionEquals(null, Arrays.asList(), new LeniencyMode[] {IGNORE_DEFAULTS});
  }

  @Test
  public void assertReflectionEquals_listsNullActualStrict() {
    assertComparisonThrowsWithMessage(Arrays.asList(), null, "$", "[]", "null");
  }

  @Test
  public void assertReflectionEquals_listsNullActualLenient() {
    assertComparisonThrowsWithMessage(Arrays.asList(), null, "$", "[]", "null", IGNORE_DEFAULTS);
  }

  @Test
  public void assertReflectionEquals_multiValuelistDuplicateInExpected() {
    assertComparisonThrowsWithMessage(
        Arrays.asList(1, 2, 2), Arrays.asList(2, 1), "$[*]", "2", null, LENIENT_ORDER);
  }

  @Test
  public void assertReflectionEquals_multiValuelistDuplicateInActual() {
    assertComparisonThrowsWithMessage(
        Arrays.asList(1, 2), Arrays.asList(2, 2, 1), "$[*]", null, "2", LENIENT_ORDER);
  }

  ////////// SET TESTS //////////

  @Test
  public void assertReflectionEquals_setDoesntMakeSenseForStrictOrder() {
    assertThrows(
        ReflectionAssertionInputException.class,
        () -> assertReflectionEquals(Arrays.asList(1, 2), new HashSet<>(Arrays.asList(2, 1))));
  }

  @Test
  public void assertReflectionEquals_setsSame() {
    assertReflectionEquals(
        new HashSet<>(Arrays.asList(1, 2)), new HashSet<>(Arrays.asList(1, 2)), LENIENT_ORDER);
  }

  @Test
  public void assertReflectionEquals_setsSameIgnoringOrder() {
    assertReflectionEquals(
        new HashSet<>(Arrays.asList(1, 2)), new HashSet<>(Arrays.asList(2, 1)), LENIENT_ORDER);
  }

  @Test
  public void assertReflectionEquals_orderedSetCanBeStrictlyComparedToList() {
    assertReflectionEquals(Arrays.asList(1, 2), new LinkedHashSet<>(Arrays.asList(1, 2)));
  }
}
