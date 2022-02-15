package org.mozilla.javascript;







public class DefiningClassLoader
  extends ClassLoader
  implements GeneratedClassLoader
{
  private final ClassLoader parentLoader;
  
  public DefiningClassLoader() {
    this.parentLoader = getClass().getClassLoader();
  }
  
  public DefiningClassLoader(ClassLoader parentLoader) {
    this.parentLoader = parentLoader;
  }



  
  public Class<?> defineClass(String name, byte[] data) {
    return defineClass(name, data, 0, data.length, SecurityUtilities.getProtectionDomain(getClass()));
  }

  
  public void linkClass(Class<?> cl) {
    resolveClass(cl);
  }



  
  public Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
    Class<?> cl = findLoadedClass(name);
    if (cl == null) {
      if (this.parentLoader != null) {
        cl = this.parentLoader.loadClass(name);
      } else {
        cl = findSystemClass(name);
      } 
    }
    if (resolve) {
      resolveClass(cl);
    }
    return cl;
  }
}
