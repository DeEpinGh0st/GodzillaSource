package org.mozilla.javascript;

import java.io.Serializable;













public class NativeWith
  implements Scriptable, IdFunctionCall, Serializable
{
  private static final long serialVersionUID = 1L;
  
  static void init(Scriptable scope, boolean sealed) {
    NativeWith obj = new NativeWith();
    
    obj.setParentScope(scope);
    obj.setPrototype(ScriptableObject.getObjectPrototype(scope));
    
    IdFunctionObject ctor = new IdFunctionObject(obj, FTAG, 1, "With", 0, scope);
    
    ctor.markAsConstructor(obj);
    if (sealed) {
      ctor.sealObject();
    }
    ctor.exportAsScopeProperty();
  }

  
  private NativeWith() {}
  
  protected NativeWith(Scriptable parent, Scriptable prototype) {
    this.parent = parent;
    this.prototype = prototype;
  }
  
  public String getClassName() {
    return "With";
  }

  
  public boolean has(String id, Scriptable start) {
    return this.prototype.has(id, this.prototype);
  }

  
  public boolean has(int index, Scriptable start) {
    return this.prototype.has(index, this.prototype);
  }

  
  public Object get(String id, Scriptable start) {
    if (start == this)
      start = this.prototype; 
    return this.prototype.get(id, start);
  }

  
  public Object get(int index, Scriptable start) {
    if (start == this)
      start = this.prototype; 
    return this.prototype.get(index, start);
  }

  
  public void put(String id, Scriptable start, Object value) {
    if (start == this)
      start = this.prototype; 
    this.prototype.put(id, start, value);
  }

  
  public void put(int index, Scriptable start, Object value) {
    if (start == this)
      start = this.prototype; 
    this.prototype.put(index, start, value);
  }

  
  public void delete(String id) {
    this.prototype.delete(id);
  }

  
  public void delete(int index) {
    this.prototype.delete(index);
  }
  
  public Scriptable getPrototype() {
    return this.prototype;
  }
  
  public void setPrototype(Scriptable prototype) {
    this.prototype = prototype;
  }
  
  public Scriptable getParentScope() {
    return this.parent;
  }
  
  public void setParentScope(Scriptable parent) {
    this.parent = parent;
  }
  
  public Object[] getIds() {
    return this.prototype.getIds();
  }
  
  public Object getDefaultValue(Class<?> typeHint) {
    return this.prototype.getDefaultValue(typeHint);
  }
  
  public boolean hasInstance(Scriptable value) {
    return this.prototype.hasInstance(value);
  }





  
  protected Object updateDotQuery(boolean value) {
    throw new IllegalStateException();
  }


  
  public Object execIdCall(IdFunctionObject f, Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
    if (f.hasTag(FTAG) && 
      f.methodId() == 1) {
      throw Context.reportRuntimeError1("msg.cant.call.indirect", "With");
    }
    
    throw f.unknown();
  }

  
  static boolean isWithFunction(Object functionObj) {
    if (functionObj instanceof IdFunctionObject) {
      IdFunctionObject f = (IdFunctionObject)functionObj;
      return (f.hasTag(FTAG) && f.methodId() == 1);
    } 
    return false;
  }

  
  static Object newWithSpecial(Context cx, Scriptable scope, Object[] args) {
    ScriptRuntime.checkDeprecated(cx, "With");
    scope = ScriptableObject.getTopLevelScope(scope);
    NativeWith thisObj = new NativeWith();
    thisObj.setPrototype((args.length == 0) ? ScriptableObject.getObjectPrototype(scope) : ScriptRuntime.toObject(cx, scope, args[0]));

    
    thisObj.setParentScope(scope);
    return thisObj;
  }
  
  private static final Object FTAG = "With";
  private static final int Id_constructor = 1;
  protected Scriptable prototype;
  protected Scriptable parent;
}
