package org.fife.rsta.ac.js.ecma.api.dom;

import org.fife.rsta.ac.js.ecma.api.ecma3.JSFunction;
import org.fife.rsta.ac.js.ecma.api.ecma5.functions.JS5ObjectFunctions;
import org.w3c.dom.DOMImplementationList;

public abstract class JSDOMImplementationSource implements DOMImplementationList, JS5ObjectFunctions {
  public DOMImplementationList protype;
  
  protected JSFunction constructor;
}
