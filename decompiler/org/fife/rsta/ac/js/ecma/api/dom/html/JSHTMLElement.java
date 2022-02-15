package org.fife.rsta.ac.js.ecma.api.dom.html;

import org.fife.rsta.ac.js.ecma.api.ecma3.JSFunction;
import org.fife.rsta.ac.js.ecma.api.ecma5.functions.JS5ObjectFunctions;
import org.w3c.dom.html.HTMLElement;

public abstract class JSHTMLElement implements HTMLElement, JS5ObjectFunctions {
  public JSHTMLElement protype;
  
  protected JSFunction constructor;
}
