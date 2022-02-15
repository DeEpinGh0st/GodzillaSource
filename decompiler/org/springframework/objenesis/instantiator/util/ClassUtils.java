package org.springframework.objenesis.instantiator.util;

import org.springframework.objenesis.ObjenesisException;






























public final class ClassUtils
{
  public static String classNameToInternalClassName(String className) {
    return className.replace('.', '/');
  }







  
  public static String classNameToResource(String className) {
    return classNameToInternalClassName(className) + ".class";
  }









  
  public static <T> Class<T> getExistingClass(ClassLoader classLoader, String className) {
    try {
      return (Class)Class.forName(className, true, classLoader);
    }
    catch (ClassNotFoundException e) {
      return null;
    } 
  }

  
  public static <T> T newInstance(Class<T> clazz) {
    try {
      return clazz.newInstance();
    } catch (InstantiationException|IllegalAccessException e) {
      throw new ObjenesisException(e);
    } 
  }
}
