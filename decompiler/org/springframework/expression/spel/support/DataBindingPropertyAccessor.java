package org.springframework.expression.spel.support;

import java.lang.reflect.Method;








































public final class DataBindingPropertyAccessor
  extends ReflectivePropertyAccessor
{
  private DataBindingPropertyAccessor(boolean allowWrite) {
    super(allowWrite);
  }

  
  protected boolean isCandidateForProperty(Method method, Class<?> targetClass) {
    Class<?> clazz = method.getDeclaringClass();
    return (clazz != Object.class && clazz != Class.class && !ClassLoader.class.isAssignableFrom(targetClass));
  }




  
  public static DataBindingPropertyAccessor forReadOnlyAccess() {
    return new DataBindingPropertyAccessor(false);
  }



  
  public static DataBindingPropertyAccessor forReadWriteAccess() {
    return new DataBindingPropertyAccessor(true);
  }
}
