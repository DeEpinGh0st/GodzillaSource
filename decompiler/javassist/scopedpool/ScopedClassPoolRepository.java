package javassist.scopedpool;

import java.util.Map;
import javassist.ClassPool;

public interface ScopedClassPoolRepository {
  void setClassPoolFactory(ScopedClassPoolFactory paramScopedClassPoolFactory);
  
  ScopedClassPoolFactory getClassPoolFactory();
  
  boolean isPrune();
  
  void setPrune(boolean paramBoolean);
  
  ScopedClassPool createScopedClassPool(ClassLoader paramClassLoader, ClassPool paramClassPool);
  
  ClassPool findClassPool(ClassLoader paramClassLoader);
  
  ClassPool registerClassLoader(ClassLoader paramClassLoader);
  
  Map<ClassLoader, ScopedClassPool> getRegisteredCLs();
  
  void clearUnregisteredClassLoaders();
  
  void unregisterClassLoader(ClassLoader paramClassLoader);
}
