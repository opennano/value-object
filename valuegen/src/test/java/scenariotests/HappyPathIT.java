package scenariotests;

import static com.github.opennano.reflectionassert.hamcrest.ReflectionEqualsMatcher.reflectionEquals;
import static com.github.opennano.valuegen.Valuegen.createValueObject;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import scenariotests.samplepojos.extremepojos.DeepObject;
import scenariotests.samplepojos.extremepojos.DeepObject.Level1;
import scenariotests.samplepojos.extremepojos.DeepObject.Level2;
import scenariotests.samplepojos.extremepojos.DeepObject.Level3;
import scenariotests.samplepojos.inheritancepojos.InheritedFieldObject;
import scenariotests.samplepojos.inheritancepojos.MultipleStringsSameFieldNameObject;
import scenariotests.samplepojos.inheritancepojos.MultipleStringsSameFieldNameObject.DuplicateFieldObject;
import scenariotests.samplepojos.realisticpojos.Role;
import scenariotests.samplepojos.realisticpojos.User;
import scenariotests.samplepojos.simplepojos.CustomConstructorObject;
import scenariotests.samplepojos.simplepojos.InnerTypeObject.InnerType;
import scenariotests.samplepojos.simplepojos.MultipleBooleansObject;
import scenariotests.samplepojos.simplepojos.MultipleCharactersObject;
import scenariotests.samplepojos.simplepojos.MultipleNumbersObject;
import scenariotests.samplepojos.simplepojos.StaticInnerTypeObject.StaticInnerType;

public class HappyPathIT {

  @Test
  public void generateSimpleUserJavaBean() {
    User expected = new User();
    expected.setCreatedDate(new Date(1000));
    expected.setId(1);
    expected.setName("mockName");
    Role role = new Role();
    role.setActive(true);
    role.setId(2);
    role.setName("mockName2");
    expected.setRoles(Arrays.asList(role));

    User actual = createValueObject(User.class);
    assertThat(actual, reflectionEquals(expected));
  }

  @Test
  public void generateDeeplyNestedObject() {
    DeepObject expected = new DeepObject();
    Level1 level1Field = new Level1();
    Level2 level2Field = new Level2();
    Map<String, Level3> level3Map = new HashMap<>();
    Level3 level3Value = new Level3();
    level3Value.setLevel4Field("mockLevel4Field");
    level3Map.put("mockLevel3FieldKey", level3Value);
    level2Field.setLevel3Field(level3Map);
    level1Field.setLevel2Fields(Arrays.asList(level2Field));
    expected.setLevel1Field(level1Field);

    DeepObject actual = createValueObject(DeepObject.class);
    assertThat(actual, reflectionEquals(expected));
  }

  @Test
  public void generateObjectWithNonTrivialConstructor() {
    CustomConstructorObject expected = new CustomConstructorObject("mockTestedField");

    CustomConstructorObject actual = createValueObject(CustomConstructorObject.class);
    assertThat(actual, reflectionEquals(expected));
  }

  @Test
  public void generateInnerTypeObject() {
    InnerType expected = new InnerType();
    expected.setTestedField("mockTestedField");

    InnerType actual = createValueObject(InnerType.class);
    assertThat(actual, reflectionEquals(expected));
  }

  @Test
  public void generateStaticInnerTypeObject() {
    StaticInnerType expected = new StaticInnerType();
    expected.setTestedField("mockTestedField");

    StaticInnerType actual = createValueObject(StaticInnerType.class);
    assertThat(actual, reflectionEquals(expected));
  }

  @Test
  public void generateInheritedFieldObject() {
    InheritedFieldObject expected = new InheritedFieldObject();
    expected.setChildField("mockChildField");
    expected.setParentField("mockParentField");

    InheritedFieldObject actual = createValueObject(InheritedFieldObject.class);
    assertThat(actual, reflectionEquals(expected));
  }

  @Test
  public void generatePrivateClassObject() {
    PrivateClass expected = new PrivateClass();
    expected.setTestedField("mockTestedField");

    PrivateClass actual = createValueObject(PrivateClass.class);
    assertThat(actual, reflectionEquals(expected));
  }

  @Test
  public void allBooleansSetToTrue() {
    MultipleBooleansObject expected = new MultipleBooleansObject();
    expected.setTestedField1(true);
    expected.setTestedField2(true);

    MultipleBooleansObject actual = createValueObject(MultipleBooleansObject.class);
    assertThat(actual, reflectionEquals(expected));
  }

  @Test
  public void characterValuesIncrementFromA() {
    MultipleCharactersObject expected = new MultipleCharactersObject();
    expected.setTestedField1('a');
    expected.setTestedField2('b');

    MultipleCharactersObject actual = createValueObject(MultipleCharactersObject.class);
    assertThat(actual, reflectionEquals(expected));
  }

  @Test
  public void numericValuesIncrementFrom1() {
    MultipleNumbersObject expected = new MultipleNumbersObject();
    expected.setTestedField1(1);
    expected.setTestedField2(2f);

    MultipleNumbersObject actual = createValueObject(MultipleNumbersObject.class);
    assertThat(actual, reflectionEquals(expected));
  }

  @Test
  public void stringValuesAppendNumbersForUniqueness() {
    MultipleStringsSameFieldNameObject expected = new MultipleStringsSameFieldNameObject();
    expected.setTestedField("mockTestedField");
    DuplicateFieldObject duplicateFieldObject = new DuplicateFieldObject();
    duplicateFieldObject.setTestedField("mockTestedField2");
    expected.setNestedField(duplicateFieldObject);

    MultipleStringsSameFieldNameObject actual =
        createValueObject(MultipleStringsSameFieldNameObject.class);
    assertThat(actual, reflectionEquals(expected));
  }

  // this can't go in a separate class, because then we wouldn't be able to see it!
  private static class PrivateClass {

    @SuppressWarnings("unused") // used via reflection
    private String testedField;

    public void setTestedField(String testedField) {
      this.testedField = testedField;
    }
  }
}
