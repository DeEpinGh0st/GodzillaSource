package org.apache.log4j.lf5.util;

import java.io.InputStream;
import java.net.URL;

































































public class ResourceUtils
{
  public static InputStream getResourceAsStream(Object object, Resource resource) {
    ClassLoader loader = object.getClass().getClassLoader();
    
    InputStream in = null;
    
    if (loader != null) {
      in = loader.getResourceAsStream(resource.getName());
    } else {
      in = ClassLoader.getSystemResourceAsStream(resource.getName());
    } 
    
    return in;
  }















  
  public static URL getResourceAsURL(Object object, Resource resource) {
    ClassLoader loader = object.getClass().getClassLoader();
    
    URL url = null;
    
    if (loader != null) {
      url = loader.getResource(resource.getName());
    } else {
      url = ClassLoader.getSystemResource(resource.getName());
    } 
    
    return url;
  }
}
