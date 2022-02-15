package org.fife.rsta.ac.js.ecma.api.dom;

import org.fife.rsta.ac.js.ecma.api.ecma3.JSFunction;
import org.fife.rsta.ac.js.ecma.api.ecma5.functions.JS5ObjectFunctions;
import org.w3c.dom.NodeList;

public abstract class JSNodeList implements NodeList, JS5ObjectFunctions {
  public JSNodeList protype;
  
  protected JSFunction constructor;
}
