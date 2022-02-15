package org.fife.rsta.ac.js.ecma.api.ecma3.functions;

import org.fife.rsta.ac.js.ecma.api.ecma3.JSArray;
import org.fife.rsta.ac.js.ecma.api.ecma3.JSFunction;
import org.fife.rsta.ac.js.ecma.api.ecma3.JSNumber;
import org.fife.rsta.ac.js.ecma.api.ecma3.JSObject;
import org.fife.rsta.ac.js.ecma.api.ecma3.JSString;

public interface JSArrayFunctions extends JSObjectFunctions {
  JSArray concat(JSArray paramJSArray);
  
  JSString join(String paramString);
  
  JSObject pop();
  
  void push(JSArray paramJSArray);
  
  JSArray reverse();
  
  JSObject shift();
  
  JSArray slice(Number paramNumber1, Number paramNumber2);
  
  JSArray sort(JSFunction paramJSFunction);
  
  JSArray splice(JSNumber paramJSNumber1, JSNumber paramJSNumber2, JSArray paramJSArray);
  
  JSNumber unshift(JSArray paramJSArray);
}
