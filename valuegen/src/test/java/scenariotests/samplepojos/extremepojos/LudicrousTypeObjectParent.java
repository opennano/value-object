package scenariotests.samplepojos.extremepojos;

import java.io.Serializable;
import java.util.List;
import java.util.TreeMap;

public class LudicrousTypeObjectParent<S extends CharSequence, T extends Object> {

  public static class LudicrousTypeObject<A extends Number & Serializable & Comparable<A>>
      extends LudicrousTypeObjectParent<String, A> {}

  // can be resolved to TreeMap<String, List<BigDecimal[][]>
  private TreeMap<S, List<? super T[][]>> testedField;

  public TreeMap<S, List<? super T[][]>> getTestedField() {
    return testedField;
  }

  public void setTestedField(TreeMap<S, List<? super T[][]>> testedField) {
    this.testedField = testedField;
  }
}
