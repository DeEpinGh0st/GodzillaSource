package org.fife.rsta.ac.js.ecma.api.ecma3.functions;

import org.fife.rsta.ac.js.ecma.api.ecma3.JSArray;
import org.fife.rsta.ac.js.ecma.api.ecma3.JSBoolean;

public interface JSRegExpFunctions extends JSObjectFunctions {
  JSArray exec(String paramString);
  
  JSBoolean test(String paramString);
}
