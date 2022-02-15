package org.mozilla.javascript.tools.shell;

import java.util.Map;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;






















public class Environment
  extends ScriptableObject
{
  static final long serialVersionUID = -430727378460177065L;
  private Environment thePrototypeInstance = null;
  
  public static void defineClass(ScriptableObject scope) {
    try {
      ScriptableObject.defineClass((Scriptable)scope, Environment.class);
    } catch (Exception e) {
      throw new Error(e.getMessage());
    } 
  }

  
  public String getClassName() {
    return "Environment";
  }
  
  public Environment() {
    if (this.thePrototypeInstance == null)
      this.thePrototypeInstance = this; 
  }
  
  public Environment(ScriptableObject scope) {
    setParentScope((Scriptable)scope);
    Object ctor = ScriptRuntime.getTopLevelProp((Scriptable)scope, "Environment");
    if (ctor != null && ctor instanceof Scriptable) {
      Scriptable s = (Scriptable)ctor;
      setPrototype((Scriptable)s.get("prototype", s));
    } 
  }

  
  public boolean has(String name, Scriptable start) {
    if (this == this.thePrototypeInstance) {
      return super.has(name, start);
    }
    return (System.getProperty(name) != null);
  }

  
  public Object get(String name, Scriptable start) {
    if (this == this.thePrototypeInstance) {
      return super.get(name, start);
    }
    String result = System.getProperty(name);
    if (result != null) {
      return ScriptRuntime.toObject(getParentScope(), result);
    }
    return Scriptable.NOT_FOUND;
  }

  
  public void put(String name, Scriptable start, Object value) {
    if (this == this.thePrototypeInstance) {
      super.put(name, start, value);
    } else {
      System.getProperties().put(name, ScriptRuntime.toString(value));
    } 
  }
  private Object[] collectIds() {
    Map<Object, Object> props = System.getProperties();
    return props.keySet().toArray();
  }

  
  public Object[] getIds() {
    if (this == this.thePrototypeInstance)
      return super.getIds(); 
    return collectIds();
  }

  
  public Object[] getAllIds() {
    if (this == this.thePrototypeInstance)
      return super.getAllIds(); 
    return collectIds();
  }
}
