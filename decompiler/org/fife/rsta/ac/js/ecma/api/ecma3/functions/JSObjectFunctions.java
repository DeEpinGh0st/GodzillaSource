package org.fife.rsta.ac.js.ecma.api.ecma3.functions;

import org.fife.rsta.ac.js.ecma.api.ecma3.JSBoolean;
import org.fife.rsta.ac.js.ecma.api.ecma3.JSObject;
import org.fife.rsta.ac.js.ecma.api.ecma3.JSString;

public interface JSObjectFunctions {
  String toString();
  
  JSString toLocaleString();
  
  JSObject valueOf();
  
  JSBoolean hasOwnProperty(String paramString);
  
  JSBoolean isPrototypeOf(JSObject paramJSObject);
  
  JSBoolean propertyIsEnumerable(JSObject paramJSObject);
}
