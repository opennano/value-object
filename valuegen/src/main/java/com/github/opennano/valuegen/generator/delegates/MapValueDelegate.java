package com.github.opennano.valuegen.generator.delegates;

import static com.github.opennano.valuegen.generator.ValueGeneratorDelegate.isInstanceOfAny;
import static com.github.opennano.valuegen.utils.ReflectionUtil.getTypeInfo;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import com.github.opennano.valuegen.GeneratorConfig;
import com.github.opennano.valuegen.generator.ValueObjectGenerator;
import com.github.opennano.valuegen.generator.TypeInfo;
import com.github.opennano.valuegen.generator.ValueGeneratorDelegate;
import com.github.opennano.valuegen.generator.strategies.MapKeyStrategy;

public class MapValueDelegate implements ValueGeneratorDelegate {

  private static final String KEY_NAME_TEMPLATE = "%sKey";
  private static final String VALUE_NAME_TEMPLATE = "%sValue";

  private MapKeyStrategy mapKeyStrategy;

  public MapValueDelegate(GeneratorConfig config) {
    mapKeyStrategy = config.getMapKeyStrategy();
  }

  @Override
  public boolean handlesClass(Class<?> type) {
    return isInstanceOfAny(type, Map.class);
  }

  @Override
  public Object generateValue(
      TypeInfo typeInfo,
      Field declaringField,
      String parentNameHint,
      Class<?> owningClass,
      ValueObjectGenerator valueGenerator) {

    //	  create a map with a single key/value pair
    String nameHint = maybeShorten(parentNameHint);
    Map<Object, Object> map = instantiate(typeInfo.getResolvedClass());
    Type[] parameterTypes = typeInfo.getGenericParameterTypes();

    Type keyType = parameterTypes.length < 1 ? Object.class : parameterTypes[0];
    Type valueType = parameterTypes.length < 2 ? Object.class : parameterTypes[1];
    try {
      map.put(
          generateMapKey(keyType, declaringField, nameHint, owningClass, valueGenerator),
          generateMapValue(valueType, declaringField, nameHint, owningClass, valueGenerator));
    } catch (Exception e) {
    	e.printStackTrace();
      // TODO log
      // couldn't add an item to the collection
    }
    return map;
  }

  private String maybeShorten(String parentNameHint) {

    // if we got no hint use empty
    String nullSafeName = Optional.ofNullable(parentNameHint).orElse("");

    // when maps are nested you can get Strings like mockFieldValueKey and mockFieldValueValue
    // if we strip off everything after the first "Value" then these values read a lot better
    int indexOfSuffix = nullSafeName.indexOf("Value");
    if (indexOfSuffix != -1) {
      nullSafeName = nullSafeName.substring(0, indexOfSuffix);
    }
    return nullSafeName;
  }

  private Object generateMapKey(
      Type keyType,
      Field declaringField,
      String nameHint,
      Class<?> owningClass,
      ValueObjectGenerator valueGenerator) {

    TypeInfo keyTypeInfo = getTypeInfo(keyType, owningClass, declaringField);
    Class<?> resolvedKeyClass = keyTypeInfo.getResolvedClass();

    // sometimes we want to overrule the resolved type
    // for example if we get a key type of Object but we want all maps to use Strings
    // in this case we may set the effective key type to String
    TypeInfo effectiveKeyInfo =
        new TypeInfo(
            mapKeyStrategy.apply(resolvedKeyClass),
            keyTypeInfo.getGenericParameterTypes(),
            keyTypeInfo.getAdditionalInterfaces());

    String keyNameHint = String.format(KEY_NAME_TEMPLATE, nameHint);
    return valueGenerator.valueFor(effectiveKeyInfo, declaringField, keyNameHint, owningClass);
  }

  private Object generateMapValue(
      Type valueType,
      Field declaringField,
      String nameHint,
      Class<?> owningClass,
      ValueObjectGenerator valueGenerator) {

    TypeInfo valueTypeInfo = getTypeInfo(valueType, owningClass, declaringField);
    String valueNameHint = String.format(VALUE_NAME_TEMPLATE, nameHint);
    return valueGenerator.valueFor(valueTypeInfo, declaringField, valueNameHint, owningClass);
  }

  @SuppressWarnings("unchecked") // map of objects is always safe afaik
  private Map<Object, Object> instantiate(Class<?> type) {
    return (Map<Object, Object>) objenesis.newInstance(findCompatibleMapType(type));
  }

  private Class<?> findCompatibleMapType(Class<?> mapType) {
    if (!mapType.isInterface() && !Modifier.isAbstract(mapType.getModifiers())) {
      return mapType; // use whatever concrete type this is
    }
    return LinkedHashMap.class;
  }
}
