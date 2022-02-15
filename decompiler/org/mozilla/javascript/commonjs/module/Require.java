package org.mozilla.javascript.commonjs.module;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.mozilla.javascript.BaseFunction;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;


























public class Require
  extends BaseFunction
{
  private static final long serialVersionUID = 1L;
  private final ModuleScriptProvider moduleScriptProvider;
  private final Scriptable nativeScope;
  private final Scriptable paths;
  private final boolean sandboxed;
  private final Script preExec;
  private final Script postExec;
  private String mainModuleId = null;
  
  private Scriptable mainExports;
  
  private final Map<String, Scriptable> exportedModuleInterfaces = new ConcurrentHashMap<String, Scriptable>();
  
  private final Object loadLock = new Object();


  
  private static final ThreadLocal<Map<String, Scriptable>> loadingModuleInterfaces = new ThreadLocal<Map<String, Scriptable>>();




















  
  public Require(Context cx, Scriptable nativeScope, ModuleScriptProvider moduleScriptProvider, Script preExec, Script postExec, boolean sandboxed) {
    this.moduleScriptProvider = moduleScriptProvider;
    this.nativeScope = nativeScope;
    this.sandboxed = sandboxed;
    this.preExec = preExec;
    this.postExec = postExec;
    setPrototype(ScriptableObject.getFunctionPrototype(nativeScope));
    if (!sandboxed) {
      this.paths = cx.newArray(nativeScope, 0);
      defineReadOnlyProperty((ScriptableObject)this, "paths", this.paths);
    } else {
      
      this.paths = null;
    } 
  }
















  
  public Scriptable requireMain(Context cx, String mainModuleId) {
    ModuleScript moduleScript;
    if (this.mainModuleId != null) {
      if (!this.mainModuleId.equals(mainModuleId)) {
        throw new IllegalStateException("Main module already set to " + this.mainModuleId);
      }
      
      return this.mainExports;
    } 


    
    try {
      moduleScript = this.moduleScriptProvider.getModuleScript(cx, mainModuleId, null, null, this.paths);
    }
    catch (RuntimeException x) {
      throw x;
    } catch (Exception x) {
      throw new RuntimeException(x);
    } 
    
    if (moduleScript != null) {
      this.mainExports = getExportedModuleInterface(cx, mainModuleId, (URI)null, (URI)null, true);
    }
    else if (!this.sandboxed) {
      
      URI mainUri = null;

      
      try {
        mainUri = new URI(mainModuleId);
      } catch (URISyntaxException usx) {}



      
      if (mainUri == null || !mainUri.isAbsolute()) {
        File file = new File(mainModuleId);
        if (!file.isFile()) {
          throw ScriptRuntime.throwError(cx, this.nativeScope, "Module \"" + mainModuleId + "\" not found.");
        }
        
        mainUri = file.toURI();
      } 
      this.mainExports = getExportedModuleInterface(cx, mainUri.toString(), mainUri, (URI)null, true);
    } 

    
    this.mainModuleId = mainModuleId;
    return this.mainExports;
  }





  
  public void install(Scriptable scope) {
    ScriptableObject.putProperty(scope, "require", this);
  }



  
  public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
    if (args == null || args.length < 1) {
      throw ScriptRuntime.throwError(cx, scope, "require() needs one argument");
    }

    
    String id = (String)Context.jsToJava(args[0], String.class);
    URI uri = null;
    URI base = null;
    if (id.startsWith("./") || id.startsWith("../")) {
      if (!(thisObj instanceof ModuleScope)) {
        throw ScriptRuntime.throwError(cx, scope, "Can't resolve relative module ID \"" + id + "\" when require() is used outside of a module");
      }


      
      ModuleScope moduleScope = (ModuleScope)thisObj;
      base = moduleScope.getBase();
      URI current = moduleScope.getUri();
      uri = current.resolve(id);
      
      if (base == null) {

        
        id = uri.toString();
      } else {
        
        id = base.relativize(current).resolve(id).toString();
        if (id.charAt(0) == '.') {

          
          if (this.sandboxed) {
            throw ScriptRuntime.throwError(cx, scope, "Module \"" + id + "\" is not contained in sandbox.");
          }
          
          id = uri.toString();
        } 
      } 
    } 
    
    return getExportedModuleInterface(cx, id, uri, base, false);
  }

  
  public Scriptable construct(Context cx, Scriptable scope, Object[] args) {
    throw ScriptRuntime.throwError(cx, scope, "require() can not be invoked as a constructor");
  }




  
  private Scriptable getExportedModuleInterface(Context cx, String id, URI uri, URI base, boolean isMain) {
    Scriptable exports = this.exportedModuleInterfaces.get(id);
    if (exports != null) {
      if (isMain) {
        throw new IllegalStateException("Attempt to set main module after it was loaded");
      }
      
      return exports;
    } 

    
    Map<String, Scriptable> threadLoadingModules = loadingModuleInterfaces.get();
    
    if (threadLoadingModules != null) {
      exports = threadLoadingModules.get(id);
      if (exports != null) {
        return exports;
      }
    } 







    
    synchronized (this.loadLock) {

      
      exports = this.exportedModuleInterfaces.get(id);
      if (exports != null) {
        return exports;
      }
      
      ModuleScript moduleScript = getModule(cx, id, uri, base);
      if (this.sandboxed && !moduleScript.isSandboxed()) {
        throw ScriptRuntime.throwError(cx, this.nativeScope, "Module \"" + id + "\" is not contained in sandbox.");
      }
      
      exports = cx.newObject(this.nativeScope);
      
      boolean outermostLocked = (threadLoadingModules == null);
      if (outermostLocked) {
        threadLoadingModules = new HashMap<String, Scriptable>();
        loadingModuleInterfaces.set(threadLoadingModules);
      } 







      
      threadLoadingModules.put(id, exports);

      
      try {
        Scriptable newExports = executeModuleScript(cx, id, exports, moduleScript, isMain);
        
        if (exports != newExports) {
          threadLoadingModules.put(id, newExports);
          exports = newExports;
        }
      
      } catch (RuntimeException e) {
        
        threadLoadingModules.remove(id);
        throw e;
      } finally {
        
        if (outermostLocked) {





          
          this.exportedModuleInterfaces.putAll(threadLoadingModules);
          loadingModuleInterfaces.set(null);
        } 
      } 
    } 
    return exports;
  }


  
  private Scriptable executeModuleScript(Context cx, String id, Scriptable exports, ModuleScript moduleScript, boolean isMain) {
    ScriptableObject moduleObject = (ScriptableObject)cx.newObject(this.nativeScope);
    
    URI uri = moduleScript.getUri();
    URI base = moduleScript.getBase();
    defineReadOnlyProperty(moduleObject, "id", id);
    if (!this.sandboxed) {
      defineReadOnlyProperty(moduleObject, "uri", uri.toString());
    }
    ModuleScope moduleScope = new ModuleScope(this.nativeScope, uri, base);



    
    moduleScope.put("exports", (Scriptable)moduleScope, exports);
    moduleScope.put("module", (Scriptable)moduleScope, moduleObject);
    moduleObject.put("exports", (Scriptable)moduleObject, exports);
    install((Scriptable)moduleScope);
    if (isMain) {
      defineReadOnlyProperty((ScriptableObject)this, "main", moduleObject);
    }
    executeOptionalScript(this.preExec, cx, (Scriptable)moduleScope);
    moduleScript.getScript().exec(cx, (Scriptable)moduleScope);
    executeOptionalScript(this.postExec, cx, (Scriptable)moduleScope);
    return ScriptRuntime.toObject(cx, this.nativeScope, ScriptableObject.getProperty((Scriptable)moduleObject, "exports"));
  }



  
  private static void executeOptionalScript(Script script, Context cx, Scriptable executionScope) {
    if (script != null) {
      script.exec(cx, executionScope);
    }
  }

  
  private static void defineReadOnlyProperty(ScriptableObject obj, String name, Object value) {
    ScriptableObject.putProperty((Scriptable)obj, name, value);
    obj.setAttributes(name, 5);
  }

  
  private ModuleScript getModule(Context cx, String id, URI uri, URI base) {
    try {
      ModuleScript moduleScript = this.moduleScriptProvider.getModuleScript(cx, id, uri, base, this.paths);
      
      if (moduleScript == null) {
        throw ScriptRuntime.throwError(cx, this.nativeScope, "Module \"" + id + "\" not found.");
      }
      
      return moduleScript;
    }
    catch (RuntimeException e) {
      throw e;
    }
    catch (Exception e) {
      throw Context.throwAsScriptRuntimeEx(e);
    } 
  }

  
  public String getFunctionName() {
    return "require";
  }

  
  public int getArity() {
    return 1;
  }

  
  public int getLength() {
    return 1;
  }
}
