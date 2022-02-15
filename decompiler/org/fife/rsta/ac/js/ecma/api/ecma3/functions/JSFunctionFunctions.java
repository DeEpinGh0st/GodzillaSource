package org.fife.rsta.ac.js.ecma.api.ecma3.functions;

import org.fife.rsta.ac.js.ecma.api.ecma3.JSArray;
import org.fife.rsta.ac.js.ecma.api.ecma3.JSObject;

public interface JSFunctionFunctions extends JSObjectFunctions {
  JSObject apply(JSObject paramJSObject, JSArray paramJSArray);
  
  JSObject call(JSObject paramJSObject1, JSObject paramJSObject2);
}
