package com.github.opennano.valuegen.generator.delegates;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.io.File;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class FileValueDelegateTest {

  @InjectMocks private FileValueDelegate delegate;

  @ParameterizedTest
  @ValueSource(classes = File.class)
  public void handlesClass_handledTypes(Class<?> type) {
    assertThat(delegate.handlesClass(type), is(true));
  }

  @ParameterizedTest
  @ValueSource(classes = {int.class, String.class, Object.class})
  public void handlesClass_notHandledTypes(Class<?> type) {
    assertThat(delegate.handlesClass(type), is(false));
  }

  @Test
  public void generateValue_success() {
    Object fileValue = delegate.generateValue(null, null, "mockFileName.ext", null, null);

    assertThat(fileValue, is(equalTo(new File("/mock/path/mockFileName.ext"))));
  }
}
