package org.mozilla.javascript.commonjs.module.provider;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.mozilla.javascript.commonjs.module.ModuleScript;














public class StrongCachingModuleScriptProvider
  extends CachingModuleScriptProviderBase
{
  private static final long serialVersionUID = 1L;
  private final Map<String, CachingModuleScriptProviderBase.CachedModuleScript> modules = new ConcurrentHashMap<String, CachingModuleScriptProviderBase.CachedModuleScript>(16, 0.75F, getConcurrencyLevel());







  
  public StrongCachingModuleScriptProvider(ModuleSourceProvider moduleSourceProvider) {
    super(moduleSourceProvider);
  }

  
  protected CachingModuleScriptProviderBase.CachedModuleScript getLoadedModule(String moduleId) {
    return this.modules.get(moduleId);
  }


  
  protected void putLoadedModule(String moduleId, ModuleScript moduleScript, Object validator) {
    this.modules.put(moduleId, new CachingModuleScriptProviderBase.CachedModuleScript(moduleScript, validator));
  }
}
