package org.fife.rsta.ac.js.ecma.api.ecma3.functions;

import org.fife.rsta.ac.js.ecma.api.ecma3.JSBoolean;
import org.fife.rsta.ac.js.ecma.api.ecma3.JSNumber;
import org.fife.rsta.ac.js.ecma.api.ecma3.JSObject;
import org.fife.rsta.ac.js.ecma.api.ecma3.JSString;

public interface JSGlobalFunctions extends JSObjectFunctions {
  JSString decodeURI(JSString paramJSString);
  
  JSString decodeURIComponent(JSString paramJSString);
  
  JSString encodeURI(JSString paramJSString);
  
  JSString encodeURIComponent(JSString paramJSString);
  
  JSString escape(JSString paramJSString);
  
  JSObject eval(JSString paramJSString);
  
  JSBoolean isFinite(JSNumber paramJSNumber);
  
  JSBoolean isNaN(JSNumber paramJSNumber);
  
  JSString parseFloat(JSString paramJSString);
  
  JSString parseInt(JSString paramJSString, JSNumber paramJSNumber);
  
  JSString unescape(JSString paramJSString);
}
