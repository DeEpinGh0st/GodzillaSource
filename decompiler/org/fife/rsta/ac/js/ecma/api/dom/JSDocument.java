package org.fife.rsta.ac.js.ecma.api.dom;

import org.fife.rsta.ac.js.ecma.api.ecma3.JSFunction;
import org.fife.rsta.ac.js.ecma.api.ecma5.functions.JS5ObjectFunctions;
import org.w3c.dom.Document;

public abstract class JSDocument implements Document, JS5ObjectFunctions {
  public JSDocument protype;
  
  protected JSFunction constructor;
}
