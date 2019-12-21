package com.github.opennano.reflectionassert.worker;

import static com.github.opennano.reflectionassert.diffs.NullDiff.NULL_TOKEN;
import static com.github.opennano.reflectionassert.diffs.PartialDiff.PARTIAL_DIFF_TOKEN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.opennano.reflectionassert.diffs.Diff;
import com.github.opennano.reflectionassert.diffs.ParentDiff;
import com.github.opennano.reflectionassert.worker.ComparerManager;
import com.github.opennano.reflectionassert.worker.ValueComparer;

@ExtendWith(MockitoExtension.class)
public class ValueComparerTest {

  // stub subclass with trivial implementations of abstract methods for testing only
  private final class StubValueComparer extends ValueComparer {

    @Override
    public boolean canCompare(Object expected, Object actual) {
      return false;
    }

    @Override
    public Diff compare(
        String path, Object expected, Object actual, ComparerManager comparer, boolean fullDiff) {

      return null;
    }

    // overridden for visibility only (test can't directly access protected methods in super)

    @Override
    protected Diff createDiff(String path, List<Diff> fieldDiffs, boolean fullDiff) {
      return super.createDiff(path, fieldDiffs, fullDiff);
    }

    @Override
    protected boolean areBothOneOfTheseTypes(Object expected, Object actual, Class<?>... types) {
      return super.areBothOneOfTheseTypes(expected, actual, types);
    }
  }

  @Mock private Diff mockDiff;

  private StubValueComparer comparer = new StubValueComparer();

  @Test
  public void createDiff_parentDiffNeeded() {
    Diff expected = new ParentDiff("mockPath", Arrays.asList(mockDiff));
    Diff actual = comparer.createDiff("mockPath", Arrays.asList(mockDiff), true);

    assertEquals(ParentDiff.class, actual.getClass());
    assertEquals(expected.getPath(), actual.getPath());
    assertEquals(expected.getExpectedValue(), actual.getExpectedValue());
    assertEquals(expected.getActualValue(), actual.getActualValue());
  }

  @Test
  public void createDiff_nullTokenDiffNeeded() {
    assertEquals(NULL_TOKEN, comparer.createDiff("mockPath", Collections.emptyList(), true));
  }

  @Test
  public void createDiff_nullTokenDiffNeededPartialDoesntMatter() {
    assertEquals(NULL_TOKEN, comparer.createDiff("mockPath", Collections.emptyList(), false));
  }

  @Test
  public void createDiff_partialTokenDiffNeeded() {
    assertEquals(
        PARTIAL_DIFF_TOKEN, comparer.createDiff("mockPath", Arrays.asList(mockDiff), false));
  }

  @Test
  public void areBothOneOfTheseTypes_simpleCaseBothAre() {
    assertTrue(comparer.areBothOneOfTheseTypes("", "", String.class));
  }

  @Test
  public void areBothOneOfTheseTypes_simpleCaseBothAreSubtypes() {
    assertTrue(comparer.areBothOneOfTheseTypes("", "", Object.class));
  }

  @Test
  public void areBothOneOfTheseTypes_simpleCaseDifferentSubtypes() {
    assertTrue(comparer.areBothOneOfTheseTypes("", 1, Object.class));
  }

  @Test
  public void areBothOneOfTheseTypes_simpleCaseBothAreSuperTypes() {
    assertFalse(comparer.areBothOneOfTheseTypes(new Object(), new Object(), String.class));
  }

  @Test
  public void areBothOneOfTheseTypes_simpleCaseExpectedIsNull() {
    assertFalse(comparer.areBothOneOfTheseTypes("", null, String.class));
  }

  @Test
  public void areBothOneOfTheseTypes_simpleCaseActualIsNull() {
    assertFalse(comparer.areBothOneOfTheseTypes(null, "", String.class));
  }

  @Test
  public void areBothOneOfTheseTypes_simpleCaseBothNull() {
    assertFalse(comparer.areBothOneOfTheseTypes(null, null, String.class));
  }

  @Test
  public void areBothOneOfTheseTypes_noType() {
    assertFalse(comparer.areBothOneOfTheseTypes("", ""));
  }

  @Test
  public void areBothOneOfTheseTypes_multipleTypesThatWork() {
    assertTrue(comparer.areBothOneOfTheseTypes("", "", String.class, Object.class));
  }

  @Test
  public void areBothOneOfTheseTypes_multipleTypesOneWorks() {
    assertTrue(
        comparer.areBothOneOfTheseTypes(new Object(), new Object(), String.class, Object.class));
  }

  @Test
  public void areBothOneOfTheseTypes_multipleTypesNoneWork() {
    assertFalse(
        comparer.areBothOneOfTheseTypes(new Object(), new Object(), String.class, Integer.class));
  }

  @Test
  public void areBothOneOfTheseTypes_multipleTypesIncompatible() {
    assertFalse(comparer.areBothOneOfTheseTypes("", 1, String.class, Integer.class));
  }
}
