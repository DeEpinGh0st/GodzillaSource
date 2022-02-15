package org.fife.rsta.ac.js.ecma.api.dom;

import org.fife.rsta.ac.js.ecma.api.ecma3.JSFunction;
import org.fife.rsta.ac.js.ecma.api.ecma5.functions.JS5ObjectFunctions;
import org.w3c.dom.DOMConfiguration;

public abstract class JSDOMConfiguration implements DOMConfiguration, JS5ObjectFunctions {
  public JSDOMConfiguration protype;
  
  protected JSFunction constructor;
}
