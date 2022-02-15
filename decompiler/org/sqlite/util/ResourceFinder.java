package org.sqlite.util;

import java.net.URL;











































public class ResourceFinder
{
  public static URL find(Class<?> referenceClass, String resourceFileName) {
    return find(referenceClass.getClassLoader(), referenceClass.getPackage(), resourceFileName);
  }










  
  public static URL find(ClassLoader classLoader, Package basePackage, String resourceFileName) {
    return find(classLoader, basePackage.getName(), resourceFileName);
  }










  
  public static URL find(ClassLoader classLoader, String packageName, String resourceFileName) {
    String packagePath = packagePath(packageName);
    String resourcePath = packagePath + resourceFileName;
    if (!resourcePath.startsWith("/")) {
      resourcePath = "/" + resourcePath;
    }
    return classLoader.getResource(resourcePath);
  }


  
  private static String packagePath(Class<?> referenceClass) {
    return packagePath(referenceClass.getPackage());
  }





  
  private static String packagePath(Package basePackage) {
    return packagePath(basePackage.getName());
  }





  
  private static String packagePath(String packageName) {
    String packageAsPath = packageName.replaceAll("\\.", "/");
    return packageAsPath.endsWith("/") ? packageAsPath : (packageAsPath + "/");
  }
}
