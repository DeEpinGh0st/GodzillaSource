package org.mozilla.javascript;






































public class ImporterTopLevel
  extends TopLevel
{
  static final long serialVersionUID = -9095380847465315412L;
  private static final Object IMPORTER_TAG = "Importer";
  private static final int Id_constructor = 1;
  private static final int Id_importClass = 2;
  
  public ImporterTopLevel(Context cx) {
    this(cx, false);
  }
  private static final int Id_importPackage = 3; private static final int MAX_PROTOTYPE_ID = 3;
  public ImporterTopLevel() {}
  public ImporterTopLevel(Context cx, boolean sealed) {
    initStandardObjects(cx, sealed);
  }


  
  public String getClassName() {
    return this.topScopeFlag ? "global" : "JavaImporter";
  }

  
  public static void init(Context cx, Scriptable scope, boolean sealed) {
    ImporterTopLevel obj = new ImporterTopLevel();
    obj.exportAsJSClass(3, scope, sealed);
  }



  
  public void initStandardObjects(Context cx, boolean sealed) {
    cx.initStandardObjects(this, sealed);
    this.topScopeFlag = true;


    
    IdFunctionObject ctor = exportAsJSClass(3, this, false);
    if (sealed) {
      ctor.sealObject();
    }


    
    delete("constructor");
  }

  
  public boolean has(String name, Scriptable start) {
    return (super.has(name, start) || getPackageProperty(name, start) != NOT_FOUND);
  }


  
  public Object get(String name, Scriptable start) {
    Object result = super.get(name, start);
    if (result != NOT_FOUND)
      return result; 
    result = getPackageProperty(name, start);
    return result;
  }
  
  private Object getPackageProperty(String name, Scriptable start) {
    Object elements[], result = NOT_FOUND;
    
    synchronized (this.importedPackages) {
      elements = this.importedPackages.toArray();
    } 
    for (int i = 0; i < elements.length; i++) {
      NativeJavaPackage p = (NativeJavaPackage)elements[i];
      Object v = p.getPkgProperty(name, start, false);
      if (v != null && !(v instanceof NativeJavaPackage)) {
        if (result == NOT_FOUND) {
          result = v;
        } else {
          throw Context.reportRuntimeError2("msg.ambig.import", result.toString(), v.toString());
        } 
      }
    } 
    
    return result;
  }





  
  @Deprecated
  public void importPackage(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
    js_importPackage(args);
  }

  
  private Object js_construct(Scriptable scope, Object[] args) {
    ImporterTopLevel result = new ImporterTopLevel();
    for (int i = 0; i != args.length; i++) {
      Object arg = args[i];
      if (arg instanceof NativeJavaClass) {
        result.importClass((NativeJavaClass)arg);
      } else if (arg instanceof NativeJavaPackage) {
        result.importPackage((NativeJavaPackage)arg);
      } else {
        throw Context.reportRuntimeError1("msg.not.class.not.pkg", Context.toString(arg));
      } 
    } 





    
    result.setParentScope(scope);
    result.setPrototype(this);
    return result;
  }

  
  private Object js_importClass(Object[] args) {
    for (int i = 0; i != args.length; i++) {
      Object arg = args[i];
      if (!(arg instanceof NativeJavaClass)) {
        throw Context.reportRuntimeError1("msg.not.class", Context.toString(arg));
      }
      
      importClass((NativeJavaClass)arg);
    } 
    return Undefined.instance;
  }

  
  private Object js_importPackage(Object[] args) {
    for (int i = 0; i != args.length; i++) {
      Object arg = args[i];
      if (!(arg instanceof NativeJavaPackage)) {
        throw Context.reportRuntimeError1("msg.not.pkg", Context.toString(arg));
      }
      
      importPackage((NativeJavaPackage)arg);
    } 
    return Undefined.instance;
  }

  
  private void importPackage(NativeJavaPackage pkg) {
    if (pkg == null) {
      return;
    }
    synchronized (this.importedPackages) {
      for (int j = 0; j != this.importedPackages.size(); j++) {
        if (pkg.equals(this.importedPackages.get(j))) {
          return;
        }
      } 
      this.importedPackages.add(pkg);
    } 
  }

  
  private void importClass(NativeJavaClass cl) {
    String s = cl.getClassObject().getName();
    String n = s.substring(s.lastIndexOf('.') + 1);
    Object val = get(n, this);
    if (val != NOT_FOUND && val != cl) {
      throw Context.reportRuntimeError1("msg.prop.defined", n);
    }
    
    put(n, this, cl);
  }


  
  protected void initPrototypeId(int id) {
    String s;
    int arity;
    switch (id) { case 1:
        arity = 0; s = "constructor"; break;
      case 2: arity = 1; s = "importClass"; break;
      case 3: arity = 1; s = "importPackage"; break;
      default: throw new IllegalArgumentException(String.valueOf(id)); }
    
    initPrototypeMethod(IMPORTER_TAG, id, s, arity);
  }



  
  public Object execIdCall(IdFunctionObject f, Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
    if (!f.hasTag(IMPORTER_TAG)) {
      return super.execIdCall(f, cx, scope, thisObj, args);
    }
    int id = f.methodId();
    switch (id) {
      case 1:
        return js_construct(scope, args);
      
      case 2:
        return realThis(thisObj, f).js_importClass(args);
      
      case 3:
        return realThis(thisObj, f).js_importPackage(args);
    } 
    throw new IllegalArgumentException(String.valueOf(id));
  }

  
  private ImporterTopLevel realThis(Scriptable thisObj, IdFunctionObject f) {
    if (this.topScopeFlag)
    {
      
      return this;
    }
    if (!(thisObj instanceof ImporterTopLevel))
      throw incompatibleCallError(f); 
    return (ImporterTopLevel)thisObj;
  }






  
  protected int findPrototypeId(String s) {
    int id = 0; String X = null;
    int s_length = s.length();
    if (s_length == 11)
    { int c = s.charAt(0);
      if (c == 99) { X = "constructor"; id = 1; }
      else if (c == 105) { X = "importClass"; id = 2; }
       }
    else if (s_length == 13) { X = "importPackage"; id = 3; }
     if (X != null && X != s && !X.equals(s)) id = 0;


    
    return id;
  }








  
  private ObjArray importedPackages = new ObjArray();
  private boolean topScopeFlag;
}
