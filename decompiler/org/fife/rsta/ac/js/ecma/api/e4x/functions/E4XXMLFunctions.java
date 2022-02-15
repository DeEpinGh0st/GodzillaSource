package org.fife.rsta.ac.js.ecma.api.e4x.functions;

import org.fife.rsta.ac.js.ecma.api.e4x.E4XNamespace;
import org.fife.rsta.ac.js.ecma.api.e4x.E4XQName;
import org.fife.rsta.ac.js.ecma.api.e4x.E4XXML;
import org.fife.rsta.ac.js.ecma.api.e4x.E4XXMLList;
import org.fife.rsta.ac.js.ecma.api.ecma3.JSArray;
import org.fife.rsta.ac.js.ecma.api.ecma3.JSBoolean;
import org.fife.rsta.ac.js.ecma.api.ecma3.JSNumber;
import org.fife.rsta.ac.js.ecma.api.ecma3.JSObject;
import org.fife.rsta.ac.js.ecma.api.ecma3.JSString;
import org.fife.rsta.ac.js.ecma.api.ecma3.functions.JSObjectFunctions;

public interface E4XXMLFunctions extends JSObjectFunctions {
  void addNamespace(E4XNamespace paramE4XNamespace);
  
  E4XXML appendChild(E4XXML paramE4XXML);
  
  E4XXMLList attribute(JSString paramJSString);
  
  E4XXMLList attributes();
  
  E4XXMLList child(JSString paramJSString);
  
  E4XXMLList child(JSNumber paramJSNumber);
  
  JSNumber childIndex();
  
  E4XXMLList children();
  
  E4XXMLList comments();
  
  JSBoolean contains(E4XXML paramE4XXML);
  
  JSBoolean contains(E4XXMLList paramE4XXMLList);
  
  JSBoolean copy();
  
  E4XXMLList descendants(JSString paramJSString);
  
  E4XXMLList elements(JSString paramJSString);
  
  JSBoolean hasComplexContent();
  
  JSBoolean hasSimpleContent();
  
  JSArray inScopeNamespaces();
  
  E4XXML insertChildAfter(E4XXML paramE4XXML1, E4XXML paramE4XXML2);
  
  E4XXML insertChildBefore(E4XXML paramE4XXML1, E4XXML paramE4XXML2);
  
  JSNumber length();
  
  JSNumber localName();
  
  E4XQName name();
  
  E4XNamespace namespace(JSString paramJSString);
  
  JSArray namespaceDeclarations();
  
  JSString nodeKind();
  
  E4XXML normalize();
  
  E4XXML parent();
  
  E4XXMLList processingInstructions(JSString paramJSString);
  
  E4XXML prependChild(E4XXML paramE4XXML);
  
  E4XXML removeNamespace(E4XNamespace paramE4XNamespace);
  
  E4XXML replace(JSString paramJSString, JSObject paramJSObject);
  
  E4XXML replace(JSNumber paramJSNumber, JSObject paramJSObject);
  
  E4XXML setChildren(E4XXML paramE4XXML);
  
  E4XXML setChildren(E4XXMLList paramE4XXMLList);
  
  void setLocalName(JSString paramJSString);
  
  void setName(E4XQName paramE4XQName);
  
  void setNamespace(E4XNamespace paramE4XNamespace);
  
  E4XXMLList text();
  
  JSString toXMLString();
}
