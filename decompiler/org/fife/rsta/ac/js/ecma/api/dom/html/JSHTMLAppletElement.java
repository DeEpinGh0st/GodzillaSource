package org.fife.rsta.ac.js.ecma.api.dom.html;

import org.fife.rsta.ac.js.ecma.api.ecma3.JSFunction;
import org.fife.rsta.ac.js.ecma.api.ecma5.functions.JS5ObjectFunctions;
import org.w3c.dom.html.HTMLAppletElement;

public abstract class JSHTMLAppletElement implements HTMLAppletElement, JS5ObjectFunctions {
  public JSHTMLAppletElement protype;
  
  protected JSFunction constructor;
}
