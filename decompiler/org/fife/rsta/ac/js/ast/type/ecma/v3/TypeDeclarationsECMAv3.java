package org.fife.rsta.ac.js.ast.type.ecma.v3;

import org.fife.rsta.ac.js.ast.type.TypeDeclaration;
import org.fife.rsta.ac.js.ast.type.ecma.TypeDeclarations;


public class TypeDeclarationsECMAv3
  extends TypeDeclarations
{
  protected void loadTypes() {
    addTypeDeclaration("JSArray", new TypeDeclaration("org.fife.rsta.ac.js.ecma.api.ecma3", "JSArray", "Array", false, false));
    
    addTypeDeclaration("JSBoolean", new TypeDeclaration("org.fife.rsta.ac.js.ecma.api.ecma3", "JSBoolean", "Boolean", false, false));
    
    addTypeDeclaration("JSDate", new TypeDeclaration("org.fife.rsta.ac.js.ecma.api.ecma3", "JSDate", "Date", false, false));
    
    addTypeDeclaration("JSError", new TypeDeclaration("org.fife.rsta.ac.js.ecma.api.ecma3", "JSError", "Error", false, false));
    
    addTypeDeclaration("JSFunction", new TypeDeclaration("org.fife.rsta.ac.js.ecma.api.ecma3", "JSFunction", "Function", false, false));
    
    addTypeDeclaration("JSMath", new TypeDeclaration("org.fife.rsta.ac.js.ecma.api.ecma3", "JSMath", "Math", false, false));
    
    addTypeDeclaration("JSNumber", new TypeDeclaration("org.fife.rsta.ac.js.ecma.api.ecma3", "JSNumber", "Number", false, false));
    
    addTypeDeclaration("JSObject", new TypeDeclaration("org.fife.rsta.ac.js.ecma.api.ecma3", "JSObject", "Object", false, false));
    
    addTypeDeclaration("JSRegExp", new TypeDeclaration("org.fife.rsta.ac.js.ecma.api.ecma3", "JSRegExp", "RegExp", false, false));
    
    addTypeDeclaration("JSString", new TypeDeclaration("org.fife.rsta.ac.js.ecma.api.ecma3", "JSString", "String", false, false));
    
    addTypeDeclaration("JSGlobal", new TypeDeclaration("org.fife.rsta.ac.js.ecma.api.ecma3", "JSGlobal", "Global", false, false));
    
    addTypeDeclaration("any", new TypeDeclaration("org.fife.rsta.ac.js.ecma.api.ecma3", "JSUndefined", "undefined", false, false));

    
    addTypeDeclaration("org.fife.rsta.ac.js.ecma.api.ecma3.functions.JSObjectFunctions", new TypeDeclaration("org.fife.rsta.ac.js.ecma.api.ecma3.functions", "JSObjectFunctions", "Object", false, false));
    
    addTypeDeclaration("org.fife.rsta.ac.js.ecma.api.ecma3.functions.JSArrayFunctions", new TypeDeclaration("org.fife.rsta.ac.js.ecma.api.ecma3.functions", "JSArrayFunctions", "Array", false, false));
    
    addTypeDeclaration("org.fife.rsta.ac.js.ecma.api.ecma3.functions.JSDateFunctions", new TypeDeclaration("org.fife.rsta.ac.js.ecma.api.ecma3.functions", "JSDateFunctions", "Date", false, false));
    
    addTypeDeclaration("org.fife.rsta.ac.js.ecma.api.ecma3.functions.JSFunctionFunctions", new TypeDeclaration("org.fife.rsta.ac.js.ecma.api.ecma3.functions", "JSFunctionFunctions", "Function", false, false));
    
    addTypeDeclaration("org.fife.rsta.ac.js.ecma.api.ecma3.functions.JSNumberFunctions", new TypeDeclaration("org.fife.rsta.ac.js.ecma.api.ecma3.functions", "JSNumberFunctions", "Number", false, false));
    
    addTypeDeclaration("org.fife.rsta.ac.js.ecma.api.ecma3.functions.JSRegExpFunctions", new TypeDeclaration("org.fife.rsta.ac.js.ecma.api.ecma3.functions", "JSRegExpFunctions", "RegExp", false, false));
    
    addTypeDeclaration("org.fife.rsta.ac.js.ecma.api.ecma3.functions.JSStringFunctions", new TypeDeclaration("org.fife.rsta.ac.js.ecma.api.ecma3.functions", "JSStringFunctions", "String", false, false));
    
    addTypeDeclaration("org.fife.rsta.ac.js.ecma.api.ecma3.functions.JSGlobalFunctions", new TypeDeclaration("org.fife.rsta.ac.js.ecma.api.ecma3.functions", "JSGlobalFunctions", "Global", false, false));
  }
}
