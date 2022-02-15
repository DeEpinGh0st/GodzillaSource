package org.springframework.core;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.ReflectionUtils;






































public final class BridgeMethodResolver
{
  private static final Map<Method, Method> cache = (Map<Method, Method>)new ConcurrentReferenceHashMap();













  
  public static Method findBridgedMethod(Method bridgeMethod) {
    if (!bridgeMethod.isBridge()) {
      return bridgeMethod;
    }
    Method bridgedMethod = cache.get(bridgeMethod);
    if (bridgedMethod == null) {
      
      List<Method> candidateMethods = new ArrayList<>();
      ReflectionUtils.MethodFilter filter = candidateMethod -> isBridgedCandidateFor(candidateMethod, bridgeMethod);
      
      ReflectionUtils.doWithMethods(bridgeMethod.getDeclaringClass(), candidateMethods::add, filter);
      if (!candidateMethods.isEmpty())
      {
        
        bridgedMethod = (candidateMethods.size() == 1) ? candidateMethods.get(0) : searchCandidates(candidateMethods, bridgeMethod);
      }
      if (bridgedMethod == null)
      {
        
        bridgedMethod = bridgeMethod;
      }
      cache.put(bridgeMethod, bridgedMethod);
    } 
    return bridgedMethod;
  }






  
  private static boolean isBridgedCandidateFor(Method candidateMethod, Method bridgeMethod) {
    return (!candidateMethod.isBridge() && !candidateMethod.equals(bridgeMethod) && candidateMethod
      .getName().equals(bridgeMethod.getName()) && candidateMethod
      .getParameterCount() == bridgeMethod.getParameterCount());
  }






  
  @Nullable
  private static Method searchCandidates(List<Method> candidateMethods, Method bridgeMethod) {
    if (candidateMethods.isEmpty()) {
      return null;
    }
    Method previousMethod = null;
    boolean sameSig = true;
    for (Method candidateMethod : candidateMethods) {
      if (isBridgeMethodFor(bridgeMethod, candidateMethod, bridgeMethod.getDeclaringClass())) {
        return candidateMethod;
      }
      if (previousMethod != null)
      {
        sameSig = (sameSig && Arrays.equals((Object[])candidateMethod.getGenericParameterTypes(), (Object[])previousMethod.getGenericParameterTypes()));
      }
      previousMethod = candidateMethod;
    } 
    return sameSig ? candidateMethods.get(0) : null;
  }




  
  static boolean isBridgeMethodFor(Method bridgeMethod, Method candidateMethod, Class<?> declaringClass) {
    if (isResolvedTypeMatch(candidateMethod, bridgeMethod, declaringClass)) {
      return true;
    }
    Method method = findGenericDeclaration(bridgeMethod);
    return (method != null && isResolvedTypeMatch(method, candidateMethod, declaringClass));
  }






  
  private static boolean isResolvedTypeMatch(Method genericMethod, Method candidateMethod, Class<?> declaringClass) {
    Type[] genericParameters = genericMethod.getGenericParameterTypes();
    if (genericParameters.length != candidateMethod.getParameterCount()) {
      return false;
    }
    Class<?>[] candidateParameters = candidateMethod.getParameterTypes();
    for (int i = 0; i < candidateParameters.length; i++) {
      ResolvableType genericParameter = ResolvableType.forMethodParameter(genericMethod, i, declaringClass);
      Class<?> candidateParameter = candidateParameters[i];
      if (candidateParameter.isArray())
      {
        if (!candidateParameter.getComponentType().equals(genericParameter.getComponentType().toClass())) {
          return false;
        }
      }
      
      if (!ClassUtils.resolvePrimitiveIfNecessary(candidateParameter).equals(ClassUtils.resolvePrimitiveIfNecessary(genericParameter.toClass()))) {
        return false;
      }
    } 
    return true;
  }






  
  @Nullable
  private static Method findGenericDeclaration(Method bridgeMethod) {
    Class<?> superclass = bridgeMethod.getDeclaringClass().getSuperclass();
    while (superclass != null && Object.class != superclass) {
      Method method = searchForMatch(superclass, bridgeMethod);
      if (method != null && !method.isBridge()) {
        return method;
      }
      superclass = superclass.getSuperclass();
    } 
    
    Class<?>[] interfaces = ClassUtils.getAllInterfacesForClass(bridgeMethod.getDeclaringClass());
    return searchInterfaces(interfaces, bridgeMethod);
  }
  
  @Nullable
  private static Method searchInterfaces(Class<?>[] interfaces, Method bridgeMethod) {
    for (Class<?> ifc : interfaces) {
      Method method = searchForMatch(ifc, bridgeMethod);
      if (method != null && !method.isBridge()) {
        return method;
      }
      
      method = searchInterfaces(ifc.getInterfaces(), bridgeMethod);
      if (method != null) {
        return method;
      }
    } 
    
    return null;
  }





  
  @Nullable
  private static Method searchForMatch(Class<?> type, Method bridgeMethod) {
    try {
      return type.getDeclaredMethod(bridgeMethod.getName(), bridgeMethod.getParameterTypes());
    }
    catch (NoSuchMethodException ex) {
      return null;
    } 
  }







  
  public static boolean isVisibilityBridgeMethodPair(Method bridgeMethod, Method bridgedMethod) {
    if (bridgeMethod == bridgedMethod) {
      return true;
    }
    return (bridgeMethod.getReturnType().equals(bridgedMethod.getReturnType()) && bridgeMethod
      .getParameterCount() == bridgedMethod.getParameterCount() && 
      Arrays.equals((Object[])bridgeMethod.getParameterTypes(), (Object[])bridgedMethod.getParameterTypes()));
  }
}
