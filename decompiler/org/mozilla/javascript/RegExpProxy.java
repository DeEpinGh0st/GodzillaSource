package org.mozilla.javascript;

public interface RegExpProxy {
  public static final int RA_MATCH = 1;
  
  public static final int RA_REPLACE = 2;
  
  public static final int RA_SEARCH = 3;
  
  boolean isRegExp(Scriptable paramScriptable);
  
  Object compileRegExp(Context paramContext, String paramString1, String paramString2);
  
  Scriptable wrapRegExp(Context paramContext, Scriptable paramScriptable, Object paramObject);
  
  Object action(Context paramContext, Scriptable paramScriptable1, Scriptable paramScriptable2, Object[] paramArrayOfObject, int paramInt);
  
  int find_split(Context paramContext, Scriptable paramScriptable1, String paramString1, String paramString2, Scriptable paramScriptable2, int[] paramArrayOfint1, int[] paramArrayOfint2, boolean[] paramArrayOfboolean, String[][] paramArrayOfString);
  
  Object js_split(Context paramContext, Scriptable paramScriptable, String paramString, Object[] paramArrayOfObject);
}
