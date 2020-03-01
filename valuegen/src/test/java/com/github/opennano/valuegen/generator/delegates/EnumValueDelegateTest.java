package com.github.opennano.valuegen.generator.delegates;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.opennano.valuegen.generator.TypeInfo;

@ExtendWith(MockitoExtension.class)
public class EnumValueDelegateTest {

  @InjectMocks private EnumValueDelegate delegate;

  @ParameterizedTest
  @ValueSource(classes = SampleEnum.class)
  public void handlesClass_handledTypes(Class<?> type) {
    assertThat(delegate.handlesClass(type), is(true));
  }

  @ParameterizedTest
  @ValueSource(classes = {int.class, Object.class})
  public void handlesClass_notHandledTypes(Class<?> type) {
    assertThat(delegate.handlesClass(type), is(false));
  }

  @Test
  public void generateValue_success() {
    TypeInfo mockTypeInfo = new TypeInfo(SampleEnum.class);
    Object dateValue = delegate.generateValue(mockTypeInfo, null, null, null, null);

    assertThat(dateValue, is(SampleEnum.A));
  }

  @Test
  public void generateValue_incrementsAndResets() {
    TypeInfo mockTypeInfo = new TypeInfo(SampleEnum.class);

    assertThat(delegate.generateValue(mockTypeInfo, null, null, null, null), is(SampleEnum.A));
    assertThat(delegate.generateValue(mockTypeInfo, null, null, null, null), is(SampleEnum.B));
    assertThat(delegate.generateValue(mockTypeInfo, null, null, null, null), is(SampleEnum.C));
    assertThat(delegate.generateValue(mockTypeInfo, null, null, null, null), is(SampleEnum.A));
  }

  private static enum SampleEnum {
    A,
    B,
    C
  }
}
