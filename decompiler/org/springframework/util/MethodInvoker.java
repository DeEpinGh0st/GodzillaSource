package org.springframework.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import org.springframework.lang.Nullable;
































public class MethodInvoker
{
  private static final Object[] EMPTY_ARGUMENTS = new Object[0];


  
  @Nullable
  protected Class<?> targetClass;


  
  @Nullable
  private Object targetObject;

  
  @Nullable
  private String targetMethod;

  
  @Nullable
  private String staticMethod;

  
  @Nullable
  private Object[] arguments;

  
  @Nullable
  private Method methodObject;


  
  public void setTargetClass(@Nullable Class<?> targetClass) {
    this.targetClass = targetClass;
  }



  
  @Nullable
  public Class<?> getTargetClass() {
    return this.targetClass;
  }







  
  public void setTargetObject(@Nullable Object targetObject) {
    this.targetObject = targetObject;
    if (targetObject != null) {
      this.targetClass = targetObject.getClass();
    }
  }



  
  @Nullable
  public Object getTargetObject() {
    return this.targetObject;
  }







  
  public void setTargetMethod(@Nullable String targetMethod) {
    this.targetMethod = targetMethod;
  }



  
  @Nullable
  public String getTargetMethod() {
    return this.targetMethod;
  }







  
  public void setStaticMethod(String staticMethod) {
    this.staticMethod = staticMethod;
  }




  
  public void setArguments(Object... arguments) {
    this.arguments = arguments;
  }



  
  public Object[] getArguments() {
    return (this.arguments != null) ? this.arguments : EMPTY_ARGUMENTS;
  }







  
  public void prepare() throws ClassNotFoundException, NoSuchMethodException {
    if (this.staticMethod != null) {
      int lastDotIndex = this.staticMethod.lastIndexOf('.');
      if (lastDotIndex == -1 || lastDotIndex == this.staticMethod.length()) {
        throw new IllegalArgumentException("staticMethod must be a fully qualified class plus method name: e.g. 'example.MyExampleClass.myExampleMethod'");
      }

      
      String className = this.staticMethod.substring(0, lastDotIndex);
      String methodName = this.staticMethod.substring(lastDotIndex + 1);
      this.targetClass = resolveClassName(className);
      this.targetMethod = methodName;
    } 
    
    Class<?> targetClass = getTargetClass();
    String targetMethod = getTargetMethod();
    Assert.notNull(targetClass, "Either 'targetClass' or 'targetObject' is required");
    Assert.notNull(targetMethod, "Property 'targetMethod' is required");
    
    Object[] arguments = getArguments();
    Class<?>[] argTypes = new Class[arguments.length];
    for (int i = 0; i < arguments.length; i++) {
      argTypes[i] = (arguments[i] != null) ? arguments[i].getClass() : Object.class;
    }

    
    try {
      this.methodObject = targetClass.getMethod(targetMethod, argTypes);
    }
    catch (NoSuchMethodException ex) {
      
      this.methodObject = findMatchingMethod();
      if (this.methodObject == null) {
        throw ex;
      }
    } 
  }








  
  protected Class<?> resolveClassName(String className) throws ClassNotFoundException {
    return ClassUtils.forName(className, ClassUtils.getDefaultClassLoader());
  }







  
  @Nullable
  protected Method findMatchingMethod() {
    String targetMethod = getTargetMethod();
    Object[] arguments = getArguments();
    int argCount = arguments.length;
    
    Class<?> targetClass = getTargetClass();
    Assert.state((targetClass != null), "No target class set");
    Method[] candidates = ReflectionUtils.getAllDeclaredMethods(targetClass);
    int minTypeDiffWeight = Integer.MAX_VALUE;
    Method matchingMethod = null;
    
    for (Method candidate : candidates) {
      if (candidate.getName().equals(targetMethod) && 
        candidate.getParameterCount() == argCount) {
        Class<?>[] paramTypes = candidate.getParameterTypes();
        int typeDiffWeight = getTypeDifferenceWeight(paramTypes, arguments);
        if (typeDiffWeight < minTypeDiffWeight) {
          minTypeDiffWeight = typeDiffWeight;
          matchingMethod = candidate;
        } 
      } 
    } 

    
    return matchingMethod;
  }








  
  public Method getPreparedMethod() throws IllegalStateException {
    if (this.methodObject == null) {
      throw new IllegalStateException("prepare() must be called prior to invoke() on MethodInvoker");
    }
    return this.methodObject;
  }




  
  public boolean isPrepared() {
    return (this.methodObject != null);
  }










  
  @Nullable
  public Object invoke() throws InvocationTargetException, IllegalAccessException {
    Object targetObject = getTargetObject();
    Method preparedMethod = getPreparedMethod();
    if (targetObject == null && !Modifier.isStatic(preparedMethod.getModifiers())) {
      throw new IllegalArgumentException("Target method must not be non-static without a target");
    }
    ReflectionUtils.makeAccessible(preparedMethod);
    return preparedMethod.invoke(targetObject, getArguments());
  }





















  
  public static int getTypeDifferenceWeight(Class<?>[] paramTypes, Object[] args) {
    int result = 0;
    for (int i = 0; i < paramTypes.length; i++) {
      if (!ClassUtils.isAssignableValue(paramTypes[i], args[i])) {
        return Integer.MAX_VALUE;
      }
      if (args[i] != null) {
        Class<?> paramType = paramTypes[i];
        Class<?> superClass = args[i].getClass().getSuperclass();
        while (superClass != null) {
          if (paramType.equals(superClass)) {
            result += 2;
            superClass = null; continue;
          } 
          if (ClassUtils.isAssignable(paramType, superClass)) {
            result += 2;
            superClass = superClass.getSuperclass();
            continue;
          } 
          superClass = null;
        } 
        
        if (paramType.isInterface()) {
          result++;
        }
      } 
    } 
    return result;
  }
}
