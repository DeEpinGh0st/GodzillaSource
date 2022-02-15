package org.springframework.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;



























public abstract class KotlinDetector
{
  @Nullable
  private static final Class<? extends Annotation> kotlinMetadata;
  private static final boolean kotlinReflectPresent;
  
  static {
    Class<?> metadata;
    ClassLoader classLoader = KotlinDetector.class.getClassLoader();
    try {
      metadata = ClassUtils.forName("kotlin.Metadata", classLoader);
    }
    catch (ClassNotFoundException ex) {
      
      metadata = null;
    } 
    kotlinMetadata = (Class)metadata;
    kotlinReflectPresent = ClassUtils.isPresent("kotlin.reflect.full.KClasses", classLoader);
  }




  
  public static boolean isKotlinPresent() {
    return (kotlinMetadata != null);
  }




  
  public static boolean isKotlinReflectPresent() {
    return kotlinReflectPresent;
  }




  
  public static boolean isKotlinType(Class<?> clazz) {
    return (kotlinMetadata != null && clazz.getDeclaredAnnotation(kotlinMetadata) != null);
  }




  
  public static boolean isSuspendingFunction(Method method) {
    if (isKotlinType(method.getDeclaringClass())) {
      Class<?>[] types = method.getParameterTypes();
      if (types.length > 0 && "kotlin.coroutines.Continuation".equals(types[types.length - 1].getName())) {
        return true;
      }
    } 
    return false;
  }
}
