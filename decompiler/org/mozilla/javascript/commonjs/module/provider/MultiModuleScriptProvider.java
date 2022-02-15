package org.mozilla.javascript.commonjs.module.provider;

import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.commonjs.module.ModuleScript;
import org.mozilla.javascript.commonjs.module.ModuleScriptProvider;















public class MultiModuleScriptProvider
  implements ModuleScriptProvider
{
  private final ModuleScriptProvider[] providers;
  
  public MultiModuleScriptProvider(Iterable<? extends ModuleScriptProvider> providers) {
    List<ModuleScriptProvider> l = new LinkedList<ModuleScriptProvider>();
    for (ModuleScriptProvider provider : providers) {
      l.add(provider);
    }
    this.providers = l.<ModuleScriptProvider>toArray(new ModuleScriptProvider[l.size()]);
  }

  
  public ModuleScript getModuleScript(Context cx, String moduleId, URI uri, URI base, Scriptable paths) throws Exception {
    for (ModuleScriptProvider provider : this.providers) {
      ModuleScript script = provider.getModuleScript(cx, moduleId, uri, base, paths);
      
      if (script != null) {
        return script;
      }
    } 
    return null;
  }
}
