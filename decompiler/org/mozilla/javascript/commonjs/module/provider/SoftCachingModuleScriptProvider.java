package org.mozilla.javascript.commonjs.module.provider;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.commonjs.module.ModuleScript;















public class SoftCachingModuleScriptProvider
  extends CachingModuleScriptProviderBase
{
  private static final long serialVersionUID = 1L;
  private transient ReferenceQueue<Script> scriptRefQueue = new ReferenceQueue<Script>();

  
  private transient ConcurrentMap<String, ScriptReference> scripts = new ConcurrentHashMap<String, ScriptReference>(16, 0.75F, getConcurrencyLevel());








  
  public SoftCachingModuleScriptProvider(ModuleSourceProvider moduleSourceProvider) {
    super(moduleSourceProvider);
  }






  
  public ModuleScript getModuleScript(Context cx, String moduleId, URI uri, URI base, Scriptable paths) throws Exception {
    while (true) {
      ScriptReference ref = (ScriptReference)this.scriptRefQueue.poll();
      if (ref == null) {
        break;
      }
      this.scripts.remove(ref.getModuleId(), ref);
    } 
    return super.getModuleScript(cx, moduleId, uri, base, paths);
  }

  
  protected CachingModuleScriptProviderBase.CachedModuleScript getLoadedModule(String moduleId) {
    ScriptReference scriptRef = this.scripts.get(moduleId);
    return (scriptRef != null) ? scriptRef.getCachedModuleScript() : null;
  }



  
  protected void putLoadedModule(String moduleId, ModuleScript moduleScript, Object validator) {
    this.scripts.put(moduleId, new ScriptReference(moduleScript.getScript(), moduleId, moduleScript.getUri(), moduleScript.getBase(), validator, this.scriptRefQueue));
  }

  
  private static class ScriptReference
    extends SoftReference<Script>
  {
    private final String moduleId;
    private final URI uri;
    private final URI base;
    private final Object validator;
    
    ScriptReference(Script script, String moduleId, URI uri, URI base, Object validator, ReferenceQueue<Script> refQueue) {
      super(script, refQueue);
      this.moduleId = moduleId;
      this.uri = uri;
      this.base = base;
      this.validator = validator;
    }
    
    CachingModuleScriptProviderBase.CachedModuleScript getCachedModuleScript() {
      Script script = get();
      if (script == null) {
        return null;
      }
      return new CachingModuleScriptProviderBase.CachedModuleScript(new ModuleScript(script, this.uri, this.base), this.validator);
    }

    
    String getModuleId() {
      return this.moduleId;
    }
  }


  
  private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
    this.scriptRefQueue = new ReferenceQueue<Script>();
    this.scripts = new ConcurrentHashMap<String, ScriptReference>();
    Map<String, CachingModuleScriptProviderBase.CachedModuleScript> serScripts = (Map<String, CachingModuleScriptProviderBase.CachedModuleScript>)in.readObject();
    for (Map.Entry<String, CachingModuleScriptProviderBase.CachedModuleScript> entry : serScripts.entrySet()) {
      CachingModuleScriptProviderBase.CachedModuleScript cachedModuleScript = entry.getValue();
      putLoadedModule(entry.getKey(), cachedModuleScript.getModule(), cachedModuleScript.getValidator());
    } 
  }

  
  private void writeObject(ObjectOutputStream out) throws IOException {
    Map<String, CachingModuleScriptProviderBase.CachedModuleScript> serScripts = new HashMap<String, CachingModuleScriptProviderBase.CachedModuleScript>();
    
    for (Map.Entry<String, ScriptReference> entry : this.scripts.entrySet()) {
      CachingModuleScriptProviderBase.CachedModuleScript cachedModuleScript = ((ScriptReference)entry.getValue()).getCachedModuleScript();
      
      if (cachedModuleScript != null) {
        serScripts.put(entry.getKey(), cachedModuleScript);
      }
    } 
    out.writeObject(serScripts);
  }
}
