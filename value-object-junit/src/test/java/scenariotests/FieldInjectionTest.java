package scenariotests;

import org.junit.jupiter.api.Test;

import com.github.opennano.valueobject.junit.ValueObject;

public class FieldInjectionTest extends ParentFieldInjectionTest {

  @ValueObject(value = "my-object.json")
  private static MyObject myStaticObject;

  @ValueObject private int myInt;
  @ValueObject private int[] myInts;
  @ValueObject private Integer myInteger;
  @ValueObject private Integer[] myIntegers;

  @ValueObject("my-object.json")
  private MyObject thisObject;

  @Test
  public void generateStaticFieldObject() {
        verifyMyObject(myStaticObject);
  }

  @Test
  public void injectInheritedObject() {
    verifyMyObject(myObject);
  }

  @Test
  public void injectPrimitive() {
        verifyInteger(myInt);
  }

  @Test
  public void injectPrimitiveArray() {
        verifyIntegerArray(myInts);
  }

  @Test
  public void injectBoxedPrimitive() {
        verifyInteger(myInteger);
  }

  @Test
  public void injectBoxedPrimitiveArray() {
        verifyIntegerArray(myIntegers);
  }

  @Test
  public void injectNamedObject() {
    verifyMyObject(thisObject);
  }
}
