package org.fife.rsta.ac.js.ecma.api.client.funtions;

import org.fife.rsta.ac.js.ecma.api.client.Window;
import org.fife.rsta.ac.js.ecma.api.ecma3.JSBoolean;
import org.fife.rsta.ac.js.ecma.api.ecma3.JSNumber;
import org.fife.rsta.ac.js.ecma.api.ecma3.JSObject;
import org.fife.rsta.ac.js.ecma.api.ecma3.JSString;
import org.fife.rsta.ac.js.ecma.api.ecma5.JS5Object;
import org.fife.rsta.ac.js.ecma.api.ecma5.functions.JS5ObjectFunctions;
import org.w3c.dom.Element;

public interface WindowFunctions extends JS5ObjectFunctions {
  void alert(JSString paramJSString);
  
  void blur();
  
  void clearInterval(JS5Object paramJS5Object);
  
  void clearTimeout(JS5Object paramJS5Object);
  
  void close();
  
  JSBoolean confirm(JSString paramJSString);
  
  void focus();
  
  JS5Object getComputedStyle(Element paramElement, JSString paramJSString);
  
  void moveTo(JSNumber paramJSNumber1, JSNumber paramJSNumber2);
  
  void moveBy(JSNumber paramJSNumber1, JSNumber paramJSNumber2);
  
  Window open(JSString paramJSString1, JSString paramJSString2, JSString paramJSString3, JSBoolean paramJSBoolean);
  
  void print();
  
  JSString prompt();
  
  void resizeTo(JSNumber paramJSNumber1, JSNumber paramJSNumber2);
  
  void resizeBy(JSNumber paramJSNumber1, JSNumber paramJSNumber2);
  
  void scrollTo(JSNumber paramJSNumber1, JSNumber paramJSNumber2);
  
  void scrollBy(JSNumber paramJSNumber1, JSNumber paramJSNumber2);
  
  JSNumber setInterval(JSObject paramJSObject, JSNumber paramJSNumber);
  
  JSNumber setTimeout(JSObject paramJSObject, JSNumber paramJSNumber);
  
  JSString atob(JSString paramJSString);
  
  JSString btoa(JSString paramJSString);
  
  void setResizable(JSBoolean paramJSBoolean);
  
  void captureEvents(JSObject paramJSObject);
  
  void releaseEvents(JSObject paramJSObject);
  
  void routeEvent(JSObject paramJSObject);
  
  void enableExternalCapture();
  
  void disableExternalCapture();
  
  void find();
  
  void back();
  
  void forward();
  
  void home();
  
  void stop();
  
  void scroll(JSNumber paramJSNumber1, JSNumber paramJSNumber2);
}
