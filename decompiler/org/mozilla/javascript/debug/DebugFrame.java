package org.mozilla.javascript.debug;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

public interface DebugFrame {
  void onEnter(Context paramContext, Scriptable paramScriptable1, Scriptable paramScriptable2, Object[] paramArrayOfObject);
  
  void onLineChange(Context paramContext, int paramInt);
  
  void onExceptionThrown(Context paramContext, Throwable paramThrowable);
  
  void onExit(Context paramContext, boolean paramBoolean, Object paramObject);
  
  void onDebuggerStatement(Context paramContext);
}
