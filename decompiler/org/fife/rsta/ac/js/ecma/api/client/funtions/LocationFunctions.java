package org.fife.rsta.ac.js.ecma.api.client.funtions;

import org.fife.rsta.ac.js.ecma.api.ecma3.JSBoolean;
import org.fife.rsta.ac.js.ecma.api.ecma3.JSString;
import org.fife.rsta.ac.js.ecma.api.ecma5.functions.JS5ObjectFunctions;

public interface LocationFunctions extends JS5ObjectFunctions {
  void assign(JSString paramJSString);
  
  void reload(JSBoolean paramJSBoolean);
  
  void replace(JSString paramJSString);
}
