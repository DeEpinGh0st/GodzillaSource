package org.springframework.objenesis.instantiator.sun;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import org.springframework.objenesis.ObjenesisException;




























class SunReflectionFactoryHelper
{
  public static <T> Constructor<T> newConstructorForSerialization(Class<T> type, Constructor<?> constructor) {
    Class<?> reflectionFactoryClass = getReflectionFactoryClass();
    Object reflectionFactory = createReflectionFactory(reflectionFactoryClass);
    
    Method newConstructorForSerializationMethod = getNewConstructorForSerializationMethod(reflectionFactoryClass);

    
    try {
      return (Constructor<T>)newConstructorForSerializationMethod.invoke(reflectionFactory, new Object[] { type, constructor });
    
    }
    catch (IllegalArgumentException|IllegalAccessException|java.lang.reflect.InvocationTargetException e) {
      throw new ObjenesisException(e);
    } 
  }
  
  private static Class<?> getReflectionFactoryClass() {
    try {
      return Class.forName("sun.reflect.ReflectionFactory");
    }
    catch (ClassNotFoundException e) {
      throw new ObjenesisException(e);
    } 
  }
  
  private static Object createReflectionFactory(Class<?> reflectionFactoryClass) {
    try {
      Method method = reflectionFactoryClass.getDeclaredMethod("getReflectionFactory", new Class[0]);
      
      return method.invoke(null, new Object[0]);
    }
    catch (NoSuchMethodException|IllegalAccessException|java.lang.reflect.InvocationTargetException|IllegalArgumentException e) {
      throw new ObjenesisException(e);
    } 
  }
  
  private static Method getNewConstructorForSerializationMethod(Class<?> reflectionFactoryClass) {
    try {
      return reflectionFactoryClass.getDeclaredMethod("newConstructorForSerialization", new Class[] { Class.class, Constructor.class });
    
    }
    catch (NoSuchMethodException e) {
      throw new ObjenesisException(e);
    } 
  }
}
