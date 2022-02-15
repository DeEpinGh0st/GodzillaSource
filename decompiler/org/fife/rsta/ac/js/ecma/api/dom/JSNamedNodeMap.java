package org.fife.rsta.ac.js.ecma.api.dom;

import org.fife.rsta.ac.js.ecma.api.ecma3.JSFunction;
import org.fife.rsta.ac.js.ecma.api.ecma5.functions.JS5ObjectFunctions;
import org.w3c.dom.NamedNodeMap;

public abstract class JSNamedNodeMap implements NamedNodeMap, JS5ObjectFunctions {
  public JSNamedNodeMap protype;
  
  protected JSFunction constructor;
}
