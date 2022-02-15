package org.fife.rsta.ac.js.ecma.api.ecma3;

import org.fife.rsta.ac.js.ecma.api.ecma3.functions.JSObjectFunctions;

public abstract class JSError implements JSObjectFunctions {
  public JSError prototype;
  
  protected JSFunction constructor;
  
  protected JSString name;
  
  protected JSString message;
  
  public JSError() {}
  
  public JSError(JSString message) {}
}
