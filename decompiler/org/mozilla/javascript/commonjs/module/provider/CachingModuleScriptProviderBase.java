package org.mozilla.javascript.commonjs.module.provider;

import java.io.Reader;
import java.io.Serializable;
import java.net.URI;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.commonjs.module.ModuleScript;
import org.mozilla.javascript.commonjs.module.ModuleScriptProvider;

















public abstract class CachingModuleScriptProviderBase
  implements ModuleScriptProvider, Serializable
{
  private static final long serialVersionUID = 1L;
  private static final int loadConcurrencyLevel = Runtime.getRuntime().availableProcessors() * 8;
  private static final int loadLockShift;
  private static final int loadLockMask;
  private static final int loadLockCount;
  
  static {
    int sshift = 0;
    int ssize = 1;
    while (ssize < loadConcurrencyLevel) {
      sshift++;
      ssize <<= 1;
    } 
    loadLockShift = 32 - sshift;
    loadLockMask = ssize - 1;
    loadLockCount = ssize;
  }
  private final Object[] loadLocks = new Object[loadLockCount]; protected CachingModuleScriptProviderBase(ModuleSourceProvider moduleSourceProvider) {
    for (int i = 0; i < this.loadLocks.length; i++) {
      this.loadLocks[i] = new Object();
    }









    
    this.moduleSourceProvider = moduleSourceProvider;
  }
  
  private final ModuleSourceProvider moduleSourceProvider;
  
  public ModuleScript getModuleScript(Context cx, String moduleId, URI moduleUri, URI baseUri, Scriptable paths) throws Exception {
    CachedModuleScript cachedModule1 = getLoadedModule(moduleId);
    Object validator1 = getValidator(cachedModule1);
    ModuleSource moduleSource = (moduleUri == null) ? this.moduleSourceProvider.loadSource(moduleId, paths, validator1) : this.moduleSourceProvider.loadSource(moduleUri, baseUri, validator1);

    
    if (moduleSource == ModuleSourceProvider.NOT_MODIFIED) {
      return cachedModule1.getModule();
    }
    if (moduleSource == null) {
      return null;
    }
    Reader reader = moduleSource.getReader();
    try {
      int idHash = moduleId.hashCode();







    
    }
    finally {







      
      reader.close();
    } 
  }










  
  public static class CachedModuleScript
  {
    private final ModuleScript moduleScript;








    
    private final Object validator;









    
    public CachedModuleScript(ModuleScript moduleScript, Object validator) {
      this.moduleScript = moduleScript;
      this.validator = validator;
    }




    
    ModuleScript getModule() {
      return this.moduleScript;
    }




    
    Object getValidator() {
      return this.validator;
    }
  }
  
  private static Object getValidator(CachedModuleScript cachedModule) {
    return (cachedModule == null) ? null : cachedModule.getValidator();
  }
  
  private static boolean equal(Object o1, Object o2) {
    return (o1 == null) ? ((o2 == null)) : o1.equals(o2);
  }




  
  protected static int getConcurrencyLevel() {
    return loadLockCount;
  }
  
  protected abstract void putLoadedModule(String paramString, ModuleScript paramModuleScript, Object paramObject);
  
  protected abstract CachedModuleScript getLoadedModule(String paramString);
}
