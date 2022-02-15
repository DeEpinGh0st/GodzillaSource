package javassist.scopedpool;

import javassist.ClassPool;

public interface ScopedClassPoolFactory {
  ScopedClassPool create(ClassLoader paramClassLoader, ClassPool paramClassPool, ScopedClassPoolRepository paramScopedClassPoolRepository);
  
  ScopedClassPool create(ClassPool paramClassPool, ScopedClassPoolRepository paramScopedClassPoolRepository);
}
