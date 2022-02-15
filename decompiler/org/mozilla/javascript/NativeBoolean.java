package org.mozilla.javascript;












final class NativeBoolean
  extends IdScriptableObject
{
  static final long serialVersionUID = -3716996899943880933L;
  private static final Object BOOLEAN_TAG = "Boolean"; private static final int Id_constructor = 1; private static final int Id_toString = 2;
  private static final int Id_toSource = 3;
  
  static void init(Scriptable scope, boolean sealed) {
    NativeBoolean obj = new NativeBoolean(false);
    obj.exportAsJSClass(4, scope, sealed);
  }
  private static final int Id_valueOf = 4; private static final int MAX_PROTOTYPE_ID = 4; private boolean booleanValue;
  
  NativeBoolean(boolean b) {
    this.booleanValue = b;
  }


  
  public String getClassName() {
    return "Boolean";
  }



  
  public Object getDefaultValue(Class<?> typeHint) {
    if (typeHint == ScriptRuntime.BooleanClass)
      return ScriptRuntime.wrapBoolean(this.booleanValue); 
    return super.getDefaultValue(typeHint);
  }


  
  protected void initPrototypeId(int id) {
    String s;
    int arity;
    switch (id) { case 1:
        arity = 1; s = "constructor"; break;
      case 2: arity = 0; s = "toString"; break;
      case 3: arity = 0; s = "toSource"; break;
      case 4: arity = 0; s = "valueOf"; break;
      default: throw new IllegalArgumentException(String.valueOf(id)); }
    
    initPrototypeMethod(BOOLEAN_TAG, id, s, arity);
  }



  
  public Object execIdCall(IdFunctionObject f, Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
    if (!f.hasTag(BOOLEAN_TAG)) {
      return super.execIdCall(f, cx, scope, thisObj, args);
    }
    int id = f.methodId();
    
    if (id == 1) {
      boolean b;
      if (args.length == 0) {
        b = false;
      } else {
        b = (args[0] instanceof ScriptableObject && ((ScriptableObject)args[0]).avoidObjectDetection()) ? true : ScriptRuntime.toBoolean(args[0]);
      } 


      
      if (thisObj == null)
      {
        return new NativeBoolean(b);
      }
      
      return ScriptRuntime.wrapBoolean(b);
    } 


    
    if (!(thisObj instanceof NativeBoolean))
      throw incompatibleCallError(f); 
    boolean value = ((NativeBoolean)thisObj).booleanValue;
    
    switch (id) {
      
      case 2:
        return value ? "true" : "false";
      
      case 3:
        return value ? "(new Boolean(true))" : "(new Boolean(false))";
      
      case 4:
        return ScriptRuntime.wrapBoolean(value);
    } 
    throw new IllegalArgumentException(String.valueOf(id));
  }






  
  protected int findPrototypeId(String s) {
    int id = 0; String X = null;
    int s_length = s.length();
    if (s_length == 7) { X = "valueOf"; id = 4; }
    else if (s_length == 8)
    { int c = s.charAt(3);
      if (c == 111) { X = "toSource"; id = 3; }
      else if (c == 116) { X = "toString"; id = 2; }
       }
    else if (s_length == 11) { X = "constructor"; id = 1; }
     if (X != null && X != s && !X.equals(s)) id = 0;


    
    return id;
  }
}
