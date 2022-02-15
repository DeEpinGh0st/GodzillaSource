package org.fife.rsta.ac.js.ecma.api.e4x;

import org.fife.rsta.ac.js.ecma.api.e4x.functions.E4XXMLListFunctions;
import org.fife.rsta.ac.js.ecma.api.ecma3.JSFunction;
import org.fife.rsta.ac.js.ecma.api.ecma3.JSObject;

public abstract class E4XXMLList implements E4XXMLListFunctions {
  public E4XXMLList protype;
  
  protected JSFunction constructor;
  
  public E4XXMLList(JSObject xml) {}
}
