package org.mozilla.javascript.commonjs.module;

import java.net.URI;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

public interface ModuleScriptProvider {
  ModuleScript getModuleScript(Context paramContext, String paramString, URI paramURI1, URI paramURI2, Scriptable paramScriptable) throws Exception;
}
