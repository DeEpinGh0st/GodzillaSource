package org.fife.rsta.ac.js.ecma.api.dom;

import org.fife.rsta.ac.js.ecma.api.ecma3.JSFunction;
import org.fife.rsta.ac.js.ecma.api.ecma5.functions.JS5ObjectFunctions;
import org.w3c.dom.Notation;

public abstract class JSNotation implements Notation, JS5ObjectFunctions {
  public JSNotation protype;
  
  protected JSFunction constructor;
}
