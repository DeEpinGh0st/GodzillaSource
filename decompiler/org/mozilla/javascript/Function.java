package org.mozilla.javascript;

public interface Function extends Scriptable, Callable {
  Object call(Context paramContext, Scriptable paramScriptable1, Scriptable paramScriptable2, Object[] paramArrayOfObject);
  
  Scriptable construct(Context paramContext, Scriptable paramScriptable, Object[] paramArrayOfObject);
}
