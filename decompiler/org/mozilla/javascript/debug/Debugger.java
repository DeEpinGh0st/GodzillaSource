package org.mozilla.javascript.debug;

import org.mozilla.javascript.Context;

public interface Debugger {
  void handleCompilationDone(Context paramContext, DebuggableScript paramDebuggableScript, String paramString);
  
  DebugFrame getFrame(Context paramContext, DebuggableScript paramDebuggableScript);
}
