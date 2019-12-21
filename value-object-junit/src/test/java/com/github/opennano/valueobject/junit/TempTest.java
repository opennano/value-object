package com.github.opennano.valueobject.junit;

import static com.github.opennano.reflectionassert.hamcrest.ReflectionEqualsMatcher.reflectionEquals;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Arrays;
import java.util.Date;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.github.opennano.reflectionassert.LeniencyMode;
import com.github.opennano.reflectionassert.annotations.ValueObject;

@ExtendWith(ValueObjectExtension.class)
public class TempTest {

  //  @ValueObject private static CatalogItem myItem;
  //  @ValueObject private CatalogItem catalogItem;

//    @Test
//    public void testParameterInjection1(@ValueObject Boxset boxset) {
//      assertThat(returnedObject(), reflectionEquals(boxset));
//    }

  @Test
  public void testParameterInjection2(@ValueObject(generate = true) int[] ints) {
    assertThat(Arrays.asList(1), reflectionEquals(ints));
  }

  @Test
  public void testParameterInjection3(@ValueObject(generate = true) Date date) {
    assertThat(new Date(), reflectionEquals(date, LeniencyMode.LENIENT_DATES));
  }

  //  private Boxset returnedObject() {
  //    Boxset boxset = new Boxset();
  //    boxset.setId(1l);
  //    boxset.setName("mockName");
  //    boxset.setAlbums(Arrays.asList(new Album()));
  //    return boxset;
  //  }
}
