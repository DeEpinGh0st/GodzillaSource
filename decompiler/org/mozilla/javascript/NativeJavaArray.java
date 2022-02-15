package org.mozilla.javascript;

import java.lang.reflect.Array;













public class NativeJavaArray
  extends NativeJavaObject
{
  static final long serialVersionUID = -924022554283675333L;
  Object array;
  int length;
  Class<?> cls;
  
  public String getClassName() {
    return "JavaArray";
  }
  
  public static NativeJavaArray wrap(Scriptable scope, Object array) {
    return new NativeJavaArray(scope, array);
  }

  
  public Object unwrap() {
    return this.array;
  }
  
  public NativeJavaArray(Scriptable scope, Object array) {
    super(scope, (Object)null, ScriptRuntime.ObjectClass);
    Class<?> cl = array.getClass();
    if (!cl.isArray()) {
      throw new RuntimeException("Array expected");
    }
    this.array = array;
    this.length = Array.getLength(array);
    this.cls = cl.getComponentType();
  }

  
  public boolean has(String id, Scriptable start) {
    return (id.equals("length") || super.has(id, start));
  }

  
  public boolean has(int index, Scriptable start) {
    return (0 <= index && index < this.length);
  }

  
  public Object get(String id, Scriptable start) {
    if (id.equals("length"))
      return Integer.valueOf(this.length); 
    Object result = super.get(id, start);
    if (result == NOT_FOUND && !ScriptableObject.hasProperty(getPrototype(), id))
    {
      
      throw Context.reportRuntimeError2("msg.java.member.not.found", this.array.getClass().getName(), id);
    }
    
    return result;
  }

  
  public Object get(int index, Scriptable start) {
    if (0 <= index && index < this.length) {
      Context cx = Context.getContext();
      Object obj = Array.get(this.array, index);
      return cx.getWrapFactory().wrap(cx, this, obj, this.cls);
    } 
    return Undefined.instance;
  }


  
  public void put(String id, Scriptable start, Object value) {
    if (!id.equals("length")) {
      throw Context.reportRuntimeError1("msg.java.array.member.not.found", id);
    }
  }

  
  public void put(int index, Scriptable start, Object value) {
    if (0 <= index && index < this.length) {
      Array.set(this.array, index, Context.jsToJava(value, this.cls));
    } else {
      
      throw Context.reportRuntimeError2("msg.java.array.index.out.of.bounds", String.valueOf(index), String.valueOf(this.length - 1));
    } 
  }



  
  public Object getDefaultValue(Class<?> hint) {
    if (hint == null || hint == ScriptRuntime.StringClass)
      return this.array.toString(); 
    if (hint == ScriptRuntime.BooleanClass)
      return Boolean.TRUE; 
    if (hint == ScriptRuntime.NumberClass)
      return ScriptRuntime.NaNobj; 
    return this;
  }

  
  public Object[] getIds() {
    Object[] result = new Object[this.length];
    int i = this.length;
    while (--i >= 0)
      result[i] = Integer.valueOf(i); 
    return result;
  }

  
  public boolean hasInstance(Scriptable value) {
    if (!(value instanceof Wrapper))
      return false; 
    Object instance = ((Wrapper)value).unwrap();
    return this.cls.isInstance(instance);
  }

  
  public Scriptable getPrototype() {
    if (this.prototype == null) {
      this.prototype = ScriptableObject.getArrayPrototype(getParentScope());
    }
    
    return this.prototype;
  }
}
