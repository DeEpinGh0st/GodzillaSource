package org.springframework.core;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.Iterator;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

















































public final class Conventions
{
  private static final String PLURAL_SUFFIX = "List";
  
  public static String getVariableName(Object value) {
    Class<?> valueClass;
    Assert.notNull(value, "Value must not be null");
    
    boolean pluralize = false;
    
    if (value.getClass().isArray()) {
      valueClass = value.getClass().getComponentType();
      pluralize = true;
    }
    else if (value instanceof Collection) {
      Collection<?> collection = (Collection)value;
      if (collection.isEmpty()) {
        throw new IllegalArgumentException("Cannot generate variable name for an empty Collection");
      }
      
      Object valueToCheck = peekAhead(collection);
      valueClass = getClassForValue(valueToCheck);
      pluralize = true;
    } else {
      
      valueClass = getClassForValue(value);
    } 
    
    String name = ClassUtils.getShortNameAsProperty(valueClass);
    return pluralize ? pluralize(name) : name;
  }









  
  public static String getVariableNameForParameter(MethodParameter parameter) {
    Class<?> valueClass;
    Assert.notNull(parameter, "MethodParameter must not be null");
    
    boolean pluralize = false;
    String reactiveSuffix = "";
    
    if (parameter.getParameterType().isArray()) {
      valueClass = parameter.getParameterType().getComponentType();
      pluralize = true;
    }
    else if (Collection.class.isAssignableFrom(parameter.getParameterType())) {
      valueClass = ResolvableType.forMethodParameter(parameter).asCollection().resolveGeneric(new int[0]);
      if (valueClass == null) {
        throw new IllegalArgumentException("Cannot generate variable name for non-typed Collection parameter type");
      }
      
      pluralize = true;
    } else {
      
      valueClass = parameter.getParameterType();
      ReactiveAdapter adapter = ReactiveAdapterRegistry.getSharedInstance().getAdapter(valueClass);
      if (adapter != null && !adapter.getDescriptor().isNoValue()) {
        reactiveSuffix = ClassUtils.getShortName(valueClass);
        valueClass = parameter.nested().getNestedParameterType();
      } 
    } 
    
    String name = ClassUtils.getShortNameAsProperty(valueClass);
    return pluralize ? pluralize(name) : (name + reactiveSuffix);
  }






  
  public static String getVariableNameForReturnType(Method method) {
    return getVariableNameForReturnType(method, method.getReturnType(), null);
  }









  
  public static String getVariableNameForReturnType(Method method, @Nullable Object value) {
    return getVariableNameForReturnType(method, method.getReturnType(), value);
  }













  
  public static String getVariableNameForReturnType(Method method, Class<?> resolvedType, @Nullable Object value) {
    Class<?> valueClass;
    Assert.notNull(method, "Method must not be null");
    
    if (Object.class == resolvedType) {
      if (value == null) {
        throw new IllegalArgumentException("Cannot generate variable name for an Object return type with null value");
      }
      
      return getVariableName(value);
    } 

    
    boolean pluralize = false;
    String reactiveSuffix = "";
    
    if (resolvedType.isArray()) {
      valueClass = resolvedType.getComponentType();
      pluralize = true;
    }
    else if (Collection.class.isAssignableFrom(resolvedType)) {
      valueClass = ResolvableType.forMethodReturnType(method).asCollection().resolveGeneric(new int[0]);
      if (valueClass == null) {
        if (!(value instanceof Collection)) {
          throw new IllegalArgumentException("Cannot generate variable name for non-typed Collection return type and a non-Collection value");
        }
        
        Collection<?> collection = (Collection)value;
        if (collection.isEmpty()) {
          throw new IllegalArgumentException("Cannot generate variable name for non-typed Collection return type and an empty Collection value");
        }
        
        Object valueToCheck = peekAhead(collection);
        valueClass = getClassForValue(valueToCheck);
      } 
      pluralize = true;
    } else {
      
      valueClass = resolvedType;
      ReactiveAdapter adapter = ReactiveAdapterRegistry.getSharedInstance().getAdapter(valueClass);
      if (adapter != null && !adapter.getDescriptor().isNoValue()) {
        reactiveSuffix = ClassUtils.getShortName(valueClass);
        valueClass = ResolvableType.forMethodReturnType(method).getGeneric(new int[0]).toClass();
      } 
    } 
    
    String name = ClassUtils.getShortNameAsProperty(valueClass);
    return pluralize ? pluralize(name) : (name + reactiveSuffix);
  }





  
  public static String attributeNameToPropertyName(String attributeName) {
    Assert.notNull(attributeName, "'attributeName' must not be null");
    if (!attributeName.contains("-")) {
      return attributeName;
    }
    char[] result = new char[attributeName.length() - 1];
    int currPos = 0;
    boolean upperCaseNext = false;
    for (int i = 0; i < attributeName.length(); i++) {
      char c = attributeName.charAt(i);
      if (c == '-') {
        upperCaseNext = true;
      }
      else if (upperCaseNext) {
        result[currPos++] = Character.toUpperCase(c);
        upperCaseNext = false;
      } else {
        
        result[currPos++] = c;
      } 
    } 
    return new String(result, 0, currPos);
  }





  
  public static String getQualifiedAttributeName(Class<?> enclosingClass, String attributeName) {
    Assert.notNull(enclosingClass, "'enclosingClass' must not be null");
    Assert.notNull(attributeName, "'attributeName' must not be null");
    return enclosingClass.getName() + '.' + attributeName;
  }









  
  private static Class<?> getClassForValue(Object value) {
    Class<?> valueClass = value.getClass();
    if (Proxy.isProxyClass(valueClass)) {
      Class<?>[] ifcs = valueClass.getInterfaces();
      for (Class<?> ifc : ifcs) {
        if (!ClassUtils.isJavaLanguageInterface(ifc)) {
          return ifc;
        }
      }
    
    } else if (valueClass.getName().lastIndexOf('$') != -1 && valueClass.getDeclaringClass() == null) {

      
      valueClass = valueClass.getSuperclass();
    } 
    return valueClass;
  }



  
  private static String pluralize(String name) {
    return name + "List";
  }





  
  private static <E> E peekAhead(Collection<E> collection) {
    Iterator<E> it = collection.iterator();
    if (!it.hasNext()) {
      throw new IllegalStateException("Unable to peek ahead in non-empty collection - no element found");
    }
    
    E value = it.next();
    if (value == null) {
      throw new IllegalStateException("Unable to peek ahead in non-empty collection - only null element found");
    }
    
    return value;
  }
}
