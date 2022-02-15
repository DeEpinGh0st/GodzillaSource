package org.fife.rsta.ac.js.ecma.api.dom.html;

import org.fife.rsta.ac.js.ecma.api.ecma3.JSFunction;
import org.fife.rsta.ac.js.ecma.api.ecma5.functions.JS5ObjectFunctions;
import org.w3c.dom.html.HTMLCollection;

public abstract class JSHTMLCollection implements HTMLCollection, JS5ObjectFunctions {
  public JSHTMLCollection protype;
  
  protected JSFunction constructor;
}
