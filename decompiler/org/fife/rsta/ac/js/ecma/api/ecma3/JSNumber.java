package org.fife.rsta.ac.js.ecma.api.ecma3;

import org.fife.rsta.ac.js.ecma.api.ecma3.functions.JSNumberFunctions;

public abstract class JSNumber implements JSNumberFunctions {
  public JSNumber prototype;
  
  protected JSFunction constructor;
  
  public static JSNumber MIN_VALUE;
  
  public static JSNumber MAX_VALUE;
  
  public static JSNumber NaN;
  
  public static JSNumber NEGATIVE_INFINITY;
  
  public static JSNumber POSITIVE_INFINITY;
  
  public JSNumber(JSObject value) {}
}
