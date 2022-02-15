package org.fife.rsta.ac.js.ecma.api.dom.html;

import org.fife.rsta.ac.js.ecma.api.ecma3.JSFunction;
import org.fife.rsta.ac.js.ecma.api.ecma5.functions.JS5ObjectFunctions;
import org.w3c.dom.html.HTMLFrameElement;

public abstract class JSHTMLFrameElement implements HTMLFrameElement, JS5ObjectFunctions {
  public JSHTMLFrameElement protype;
  
  protected JSFunction constructor;
}
