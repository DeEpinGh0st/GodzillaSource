package org.jetbrains.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public final class ApiStatus {
  @Documented
  @Retention(RetentionPolicy.CLASS)
  @Target({ElementType.TYPE, ElementType.METHOD})
  public static @interface OverrideOnly {}
  
  @Documented
  @Retention(RetentionPolicy.CLASS)
  @Target({ElementType.TYPE, ElementType.METHOD})
  public static @interface NonExtendable {}
  
  @Documented
  @Retention(RetentionPolicy.CLASS)
  @Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.FIELD, ElementType.PACKAGE})
  public static @interface AvailableSince {
    String value();
  }
  
  @Documented
  @Retention(RetentionPolicy.CLASS)
  @Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.FIELD, ElementType.PACKAGE})
  public static @interface ScheduledForRemoval {
    String inVersion() default "";
  }
  
  @Documented
  @Retention(RetentionPolicy.CLASS)
  @Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.FIELD, ElementType.PACKAGE})
  public static @interface Internal {}
  
  @Documented
  @Retention(RetentionPolicy.CLASS)
  @Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.FIELD, ElementType.PACKAGE})
  public static @interface Experimental {}
}
