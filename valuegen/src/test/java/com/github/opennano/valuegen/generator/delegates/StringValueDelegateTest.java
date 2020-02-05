package com.github.opennano.valuegen.generator.delegates;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class StringValueDelegateTest {

  @InjectMocks private StringValueDelegate delegate;

  @ParameterizedTest
  @ValueSource(
      classes = {
        String.class,
        CharSequence.class,
      })
  public void handlesClass_handledTypes(Class<?> type) {
    assertThat(delegate.handlesClass(type), is(true));
  }

  @ParameterizedTest
  @ValueSource(classes = {int.class, Object.class})
  public void handlesClass_notHandledTypes(Class<?> type) {
    assertThat(delegate.handlesClass(type), is(false));
  }

  @Test
  public void generateValue_string() {
    Object stringValue = delegate.generateValue(null, null, "fieldName", null, null);

    assertThat(stringValue, equalTo("mockFieldName"));
  }

  @Test
  public void generateValue_defaultName() {
    Object stringValue = delegate.generateValue(null, null, null, null, null);

    assertThat(stringValue, equalTo("mockString"));
  }

  @Test
  public void generateValue_uniqueSuffixAddedForDuplicated() {
    Object stringValue1 = delegate.generateValue(null, null, "fieldName", null, null);
    assertThat(stringValue1, equalTo("mockFieldName"));

    Object stringValue2 = delegate.generateValue(null, null, "fieldName", null, null);
    assertThat(stringValue2, equalTo("mockFieldName2"));

    Object stringValue3 = delegate.generateValue(null, null, "fieldName", null, null);
    assertThat(stringValue3, equalTo("mockFieldName3"));
  }
}
