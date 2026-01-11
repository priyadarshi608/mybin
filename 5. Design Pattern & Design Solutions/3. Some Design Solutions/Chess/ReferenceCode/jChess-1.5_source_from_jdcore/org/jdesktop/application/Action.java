package org.jdesktop.application;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({java.lang.annotation.ElementType.METHOD})
public @interface Action
{
  String name() default "";
  
  String enabledProperty() default "";
  
  String selectedProperty() default "";
  
  Task.BlockingScope block() default Task.BlockingScope.NONE;
  
  @Retention(RetentionPolicy.RUNTIME)
  @Target({java.lang.annotation.ElementType.PARAMETER})
  public static @interface Parameter
  {
    String value() default "";
  }
}
