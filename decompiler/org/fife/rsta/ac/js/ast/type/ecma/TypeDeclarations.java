package org.fife.rsta.ac.js.ast.type.ecma;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.fife.rsta.ac.js.JavaScriptHelper;
import org.fife.rsta.ac.js.ast.type.ArrayTypeDeclaration;
import org.fife.rsta.ac.js.ast.type.TypeDeclaration;









public abstract class TypeDeclarations
{
  private static final String ECMA_DEFAULT_PACKAGE = "org.fife.rsta.ac.js.ecma.api";
  public static final String ECMA_ARRAY = "JSArray";
  public static final String ECMA_BOOLEAN = "JSBoolean";
  public static final String ECMA_DATE = "JSDate";
  public static final String ECMA_ERROR = "JSError";
  public static final String ECMA_FUNCTION = "JSFunction";
  public static final String ECMA_MATH = "JSMath";
  public static final String ECMA_NUMBER = "JSNumber";
  public static final String ECMA_OBJECT = "JSObject";
  public static final String ECMA_REGEXP = "JSRegExp";
  public static final String ECMA_STRING = "JSString";
  public static final String ECMA_GLOBAL = "JSGlobal";
  public static final String ECMA_JSON = "JSJSON";
  public static final String ECMA_NAMESPACE = "E4XNamespace";
  public static final String ECMA_QNAME = "E4XQName";
  public static final String ECMA_XML = "E4XXML";
  public static final String ECMA_XMLLIST = "E4XXMLList";
  public static final String FUNCTION_CALL = "FC";
  public static final String ANY = "any";
  public static String NULL_TYPE = "void";
  
  private final HashMap<String, TypeDeclaration> types = new HashMap<>();

  
  private final HashMap<String, String> javascriptReverseLookup = new HashMap<>();
  private final HashSet<JavaScriptObject> ecmaObjects = new HashSet<>();

  
  public TypeDeclarations() {
    loadTypes();
    loadExtensions();
    loadReverseLookup();
    loadJavaScriptConstructors();
  }

  
  private void loadExtensions() {
    addTypeDeclaration("FC", new TypeDeclaration(null, "FC", "FC", false, false));
    
    addTypeDeclaration("any", new TypeDeclaration(null, "any", "any"));
  }

  
  protected void loadJavaScriptConstructors() {
    addECMAObject("JSString", true);
    addECMAObject("JSDate", true);
    addECMAObject("JSNumber", true);
    addECMAObject("JSMath", false);
    addECMAObject("JSObject", true);
    addECMAObject("JSFunction", true);
    addECMAObject("JSBoolean", true);
    addECMAObject("JSRegExp", true);
    addECMAObject("JSArray", true);
    addECMAObject("JSError", true);
    addECMAObject("JSJSON", false);
  }


  
  public void addECMAObject(String type, boolean canBeInstantiated) {
    this.ecmaObjects.add(new JavaScriptObject(type, canBeInstantiated));
  }



  
  protected void loadReverseLookup() {
    addJavaScriptLookup("String", "JSString");
    addJavaScriptLookup("Date", "JSDate");
    addJavaScriptLookup("RegExp", "JSRegExp");
    addJavaScriptLookup("Number", "JSNumber");
    addJavaScriptLookup("Math", "JSMath");
    addJavaScriptLookup("Function", "JSFunction");
    addJavaScriptLookup("Object", "JSObject");
    addJavaScriptLookup("Array", "JSArray");
    addJavaScriptLookup("Boolean", "JSBoolean");
    addJavaScriptLookup("Error", "JSError");
    addJavaScriptLookup("java.lang.String", "JSString");
    addJavaScriptLookup("java.lang.Number", "JSNumber");
    addJavaScriptLookup("java.lang.Short", "JSNumber");
    addJavaScriptLookup("java.lang.Long", "JSNumber");
    addJavaScriptLookup("java.lang.Float", "JSNumber");
    addJavaScriptLookup("java.lang.Byte", "JSNumber");
    addJavaScriptLookup("java.lang.Double", "JSNumber");
    addJavaScriptLookup("java.lang.Boolean", "JSBoolean");
    addJavaScriptLookup("short", "JSNumber");
    addJavaScriptLookup("long", "JSNumber");
    addJavaScriptLookup("float", "JSNumber");
    addJavaScriptLookup("byte", "JSNumber");
    addJavaScriptLookup("double", "JSNumber");
    addJavaScriptLookup("int", "JSNumber");
    addJavaScriptLookup("boolean", "JSBoolean");
    addJavaScriptLookup("JSON", "JSJSON");
    
    addJavaScriptLookup("Namespace", "E4XNamespace");
    addJavaScriptLookup("QName", "E4XQName");
    addJavaScriptLookup("XML", "E4XXML");
    addJavaScriptLookup("XMLList", "E4XXMLList");
  }

  
  protected abstract void loadTypes();

  
  public void addTypeDeclaration(String name, TypeDeclaration dec) {
    this.types.put(name, dec);
    
    addJavaScriptLookup(dec.getQualifiedName(), name);
  }

  
  public String getClassName(String lookupType) {
    TypeDeclaration dec = this.types.get(lookupType);
    return (dec != null) ? dec.getQualifiedName() : null;
  }

  
  public List<String> getAllClasses() {
    List<String> classes = new ArrayList<>();
    
    for (String name : this.types.keySet()) {
      TypeDeclaration dec = this.types.get(name);
      if (dec != null) {
        classes.add(dec.getQualifiedName());
      }
    } 
    return classes;
  }

  
  public List<TypeDeclaration> getAllJavaScriptTypeDeclarations() {
    List<TypeDeclaration> jsTypes = new ArrayList<>();
    
    for (String name : this.types.keySet()) {
      TypeDeclaration dec = this.types.get(name);
      if (isJavaScriptType(dec)) {
        jsTypes.add(dec);
      }
    } 
    return jsTypes;
  }







  
  public void addJavaScriptLookup(String apiName, String jsName) {
    this.javascriptReverseLookup.put(apiName, jsName);
  }







  
  public void removeType(String name) {
    this.types.remove(name);
  }







  
  public boolean isJavaScriptType(TypeDeclaration td) {
    return (td != null && td.getPackageName() != null && td
      .getPackageName().startsWith("org.fife.rsta.ac.js.ecma.api"));
  }









  
  public TypeDeclaration getTypeDeclaration(String name) {
    if (name == null) {
      return null;
    }
    TypeDeclaration typeDeclation = this.types.get(name);
    if (typeDeclation == null) {
      typeDeclation = getJSType(name);
    }
    return typeDeclation;
  }










  
  private TypeDeclaration getJSType(String lookupName) {
    if (lookupName.indexOf('[') > -1 && lookupName.indexOf(']') > -1) {
      TypeDeclaration arrayType = getTypeDeclaration("JSArray");

      
      ArrayTypeDeclaration arrayDec = new ArrayTypeDeclaration(arrayType.getPackageName(), arrayType.getAPITypeName(), arrayType.getJSName());

      
      String arrayTypeName = lookupName.substring(0, lookupName
          .indexOf('['));
      
      TypeDeclaration containerType = JavaScriptHelper.createNewTypeDeclaration(arrayTypeName);
      arrayDec.setArrayType(containerType);
      return (TypeDeclaration)arrayDec;
    } 
    
    String name = this.javascriptReverseLookup.get(lookupName);
    if (name != null) {
      return this.types.get(name);
    }

    
    return null;
  }

  
  public Set<JavaScriptObject> getJavaScriptObjects() {
    return this.ecmaObjects;
  }







  
  public boolean canECMAObjectBeInstantiated(String name) {
    String tempName = this.javascriptReverseLookup.get(name);
    if (tempName != null) {
      name = tempName;
    }
    for (JavaScriptObject jo : this.ecmaObjects) {
      if (jo.getName().equals(name)) {
        return jo.canBeInstantiated();
      }
    } 
    
    return false;
  }


  
  public static class JavaScriptObject
  {
    private String name;

    
    private boolean canBeInstantiated;

    
    public JavaScriptObject(String name, boolean canBeInstantiated) {
      this.name = name;
      this.canBeInstantiated = canBeInstantiated;
    }

    
    public String getName() {
      return this.name;
    }

    
    public boolean canBeInstantiated() {
      return this.canBeInstantiated;
    }

    
    public boolean equals(Object jsObj) {
      if (jsObj == this) {
        return true;
      }
      if (jsObj instanceof JavaScriptObject)
      {
        return ((JavaScriptObject)jsObj).getName().equals(getName());
      }
      
      return false;
    }


    
    public int hashCode() {
      return this.name.hashCode();
    }
  }
}
