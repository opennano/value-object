package scenariotests;

import static com.github.opennano.reflectionassert.hamcrest.ReflectionEqualsMatcher.reflectionEquals;
import static com.github.opennano.valuegen.Valuegen.createValueObject;
import static com.github.opennano.valuegen.testutils.ProxyMatcher.isProxy;
import static org.hamcrest.MatcherAssert.assertThat;

import java.net.URI;
import java.net.URL;

import org.junit.jupiter.api.Test;

import scenariotests.samplepojos.simplepojos.BooleanObject;
import scenariotests.samplepojos.simplepojos.ByteObject;
import scenariotests.samplepojos.simplepojos.CharacterObject;
import scenariotests.samplepojos.simplepojos.DoubleObject;
import scenariotests.samplepojos.simplepojos.FinalFieldObject;
import scenariotests.samplepojos.simplepojos.FloatObject;
import scenariotests.samplepojos.simplepojos.IntObject;
import scenariotests.samplepojos.simplepojos.LongObject;
import scenariotests.samplepojos.simplepojos.MultiImplementationAbstractClass;
import scenariotests.samplepojos.simplepojos.MultiImplementationInterface;
import scenariotests.samplepojos.simplepojos.NumberObject;
import scenariotests.samplepojos.simplepojos.ShortObject;
import scenariotests.samplepojos.simplepojos.SingleImplementationAbstractClass;
import scenariotests.samplepojos.simplepojos.SingleImplementationAbstractClass.SingleImplementationOfAbstractClass;
import scenariotests.samplepojos.simplepojos.SingleImplementationInterface;
import scenariotests.samplepojos.simplepojos.SingleImplementationInterface.SingleImplementationOfInterface;
import scenariotests.samplepojos.simplepojos.StaticFieldObject;
import scenariotests.samplepojos.simplepojos.StringObject;
import scenariotests.samplepojos.simplepojos.TransientFieldObject;
import scenariotests.samplepojos.simplepojos.UnusedAbstractClass;
import scenariotests.samplepojos.simplepojos.UnusedInterface;
import scenariotests.samplepojos.simplepojos.UriObject;
import scenariotests.samplepojos.simplepojos.UrlObject;

/** test simple objects with a single field of various types */
public class SimpleObjectIT {

  @Test
  public void generateStringField() {
    StringObject expected = new StringObject();
    expected.setTestedField("mockTestedField");

    assertThat(createValueObject(StringObject.class), reflectionEquals(expected));
  }

  @Test
  public void generateUrlField() throws Exception {
    UrlObject expected = new UrlObject();
    expected.setTestedField(new URL("http://domain.mock/testedField"));

    assertThat(createValueObject(UrlObject.class), reflectionEquals(expected));
  }

  @Test
  public void generateUriField() throws Exception {
    UriObject expected = new UriObject();
    expected.setTestedField(new URI("http://domain.mock/testedField"));

    assertThat(createValueObject(UriObject.class), reflectionEquals(expected));
  }

  @Test
  public void generateBooleanField() {
    BooleanObject expected = new BooleanObject();
    expected.setTestedField(true);

    assertThat(createValueObject(BooleanObject.class), reflectionEquals(expected));
  }

  @Test
  public void generateCharacterField() {
    CharacterObject expected = new CharacterObject();
    expected.setTestedField('a');

    assertThat(createValueObject(CharacterObject.class), reflectionEquals(expected));
  }

  @Test
  public void generateByteField() {
    ByteObject expected = new ByteObject();
    expected.setTestedField((byte) 1);

    assertThat(createValueObject(ByteObject.class), reflectionEquals(expected));
  }

  @Test
  public void generateShortField() {
    ShortObject expected = new ShortObject();
    expected.setTestedField((short) 1);

    assertThat(createValueObject(ShortObject.class), reflectionEquals(expected));
  }

  @Test
  public void generateIntField() {
    IntObject expected = new IntObject();
    expected.setTestedField(1);

    assertThat(createValueObject(IntObject.class), reflectionEquals(expected));
  }

  @Test
  public void generateLongField() {
    LongObject expected = new LongObject();
    expected.setTestedField(1);

    assertThat(createValueObject(LongObject.class), reflectionEquals(expected));
  }

  @Test
  public void generateFloatField() {
    FloatObject expected = new FloatObject();
    expected.setTestedField(1f);

    assertThat(createValueObject(FloatObject.class), reflectionEquals(expected));
  }

  @Test
  public void generateDoubleField() {
    DoubleObject expected = new DoubleObject();
    expected.setTestedField(1d);

    assertThat(createValueObject(DoubleObject.class), reflectionEquals(expected));
  }

  @Test
  public void generateNumberFieldObject() {
    // Number type should produce an Integer field
    NumberObject expected = new NumberObject();
    expected.setTestedField(1d);

    assertThat(createValueObject(NumberObject.class), reflectionEquals(expected));
  }

  @Test
  public void generateFinalField() {
    FinalFieldObject expected = new FinalFieldObject("mockTestedField");

    assertThat(createValueObject(FinalFieldObject.class), reflectionEquals(expected));
  }

  @Test
  public void skipTransientField() {
    // don't set transient fields
    TransientFieldObject expected = new TransientFieldObject();
    expected.setTestedField(null);

    assertThat(createValueObject(TransientFieldObject.class), reflectionEquals(expected));
  }

  @Test
  public void skipStaticField() {
    // don't set static fields
    StaticFieldObject expected = new StaticFieldObject();
    StaticFieldObject.testedField = null;

    assertThat(createValueObject(StaticFieldObject.class), reflectionEquals(expected));
  }

  @Test
  public void interfacesWithNoSubtypesAreProxied() {
    UnusedInterface actual = createValueObject(UnusedInterface.class);
    assertThat(actual, isProxy());
  }

  @Test
  public void abstractClassesWithNoSubtypesAreProxied() {
    UnusedAbstractClass actual = createValueObject(UnusedAbstractClass.class);
    assertThat(actual, isProxy());
  }

  @Test
  public void interfacesWithOneSubtypeUseThatSubtype() {
    SingleImplementationOfInterface expected = new SingleImplementationOfInterface();
    expected.setTestedField(1);

    SingleImplementationInterface actual = createValueObject(SingleImplementationInterface.class);
    assertThat(actual, reflectionEquals(expected));
  }

  @Test
  public void abstractClassesWithOneSubtypeUseThatSubtype() {
    SingleImplementationOfAbstractClass expected = new SingleImplementationOfAbstractClass();
    expected.setTestedField(1);

    SingleImplementationAbstractClass actual =
        createValueObject(SingleImplementationAbstractClass.class);

    assertThat(actual, reflectionEquals(expected));
  }

  @Test
  public void interfacesWithManySubtypesAreProxied() {
    MultiImplementationInterface actual = createValueObject(MultiImplementationInterface.class);
    assertThat(actual, isProxy());
  }

  @Test
  public void abstractClassesWithManySubtypesAreProxied() {
    MultiImplementationAbstractClass actual =
        createValueObject(MultiImplementationAbstractClass.class);

    assertThat(actual, isProxy());
  }
}
