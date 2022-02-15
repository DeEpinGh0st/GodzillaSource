package org.fife.rsta.ac.js.ecma.api.ecma3.functions;

import org.fife.rsta.ac.js.ecma.api.ecma3.JSArray;
import org.fife.rsta.ac.js.ecma.api.ecma3.JSNumber;
import org.fife.rsta.ac.js.ecma.api.ecma3.JSRegExp;
import org.fife.rsta.ac.js.ecma.api.ecma3.JSString;

public interface JSStringFunctions extends JSObjectFunctions {
  JSString charAt(JSNumber paramJSNumber);
  
  JSNumber charCodeAt(JSNumber paramJSNumber);
  
  JSString concat(JSString paramJSString);
  
  JSNumber indexOf(JSString paramJSString, JSNumber paramJSNumber);
  
  JSNumber lastIndexOf(JSString paramJSString, JSNumber paramJSNumber);
  
  JSNumber localeCompare(JSString paramJSString);
  
  JSString match(JSRegExp paramJSRegExp);
  
  JSString replace(JSRegExp paramJSRegExp, JSString paramJSString);
  
  JSNumber search(JSRegExp paramJSRegExp);
  
  JSString slice(JSNumber paramJSNumber1, JSNumber paramJSNumber2);
  
  JSArray split(JSString paramJSString, JSNumber paramJSNumber);
  
  JSString substring(JSNumber paramJSNumber1, JSNumber paramJSNumber2);
  
  JSString toLowerCase();
  
  JSString toLocaleLowerCase();
  
  JSString toUpperCase();
  
  JSString toLocaleUpperCase();
}
