package scenariotests;

import org.junit.jupiter.api.Test;

import com.github.opennano.valueobject.junit.ValueObject;

public class ValueGenerationTest extends BaseValueObjectTest {

  @ValueObject(generate = true, value = "my-object.json")
  private static MyObject myStaticObject;

  @ValueObject(generate = true)
  private MyObject myObject;

  @Test
  public void generateStaticFieldObject() {
    verifyMyObject(myStaticObject);
  }

  @Test
  public void generateFieldObject() {
    verifyMyObject(myObject);
  }

  @Test
  public void generateSimpleObjectParameter(@ValueObject(generate = true) MyObject myObject) {
    verifyMyObject(myObject);
  }

  @Test
  public void generatePrimitiveParameter(@ValueObject(generate = true) int myInt) {
    verifyInteger(myInt);
  }

  @Test
  public void generateBoxedPrimitiveParameter(@ValueObject(generate = true) Integer myInteger) {
    verifyInteger(myInteger);
  }

  @Test
  public void generatePrimitiveArrayParameter(@ValueObject(generate = true) int[] myInts) {
    verifyIntegerArray(myInts);
  }

  @Test
  public void generatePrimitiveArrayParameter(@ValueObject(generate = true) Integer[] myIntegers) {
    verifyIntegerArray(myIntegers);
  }

  @Test
  public void generateVarargArrayParameter(@ValueObject(generate = true) Integer... myInts) {
    verifyIntegerArray(myInts);
  }

  @Test
  public void generateVariousParameters(
      @ValueObject("my-object.json") MyObject thisObject,
      @ValueObject int myInt,
      @ValueObject(generate = true) Integer... myInts) {

    verifyMyObject(myObject);
    verifyInteger(myInt);
    verifyIntegerArray(myInts);
  }
}
