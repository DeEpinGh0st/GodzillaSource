package org.mozilla.javascript;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;














public class ClassCache
  implements Serializable
{
  private static final long serialVersionUID = -8866246036237312215L;
  private static final Object AKEY = "ClassCache";

  
  private volatile boolean cachingIsEnabled = true;

  
  private transient Map<Class<?>, JavaMembers> classTable;

  
  private transient Map<JavaAdapter.JavaAdapterSignature, Class<?>> classAdapterCache;

  
  private transient Map<Class<?>, Object> interfaceAdapterCache;

  
  private int generatedClassSerial;

  
  private Scriptable associatedScope;


  
  public static ClassCache get(Scriptable scope) {
    ClassCache cache = (ClassCache)ScriptableObject.getTopScopeValue(scope, AKEY);
    
    if (cache == null) {
      throw new RuntimeException("Can't find top level scope for ClassCache.get");
    }
    
    return cache;
  }












  
  public boolean associate(ScriptableObject topScope) {
    if (topScope.getParentScope() != null)
    {
      throw new IllegalArgumentException();
    }
    if (this == topScope.associateValue(AKEY, this)) {
      this.associatedScope = topScope;
      return true;
    } 
    return false;
  }




  
  public synchronized void clearCaches() {
    this.classTable = null;
    this.classAdapterCache = null;
    this.interfaceAdapterCache = null;
  }





  
  public final boolean isCachingEnabled() {
    return this.cachingIsEnabled;
  }



















  
  public synchronized void setCachingEnabled(boolean enabled) {
    if (enabled == this.cachingIsEnabled)
      return; 
    if (!enabled)
      clearCaches(); 
    this.cachingIsEnabled = enabled;
  }



  
  Map<Class<?>, JavaMembers> getClassCacheMap() {
    if (this.classTable == null)
    {
      
      this.classTable = new ConcurrentHashMap<Class<?>, JavaMembers>(16, 0.75F, 1);
    }
    return this.classTable;
  }

  
  Map<JavaAdapter.JavaAdapterSignature, Class<?>> getInterfaceAdapterCacheMap() {
    if (this.classAdapterCache == null) {
      this.classAdapterCache = new ConcurrentHashMap<JavaAdapter.JavaAdapterSignature, Class<?>>(16, 0.75F, 1);
    }
    return this.classAdapterCache;
  }






  
  @Deprecated
  public boolean isInvokerOptimizationEnabled() {
    return false;
  }








  
  @Deprecated
  public synchronized void setInvokerOptimizationEnabled(boolean enabled) {}








  
  public final synchronized int newClassSerialNumber() {
    return ++this.generatedClassSerial;
  }

  
  Object getInterfaceAdapter(Class<?> cl) {
    return (this.interfaceAdapterCache == null) ? null : this.interfaceAdapterCache.get(cl);
  }



  
  synchronized void cacheInterfaceAdapter(Class<?> cl, Object iadapter) {
    if (this.cachingIsEnabled) {
      if (this.interfaceAdapterCache == null) {
        this.interfaceAdapterCache = new ConcurrentHashMap<Class<?>, Object>(16, 0.75F, 1);
      }
      this.interfaceAdapterCache.put(cl, iadapter);
    } 
  }
  
  Scriptable getAssociatedScope() {
    return this.associatedScope;
  }
}
