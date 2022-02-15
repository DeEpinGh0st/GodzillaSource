package org.fife.rsta.ac.js.ecma.api.ecma5.functions;

import org.fife.rsta.ac.js.ecma.api.ecma3.JSBoolean;
import org.fife.rsta.ac.js.ecma.api.ecma3.JSNumber;
import org.fife.rsta.ac.js.ecma.api.ecma3.functions.JSArrayFunctions;
import org.fife.rsta.ac.js.ecma.api.ecma5.JS5Array;
import org.fife.rsta.ac.js.ecma.api.ecma5.JS5Function;
import org.fife.rsta.ac.js.ecma.api.ecma5.JS5Object;

public interface JS5ArrayFunctions extends JS5ObjectFunctions, JSArrayFunctions {
  JSBoolean every(JS5Function paramJS5Function, JS5Object paramJS5Object);
  
  JS5Array filter(JS5Function paramJS5Function, JS5Object paramJS5Object);
  
  void forEach(JS5Function paramJS5Function, JS5Object paramJS5Object);
  
  JSNumber indexOf(JS5Object paramJS5Object, JSNumber paramJSNumber);
  
  JSNumber lastIndexOf(JS5Object paramJS5Object, JSNumber paramJSNumber);
  
  JS5Array map(JS5Function paramJS5Function, JS5Object paramJS5Object);
  
  JS5Object reduce(JS5Function paramJS5Function, JS5Object paramJS5Object);
  
  JS5Object reduceRight(JS5Function paramJS5Function, JS5Object paramJS5Object);
  
  JSBoolean some(JS5Function paramJS5Function, JS5Object paramJS5Object);
}
