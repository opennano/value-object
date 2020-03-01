package com.github.opennano.reflectionassert.hamcrest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import static com.github.opennano.reflectionassert.LeniencyMode.*;
import static com.github.opennano.reflectionassert.ReflectionAssertions.*;

import com.github.opennano.reflectionassert.LeniencyMode;
import com.github.opennano.reflectionassert.exceptions.ReflectionAssertionException;

public class ReflectionEqualsMatcher extends TypeSafeMatcher<Object> {

  private static final GrammaticListCollector GRAMMATIC_LIST_COLLECTOR =
      new GrammaticListCollector();

  private Object expected;
  private LeniencyMode[] modes;
  private String errorMessage;

  public static Matcher<Object> reflectionEquals(Object expected, LeniencyMode... modes) {
    return new ReflectionEqualsMatcher(expected, modes);
  }

  public static Matcher<Object> lenientEquals(Object expected) {
    return new ReflectionEqualsMatcher(expected, IGNORE_DEFAULTS, LENIENT_ORDER);
  }

  public ReflectionEqualsMatcher(Object expected, LeniencyMode... modes) {
    this.expected = expected;
    this.modes = modes;
  }

  @Override
  protected boolean matchesSafely(Object item) {
    try {
      assertReflectionEquals(expected, item, modes);
      return true;
    } catch (ReflectionAssertionException e) {
      errorMessage = e.getMessage();
      return false;
    }
  }

  @Override
  public void describeTo(Description description) {
    String type = expected.getClass().getSimpleName();
    description.appendText(type + " objects should be reflectively equal");
    String modeDescriptions =
        Stream.of(modes).map(this::describe).collect(GRAMMATIC_LIST_COLLECTOR);

    if (!modeDescriptions.isEmpty()) {
      description.appendText(String.format(" (ignoring %s)", modeDescriptions));
    }
  }

  private String describe(LeniencyMode mode) {
    if (IGNORE_DEFAULTS.equals(mode)) {
      return "default values";
    } else if (LENIENT_DATES.equals(mode)) {
      return "date differences";
    } else if (LENIENT_ORDER.equals(mode)) {
      return "collection order";
    }
    return "unknown mode " + mode;
  }

  @Override
  protected void describeMismatchSafely(Object item, Description mismatchDescription) {
    mismatchDescription.appendText(errorMessage);
  }

  public static class GrammaticListCollector
      implements Collector<CharSequence, List<CharSequence>, String> {

    private static final String SEPARATOR = ", ";
    private static final String RIGHT_PADDED_LOGIC_LABEL = "and ";
    private static final String DOUBLY_PADDED_LOGIC_LABEL = " and ";

    @Override
    public Supplier<List<CharSequence>> supplier() {
      return ArrayList::new;
    }

    @Override
    public BiConsumer<List<CharSequence>, CharSequence> accumulator() {
      return (strs, str) -> strs.add(str);
    }

    @Override
    public BinaryOperator<List<CharSequence>> combiner() {
      return (left, right) -> {
        left.addAll(right);
        return left;
      };
    }

    @Override
    public Set<Characteristics> characteristics() {
      return Collections.emptySet();
    }

    @Override
    public Function<List<CharSequence>, String> finisher() {
      return this::finish;
    }

    private String finish(List<CharSequence> items) {
      if (items.size() > 2) {
        // "a, b, and c"
        CharSequence lastElement = items.remove(items.size() - 1);
        items.add(RIGHT_PADDED_LOGIC_LABEL + lastElement);
        return join(items, SEPARATOR);
      }

      // "a and b" or just "a"
      return join(items, DOUBLY_PADDED_LOGIC_LABEL);
    }

    private String join(List<CharSequence> items, String separator) {
      return items.stream().collect(Collectors.joining(separator));
    }
  }
}
