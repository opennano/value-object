package com.github.opennano.valueobject.junit;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.Field;
import java.security.Permission;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class ValueObjectExtensionTest {

  @Mock private ObjectMarshaller mockObjectMarshaller;
  @InjectMocks private ValueObjectExtension extension;

  @Mock private ExtensionContext mockExtensionContext;

  @ValueObject private Object targetField;

  // hard to construct a scenario test that fails because then the whole test fails
  // just get the extra coverage by "cheating" and calling the underlying private method
  @Test
  public void injectField_simulateInjectionError() throws Exception {
    Field targetField = getClass().getDeclaredField("targetField");

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
              // lame way to do this as it gets called a bazillion times but it works
              targetField.setAccessible(false);
            }
          });

      assertThrows(
          FieldInjectionException.class,
          () ->
              ReflectionTestUtils.invokeMethod(
                  extension, "injectField", targetField, this, Optional.empty()));

    } finally {
      System.setSecurityManager(defaultSecurity);
    }
  }
}
