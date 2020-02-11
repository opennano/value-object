package com.github.opennano.valuegen.testutils;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

public class ProxyMatcher {

  private static final String PROXY_SIGNATURE = "$$enhancerbycglib$$";

  public static Matcher<Object> isProxy() {
    return new BaseMatcher<Object>() {

      private Object item;

      @Override
      public boolean matches(Object item) {
        this.item = item;
        return item.getClass().getName().toLowerCase().contains(PROXY_SIGNATURE);
      }

      @Override
      public void describeTo(Description description) {
        description.appendText(item.getClass() + " is not a cglib proxy");
      }
    };
  }
}
