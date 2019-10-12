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
import com.github.opennano.reflectionassert.diffs.Diff.DiffType;
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
   * returns an object containing all diffs detected using a deep reflection comparison between the
   * left and right values, or a {@link NullDiff#NULL_TOKEN} if none were found
   */
  public Diff getDiff(String path, Object left, Object right, boolean fullDiff) {
    // check whether we've already compared these
    CacheKey key = new CacheKey(left, right);
    Diff cachedDiff = getCachedDiff(key, fullDiff);
    if (cachedDiff != null) {
      // certainly not the most efficient way to do this, but good enough I suppose
      return cachedDiff.cloneAndRepath(cachedDiff.getPath(), path);
    }

    // insert a null token for this new key before recursing
    // this signals that this comparison has been seen before,
    // if we encounter it again further down the call stack it will return the null token
    // instead of infinitely recursing (and also means there are no diffs)
    cachedDiffs.put(key, NULL_TOKEN);
    Diff diff = computeDiff(path, left, right, fullDiff);
    cachedDiffs.put(key, diff);
    return diff;
  }

  private Diff getCachedDiff(CacheKey key, boolean fullDiffNeeded) {
    Diff cachedDiff = cachedDiffs.get(key);
    return (cachedDiff == null || (fullDiffNeeded && cachedDiff.getType() == DiffType.PARTIAL))
        ? null
        : cachedDiff;
  }

  private Diff computeDiff(String path, Object left, Object right, boolean fullDiff) {
    return comparerChain
        .stream()
        .filter(comp -> comp.canCompare(left, right))
        .findFirst()
        .orElseThrow(() -> internalError(left, right))
        .compare(path, left, right, this, fullDiff);
  }

  private RuntimeException internalError(Object left, Object right) {
    String template = "no comparer found for values: left=%s, right=%s";
    return new ReflectionAssertionInternalException(String.format(template, left, right));
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

  public static final class CacheKey {
    private final Object left;
    private final Object right;

    public CacheKey(Object left, Object right) {
      this.left = left;
      this.right = right;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((left == null) ? 0 : left.hashCode());
      result = prime * result + ((right == null) ? 0 : right.hashCode());
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
      if (left == null) {
        if (other.left != null) {
          return false;
        }
      } else if (!left.equals(other.left)) {
        return false;
      }
      if (right == null) {
        if (other.right != null) {
          return false;
        }
      } else if (!right.equals(other.right)) {
        return false;
      }
      return true;
    }
  }
}
