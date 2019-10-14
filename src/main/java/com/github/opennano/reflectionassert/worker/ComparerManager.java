package com.github.opennano.reflectionassert.worker;

import static com.github.opennano.reflectionassert.diffs.NullDiff.NULL_TOKEN;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.opennano.reflectionassert.LeniencyMode;
import com.github.opennano.reflectionassert.comparers.DefaultIgnoringComparer;
import com.github.opennano.reflectionassert.comparers.LenientDateComparer;
import com.github.opennano.reflectionassert.comparers.LenientNumberComparer;
import com.github.opennano.reflectionassert.comparers.MapComparer;
import com.github.opennano.reflectionassert.comparers.ObjectComparer;
import com.github.opennano.reflectionassert.comparers.OrderedCollectionComparer;
import com.github.opennano.reflectionassert.comparers.SimpleComparer;
import com.github.opennano.reflectionassert.comparers.UnorderedCollectionComparer;
import com.github.opennano.reflectionassert.diffs.Diff;
import static com.github.opennano.reflectionassert.diffs.Diff.DiffType.*;
import com.github.opennano.reflectionassert.diffs.NullDiff;
import com.github.opennano.reflectionassert.diffs.PartialDiff;
import com.github.opennano.reflectionassert.exceptions.ReflectionAssertionInternalException;

/**
 * Compares any two objects using deep field reflection, delegating to {@link ValueComparer}
 * instances for comparison logic.
 */
public class ComparerManager {

  private List<ValueComparer> comparerChain;

  /** a cache of results for performance and object graph cycle protection */
  private Map<CacheKey, Diff> cachedDiffs = new HashMap<>();

  public ComparerManager(LeniencyMode... modes) {
    Set<LeniencyMode> uniqueModes = Stream.of(modes).distinct().collect(Collectors.toSet());
    comparerChain = createComparerChain(uniqueModes);
  }

  /**
   * @param path the path so far (from root down to the objects being compared)
   * @param expected the expected object
   * @param actual the actual object
   * @param fullDiff when false comparison should end at the first found difference, in which case a
   *     {@link PartialDiff#PARTIAL_DIFF_TOKEN} should be returned.
   * @return an object containing all diffs detected using a deep reflection comparison between the
   *     expected and actual values, or a {@link NullDiff#NULL_TOKEN} if none were found
   */
  public Diff getDiff(String path, Object expected, Object actual, boolean fullDiff) {
    // check whether we've already compared these
    CacheKey key = new CacheKey(expected, actual);
    Diff cachedDiff = getCachedDiff(key, fullDiff);
    if (cachedDiff != null) {
      // not the most efficient way to do this--in theory we shouldn't need new diff objects
      // definitely more convenient to operate on a full diff tree when we're done though
      return cachedDiff.cloneAndRepath(cachedDiff.getPath(), path);
    }

    /* Insert a null token for this new key before recursing
    signaling that this comparison has been seen before.
    If we encounter it again further down the call stack then the object graph has a cycle
    This handles the cycle by returning the null token on the second encounter
    instead of infinitely recursing (it also means there are no diffs in that part of the tree). */
    cachedDiffs.put(key, NULL_TOKEN);
    Diff diff = computeDiff(path, expected, actual, fullDiff);
    cachedDiffs.put(key, diff);
    return diff;
  }

  private List<ValueComparer> createComparerChain(Set<LeniencyMode> modes) {
    List<ValueComparer> comparerChain = new ArrayList<>();
    if (modes.contains(LeniencyMode.IGNORE_DEFAULTS)) {
      comparerChain.add(new DefaultIgnoringComparer());
    }
    if (modes.contains(LeniencyMode.LENIENT_DATES)) {
      comparerChain.add(new LenientDateComparer());
    }
    comparerChain.add(new LenientNumberComparer());
    comparerChain.add(new SimpleComparer());
    if (modes.contains(LeniencyMode.LENIENT_ORDER)) {
      comparerChain.add(new UnorderedCollectionComparer());
    } else {
      comparerChain.add(new OrderedCollectionComparer());
    }
    comparerChain.add(new MapComparer());
    comparerChain.add(new ObjectComparer());
    return comparerChain;
  }

  private Diff getCachedDiff(CacheKey key, boolean fullDiffNeeded) {
    Diff cachedDiff = cachedDiffs.get(key);
    return cachedDiff == null || (fullDiffNeeded && cachedDiff.getType() == PARTIAL)
        ? null
        : cachedDiff;
  }

  private Diff computeDiff(String path, Object expected, Object actual, boolean fullDiff) {
    return comparerChain
        .stream()
        .filter(comp -> comp.canCompare(expected, actual))
        .findFirst()
        .orElseThrow(() -> internalError(expected, actual))
        .compare(path, expected, actual, this, fullDiff);
  }

  private RuntimeException internalError(Object expected, Object actual) {
    String template = "no comparer found for values: expected=%s, actual=%s";
    return new ReflectionAssertionInternalException(String.format(template, expected, actual));
  }

  public static final class CacheKey {
    private final Object expected;
    private final Object actual;

    public CacheKey(Object expected, Object actual) {
      this.expected = expected;
      this.actual = actual;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((expected == null) ? 0 : expected.hashCode());
      result = prime * result + ((actual == null) ? 0 : actual.hashCode());
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }
      if (!(obj instanceof CacheKey)) {
        return false;
      }
      CacheKey other = (CacheKey) obj;
      if (expected == null) {
        if (other.expected != null) {
          return false;
        }
      } else if (!expected.equals(other.expected)) {
        return false;
      }
      if (actual == null) {
        if (other.actual != null) {
          return false;
        }
      } else if (!actual.equals(other.actual)) {
        return false;
      }
      return true;
    }
  }
}
