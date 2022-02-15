package org.mozilla.javascript;

public interface RefCallable extends Callable {
  Ref refCall(Context paramContext, Scriptable paramScriptable, Object[] paramArrayOfObject);
}
