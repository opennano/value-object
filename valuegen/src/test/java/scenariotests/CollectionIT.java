package scenariotests;

import static com.github.opennano.reflectionassert.hamcrest.ReflectionEqualsMatcher.reflectionEquals;
import static com.github.opennano.valuegen.Valuegen.createValueObject;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ArrayBlockingQueue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import scenariotests.samplepojos.collectionpojos.ArrayObject;
import scenariotests.samplepojos.collectionpojos.ArrayOfArraysObject;
import scenariotests.samplepojos.collectionpojos.ListObject;
import scenariotests.samplepojos.collectionpojos.ListOfArraysObject;
import scenariotests.samplepojos.collectionpojos.ListOfListsObject;
import scenariotests.samplepojos.collectionpojos.PrimitiveArrayObject;

public class CollectionIT {

  ////////// ARRAY VALUE TESTS //////////

  @ParameterizedTest
  @ValueSource(
      classes = {
        byte[].class,
        Byte[].class,
        short[].class,
        Short[].class,
        int[].class,
        Integer[].class,
        long[].class,
        Long[].class,
        float[].class,
        Float[].class,
        double[].class,
        Double[].class,
      })
  public void generateNumericArrayValue(Class<?> type) {
    assertThat(createValueObject(type), reflectionEquals(Arrays.asList(1)));
  }

  @ParameterizedTest
  @ValueSource(
      classes = {
        boolean[].class,
        Boolean[].class,
      })
  public void generateBooleanArrayValue(Class<?> type) {
    assertThat(createValueObject(type), reflectionEquals(Arrays.asList(true)));
  }

  @ParameterizedTest
  @ValueSource(
      classes = {
        char[].class,
        Character[].class,
      })
  public void generateCharacterArrayValue(Class<?> type) {
    assertThat(createValueObject(type), reflectionEquals(Arrays.asList('a')));
  }

  @ParameterizedTest
  @ValueSource(
      classes = {
        String[].class,
        CharSequence[].class,
      })
  public void generateStringArrayValue(Class<?> type) {
    assertThat(createValueObject(type), reflectionEquals(Arrays.asList("mockString")));
  }

  ////////// COLLECTION VALUE TESTS //////////

  @ParameterizedTest
  @ValueSource(
      classes = {
        Collection.class,
        List.class,
        ArrayList.class,
        Vector.class,
        LinkedList.class,
        Set.class,
        HashSet.class,
        //        TreeSet.class, // TODO not currently working
        LinkedHashSet.class,
        Queue.class,
        ArrayBlockingQueue.class
      })
  public void generateCommonCollectionValue(Class<?> type) {
    assertThat(createValueObject(type), reflectionEquals(Arrays.asList(new Object())));
  }

  ////////// LIST AND ARRAY FIELD TESTS //////////

  @Test
  public void generateArrayField1() {
    ArrayObject expected = new ArrayObject();
    expected.setTestedField(new Integer[] {1});

    assertThat(createValueObject(ArrayObject.class), reflectionEquals(expected));
  }

  @Test
  public void generateArrayField2() {
    PrimitiveArrayObject expected = new PrimitiveArrayObject();
    expected.setTestedField(new int[] {1});

    assertThat(createValueObject(PrimitiveArrayObject.class), reflectionEquals(expected));
  }

  @Test
  public void generateArrayField3() {
    ListObject expected = new ListObject();
    expected.setTestedField(Arrays.asList(1));

    assertThat(createValueObject(ListObject.class), reflectionEquals(expected));
  }

  ////////// NESTED COLLECTION TESTS //////////

  @Test
  public void generateNestedListObject1() {
    ListOfListsObject expected = new ListOfListsObject();
    expected.setTestedField(Arrays.asList(Arrays.asList(1)));

    assertThat(createValueObject(ListOfListsObject.class), reflectionEquals(expected));
  }

  @Test
  public void generateNestedListObject2() {
    ArrayOfArraysObject expected = new ArrayOfArraysObject();
    expected.setTestedField(new Integer[][] {{1}});

    assertThat(createValueObject(ArrayOfArraysObject.class), reflectionEquals(expected));
  }

  @Test
  public void generateNestedListObject3() {
    ListOfArraysObject expected = new ListOfArraysObject();
    expected.setTestedField(Arrays.asList(new Integer[][] {{1}}));

    assertThat(createValueObject(ListOfArraysObject.class), reflectionEquals(expected));
  }

  ////////// NESTED ARRAY VALUE TESTS //////////

  @ParameterizedTest
  @ValueSource(
      classes = {
        byte[][].class,
        Byte[][].class,
        short[][].class,
        Short[][].class,
        int[][].class,
        Integer[][].class,
        long[][].class,
        Long[][].class,
        float[][].class,
        Float[][].class,
        double[][].class,
        Double[][].class,
      })
  public void generatePrimitiveIntNestedArrayValue(Class<?> type) {
    assertThat(createValueObject(type), reflectionEquals(new int[][] {{1}}));
  }
}
