package org.hamcrest.internal;

import java.lang.reflect.Method;

























public class ReflectiveTypeFinder
{
  private final String methodName;
  private final int expectedNumberOfParameters;
  private final int typedParameter;
  
  public ReflectiveTypeFinder(String methodName, int expectedNumberOfParameters, int typedParameter) {
    this.methodName = methodName;
    this.expectedNumberOfParameters = expectedNumberOfParameters;
    this.typedParameter = typedParameter;
  }
  
  public Class<?> findExpectedType(Class<?> fromClass) {
    for (Class<?> c = fromClass; c != Object.class; c = c.getSuperclass()) {
      for (Method method : c.getDeclaredMethods()) {
        if (canObtainExpectedTypeFrom(method)) {
          return expectedTypeFrom(method);
        }
      } 
    } 
    throw new Error("Cannot determine correct type for " + this.methodName + "() method.");
  }




  
  protected boolean canObtainExpectedTypeFrom(Method method) {
    return (method.getName().equals(this.methodName) && (method.getParameterTypes()).length == this.expectedNumberOfParameters && !method.isSynthetic());
  }







  
  protected Class<?> expectedTypeFrom(Method method) {
    return method.getParameterTypes()[this.typedParameter];
  }
}
