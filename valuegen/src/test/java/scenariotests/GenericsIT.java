package scenariotests;

import static com.github.opennano.reflectionassert.hamcrest.ReflectionEqualsMatcher.reflectionEquals;
import static com.github.opennano.valuegen.Valuegen.createValueObject;
import static com.github.opennano.valuegen.testutils.ProxyMatcher.isProxy;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import scenariotests.samplepojos.genericpojos.GenericArrayObjectParent.GenericArrayObject;
import scenariotests.samplepojos.genericpojos.GenericArrayOfListObjectParent.GenericArrayOfListObject;
import scenariotests.samplepojos.genericpojos.GenericIntegerObjectParent.GenericIntegerObject;
import scenariotests.samplepojos.genericpojos.GenericInterfaceBoundTypeObject;
import scenariotests.samplepojos.genericpojos.GenericListObject2Parent.GenericListObject2;
import scenariotests.samplepojos.genericpojos.GenericListObjectParent.GenericListObject;
import scenariotests.samplepojos.genericpojos.GenericListOfArrayObjectParent.GenericListOfArrayObject;
import scenariotests.samplepojos.genericpojos.GenericMapKeyObjectParent.GenericMapKeyObject;
import scenariotests.samplepojos.genericpojos.GenericMapObjectParent.GenericMapObject;
import scenariotests.samplepojos.genericpojos.GenericMapObjectWithArrayValueParent.GenericMapObjectWithArrayValue;
import scenariotests.samplepojos.genericpojos.GenericMapValueObjectParent.GenericMapValueObject;
import scenariotests.samplepojos.genericpojos.GenericMultipleInterfaceBoundTypeObject;
import scenariotests.samplepojos.genericpojos.GenericNestedArrayObjectParent.GenericNestedArrayObject;
import scenariotests.samplepojos.genericpojos.GenericNumberAndInterfaceBoundTypeObject;
import scenariotests.samplepojos.genericpojos.GenericNumberAndInterfaceBoundTypeObject.CustomNumber;
import scenariotests.samplepojos.genericpojos.GenericNumberBoundTypeObject;
import scenariotests.samplepojos.genericpojos.GenericRedundantlyBoundNumberTypeObject;

public class GenericsIT {

  @Test
  public void generateGenericFieldObject() {
    // T where T is bound to Integer
    GenericIntegerObject expected = new GenericIntegerObject();
    expected.setTestedField(1);

    GenericIntegerObject actual = createValueObject(GenericIntegerObject.class);
    assertThat(actual, reflectionEquals(expected));
  }

  @Test
  public void generateGenericArrayObject() {
    // T[] where T is bound to Integer
    GenericArrayObject expected = new GenericArrayObject();
    expected.setTestedField(new Integer[] {1});

    GenericArrayObject actual = createValueObject(GenericArrayObject.class);
    assertThat(actual, reflectionEquals(expected));
  }

  @Test
  public void generateGenericNestedArrayObject() {
    // T[][] where T is bound to Integer
    GenericNestedArrayObject expected = new GenericNestedArrayObject();
    expected.setTestedField(new Integer[][] {{1}});

    GenericNestedArrayObject actual = createValueObject(GenericNestedArrayObject.class);
    assertThat(actual, reflectionEquals(expected));
  }

  @Test
  public void generateGenericListObject() {
    // List<T> where T is bound to Integer
    GenericListObject expected = new GenericListObject();
    expected.setTestedField(Arrays.asList(1));

    GenericListObject actual = createValueObject(GenericListObject.class);
    assertThat(actual, reflectionEquals(expected));
  }

  @Test
  public void generateGenericListObject2() {
    // T where T is bound to List<Integer>
    GenericListObject2 expected = new GenericListObject2();
    expected.setTestedField(Arrays.asList(1));

    GenericListObject2 actual = createValueObject(GenericListObject2.class);
    assertThat(actual, reflectionEquals(expected));
  }

  @Test
  public void generateGenericArrayOfListObject() {
    // T[] where T is bound to List<Integer>
    GenericArrayOfListObject expected = new GenericArrayOfListObject();
    expected.setTestedField(Arrays.asList(new Integer[][] {{1}}));

    GenericArrayOfListObject actual = createValueObject(GenericArrayOfListObject.class);
    assertThat(actual, reflectionEquals(expected));
  }

  @SuppressWarnings("unchecked") // can't directly create a generic array in Java
  @Test
  public void generateGenericListOfArrayObject() {
    // List<T> where T is bound to Integer[]
    GenericListOfArrayObject expected = new GenericListOfArrayObject();
    expected.setTestedField(new List[] {Arrays.asList(1)});

    GenericListOfArrayObject actual = createValueObject(GenericListOfArrayObject.class);
    assertThat(actual, reflectionEquals(expected));
  }

  ////////// MAP TESTS //////////

  @Test
  public void generateGenericMapValueField() {
    // Map<String, T> where T is bound to Integer
    GenericMapValueObject expected = new GenericMapValueObject();
    Map<String, Integer> map = new HashMap<>(2);
    map.put("mockTestedFieldKey", 1);
    expected.setTestedField(map);

    GenericMapValueObject actual = createValueObject(GenericMapValueObject.class);
    assertThat(actual, reflectionEquals(expected));
  }

  @Test
  public void generateGenericMapKeyField() {
    // Map<T, Integer> where T is bound to String
    GenericMapKeyObject expected = new GenericMapKeyObject();
    Map<String, Integer> map = new HashMap<>(2);
    map.put("mockTestedFieldKey", 1);
    expected.setTestedField(map);

    GenericMapKeyObject actual = createValueObject(GenericMapKeyObject.class);
    assertThat(actual, reflectionEquals(expected));
  }

  @Test
  public void generateGenericMapField() {
    // Map<K, V> where K is bound to String, T to Integer
    GenericMapObject expected = new GenericMapObject();
    Map<String, Integer> map = new HashMap<>(2);
    map.put("mockTestedFieldKey", 1);
    expected.setTestedField(map);

    GenericMapObject actual = createValueObject(GenericMapObject.class);
    assertThat(actual, reflectionEquals(expected));
  }

  @Test
  public void generateGenericMapWithArrayValueField() {
    // Map<String, T[]> where T is bound to Integer
    GenericMapObjectWithArrayValue expected = new GenericMapObjectWithArrayValue();
    Map<String, Integer[]> map = new HashMap<>(2);
    map.put("mockTestedFieldKey", new Integer[] {1});
    expected.setTestedField(map);

    GenericMapObjectWithArrayValue actual = createValueObject(GenericMapObjectWithArrayValue.class);
    assertThat(actual, reflectionEquals(expected));
  }

  ////////// BOUNDED TYPE VARIABLES //////////

  @Test
  public void generateGenericNumberBoundTypeObject() {
    // T where T extends Number
    GenericNumberBoundTypeObject<Integer> expected = new GenericNumberBoundTypeObject<>();
    expected.setTestedField(1);

    GenericNumberBoundTypeObject<?> actual = createValueObject(GenericNumberBoundTypeObject.class);
    assertThat(actual, reflectionEquals(expected));
  }

  @Test
  public void generateGenericInterfaceBoundTypeObject() {
    // T where T extends Serializable
    // interface will be proxied
    GenericInterfaceBoundTypeObject<Integer> expected = new GenericInterfaceBoundTypeObject<>();
    expected.setTestedField(1);

    GenericInterfaceBoundTypeObject<?> actual =
        createValueObject(GenericInterfaceBoundTypeObject.class);
    assertThat(actual.getTestedField(), isProxy());
  }

  @Test
  public void generateGenericRedundantlyBoundedNumberTypeObject() {
    // T where T extends Number & Serializable
    // Number already extends Serializable so the latter is ignored
    GenericRedundantlyBoundNumberTypeObject<Integer> expected =
        new GenericRedundantlyBoundNumberTypeObject<>();

    expected.setTestedField(1);

    GenericRedundantlyBoundNumberTypeObject<?> actual =
        createValueObject(GenericRedundantlyBoundNumberTypeObject.class);

    assertThat(actual, reflectionEquals(expected));
  }

  @Test // FIXME extra interfaces should not be ignored
  public void generateGenericNumberAndInterfaceBoundTypeObject() {
    // T where T extends Number & Cloneable
    // Number class is useful, extra interfaces are not and are ignored
    GenericNumberAndInterfaceBoundTypeObject<CustomNumber> expected =
        new GenericNumberAndInterfaceBoundTypeObject<>();

    expected.setTestedField(new CustomNumber(1));

    GenericNumberAndInterfaceBoundTypeObject<?> actual =
        createValueObject(GenericNumberAndInterfaceBoundTypeObject.class);

    assertThat(actual, reflectionEquals(expected));
  }

  @Test
  public void generateGenericMultipleInterfaceBoundTypeObject() {
    // T where T extends Serializable & Cloneable
    // a proxy will be created that implements both interfaces
    GenericMultipleInterfaceBoundTypeObject<?> actual =
        createValueObject(GenericMultipleInterfaceBoundTypeObject.class);

    assertThat(actual.getTestedField(), isProxy());
  }
}
