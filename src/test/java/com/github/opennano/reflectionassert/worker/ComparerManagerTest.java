package com.github.opennano.reflectionassert.worker;

import static com.github.opennano.reflectionassert.LeniencyMode.IGNORE_DEFAULTS;
import static com.github.opennano.reflectionassert.LeniencyMode.LENIENT_DATES;
import static com.github.opennano.reflectionassert.LeniencyMode.LENIENT_ORDER;
import static com.github.opennano.reflectionassert.diffs.NullDiff.NULL_TOKEN;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

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
import com.github.opennano.reflectionassert.worker.ComparerManager;
import com.github.opennano.reflectionassert.worker.ValueComparer;
import com.github.opennano.reflectionassert.worker.ComparerManager.CacheKey;

import nl.jqno.equalsverifier.EqualsVerifier;

@ExtendWith(MockitoExtension.class)
public class ComparerManagerTest {

  @InjectMocks private ComparerManager manager = new ComparerManager();
  @Mock private List<ValueComparer> mockComparerChain;
  @Mock private ValueComparer mockValueComparer;
  @Mock private Map<CacheKey, Diff> cachedDiffs;
  @Mock private Diff mockDiff;
  @Mock private Object mockLeft;
  @Mock private Object mockRight;

  @Test
  public void ctor_strict() {
    ComparerManager manager = new ComparerManager();

    List<?> comparerChain = (List<?>) ReflectionTestUtils.getField(manager, "comparerChain");

    assertTrue(comparerChain.size() == 5);
    assertTrue(comparerChain.get(0) instanceof LenientNumberComparer);
    assertTrue(comparerChain.get(1) instanceof SimpleComparer);
    assertTrue(comparerChain.get(2) instanceof OrderedCollectionComparer);
    assertTrue(comparerChain.get(3) instanceof MapComparer);
    assertTrue(comparerChain.get(4) instanceof ObjectComparer);
  }

  @Test
  public void ctor_lenient() {
    ComparerManager manager = new ComparerManager(IGNORE_DEFAULTS, LENIENT_ORDER);

    List<?> comparerChain = (List<?>) ReflectionTestUtils.getField(manager, "comparerChain");

    assertTrue(comparerChain.size() == 6);
    assertTrue(comparerChain.get(0) instanceof DefaultIgnoringComparer);
    assertTrue(comparerChain.get(1) instanceof LenientNumberComparer);
    assertTrue(comparerChain.get(2) instanceof SimpleComparer);
    assertTrue(comparerChain.get(3) instanceof UnorderedCollectionComparer);
    assertTrue(comparerChain.get(4) instanceof MapComparer);
    assertTrue(comparerChain.get(5) instanceof ObjectComparer);
  }

  @Test
  public void ctor_all() {
    ComparerManager manager = new ComparerManager(IGNORE_DEFAULTS, LENIENT_ORDER, LENIENT_DATES);

    List<?> comparerChain = (List<?>) ReflectionTestUtils.getField(manager, "comparerChain");

    assertTrue(comparerChain.size() == 7);
    assertTrue(comparerChain.get(0) instanceof DefaultIgnoringComparer);
    assertTrue(comparerChain.get(1) instanceof LenientDateComparer);
    assertTrue(comparerChain.get(2) instanceof LenientNumberComparer);
    assertTrue(comparerChain.get(3) instanceof SimpleComparer);
    assertTrue(comparerChain.get(4) instanceof UnorderedCollectionComparer);
    assertTrue(comparerChain.get(5) instanceof MapComparer);
    assertTrue(comparerChain.get(6) instanceof ObjectComparer);
  }

  @Test
  public void ctor_duplicatedModeOk() {
    ComparerManager manager = new ComparerManager(IGNORE_DEFAULTS, LENIENT_ORDER);

    List<?> comparerChain = (List<?>) ReflectionTestUtils.getField(manager, "comparerChain");

    assertTrue(comparerChain.size() == 6);
    assertTrue(comparerChain.get(0) instanceof DefaultIgnoringComparer);
    assertTrue(comparerChain.get(1) instanceof LenientNumberComparer);
    assertTrue(comparerChain.get(2) instanceof SimpleComparer);
    assertTrue(comparerChain.get(3) instanceof UnorderedCollectionComparer);
    assertTrue(comparerChain.get(4) instanceof MapComparer);
    assertTrue(comparerChain.get(5) instanceof ObjectComparer);
  }

  @Test
  public void getDiff_cached() {
    when(cachedDiffs.get(new CacheKey(mockLeft, mockRight))).thenReturn(mockDiff);
    when(mockDiff.getType()).thenReturn(DiffType.DIFFERENT);
    when(mockDiff.getPath()).thenReturn("mockDiffPath");

    manager.getDiff("mockPathArg", mockLeft, mockRight, true);

    verify(mockDiff).cloneAndRepath("mockDiffPath", "mockPathArg");
    verifyNoMoreInteractions(mockComparerChain, cachedDiffs, mockDiff, mockLeft, mockRight);
  }

  @Test
  public void getDiff_cachedPartialOk() {
    when(cachedDiffs.get(new CacheKey(mockLeft, mockRight))).thenReturn(mockDiff);
    when(mockDiff.getPath()).thenReturn("mockDiffPath");

    manager.getDiff("mockPathArg", mockLeft, mockRight, false);

    verify(mockDiff).cloneAndRepath("mockDiffPath", "mockPathArg");
    verifyNoMoreInteractions(mockComparerChain, cachedDiffs, mockDiff, mockLeft, mockRight);
  }

  @Test
  public void getDiff_cachedPartialNotOk() {
    CacheKey key = new CacheKey(mockLeft, mockRight);

    when(cachedDiffs.get(key)).thenReturn(mockDiff);
    when(mockDiff.getType()).thenReturn(DiffType.PARTIAL);
    when(mockComparerChain.stream()).thenReturn(Stream.of(mockValueComparer));
    when(mockValueComparer.canCompare(mockLeft, mockRight)).thenReturn(true);
    when(mockValueComparer.compare("mockPathArg", mockLeft, mockRight, manager, true))
        .thenReturn(mockDiff);

    manager.getDiff("mockPathArg", mockLeft, mockRight, true);

    verify(cachedDiffs).put(key, NULL_TOKEN);
    verify(cachedDiffs).put(key, mockDiff);
    verifyNoMoreInteractions(mockComparerChain, cachedDiffs, mockDiff, mockLeft, mockRight);
  }

  @Test
  public void getDiff_notCached() {
    CacheKey key = new CacheKey(mockLeft, mockRight);

    when(cachedDiffs.get(key)).thenReturn(null);
    when(mockComparerChain.stream()).thenReturn(Stream.of(mockValueComparer));
    when(mockValueComparer.canCompare(mockLeft, mockRight)).thenReturn(true);
    when(mockValueComparer.compare("mockPathArg", mockLeft, mockRight, manager, true))
        .thenReturn(mockDiff);

    manager.getDiff("mockPathArg", mockLeft, mockRight, true);

    verify(cachedDiffs).put(key, NULL_TOKEN);
    verify(cachedDiffs).put(key, mockDiff);
    verifyNoMoreInteractions(mockComparerChain, cachedDiffs, mockDiff, mockLeft, mockRight);
  }

  @Test
  public void getDiff_noComparerForDiff() {
    CacheKey key = new CacheKey(mockLeft, mockRight);

    when(cachedDiffs.get(key)).thenReturn(null);
    when(mockComparerChain.stream()).thenReturn(Stream.of(mockValueComparer));
    when(mockValueComparer.canCompare(mockLeft, mockRight)).thenReturn(false);

    Class<? extends Exception> type = ReflectionAssertionInternalException.class;
    assertThrows(type, () -> manager.getDiff("mockPathArg", mockLeft, mockRight, true));

    verify(cachedDiffs).put(key, NULL_TOKEN);
    verifyNoMoreInteractions(mockComparerChain, cachedDiffs, mockDiff, mockLeft, mockRight);
  }

  @Test
  public void equals_test() {
    EqualsVerifier.forClass(CacheKey.class).verify();
  }
}
