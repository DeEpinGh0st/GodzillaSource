package org.fife.rsta.ac.js.ecma.api.e4x.functions;

import org.fife.rsta.ac.js.ecma.api.e4x.E4XXML;
import org.fife.rsta.ac.js.ecma.api.e4x.E4XXMLList;
import org.fife.rsta.ac.js.ecma.api.ecma3.JSBoolean;
import org.fife.rsta.ac.js.ecma.api.ecma3.JSNumber;
import org.fife.rsta.ac.js.ecma.api.ecma3.JSString;
import org.fife.rsta.ac.js.ecma.api.ecma3.functions.JSObjectFunctions;

public interface E4XXMLListFunctions extends JSObjectFunctions {
  E4XXMLList attribute(JSString paramJSString);
  
  E4XXMLList attributes();
  
  E4XXMLList child(JSString paramJSString);
  
  E4XXMLList children();
  
  E4XXMLList comments();
  
  JSBoolean contains(E4XXML paramE4XXML);
  
  JSBoolean copy();
  
  E4XXMLList descendants(JSString paramJSString);
  
  E4XXMLList elements(JSString paramJSString);
  
  JSBoolean hasComplexContent();
  
  JSBoolean hasSimpleContent();
  
  JSNumber length();
  
  E4XXMLList normalize();
  
  E4XXML parent();
  
  E4XXMLList processingInstructions(JSString paramJSString);
  
  E4XXMLList text();
  
  JSString toXMLString();
}
