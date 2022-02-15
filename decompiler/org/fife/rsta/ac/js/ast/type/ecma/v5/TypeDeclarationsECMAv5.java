package org.fife.rsta.ac.js.ast.type.ecma.v5;

import org.fife.rsta.ac.js.ast.type.TypeDeclaration;
import org.fife.rsta.ac.js.ast.type.ecma.v3.TypeDeclarationsECMAv3;



public class TypeDeclarationsECMAv5
  extends TypeDeclarationsECMAv3
{
  protected void loadTypes() {
    super.loadTypes();
    
    addTypeDeclaration("JSArray", new TypeDeclaration("org.fife.rsta.ac.js.ecma.api.ecma5", "JS5Array", "Array", false, false));
    
    addTypeDeclaration("JSDate", new TypeDeclaration("org.fife.rsta.ac.js.ecma.api.ecma5", "JS5Date", "Date", false, false));
    
    addTypeDeclaration("JSFunction", new TypeDeclaration("org.fife.rsta.ac.js.ecma.api.ecma5", "JS5Function", "Function", false, false));
    
    addTypeDeclaration("JSObject", new TypeDeclaration("org.fife.rsta.ac.js.ecma.api.ecma5", "JS5Object", "Object", false, false));
    
    addTypeDeclaration("JSString", new TypeDeclaration("org.fife.rsta.ac.js.ecma.api.ecma5", "JS5String", "String", false, false));
    
    addTypeDeclaration("JSJSON", new TypeDeclaration("org.fife.rsta.ac.js.ecma.api.ecma5", "JS5JSON", "JSON", false, false));

    
    addTypeDeclaration("org.fife.rsta.ac.js.ecma.api.ecma5.functions.JS5ArrayFunctions", new TypeDeclaration("org.fife.rsta.ac.js.ecma.api.ecma5.functions", "JS5ArrayFunctions", "Array", false, false));
    
    addTypeDeclaration("org.fife.rsta.ac.js.ecma.api.ecma5.functions.JS5DateFunctions", new TypeDeclaration("org.fife.rsta.ac.js.ecma.api.ecma5.functions", "JS5DateFunctions", "Date", false, false));
    
    addTypeDeclaration("org.fife.rsta.ac.js.ecma.api.ecma5.functions.JS5FunctionFunctions", new TypeDeclaration("org.fife.rsta.ac.js.ecma.api.ecma5.functions", "JS5FunctionFunctions", "Function", false, false));
    
    addTypeDeclaration("org.fife.rsta.ac.js.ecma.api.ecma5.functions.JS5ObjectFunctions", new TypeDeclaration("org.fife.rsta.ac.js.ecma.api.ecma5.functions", "JS5ObjectFunctions", "Object", false, false));
    
    addTypeDeclaration("org.fife.rsta.ac.js.ecma.api.ecma5.functions.JS5StringFunctions", new TypeDeclaration("org.fife.rsta.ac.js.ecma.api.ecma5.functions", "JS5StringFunctions", "String", false, false));
  }
}
