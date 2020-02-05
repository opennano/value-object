package com.github.opennano.jsongen.serializer;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.fasterxml.jackson.databind.type.ArrayType;
import com.fasterxml.jackson.databind.type.MapType;

public class JsongenSerializerModifier extends BeanSerializerModifier {

  private static final String CGLIB_SIGNATURE = "$$enhancerbycglib$$";

  public static boolean isProxy(Class<?> type) {
    return type.getName().toLowerCase().contains(CGLIB_SIGNATURE);
  }

  /**
   * Customize the default behavior for byte arrays, which Jackson tries to interpret as strings.
   * When generating a value object we just create a single byte for the array, which does not make
   * sense as a String.
   */
  @Override
  public JsonSerializer<?> modifyArraySerializer(
      SerializationConfig config,
      ArrayType valueType,
      BeanDescription beanDesc,
      JsonSerializer<?> serializer) {

    return (byte[].class.equals(beanDesc.getBeanClass()))
        ? new ByteArrayAsIntArraySerializer()
        : serializer;
  }

  /**
   * If we get a String as a key, serialize the map the usual way (as a JSON object). But if the key
   * is not a String, we'll need to be a bit more creative. In this case we will basically try to
   * replicate GSON behavior, which is to serialize the map as a list of map entries, where each
   * entry is serialized as a 2-element array. For a single-item map, it will serialize like this:
   * [[{key1}. {value1}]]
   */
  @Override
  public JsonSerializer<?> modifyMapSerializer(
      SerializationConfig config,
      MapType valueType,
      BeanDescription beanDesc,
      JsonSerializer<?> serializer) {

    return new CustomMapSerializer();
  }

  /**
   * Proxies are basically instances of an object where we couldn't find a good value object to
   * generate. To be as helpful as possible, we can serialize these as simple empty objects, and let
   * the user touch up the resulting JSON manually.
   */
  @Override
  public JsonSerializer<?> modifySerializer(
      SerializationConfig config, BeanDescription beanDesc, JsonSerializer<?> serializer) {

    Class<?> beanType = beanDesc.getBeanClass();
    return isProxy(beanType) ? new EmptyObjectSerializer() : serializer;
  }
}
