package com.github.opennano.valuegen.generator.delegates;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.net.URI;
import java.net.URL;
import java.security.Permission;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.opennano.valuegen.ValueGenerationException;
import com.github.opennano.valuegen.generator.TypeInfo;

@ExtendWith(MockitoExtension.class)
public class UrlValueDelegateTest {

  @InjectMocks private UrlValueDelegate delegate;

  @ParameterizedTest
  @ValueSource(
      classes = {
        URI.class, URL.class,
      })
  public void handlesClass_handledTypes(Class<?> type) {
    assertThat(delegate.handlesClass(type), is(true));
  }

  @ParameterizedTest
  @ValueSource(classes = {int.class, String.class, Object.class})
  public void handlesClass_notHandledTypes(Class<?> type) {
    assertThat(delegate.handlesClass(type), is(false));
  }

  @Test
  public void generateValue_url() throws Exception {
    TypeInfo typeInfo = new TypeInfo(URL.class);
    Object urlValue = delegate.generateValue(typeInfo, null, "link", null, null);

    assertThat(urlValue, equalTo(new URL("http://domain.mock/link")));
  }

  @Test
  public void generateValue_uri() throws Exception {
    TypeInfo typeInfo = new TypeInfo(URI.class);
    Object uriValue = delegate.generateValue(typeInfo, null, "link", null, null);

    assertThat(uriValue, equalTo(new URI("http://domain.mock/link")));
  }

  @Test
  public void generateValue_defaultName() throws Exception {
    TypeInfo typeInfo = new TypeInfo(URL.class);
    Object urlValue = delegate.generateValue(typeInfo, null, null, null, null);

    assertThat(urlValue, equalTo(new URL("http://domain.mock/url")));
  }

  @Test
  public void generateValue_reflectiveAccessException() {
    TypeInfo typeInfo = new TypeInfo(URL.class);
    
    // use a security manager to block reflective access to the URL constructor
    SecurityManager defaultSecurity = System.getSecurityManager();
    try {
      System.setSecurityManager(
          new SecurityManager() {
            @Override
            public void checkPermission(Permission perm) {
              // no-op: allow everything
            }

            @Override
            public void checkPackageAccess(String pkg) {
              if (URL.class.getPackage().getName().equals(pkg)) {
                throw new SecurityException("reflection exception test");
              }
            }
          });

      assertThrows(
          ValueGenerationException.class,
          () -> delegate.generateValue(typeInfo, null, null, null, null));
    } finally {
      System.setSecurityManager(defaultSecurity);
    }
  }
}
