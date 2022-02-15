package org.apache.log4j.helpers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;





























public class Loader
{
  static final String TSTR = "Caught Exception while in Loader.getResource. This may be innocuous.";
  private static boolean java1 = true;
  private static boolean ignoreTCL = false;
  
  static {
    String prop = OptionConverter.getSystemProperty("java.version", null);
    
    if (prop != null) {
      int i = prop.indexOf('.');
      if (i != -1 && 
        prop.charAt(i + 1) != '1') {
        java1 = false;
      }
    } 
    String ignoreTCLProp = OptionConverter.getSystemProperty("log4j.ignoreTCL", null);
    if (ignoreTCLProp != null) {
      ignoreTCL = OptionConverter.toBoolean(ignoreTCLProp, true);
    }
  }







  
  public static URL getResource(String resource, Class clazz) {
    return getResource(resource);
  }



















  
  public static URL getResource(String resource) {
    ClassLoader classLoader = null;
    URL url = null;
    
    try {
      if (!java1 && !ignoreTCL) {
        classLoader = getTCL();
        if (classLoader != null) {
          LogLog.debug("Trying to find [" + resource + "] using context classloader " + classLoader + ".");
          
          url = classLoader.getResource(resource);
          if (url != null) {
            return url;
          }
        } 
      } 


      
      classLoader = Loader.class.getClassLoader();
      if (classLoader != null) {
        LogLog.debug("Trying to find [" + resource + "] using " + classLoader + " class loader.");
        
        url = classLoader.getResource(resource);
        if (url != null) {
          return url;
        }
      } 
    } catch (IllegalAccessException t) {
      LogLog.warn("Caught Exception while in Loader.getResource. This may be innocuous.", t);
    } catch (InvocationTargetException t) {
      if (t.getTargetException() instanceof InterruptedException || t.getTargetException() instanceof java.io.InterruptedIOException)
      {
        Thread.currentThread().interrupt();
      }
      LogLog.warn("Caught Exception while in Loader.getResource. This may be innocuous.", t);
    } catch (Throwable t) {


      
      LogLog.warn("Caught Exception while in Loader.getResource. This may be innocuous.", t);
    } 




    
    LogLog.debug("Trying to find [" + resource + "] using ClassLoader.getSystemResource().");
    
    return ClassLoader.getSystemResource(resource);
  }





  
  public static boolean isJava1() {
    return java1;
  }









  
  private static ClassLoader getTCL() throws IllegalAccessException, InvocationTargetException {
    Method method = null;
    try {
      method = Thread.class.getMethod("getContextClassLoader", null);
    } catch (NoSuchMethodException e) {
      
      return null;
    } 
    
    return (ClassLoader)method.invoke(Thread.currentThread(), null);
  }











  
  public static Class loadClass(String clazz) throws ClassNotFoundException {
    if (java1 || ignoreTCL) {
      return Class.forName(clazz);
    }
    try {
      return getTCL().loadClass(clazz);


    
    }
    catch (InvocationTargetException e) {
      if (e.getTargetException() instanceof InterruptedException || e.getTargetException() instanceof java.io.InterruptedIOException)
      {
        Thread.currentThread().interrupt();
      }
    } catch (Throwable t) {}

    
    return Class.forName(clazz);
  }
}
