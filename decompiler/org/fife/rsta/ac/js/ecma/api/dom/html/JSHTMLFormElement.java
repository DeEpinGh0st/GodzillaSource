package org.fife.rsta.ac.js.ecma.api.dom.html;

import org.fife.rsta.ac.js.ecma.api.ecma3.JSFunction;
import org.fife.rsta.ac.js.ecma.api.ecma5.functions.JS5ObjectFunctions;
import org.w3c.dom.html.HTMLFormElement;

public abstract class JSHTMLFormElement implements HTMLFormElement, JS5ObjectFunctions {
  public JSHTMLFormElement protype;
  
  protected JSFunction constructor;
}
