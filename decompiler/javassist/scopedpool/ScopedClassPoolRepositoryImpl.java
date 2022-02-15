package javassist.scopedpool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import javassist.ClassPath;
import javassist.ClassPool;
import javassist.LoaderClassPath;























public class ScopedClassPoolRepositoryImpl
  implements ScopedClassPoolRepository
{
  private static final ScopedClassPoolRepositoryImpl instance = new ScopedClassPoolRepositoryImpl();


  
  private boolean prune = true;

  
  boolean pruneWhenCached;

  
  protected Map<ClassLoader, ScopedClassPool> registeredCLs = Collections.synchronizedMap(new WeakHashMap<>());

  
  protected ClassPool classpool;

  
  protected ScopedClassPoolFactory factory = new ScopedClassPoolFactoryImpl();





  
  public static ScopedClassPoolRepository getInstance() {
    return instance;
  }



  
  private ScopedClassPoolRepositoryImpl() {
    this.classpool = ClassPool.getDefault();
    
    ClassLoader cl = Thread.currentThread().getContextClassLoader();
    this.classpool.insertClassPath((ClassPath)new LoaderClassPath(cl));
  }






  
  public boolean isPrune() {
    return this.prune;
  }






  
  public void setPrune(boolean prune) {
    this.prune = prune;
  }








  
  public ScopedClassPool createScopedClassPool(ClassLoader cl, ClassPool src) {
    return this.factory.create(cl, src, this);
  }

  
  public ClassPool findClassPool(ClassLoader cl) {
    if (cl == null) {
      return registerClassLoader(ClassLoader.getSystemClassLoader());
    }
    return registerClassLoader(cl);
  }







  
  public ClassPool registerClassLoader(ClassLoader ucl) {
    synchronized (this.registeredCLs) {




      
      if (this.registeredCLs.containsKey(ucl)) {
        return this.registeredCLs.get(ucl);
      }
      ScopedClassPool pool = createScopedClassPool(ucl, this.classpool);
      this.registeredCLs.put(ucl, pool);
      return pool;
    } 
  }




  
  public Map<ClassLoader, ScopedClassPool> getRegisteredCLs() {
    clearUnregisteredClassLoaders();
    return this.registeredCLs;
  }





  
  public void clearUnregisteredClassLoaders() {
    List<ClassLoader> toUnregister = null;
    synchronized (this.registeredCLs) {
      for (Map.Entry<ClassLoader, ScopedClassPool> reg : this.registeredCLs.entrySet()) {
        if (((ScopedClassPool)reg.getValue()).isUnloadedClassLoader()) {
          ClassLoader cl = ((ScopedClassPool)reg.getValue()).getClassLoader();
          if (cl != null) {
            if (toUnregister == null)
              toUnregister = new ArrayList<>(); 
            toUnregister.add(cl);
          } 
          this.registeredCLs.remove(reg.getKey());
        } 
      } 
      if (toUnregister != null)
        for (ClassLoader cl : toUnregister) {
          unregisterClassLoader(cl);
        } 
    } 
  }
  
  public void unregisterClassLoader(ClassLoader cl) {
    synchronized (this.registeredCLs) {
      ScopedClassPool pool = this.registeredCLs.remove(cl);
      if (pool != null) {
        pool.close();
      }
    } 
  }

  
  public void insertDelegate(ScopedClassPoolRepository delegate) {}

  
  public void setClassPoolFactory(ScopedClassPoolFactory factory) {
    this.factory = factory;
  }

  
  public ScopedClassPoolFactory getClassPoolFactory() {
    return this.factory;
  }
}
