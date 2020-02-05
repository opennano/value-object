package com.github.opennano.valuegen.generator.delegates;

import static com.github.opennano.valuegen.generator.ValueGeneratorDelegate.isInstanceOfAny;

import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import com.github.opennano.valuegen.generator.TypeInfo;
import com.github.opennano.valuegen.generator.ValueGeneratorDelegate;
import com.github.opennano.valuegen.generator.ValueObjectGenerator;

public class DateValueDelegate implements ValueGeneratorDelegate {

  private static final TimeZone UTC = TimeZone.getTimeZone("UTC");

  private Calendar calendar = Calendar.getInstance(UTC);

  public DateValueDelegate() {
    calendar.setTimeInMillis(0);
  }

  @Override
  public boolean handlesClass(Class<?> type) {
    // TODO handle Instant and related?
    return isInstanceOfAny(type, Date.class, Calendar.class);
  }

  @Override
  public Object generateValue(
      TypeInfo typeInfo,
      Field declaringField,
      String nameHint,
      Class<?> owningClass,
      ValueObjectGenerator valueGenerator) {

    calendar.add(Calendar.SECOND, 1);

    if (Date.class.equals(typeInfo.getResolvedClass())) {
      return calendar.getTime();
    }
    return calendar;
  }
}
