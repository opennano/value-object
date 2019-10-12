package com.github.opennano.reflectionassert.e2etests.samplepojos;

public class BidirectionalOne {

  private BidirectionalTwo two;

  public BidirectionalTwo getTwo() {
    return two;
  }

  public void setTwo(BidirectionalTwo two) {
    this.two = two;
  }
}
