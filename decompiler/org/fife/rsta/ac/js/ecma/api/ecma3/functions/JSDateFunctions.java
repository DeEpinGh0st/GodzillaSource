package org.fife.rsta.ac.js.ecma.api.ecma3.functions;

import org.fife.rsta.ac.js.ecma.api.ecma3.JSNumber;
import org.fife.rsta.ac.js.ecma.api.ecma3.JSObject;
import org.fife.rsta.ac.js.ecma.api.ecma3.JSString;

public interface JSDateFunctions extends JSObjectFunctions {
  JSString toDateString();
  
  JSString toTimeString();
  
  JSString toLocaleString();
  
  JSString toLocaleDateString();
  
  JSString toLocaleTimeString();
  
  JSObject valueOf();
  
  JSNumber getFullYear();
  
  JSNumber getTime();
  
  JSNumber getUTCFullYear();
  
  JSNumber getMonth();
  
  JSNumber getUTCMonth();
  
  JSNumber getDate();
  
  JSNumber getUTCDate();
  
  JSNumber getDay();
  
  JSNumber getUTCDay();
  
  JSNumber getHours();
  
  JSNumber getUTCHours();
  
  JSNumber getMinutes();
  
  JSNumber getUTCMinutes();
  
  JSNumber getSeconds();
  
  JSNumber getUTCSeconds();
  
  JSNumber getMilliseconds();
  
  JSNumber getUTCMilliseconds();
  
  JSNumber getTimezoneOffset();
  
  JSNumber setTime(JSNumber paramJSNumber);
  
  JSNumber setMilliseconds(JSNumber paramJSNumber);
  
  JSNumber setUTCMilliseconds(JSNumber paramJSNumber);
  
  JSNumber setSeconds(JSNumber paramJSNumber1, JSNumber paramJSNumber2);
  
  JSNumber setUTCSeconds(JSNumber paramJSNumber1, JSNumber paramJSNumber2);
  
  JSNumber setMinutes(JSNumber paramJSNumber1, JSNumber paramJSNumber2, JSNumber paramJSNumber3);
  
  JSNumber setUTCMinute(JSNumber paramJSNumber1, JSNumber paramJSNumber2, JSNumber paramJSNumber3);
  
  JSNumber setHours(JSNumber paramJSNumber1, JSNumber paramJSNumber2, JSNumber paramJSNumber3, JSNumber paramJSNumber4);
  
  JSNumber setUTCHours(JSNumber paramJSNumber1, JSNumber paramJSNumber2, JSNumber paramJSNumber3, JSNumber paramJSNumber4);
  
  JSNumber setDate(JSNumber paramJSNumber);
  
  JSNumber setUTCDate(JSNumber paramJSNumber);
  
  JSNumber setMonth(JSNumber paramJSNumber1, JSNumber paramJSNumber2);
  
  JSNumber setUTCMonth(JSNumber paramJSNumber1, JSNumber paramJSNumber2);
  
  JSNumber setFullYear(JSNumber paramJSNumber1, JSNumber paramJSNumber2, JSNumber paramJSNumber3);
  
  JSNumber setUTCFullYear(JSNumber paramJSNumber1, JSNumber paramJSNumber2, JSNumber paramJSNumber3);
  
  JSString toUTCString();
}
