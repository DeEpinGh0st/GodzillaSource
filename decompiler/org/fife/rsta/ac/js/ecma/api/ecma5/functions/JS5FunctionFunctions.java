package org.fife.rsta.ac.js.ecma.api.ecma5.functions;

import org.fife.rsta.ac.js.ecma.api.ecma3.functions.JSFunctionFunctions;
import org.fife.rsta.ac.js.ecma.api.ecma5.JS5Array;
import org.fife.rsta.ac.js.ecma.api.ecma5.JS5Function;
import org.fife.rsta.ac.js.ecma.api.ecma5.JS5Object;

public interface JS5FunctionFunctions extends JS5ObjectFunctions, JSFunctionFunctions {
  JS5Function bind(JS5Object paramJS5Object, JS5Array paramJS5Array);
}
