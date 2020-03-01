package com.github.opennano.reflectionassert.hamcrest;

import static com.github.opennano.reflectionassert.hamcrest.ReflectionEqualsMatcher.lenientEquals;
import static com.github.opennano.reflectionassert.hamcrest.ReflectionEqualsMatcher.reflectionEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.github.opennano.reflectionassert.LeniencyMode;
import com.github.opennano.reflectionassert.hamcrest.ReflectionEqualsMatcher.GrammaticListCollector;

public class ReflectionEqualsMatcherTest {

  @Test
  public void reflectionEquals_true() {
    assertThat(new Object(), reflectionEquals(new Object()));
  }

  @Test
  public void reflectionEquals_false() {
    AssertionError ex =
        assertThrows(AssertionError.class, () -> assertThat(1, reflectionEquals(2)));

    assertThat(
        ex.getMessage().trim(),
        is(
            "Expected: Integer objects should be reflectively equal\n"
                + "     but: The following differences were found:\n"
                + "\n"
                + "Path:       $\n"
                + "Expected:   2\n"
                + "Actual:     1"));
  }

  @Test
  public void reflectionEquals_allModesFailure() {
    AssertionError ex =
        assertThrows(
            AssertionError.class,
            () ->
                assertThat(
                    1,
                    reflectionEquals(
                        2,
                        LeniencyMode.IGNORE_DEFAULTS,
                        LeniencyMode.LENIENT_ORDER,
                        LeniencyMode.LENIENT_DATES)));

    assertThat(
        ex.getMessage().trim(),
        is(
            "Expected: Integer objects should be reflectively equal (ignoring default values, collection order, and date differences)\n"
                + "     but: The following differences were found:\n"
                + "\n"
                + "Path:       $\n"
                + "Expected:   2\n"
                + "Actual:     1"));
  }

  @Test
  public void lenientEquals_equivalency() {
    Object dummy = new Object();
    Object expectedMatcher =
        reflectionEquals(dummy, LeniencyMode.IGNORE_DEFAULTS, LeniencyMode.LENIENT_ORDER);

    assertThat(lenientEquals(dummy), reflectionEquals(expectedMatcher));
  }

  private static final GrammaticListCollector GRAMMATIC_LIST_COLLECTOR =
      new GrammaticListCollector();

  @Test
  public void grammaticListCollector_singleItem() {
    assertThat(Stream.of("1").collect(GRAMMATIC_LIST_COLLECTOR), is("1"));
  }

  @Test
  public void grammaticListCollector_twoItems() {
    assertThat(Stream.of("1", "2").collect(GRAMMATIC_LIST_COLLECTOR), is("1 and 2"));
  }

  @Test
  public void grammaticListCollector_threeItems() {
    assertThat(Stream.of("1", "2", "3").collect(GRAMMATIC_LIST_COLLECTOR), is("1, 2, and 3"));
  }

  @Test
  public void grammaticListCollector_supportsParallelization() {
    assertThat(
        Stream.of("1", "2", "3", "4").parallel().collect(GRAMMATIC_LIST_COLLECTOR),
        is("1, 2, 3, and 4"));
  }

  @Test
  public void grammaticListCollector_empty() {
    assertThat(Stream.<String>of().collect(GRAMMATIC_LIST_COLLECTOR), is(""));
  }

  @Test
  public void describe_unknownMode() {
    ReflectionEqualsMatcher matcher = new ReflectionEqualsMatcher("");
    Object returned = ReflectionTestUtils.invokeMethod(matcher, "describe", (LeniencyMode) null);
    
    assertThat(returned, is("unknown mode null"));
  }
}
