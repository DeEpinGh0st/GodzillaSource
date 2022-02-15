package org.springframework.cglib.core;

























public class ClassLoaderAwareGeneratorStrategy
  extends DefaultGeneratorStrategy
{
  private final ClassLoader classLoader;
  
  public ClassLoaderAwareGeneratorStrategy(ClassLoader classLoader) {
    this.classLoader = classLoader;
  }
  
  public byte[] generate(ClassGenerator cg) throws Exception {
    ClassLoader threadContextClassLoader;
    if (this.classLoader == null) {
      return super.generate(cg);
    }
    
    Thread currentThread = Thread.currentThread();
    
    try {
      threadContextClassLoader = currentThread.getContextClassLoader();
    }
    catch (Throwable ex) {
      
      return super.generate(cg);
    } 
    
    boolean overrideClassLoader = !this.classLoader.equals(threadContextClassLoader);
    if (overrideClassLoader) {
      currentThread.setContextClassLoader(this.classLoader);
    }
    try {
      return super.generate(cg);
    } finally {
      
      if (overrideClassLoader)
      {
        currentThread.setContextClassLoader(threadContextClassLoader);
      }
    } 
  }
}
