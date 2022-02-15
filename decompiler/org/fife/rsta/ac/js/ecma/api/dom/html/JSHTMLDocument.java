package org.fife.rsta.ac.js.ecma.api.dom.html;

import org.fife.rsta.ac.js.ecma.api.ecma3.JSFunction;
import org.fife.rsta.ac.js.ecma.api.ecma5.functions.JS5ObjectFunctions;
import org.w3c.dom.html.HTMLDocument;

public abstract class JSHTMLDocument implements HTMLDocument, JS5ObjectFunctions {
  public JSHTMLDocument protype;
  
  protected JSFunction constructor;
}
