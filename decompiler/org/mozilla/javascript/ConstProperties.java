package org.mozilla.javascript;

public interface ConstProperties {
  void putConst(String paramString, Scriptable paramScriptable, Object paramObject);
  
  void defineConst(String paramString, Scriptable paramScriptable);
  
  boolean isConst(String paramString);
}
