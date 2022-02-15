package org.fife.rsta.ac.js.ecma.api.dom;

import org.fife.rsta.ac.js.ecma.api.ecma3.JSFunction;
import org.fife.rsta.ac.js.ecma.api.ecma5.functions.JS5ObjectFunctions;
import org.w3c.dom.UserDataHandler;

public abstract class JSUserDataHandler implements UserDataHandler, JS5ObjectFunctions {
  public JSUserDataHandler protype;
  
  protected JSFunction constructor;
}
