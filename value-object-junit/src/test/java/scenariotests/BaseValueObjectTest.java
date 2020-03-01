package scenariotests;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.jupiter.api.extension.ExtendWith;

import com.github.opennano.valueobject.junit.ValueObjectExtension;

@ExtendWith(ValueObjectExtension.class)
public abstract class BaseValueObjectTest {
	
	// still needed: id references, property references, inheritence

  protected void verifyMyObject(MyObject myObject) {
    assertThat(myObject, notNullValue());
    assertThat(myObject.myField, notNullValue());
  }

  protected void verifyInteger(Integer integer) {
    assertThat(integer, is(1));
  }

  protected void verifyIntegerArray(int[] integer) {
    assertThat(integer.length, is(1));
    assertThat(integer[0], is(1));
  }

  protected void verifyIntegerArray(Integer[] integer) {
    assertThat(integer.length, is(1));
    assertThat(integer[0], is(1));
  }

  protected void verifyCircularObject(CircularObject circularObject) {
    assertThat(circularObject.myField, is(circularObject));
  }
}
