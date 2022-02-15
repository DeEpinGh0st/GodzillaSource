package org.mozilla.javascript;






















public class NativeJavaTopPackage
  extends NativeJavaPackage
  implements Function, IdFunctionCall
{
  static final long serialVersionUID = -1455787259477709999L;
  private static final String[][] commonPackages = new String[][] { { "java", "lang", "reflect" }, { "java", "io" }, { "java", "math" }, { "java", "net" }, { "java", "util", "zip" }, { "java", "text", "resources" }, { "java", "applet" }, { "javax", "swing" } };










  
  NativeJavaTopPackage(ClassLoader loader) {
    super(true, "", loader);
  }


  
  public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
    return construct(cx, scope, args);
  }

  
  public Scriptable construct(Context cx, Scriptable scope, Object[] args) {
    ClassLoader loader = null;
    if (args.length != 0) {
      Object arg = args[0];
      if (arg instanceof Wrapper) {
        arg = ((Wrapper)arg).unwrap();
      }
      if (arg instanceof ClassLoader) {
        loader = (ClassLoader)arg;
      }
    } 
    if (loader == null) {
      Context.reportRuntimeError0("msg.not.classloader");
      return null;
    } 
    NativeJavaPackage pkg = new NativeJavaPackage(true, "", loader);
    ScriptRuntime.setObjectProtoAndParent(pkg, scope);
    return pkg;
  }

  
  public static void init(Context cx, Scriptable scope, boolean sealed) {
    ClassLoader loader = cx.getApplicationClassLoader();
    NativeJavaTopPackage top = new NativeJavaTopPackage(loader);
    top.setPrototype(getObjectPrototype(scope));
    top.setParentScope(scope);
    
    for (int i = 0; i != commonPackages.length; i++) {
      NativeJavaPackage parent = top;
      for (int m = 0; m != (commonPackages[i]).length; m++) {
        parent = parent.forcePackage(commonPackages[i][m], scope);
      }
    } 

    
    IdFunctionObject getClass = new IdFunctionObject(top, FTAG, 1, "getClass", 1, scope);




    
    String[] topNames = ScriptRuntime.getTopPackageNames();
    NativeJavaPackage[] topPackages = new NativeJavaPackage[topNames.length];
    for (int j = 0; j < topNames.length; j++) {
      topPackages[j] = (NativeJavaPackage)top.get(topNames[j], top);
    }


    
    ScriptableObject global = (ScriptableObject)scope;
    
    if (sealed) {
      getClass.sealObject();
    }
    getClass.exportAsScopeProperty();
    global.defineProperty("Packages", top, 2);
    for (int k = 0; k < topNames.length; k++) {
      global.defineProperty(topNames[k], topPackages[k], 2);
    }
  }



  
  public Object execIdCall(IdFunctionObject f, Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
    if (f.hasTag(FTAG) && 
      f.methodId() == 1) {
      return js_getClass(cx, scope, args);
    }
    
    throw f.unknown();
  }

  
  private Scriptable js_getClass(Context cx, Scriptable scope, Object[] args) {
    if (args.length > 0 && args[0] instanceof Wrapper) {
      Scriptable result = this;
      Class<?> cl = ((Wrapper)args[0]).unwrap().getClass();

      
      String name = cl.getName();
      int offset = 0;
      while (true) {
        int index = name.indexOf('.', offset);
        String propName = (index == -1) ? name.substring(offset) : name.substring(offset, index);

        
        Object prop = result.get(propName, result);
        if (!(prop instanceof Scriptable))
          break; 
        result = (Scriptable)prop;
        if (index == -1)
          return result; 
        offset = index + 1;
      } 
    } 
    throw Context.reportRuntimeError0("msg.not.java.obj");
  }
  
  private static final Object FTAG = "JavaTopPackage";
  private static final int Id_getClass = 1;
}
