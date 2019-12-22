package scenariotests;

import static com.github.opennano.reflectionassert.hamcrest.ReflectionEqualsMatcher.reflectionEquals;
import static com.github.opennano.valuegen.Valuegen.createValueObject;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import scenariotests.samplepojos.collectionpojos.MapObject;
import scenariotests.samplepojos.collectionpojos.MapOfListsObject;
import scenariotests.samplepojos.collectionpojos.MapOfMapsObject;
import scenariotests.samplepojos.collectionpojos.MapWithComplexKeyObject;
import scenariotests.samplepojos.collectionpojos.MapWithComplexKeyObject.MapKey;

public class MapIT {

  @ParameterizedTest
  @ValueSource(classes = {Map.class, HashMap.class, LinkedHashMap.class, TreeMap.class})
  public void generateCommonMapValue(Class<?> type) {
    Map<String, Object> expected = new HashMap<>(2);
    expected.put("mockKey", new Object());

    Object actual = createValueObject(type);
    assertThat(actual, reflectionEquals(expected));
  }

  @Test
  public void generateMapField() {
    MapObject expected = new MapObject();
    Map<String, Integer> map = new HashMap<>(2);
    map.put("mockTestedFieldKey", 1);
    expected.setTestedField(map);

    MapObject actual = createValueObject(MapObject.class);
    assertThat(actual, reflectionEquals(expected));
  }

  @Test
  public void generateMapOfListsObject() {
    MapOfListsObject expected = new MapOfListsObject();
    Map<String, List<Integer>> map = new HashMap<>(2);
    map.put("mockTestedFieldKey", Arrays.asList(1));
    expected.setTestedField(map);

    MapOfListsObject actual = createValueObject(MapOfListsObject.class);
    assertThat(actual, reflectionEquals(expected));
  }

  @Test
  public void generateMapOfMapsObject() {
    MapOfMapsObject expected = new MapOfMapsObject();
    Map<String, Map<String, Integer>> outerMap = new HashMap<>(2);
    Map<String, Integer> innerMap = new HashMap<>(2);
    innerMap.put("mockTestedFieldKey2", 1);
    outerMap.put("mockTestedFieldKey", innerMap);
    expected.setTestedField(outerMap);

    MapOfMapsObject actual = createValueObject(MapOfMapsObject.class);
    assertThat(actual, reflectionEquals(expected));
  }

  @Test
  public void generateMapWithComplexKeyObject() {
    MapWithComplexKeyObject expected = new MapWithComplexKeyObject();

    MapKey complexKey = new MapKey();
    complexKey.setKeyIntField(1);
    complexKey.setKeyStringField("mockKeyStringField");

    Map<MapKey, Integer> map = new HashMap<>(2);
    map.put(complexKey, 2);
    expected.setTestedField(map);

    MapWithComplexKeyObject actual = createValueObject(MapWithComplexKeyObject.class);
    assertThat(actual, reflectionEquals(expected));
  }
}
