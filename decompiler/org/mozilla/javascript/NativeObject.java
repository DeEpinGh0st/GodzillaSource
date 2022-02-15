package org.mozilla.javascript;

import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;











public class NativeObject
  extends IdScriptableObject
  implements Map
{
  static final long serialVersionUID = -6345305608474346996L;
  private static final Object OBJECT_TAG = "Object"; private static final int ConstructorId_getPrototypeOf = -1; private static final int ConstructorId_keys = -2; private static final int ConstructorId_getOwnPropertyNames = -3; private static final int ConstructorId_getOwnPropertyDescriptor = -4; private static final int ConstructorId_defineProperty = -5; private static final int ConstructorId_isExtensible = -6; private static final int ConstructorId_preventExtensions = -7; private static final int ConstructorId_defineProperties = -8; private static final int ConstructorId_create = -9; private static final int ConstructorId_isSealed = -10; private static final int ConstructorId_isFrozen = -11; private static final int ConstructorId_seal = -12;
  private static final int ConstructorId_freeze = -13;
  
  static void init(Scriptable scope, boolean sealed) {
    NativeObject obj = new NativeObject();
    obj.exportAsJSClass(12, scope, sealed);
  }
  private static final int Id_constructor = 1; private static final int Id_toString = 2; private static final int Id_toLocaleString = 3; private static final int Id_valueOf = 4; private static final int Id_hasOwnProperty = 5; private static final int Id_propertyIsEnumerable = 6; private static final int Id_isPrototypeOf = 7; private static final int Id_toSource = 8; private static final int Id___defineGetter__ = 9; private static final int Id___defineSetter__ = 10; private static final int Id___lookupGetter__ = 11; private static final int Id___lookupSetter__ = 12;
  private static final int MAX_PROTOTYPE_ID = 12;
  
  public String getClassName() {
    return "Object";
  }


  
  public String toString() {
    return ScriptRuntime.defaultObjectToString(this);
  }


  
  protected void fillConstructorProperties(IdFunctionObject ctor) {
    addIdFunctionProperty(ctor, OBJECT_TAG, -1, "getPrototypeOf", 1);
    
    addIdFunctionProperty(ctor, OBJECT_TAG, -2, "keys", 1);
    
    addIdFunctionProperty(ctor, OBJECT_TAG, -3, "getOwnPropertyNames", 1);
    
    addIdFunctionProperty(ctor, OBJECT_TAG, -4, "getOwnPropertyDescriptor", 2);
    
    addIdFunctionProperty(ctor, OBJECT_TAG, -5, "defineProperty", 3);
    
    addIdFunctionProperty(ctor, OBJECT_TAG, -6, "isExtensible", 1);
    
    addIdFunctionProperty(ctor, OBJECT_TAG, -7, "preventExtensions", 1);
    
    addIdFunctionProperty(ctor, OBJECT_TAG, -8, "defineProperties", 2);
    
    addIdFunctionProperty(ctor, OBJECT_TAG, -9, "create", 2);
    
    addIdFunctionProperty(ctor, OBJECT_TAG, -10, "isSealed", 1);
    
    addIdFunctionProperty(ctor, OBJECT_TAG, -11, "isFrozen", 1);
    
    addIdFunctionProperty(ctor, OBJECT_TAG, -12, "seal", 1);
    
    addIdFunctionProperty(ctor, OBJECT_TAG, -13, "freeze", 1);
    
    super.fillConstructorProperties(ctor);
  }

  
  protected void initPrototypeId(int id)
  {
    String s;
    int arity;
    switch (id) { case 1:
        arity = 1; s = "constructor"; break;
      case 2: arity = 0; s = "toString"; break;
      case 3: arity = 0; s = "toLocaleString"; break;
      case 4: arity = 0; s = "valueOf"; break;
      case 5: arity = 1; s = "hasOwnProperty"; break;
      case 6:
        arity = 1; s = "propertyIsEnumerable"; break;
      case 7: arity = 1; s = "isPrototypeOf"; break;
      case 8: arity = 0; s = "toSource"; break;
      case 9:
        arity = 2; s = "__defineGetter__"; break;
      case 10:
        arity = 2; s = "__defineSetter__"; break;
      case 11:
        arity = 1; s = "__lookupGetter__"; break;
      case 12:
        arity = 1; s = "__lookupSetter__"; break;
      default: throw new IllegalArgumentException(String.valueOf(id)); }
    
    initPrototypeMethod(OBJECT_TAG, id, s, arity); } public Object execIdCall(IdFunctionObject f, Context cx, Scriptable scope, Scriptable thisObj, Object[] args) { Object toString; boolean result; ScriptableObject so; Object arg; Callable fun; Object object1; String name; Scriptable scriptable2; ScriptableObject scriptableObject1; Scriptable scriptable1; ScriptableObject obj; String s; int index; Object ids[], nameArg, object2, propsObj; ScriptableObject newObject; Callable getterOrSetter; boolean isSetter; int i; String str1;
    Object descArg;
    Scriptable props;
    boolean bool1;
    Object gs;
    Scriptable scriptable3;
    ScriptableObject desc;
    if (!f.hasTag(OBJECT_TAG)) {
      return super.execIdCall(f, cx, scope, thisObj, args);
    }
    int id = f.methodId();
    switch (id) {
      case 1:
        if (thisObj != null)
        {
          return f.construct(cx, scope, args);
        }
        if (args.length == 0 || args[0] == null || args[0] == Undefined.instance)
        {
          
          return new NativeObject();
        }
        return ScriptRuntime.toObject(cx, scope, args[0]);

      
      case 3:
        toString = ScriptableObject.getProperty(thisObj, "toString");
        if (!(toString instanceof Callable)) {
          throw ScriptRuntime.notFunctionError(toString);
        }
        fun = (Callable)toString;
        return fun.call(cx, scope, thisObj, ScriptRuntime.emptyArgs);

      
      case 2:
        if (cx.hasFeature(4)) {
          String str = ScriptRuntime.defaultObjectToSource(cx, scope, thisObj, args);
          
          int L = str.length();
          if (L != 0 && str.charAt(0) == '(' && str.charAt(L - 1) == ')')
          {
            str = str.substring(1, L - 1);
          }
          return str;
        } 
        return ScriptRuntime.defaultObjectToString(thisObj);

      
      case 4:
        return thisObj;

      
      case 5:
        object1 = (args.length < 1) ? Undefined.instance : args[0];
        s = ScriptRuntime.toStringIdOrIndex(cx, object1);
        if (s == null) {
          int j = ScriptRuntime.lastIndexResult(cx);
          result = thisObj.has(j, thisObj);
        } else {
          result = thisObj.has(s, thisObj);
        } 
        return ScriptRuntime.wrapBoolean(result);


      
      case 6:
        object1 = (args.length < 1) ? Undefined.instance : args[0];
        s = ScriptRuntime.toStringIdOrIndex(cx, object1);
        if (s == null) {
          int j = ScriptRuntime.lastIndexResult(cx);
          result = thisObj.has(j, thisObj);
          if (result && thisObj instanceof ScriptableObject) {
            ScriptableObject scriptableObject = (ScriptableObject)thisObj;
            int attrs = scriptableObject.getAttributes(j);
            result = ((attrs & 0x2) == 0);
          } 
        } else {
          result = thisObj.has(s, thisObj);
          if (result && thisObj instanceof ScriptableObject) {
            ScriptableObject scriptableObject = (ScriptableObject)thisObj;
            int attrs = scriptableObject.getAttributes(s);
            result = ((attrs & 0x2) == 0);
          } 
        } 
        return ScriptRuntime.wrapBoolean(result);

      
      case 7:
        result = false;
        if (args.length != 0 && args[0] instanceof Scriptable) {
          Scriptable v = (Scriptable)args[0];
          do {
            v = v.getPrototype();
            if (v == thisObj) {
              result = true;
              break;
            } 
          } while (v != null);
        } 
        return ScriptRuntime.wrapBoolean(result);

      
      case 8:
        return ScriptRuntime.defaultObjectToSource(cx, scope, thisObj, args);

      
      case 9:
      case 10:
        if (args.length < 2 || !(args[1] instanceof Callable)) {
          Object badArg = (args.length >= 2) ? args[1] : Undefined.instance;
          
          throw ScriptRuntime.notFunctionError(badArg);
        } 
        if (!(thisObj instanceof ScriptableObject)) {
          throw Context.reportRuntimeError2("msg.extend.scriptable", thisObj.getClass().getName(), String.valueOf(args[0]));
        }


        
        so = (ScriptableObject)thisObj;
        name = ScriptRuntime.toStringIdOrIndex(cx, args[0]);
        index = (name != null) ? 0 : ScriptRuntime.lastIndexResult(cx);
        
        getterOrSetter = (Callable)args[1];
        bool1 = (id == 10);
        so.setGetterOrSetter(name, index, getterOrSetter, bool1);
        if (so instanceof NativeArray) {
          ((NativeArray)so).setDenseOnly(false);
        }
        return Undefined.instance;

      
      case 11:
      case 12:
        if (args.length < 1 || !(thisObj instanceof ScriptableObject))
        {
          return Undefined.instance;
        }
        so = (ScriptableObject)thisObj;
        name = ScriptRuntime.toStringIdOrIndex(cx, args[0]);
        index = (name != null) ? 0 : ScriptRuntime.lastIndexResult(cx);
        
        isSetter = (id == 12);
        
        while (true) {
          gs = so.getGetterOrSetter(name, index, isSetter);
          if (gs != null) {
            break;
          }
          
          Scriptable v = so.getPrototype();
          if (v == null)
            break; 
          if (v instanceof ScriptableObject) {
            so = (ScriptableObject)v; continue;
          } 
          break;
        } 
        if (gs != null) {
          return gs;
        }
        return Undefined.instance;

      
      case -1:
        arg = (args.length < 1) ? Undefined.instance : args[0];
        scriptable2 = ensureScriptable(arg);
        return scriptable2.getPrototype();

      
      case -2:
        arg = (args.length < 1) ? Undefined.instance : args[0];
        scriptable2 = ensureScriptable(arg);
        ids = scriptable2.getIds();
        for (i = 0; i < ids.length; i++) {
          ids[i] = ScriptRuntime.toString(ids[i]);
        }
        return cx.newArray(scope, ids);

      
      case -3:
        arg = (args.length < 1) ? Undefined.instance : args[0];
        scriptableObject1 = ensureScriptableObject(arg);
        ids = scriptableObject1.getAllIds();
        for (i = 0; i < ids.length; i++) {
          ids[i] = ScriptRuntime.toString(ids[i]);
        }
        return cx.newArray(scope, ids);

      
      case -4:
        arg = (args.length < 1) ? Undefined.instance : args[0];


        
        scriptableObject1 = ensureScriptableObject(arg);
        nameArg = (args.length < 2) ? Undefined.instance : args[1];
        str1 = ScriptRuntime.toString(nameArg);
        scriptable3 = scriptableObject1.getOwnPropertyDescriptor(cx, str1);
        return (scriptable3 == null) ? Undefined.instance : scriptable3;

      
      case -5:
        arg = (args.length < 1) ? Undefined.instance : args[0];
        scriptableObject1 = ensureScriptableObject(arg);
        object2 = (args.length < 2) ? Undefined.instance : args[1];
        descArg = (args.length < 3) ? Undefined.instance : args[2];
        desc = ensureScriptableObject(descArg);
        scriptableObject1.defineOwnProperty(cx, object2, desc);
        return scriptableObject1;

      
      case -6:
        arg = (args.length < 1) ? Undefined.instance : args[0];
        scriptableObject1 = ensureScriptableObject(arg);
        return Boolean.valueOf(scriptableObject1.isExtensible());

      
      case -7:
        arg = (args.length < 1) ? Undefined.instance : args[0];
        scriptableObject1 = ensureScriptableObject(arg);
        scriptableObject1.preventExtensions();
        return scriptableObject1;

      
      case -8:
        arg = (args.length < 1) ? Undefined.instance : args[0];
        scriptableObject1 = ensureScriptableObject(arg);
        propsObj = (args.length < 2) ? Undefined.instance : args[1];
        props = Context.toObject(propsObj, getParentScope());
        scriptableObject1.defineOwnProperties(cx, ensureScriptableObject(props));
        return scriptableObject1;

      
      case -9:
        arg = (args.length < 1) ? Undefined.instance : args[0];
        scriptable1 = (arg == null) ? null : ensureScriptable(arg);
        
        newObject = new NativeObject();
        newObject.setParentScope(getParentScope());
        newObject.setPrototype(scriptable1);
        
        if (args.length > 1 && args[1] != Undefined.instance) {
          props = Context.toObject(args[1], getParentScope());
          newObject.defineOwnProperties(cx, ensureScriptableObject(props));
        } 
        
        return newObject;

      
      case -10:
        arg = (args.length < 1) ? Undefined.instance : args[0];
        obj = ensureScriptableObject(arg);
        
        if (obj.isExtensible()) return Boolean.FALSE;
        
        for (Object object3 : obj.getAllIds()) {
          Object configurable = obj.getOwnPropertyDescriptor(cx, object3).get("configurable");
          if (Boolean.TRUE.equals(configurable)) {
            return Boolean.FALSE;
          }
        } 
        return Boolean.TRUE;

      
      case -11:
        arg = (args.length < 1) ? Undefined.instance : args[0];
        obj = ensureScriptableObject(arg);
        
        if (obj.isExtensible()) return Boolean.FALSE;
        
        for (Object object : obj.getAllIds()) {
          ScriptableObject scriptableObject = obj.getOwnPropertyDescriptor(cx, object);
          if (Boolean.TRUE.equals(scriptableObject.get("configurable")))
            return Boolean.FALSE; 
          if (isDataDescriptor(scriptableObject) && Boolean.TRUE.equals(scriptableObject.get("writable"))) {
            return Boolean.FALSE;
          }
        } 
        return Boolean.TRUE;

      
      case -12:
        arg = (args.length < 1) ? Undefined.instance : args[0];
        obj = ensureScriptableObject(arg);
        
        for (Object object : obj.getAllIds()) {
          ScriptableObject scriptableObject = obj.getOwnPropertyDescriptor(cx, object);
          if (Boolean.TRUE.equals(scriptableObject.get("configurable"))) {
            scriptableObject.put("configurable", scriptableObject, Boolean.FALSE);
            obj.defineOwnProperty(cx, object, scriptableObject, false);
          } 
        } 
        obj.preventExtensions();
        
        return obj;

      
      case -13:
        arg = (args.length < 1) ? Undefined.instance : args[0];
        obj = ensureScriptableObject(arg);
        
        for (Object object : obj.getAllIds()) {
          ScriptableObject scriptableObject = obj.getOwnPropertyDescriptor(cx, object);
          if (isDataDescriptor(scriptableObject) && Boolean.TRUE.equals(scriptableObject.get("writable")))
            scriptableObject.put("writable", scriptableObject, Boolean.FALSE); 
          if (Boolean.TRUE.equals(scriptableObject.get("configurable")))
            scriptableObject.put("configurable", scriptableObject, Boolean.FALSE); 
          obj.defineOwnProperty(cx, object, scriptableObject, false);
        } 
        obj.preventExtensions();
        
        return obj;
    } 


    
    throw new IllegalArgumentException(String.valueOf(id)); }




  
  public boolean containsKey(Object key) {
    if (key instanceof String)
      return has((String)key, this); 
    if (key instanceof Number) {
      return has(((Number)key).intValue(), this);
    }
    return false;
  }
  
  public boolean containsValue(Object value) {
    for (Object obj : values()) {
      if (value == obj || (value != null && value.equals(obj)))
      {
        return true;
      }
    } 
    return false;
  }
  
  public Object remove(Object key) {
    Object value = get(key);
    if (key instanceof String) {
      delete((String)key);
    } else if (key instanceof Number) {
      delete(((Number)key).intValue());
    } 
    return value;
  }

  
  public Set<Object> keySet() {
    return new KeySet();
  }
  
  public Collection<Object> values() {
    return new ValueCollection();
  }
  
  public Set<Map.Entry<Object, Object>> entrySet() {
    return new EntrySet();
  }
  
  public Object put(Object key, Object value) {
    throw new UnsupportedOperationException();
  }
  
  public void putAll(Map m) {
    throw new UnsupportedOperationException();
  }
  
  public void clear() {
    throw new UnsupportedOperationException();
  }
  
  class EntrySet
    extends AbstractSet<Map.Entry<Object, Object>>
  {
    public Iterator<Map.Entry<Object, Object>> iterator() {
      return new Iterator<Map.Entry<Object, Object>>() {
          Object[] ids = NativeObject.this.getIds();
          Object key = null;
          int index = 0;
          
          public boolean hasNext() {
            return (this.index < this.ids.length);
          }
          
          public Map.Entry<Object, Object> next() {
            final Object ekey = this.key = this.ids[this.index++];
            final Object value = NativeObject.this.get(this.key);
            return new Map.Entry<Object, Object>() {
                public Object getKey() {
                  return ekey;
                }
                
                public Object getValue() {
                  return value;
                }
                
                public Object setValue(Object value) {
                  throw new UnsupportedOperationException();
                }

                
                public boolean equals(Object other) {
                  if (!(other instanceof Map.Entry)) {
                    return false;
                  }
                  Map.Entry<?, ?> e = (Map.Entry<?, ?>)other;
                  return (((ekey == null) ? (e.getKey() == null) : ekey.equals(e.getKey())) && ((value == null) ? (e.getValue() == null) : value.equals(e.getValue())));
                }


                
                public int hashCode() {
                  return ((ekey == null) ? 0 : ekey.hashCode()) ^ ((value == null) ? 0 : value.hashCode());
                }


                
                public String toString() {
                  return ekey + "=" + value;
                }
              };
          }
          
          public void remove() {
            if (this.key == null) {
              throw new IllegalStateException();
            }
            NativeObject.this.remove(this.key);
            this.key = null;
          }
        };
    }

    
    public int size() {
      return NativeObject.this.size();
    }
  }
  
  class KeySet
    extends AbstractSet<Object>
  {
    public boolean contains(Object key) {
      return NativeObject.this.containsKey(key);
    }

    
    public Iterator<Object> iterator() {
      return new Iterator() {
          Object[] ids = NativeObject.this.getIds();
          Object key;
          int index = 0;
          
          public boolean hasNext() {
            return (this.index < this.ids.length);
          }
          
          public Object next() {
            try {
              return this.key = this.ids[this.index++];
            } catch (ArrayIndexOutOfBoundsException e) {
              this.key = null;
              throw new NoSuchElementException();
            } 
          }
          
          public void remove() {
            if (this.key == null) {
              throw new IllegalStateException();
            }
            NativeObject.this.remove(this.key);
            this.key = null;
          }
        };
    }

    
    public int size() {
      return NativeObject.this.size();
    }
  }
  
  class ValueCollection
    extends AbstractCollection<Object>
  {
    public Iterator<Object> iterator() {
      return new Iterator() {
          Object[] ids = NativeObject.this.getIds();
          Object key;
          int index = 0;
          
          public boolean hasNext() {
            return (this.index < this.ids.length);
          }
          
          public Object next() {
            return NativeObject.this.get(this.key = this.ids[this.index++]);
          }
          
          public void remove() {
            if (this.key == null) {
              throw new IllegalStateException();
            }
            NativeObject.this.remove(this.key);
            this.key = null;
          }
        };
    }

    
    public int size() {
      return NativeObject.this.size();
    }
  }







  
  protected int findPrototypeId(String s) {
    int c, id = 0; String X = null;
    switch (s.length()) { case 7:
        X = "valueOf"; id = 4; break;
      case 8: c = s.charAt(3);
        if (c == 111) { X = "toSource"; id = 8; break; }
         if (c == 116) { X = "toString"; id = 2; }  break;
      case 11:
        X = "constructor"; id = 1; break;
      case 13: X = "isPrototypeOf"; id = 7; break;
      case 14: c = s.charAt(0);
        if (c == 104) { X = "hasOwnProperty"; id = 5; break; }
         if (c == 116) { X = "toLocaleString"; id = 3; }  break;
      case 16:
        c = s.charAt(2);
        if (c == 100) {
          c = s.charAt(8);
          if (c == 71) { X = "__defineGetter__"; id = 9; break; }
           if (c == 83) { X = "__defineSetter__"; id = 10; }
           break;
        }  if (c == 108) {
          c = s.charAt(8);
          if (c == 71) { X = "__lookupGetter__"; id = 11; break; }
           if (c == 83) { X = "__lookupSetter__"; id = 12; } 
        }  break;
      case 20:
        X = "propertyIsEnumerable"; id = 6; break; }
    
    if (X != null && X != s && !X.equals(s)) id = 0;


    
    return id;
  }
}
