package scenariotests;

import org.junit.jupiter.api.Test;

import com.github.opennano.valueobject.junit.ValueObject;

public class ParameterInjectionTest extends BaseValueObjectTest {

  @Test
  public void injectSimpleObject(@ValueObject MyObject myObject) {
    verifyMyObject(myObject);
  }

  @Test
  public void injectPrimitive(@ValueObject int myInt) {
    verifyInteger(myInt);
  }

  @Test
  public void injectBoxedPrimitive(@ValueObject Integer myInteger) {
    verifyInteger(myInteger);
  }

  @Test
  public void injectPrimitiveArray(@ValueObject int[] myInts) {
    verifyIntegerArray(myInts);
  }

  @Test
  public void injectPrimitiveArray(@ValueObject Integer[] myIntegers) {
    verifyIntegerArray(myIntegers);
  }

  @Test
  public void injectVarargArray(@ValueObject Integer... myInts) {
    verifyIntegerArray(myInts);
  }

  @Test
  public void injectMultipleArgs(
      @ValueObject MyObject myObject, @ValueObject int myInt, @ValueObject Integer... myInts) {

    verifyMyObject(myObject);
    verifyInteger(myInt);
    verifyIntegerArray(myInts);
  }

  @Test
  public void injectNamedObject(@ValueObject("my-object.json") MyObject thisObject) {
    verifyMyObject(thisObject);
  }

  @Test
  public void injectCircularObject(@ValueObject CircularObject circularObject) {
    verifyCircularObject(circularObject);
  }
}
