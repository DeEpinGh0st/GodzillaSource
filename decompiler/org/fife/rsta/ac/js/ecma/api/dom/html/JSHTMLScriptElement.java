package org.fife.rsta.ac.js.ecma.api.dom.html;

import org.fife.rsta.ac.js.ecma.api.ecma3.JSFunction;
import org.fife.rsta.ac.js.ecma.api.ecma5.functions.JS5ObjectFunctions;
import org.w3c.dom.html.HTMLScriptElement;

public abstract class JSHTMLScriptElement implements HTMLScriptElement, JS5ObjectFunctions {
  public JSHTMLScriptElement protype;
  
  protected JSFunction constructor;
}
