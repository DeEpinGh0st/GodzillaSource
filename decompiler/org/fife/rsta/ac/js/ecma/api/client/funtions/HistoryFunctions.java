package org.fife.rsta.ac.js.ecma.api.client.funtions;

import org.fife.rsta.ac.js.ecma.api.ecma3.JSNumber;
import org.fife.rsta.ac.js.ecma.api.ecma3.JSString;
import org.fife.rsta.ac.js.ecma.api.ecma5.functions.JS5ObjectFunctions;

public interface HistoryFunctions extends JS5ObjectFunctions {
  void back();
  
  void forward();
  
  void go(JSNumber paramJSNumber);
  
  void go(JSString paramJSString);
}
