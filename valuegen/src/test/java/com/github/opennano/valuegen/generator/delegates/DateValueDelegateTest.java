package com.github.opennano.valuegen.generator.delegates;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.Calendar;
import java.util.Date;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.opennano.valuegen.generator.TypeInfo;

@ExtendWith(MockitoExtension.class)
public class DateValueDelegateTest {

  @InjectMocks private DateValueDelegate delegate;

  @ParameterizedTest
  @ValueSource(classes = {Date.class, Calendar.class})
  public void handlesClass_handledTypes(Class<?> type) {
    assertThat(delegate.handlesClass(type), is(true));
  }

  @ParameterizedTest
  @ValueSource(classes = {int.class, Object.class})
  public void handlesClass_notHandledTypes(Class<?> type) {
    assertThat(delegate.handlesClass(type), is(false));
  }

  @Test
  public void generateValue_dateType() {
    TypeInfo mockTypeInfo = new TypeInfo(Date.class);
    Object dateValue = delegate.generateValue(mockTypeInfo, null, null, null, null);

    assertThat(dateValue, is(new Date(1000)));
  }

  @Test
  public void generateValue_calendarType() {
    TypeInfo mockTypeInfo = new TypeInfo(Calendar.class);
    Calendar dateValue = (Calendar) delegate.generateValue(mockTypeInfo, null, null, null, null);

    assertThat(dateValue.getTime(), is(new Date(1000)));
  }

  @Test
  public void generateValue_datesIncrementsByOneSecond() {
    TypeInfo mockTypeInfo = new TypeInfo(Date.class);

    for (int i = 0; i < 3; i++) {
      Object characterValue = delegate.generateValue(mockTypeInfo, null, null, null, null);
      assertThat(characterValue, is(new Date(1000 * (i + 1))));
    }
  }
}
