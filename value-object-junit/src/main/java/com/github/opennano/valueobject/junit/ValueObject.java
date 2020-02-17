package com.github.opennano.valueobject.junit;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({FIELD, PARAMETER})
@Retention(RUNTIME)
@Documented
public @interface ValueObject {
  String value() default "";

  /**
   * Specifies if a value should be generated on the fly or deserialized from a file. Default is to
   * read from file. While tempting, generating objects on the fly is not encouraged as it makes
   * your tests brittle. For example, if you write a test that uses a value object for an interface
   * and you only have one interface implementation on your classpath, the value generator will find
   * the implementation and generate a value object as expected. However, if at some point in the
   * future a second implementation is added, the value generator won't know which to use and
   * instead will return a proxy. If the value object is then used as an expected value it will
   * likely fail during comparison, and that failure could be difficult to track down.
   * @return true to generate an object, defaults to false to marshal one instead
   */
  boolean generate() default false;
}
