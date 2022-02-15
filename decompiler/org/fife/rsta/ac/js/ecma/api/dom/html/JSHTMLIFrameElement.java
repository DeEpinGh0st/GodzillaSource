package org.fife.rsta.ac.js.ecma.api.dom.html;

import org.fife.rsta.ac.js.ecma.api.ecma3.JSFunction;
import org.fife.rsta.ac.js.ecma.api.ecma5.functions.JS5ObjectFunctions;
import org.w3c.dom.html.HTMLIFrameElement;

public abstract class JSHTMLIFrameElement implements HTMLIFrameElement, JS5ObjectFunctions {
  public JSHTMLIFrameElement protype;
  
  protected JSFunction constructor;
}
