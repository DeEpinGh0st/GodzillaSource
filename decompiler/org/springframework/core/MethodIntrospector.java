package org.springframework.core;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;











































public final class MethodIntrospector
{
  public static <T> Map<Method, T> selectMethods(Class<?> targetType, MetadataLookup<T> metadataLookup) {
    Map<Method, T> methodMap = new LinkedHashMap<>();
    Set<Class<?>> handlerTypes = new LinkedHashSet<>();
    Class<?> specificHandlerType = null;
    
    if (!Proxy.isProxyClass(targetType)) {
      specificHandlerType = ClassUtils.getUserClass(targetType);
      handlerTypes.add(specificHandlerType);
    } 
    handlerTypes.addAll(ClassUtils.getAllInterfacesForClassAsSet(targetType));
    
    for (Iterator<Class<?>> iterator = handlerTypes.iterator(); iterator.hasNext(); ) { Class<?> currentHandlerType = iterator.next();
      Class<?> targetClass = (specificHandlerType != null) ? specificHandlerType : currentHandlerType;
      
      ReflectionUtils.doWithMethods(currentHandlerType, method -> { Method specificMethod = ClassUtils.getMostSpecificMethod(method, targetClass); T result = metadataLookup.inspect(specificMethod); if (result != null) { Method bridgedMethod = BridgeMethodResolver.findBridgedMethod(specificMethod); if (bridgedMethod == specificMethod || metadataLookup.inspect(bridgedMethod) == null) methodMap.put(specificMethod, result);  }  }ReflectionUtils.USER_DECLARED_METHODS); }










    
    return methodMap;
  }








  
  public static Set<Method> selectMethods(Class<?> targetType, ReflectionUtils.MethodFilter methodFilter) {
    return selectMethods(targetType, method -> methodFilter.matches(method) ? Boolean.TRUE : null)
      .keySet();
  }













  
  public static Method selectInvocableMethod(Method method, Class<?> targetType) {
    if (method.getDeclaringClass().isAssignableFrom(targetType)) {
      return method;
    }
    try {
      String methodName = method.getName();
      Class<?>[] parameterTypes = method.getParameterTypes();
      for (Class<?> ifc : targetType.getInterfaces()) {
        try {
          return ifc.getMethod(methodName, parameterTypes);
        }
        catch (NoSuchMethodException noSuchMethodException) {}
      } 


      
      return targetType.getMethod(methodName, parameterTypes);
    }
    catch (NoSuchMethodException ex) {
      throw new IllegalStateException(String.format("Need to invoke method '%s' declared on target class '%s', but not found in any interface(s) of the exposed proxy type. Either pull the method up to an interface or switch to CGLIB proxies by enforcing proxy-target-class mode in your configuration.", new Object[] { method



              
              .getName(), method.getDeclaringClass().getSimpleName() }));
    } 
  }
  
  @FunctionalInterface
  public static interface MetadataLookup<T> {
    @Nullable
    T inspect(Method param1Method);
  }
}
