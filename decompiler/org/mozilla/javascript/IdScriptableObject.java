package org.mozilla.javascript;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;



























public abstract class IdScriptableObject
  extends ScriptableObject
  implements IdFunctionCall
{
  private transient PrototypeValues prototypeValues;
  
  private static final class PrototypeValues
    implements Serializable
  {
    static final long serialVersionUID = 3038645279153854371L;
    private static final int NAME_SLOT = 1;
    private static final int SLOT_SPAN = 2;
    private IdScriptableObject obj;
    private int maxId;
    private Object[] valueArray;
    private short[] attributeArray;
    int constructorId;
    private IdFunctionObject constructor;
    private short constructorAttrs;
    
    PrototypeValues(IdScriptableObject obj, int maxId) {
      if (obj == null) throw new IllegalArgumentException(); 
      if (maxId < 1) throw new IllegalArgumentException(); 
      this.obj = obj;
      this.maxId = maxId;
    }

    
    final int getMaxId() {
      return this.maxId;
    }

    
    final void initValue(int id, String name, Object value, int attributes) {
      if (1 > id || id > this.maxId)
        throw new IllegalArgumentException(); 
      if (name == null)
        throw new IllegalArgumentException(); 
      if (value == Scriptable.NOT_FOUND)
        throw new IllegalArgumentException(); 
      ScriptableObject.checkValidAttributes(attributes);
      if (this.obj.findPrototypeId(name) != id) {
        throw new IllegalArgumentException(name);
      }
      if (id == this.constructorId) {
        if (!(value instanceof IdFunctionObject)) {
          throw new IllegalArgumentException("consructor should be initialized with IdFunctionObject");
        }
        this.constructor = (IdFunctionObject)value;
        this.constructorAttrs = (short)attributes;
        
        return;
      } 
      initSlot(id, name, value, attributes);
    }


    
    private void initSlot(int id, String name, Object value, int attributes) {
      Object[] array = this.valueArray;
      if (array == null) {
        throw new IllegalStateException();
      }
      if (value == null) {
        value = UniqueTag.NULL_VALUE;
      }
      int index = (id - 1) * 2;
      synchronized (this) {
        Object value2 = array[index];
        if (value2 == null) {
          array[index] = value;
          array[index + 1] = name;
          this.attributeArray[id - 1] = (short)attributes;
        }
        else if (!name.equals(array[index + 1])) {
          throw new IllegalStateException();
        } 
      } 
    }

    
    final IdFunctionObject createPrecachedConstructor() {
      if (this.constructorId != 0) throw new IllegalStateException(); 
      this.constructorId = this.obj.findPrototypeId("constructor");
      if (this.constructorId == 0) {
        throw new IllegalStateException("No id for constructor property");
      }
      
      this.obj.initPrototypeId(this.constructorId);
      if (this.constructor == null) {
        throw new IllegalStateException(this.obj.getClass().getName() + ".initPrototypeId() did not " + "initialize id=" + this.constructorId);
      }

      
      this.constructor.initFunction(this.obj.getClassName(), ScriptableObject.getTopLevelScope(this.obj));
      
      this.constructor.markAsConstructor(this.obj);
      return this.constructor;
    }

    
    final int findId(String name) {
      return this.obj.findPrototypeId(name);
    }

    
    final boolean has(int id) {
      Object[] array = this.valueArray;
      if (array == null)
      {
        return true;
      }
      int valueSlot = (id - 1) * 2;
      Object value = array[valueSlot];
      if (value == null)
      {
        return true;
      }
      return (value != Scriptable.NOT_FOUND);
    }

    
    final Object get(int id) {
      Object value = ensureId(id);
      if (value == UniqueTag.NULL_VALUE) {
        value = null;
      }
      return value;
    }

    
    final void set(int id, Scriptable start, Object value) {
      if (value == Scriptable.NOT_FOUND) throw new IllegalArgumentException(); 
      ensureId(id);
      int attr = this.attributeArray[id - 1];
      if ((attr & 0x1) == 0) {
        if (start == this.obj) {
          if (value == null) {
            value = UniqueTag.NULL_VALUE;
          }
          int valueSlot = (id - 1) * 2;
          synchronized (this) {
            this.valueArray[valueSlot] = value;
          } 
        } else {
          
          int nameSlot = (id - 1) * 2 + 1;
          String name = (String)this.valueArray[nameSlot];
          start.put(name, start, value);
        } 
      }
    }

    
    final void delete(int id) {
      ensureId(id);
      int attr = this.attributeArray[id - 1];
      if ((attr & 0x4) == 0) {
        int valueSlot = (id - 1) * 2;
        synchronized (this) {
          this.valueArray[valueSlot] = Scriptable.NOT_FOUND;
          this.attributeArray[id - 1] = 0;
        } 
      } 
    }

    
    final int getAttributes(int id) {
      ensureId(id);
      return this.attributeArray[id - 1];
    }

    
    final void setAttributes(int id, int attributes) {
      ScriptableObject.checkValidAttributes(attributes);
      ensureId(id);
      synchronized (this) {
        this.attributeArray[id - 1] = (short)attributes;
      } 
    }

    
    final Object[] getNames(boolean getAll, Object[] extraEntries) {
      Object[] names = null;
      int count = 0;
      for (int id = 1; id <= this.maxId; id++) {
        Object value = ensureId(id);
        if ((getAll || (this.attributeArray[id - 1] & 0x2) == 0) && 
          value != Scriptable.NOT_FOUND) {
          int nameSlot = (id - 1) * 2 + 1;
          String name = (String)this.valueArray[nameSlot];
          if (names == null) {
            names = new Object[this.maxId];
          }
          names[count++] = name;
        } 
      } 
      
      if (count == 0)
        return extraEntries; 
      if (extraEntries == null || extraEntries.length == 0) {
        if (count != names.length) {
          Object[] arrayOfObject = new Object[count];
          System.arraycopy(names, 0, arrayOfObject, 0, count);
          names = arrayOfObject;
        } 
        return names;
      } 
      int extra = extraEntries.length;
      Object[] tmp = new Object[extra + count];
      System.arraycopy(extraEntries, 0, tmp, 0, extra);
      System.arraycopy(names, 0, tmp, extra, count);
      return tmp;
    }


    
    private Object ensureId(int id) {
      Object[] array = this.valueArray;
      if (array == null) {
        synchronized (this) {
          array = this.valueArray;
          if (array == null) {
            array = new Object[this.maxId * 2];
            this.valueArray = array;
            this.attributeArray = new short[this.maxId];
          } 
        } 
      }
      int valueSlot = (id - 1) * 2;
      Object value = array[valueSlot];
      if (value == null) {
        if (id == this.constructorId) {
          initSlot(this.constructorId, "constructor", this.constructor, this.constructorAttrs);
          
          this.constructor = null;
        } else {
          this.obj.initPrototypeId(id);
        } 
        value = array[valueSlot];
        if (value == null) {
          throw new IllegalStateException(this.obj.getClass().getName() + ".initPrototypeId(int id) " + "did not initialize id=" + id);
        }
      } 

      
      return value;
    }
  }


  
  public IdScriptableObject() {}

  
  public IdScriptableObject(Scriptable scope, Scriptable prototype) {
    super(scope, prototype);
  }

  
  protected final boolean defaultHas(String name) {
    return super.has(name, this);
  }

  
  protected final Object defaultGet(String name) {
    return super.get(name, this);
  }

  
  protected final void defaultPut(String name, Object value) {
    super.put(name, this, value);
  }


  
  public boolean has(String name, Scriptable start) {
    int info = findInstanceIdInfo(name);
    if (info != 0) {
      int attr = info >>> 16;
      if ((attr & 0x4) != 0) {
        return true;
      }
      int id = info & 0xFFFF;
      return (NOT_FOUND != getInstanceIdValue(id));
    } 
    if (this.prototypeValues != null) {
      int id = this.prototypeValues.findId(name);
      if (id != 0) {
        return this.prototypeValues.has(id);
      }
    } 
    return super.has(name, start);
  }




  
  public Object get(String name, Scriptable start) {
    Object value = super.get(name, start);
    if (value != NOT_FOUND) {
      return value;
    }
    int info = findInstanceIdInfo(name);
    if (info != 0) {
      int id = info & 0xFFFF;
      value = getInstanceIdValue(id);
      if (value != NOT_FOUND) return value; 
    } 
    if (this.prototypeValues != null) {
      int id = this.prototypeValues.findId(name);
      if (id != 0) {
        value = this.prototypeValues.get(id);
        if (value != NOT_FOUND) return value; 
      } 
    } 
    return NOT_FOUND;
  }


  
  public void put(String name, Scriptable start, Object value) {
    int info = findInstanceIdInfo(name);
    if (info != 0) {
      if (start == this && isSealed()) {
        throw Context.reportRuntimeError1("msg.modify.sealed", name);
      }
      
      int attr = info >>> 16;
      if ((attr & 0x1) == 0) {
        if (start == this) {
          int id = info & 0xFFFF;
          setInstanceIdValue(id, value);
        } else {
          
          start.put(name, start, value);
        } 
      }
      return;
    } 
    if (this.prototypeValues != null) {
      int id = this.prototypeValues.findId(name);
      if (id != 0) {
        if (start == this && isSealed()) {
          throw Context.reportRuntimeError1("msg.modify.sealed", name);
        }
        
        this.prototypeValues.set(id, start, value);
        return;
      } 
    } 
    super.put(name, start, value);
  }


  
  public void delete(String name) {
    int info = findInstanceIdInfo(name);
    if (info != 0)
    {
      if (!isSealed()) {
        int attr = info >>> 16;
        if ((attr & 0x4) == 0) {
          int id = info & 0xFFFF;
          setInstanceIdValue(id, NOT_FOUND);
        } 
        return;
      } 
    }
    if (this.prototypeValues != null) {
      int id = this.prototypeValues.findId(name);
      if (id != 0) {
        if (!isSealed()) {
          this.prototypeValues.delete(id);
        }
        return;
      } 
    } 
    super.delete(name);
  }


  
  public int getAttributes(String name) {
    int info = findInstanceIdInfo(name);
    if (info != 0) {
      int attr = info >>> 16;
      return attr;
    } 
    if (this.prototypeValues != null) {
      int id = this.prototypeValues.findId(name);
      if (id != 0) {
        return this.prototypeValues.getAttributes(id);
      }
    } 
    return super.getAttributes(name);
  }


  
  public void setAttributes(String name, int attributes) {
    ScriptableObject.checkValidAttributes(attributes);
    int info = findInstanceIdInfo(name);
    if (info != 0) {
      int id = info & 0xFFFF;
      int currentAttributes = info >>> 16;
      if (attributes != currentAttributes) {
        setInstanceIdAttributes(id, attributes);
      }
      return;
    } 
    if (this.prototypeValues != null) {
      int id = this.prototypeValues.findId(name);
      if (id != 0) {
        this.prototypeValues.setAttributes(id, attributes);
        return;
      } 
    } 
    super.setAttributes(name, attributes);
  }


  
  Object[] getIds(boolean getAll) {
    Object[] result = super.getIds(getAll);
    
    if (this.prototypeValues != null) {
      result = this.prototypeValues.getNames(getAll, result);
    }
    
    int maxInstanceId = getMaxInstanceId();
    if (maxInstanceId != 0) {
      Object[] ids = null;
      int count = 0;
      
      for (int id = maxInstanceId; id != 0; id--) {
        String name = getInstanceIdName(id);
        int info = findInstanceIdInfo(name);
        if (info != 0) {
          int attr = info >>> 16;
          if ((attr & 0x4) != 0 || 
            NOT_FOUND != getInstanceIdValue(id))
          {

            
            if (getAll || (attr & 0x2) == 0) {
              if (count == 0)
              {
                ids = new Object[id];
              }
              ids[count++] = name;
            }  } 
        } 
      } 
      if (count != 0) {
        if (result.length == 0 && ids.length == count) {
          result = ids;
        } else {
          
          Object[] tmp = new Object[result.length + count];
          System.arraycopy(result, 0, tmp, 0, result.length);
          System.arraycopy(ids, 0, tmp, result.length, count);
          result = tmp;
        } 
      }
    } 
    return result;
  }




  
  protected int getMaxInstanceId() {
    return 0;
  }

  
  protected static int instanceIdInfo(int attributes, int id) {
    return attributes << 16 | id;
  }






  
  protected int findInstanceIdInfo(String name) {
    return 0;
  }



  
  protected String getInstanceIdName(int id) {
    throw new IllegalArgumentException(String.valueOf(id));
  }







  
  protected Object getInstanceIdValue(int id) {
    throw new IllegalStateException(String.valueOf(id));
  }





  
  protected void setInstanceIdValue(int id, Object value) {
    throw new IllegalStateException(String.valueOf(id));
  }








  
  protected void setInstanceIdAttributes(int id, int attr) {
    throw ScriptRuntime.constructError("InternalError", "Changing attributes not supported for " + getClassName() + " " + getInstanceIdName(id) + " property");
  }






  
  public Object execIdCall(IdFunctionObject f, Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
    throw f.unknown();
  }




  
  public final IdFunctionObject exportAsJSClass(int maxPrototypeId, Scriptable scope, boolean sealed) {
    if (scope != this && scope != null) {
      setParentScope(scope);
      setPrototype(getObjectPrototype(scope));
    } 
    
    activatePrototypeMap(maxPrototypeId);
    IdFunctionObject ctor = this.prototypeValues.createPrecachedConstructor();
    if (sealed) {
      sealObject();
    }
    fillConstructorProperties(ctor);
    if (sealed) {
      ctor.sealObject();
    }
    ctor.exportAsScopeProperty();
    return ctor;
  }

  
  public final boolean hasPrototypeMap() {
    return (this.prototypeValues != null);
  }

  
  public final void activatePrototypeMap(int maxPrototypeId) {
    PrototypeValues values = new PrototypeValues(this, maxPrototypeId);
    synchronized (this) {
      if (this.prototypeValues != null)
        throw new IllegalStateException(); 
      this.prototypeValues = values;
    } 
  }


  
  public final void initPrototypeMethod(Object tag, int id, String name, int arity) {
    Scriptable scope = ScriptableObject.getTopLevelScope(this);
    IdFunctionObject f = newIdFunction(tag, id, name, arity, scope);
    this.prototypeValues.initValue(id, name, f, 2);
  }

  
  public final void initPrototypeConstructor(IdFunctionObject f) {
    int id = this.prototypeValues.constructorId;
    if (id == 0)
      throw new IllegalStateException(); 
    if (f.methodId() != id)
      throw new IllegalArgumentException(); 
    if (isSealed()) f.sealObject(); 
    this.prototypeValues.initValue(id, "constructor", f, 2);
  }


  
  public final void initPrototypeValue(int id, String name, Object value, int attributes) {
    this.prototypeValues.initValue(id, name, value, attributes);
  }

  
  protected void initPrototypeId(int id) {
    throw new IllegalStateException(String.valueOf(id));
  }

  
  protected int findPrototypeId(String name) {
    throw new IllegalStateException(name);
  }


  
  protected void fillConstructorProperties(IdFunctionObject ctor) {}


  
  protected void addIdFunctionProperty(Scriptable obj, Object tag, int id, String name, int arity) {
    Scriptable scope = ScriptableObject.getTopLevelScope(obj);
    IdFunctionObject f = newIdFunction(tag, id, name, arity, scope);
    f.addAsProperty(obj);
  }






















  
  protected static EcmaError incompatibleCallError(IdFunctionObject f) {
    throw ScriptRuntime.typeError1("msg.incompat.call", f.getFunctionName());
  }



  
  private IdFunctionObject newIdFunction(Object tag, int id, String name, int arity, Scriptable scope) {
    IdFunctionObject f = new IdFunctionObject(this, tag, id, name, arity, scope);
    
    if (isSealed()) f.sealObject(); 
    return f;
  }

  
  public void defineOwnProperty(Context cx, Object key, ScriptableObject desc) {
    if (key instanceof String) {
      String name = (String)key;
      int info = findInstanceIdInfo(name);
      if (info != 0) {
        int id = info & 0xFFFF;
        if (isAccessorDescriptor(desc)) {
          delete(id);
        } else {
          checkPropertyDefinition(desc);
          ScriptableObject current = getOwnPropertyDescriptor(cx, key);
          checkPropertyChange(name, current, desc);
          int attr = info >>> 16;
          Object value = getProperty(desc, "value");
          if (value != NOT_FOUND && (attr & 0x1) == 0) {
            Object currentValue = getInstanceIdValue(id);
            if (!sameValue(value, currentValue)) {
              setInstanceIdValue(id, value);
            }
          } 
          setAttributes(name, applyDescriptorToAttributeBitset(attr, desc));
          return;
        } 
      } 
      if (this.prototypeValues != null) {
        int id = this.prototypeValues.findId(name);
        if (id != 0) {
          if (isAccessorDescriptor(desc)) {
            this.prototypeValues.delete(id);
          } else {
            checkPropertyDefinition(desc);
            ScriptableObject current = getOwnPropertyDescriptor(cx, key);
            checkPropertyChange(name, current, desc);
            int attr = this.prototypeValues.getAttributes(id);
            Object value = getProperty(desc, "value");
            if (value != NOT_FOUND && (attr & 0x1) == 0) {
              Object currentValue = this.prototypeValues.get(id);
              if (!sameValue(value, currentValue)) {
                this.prototypeValues.set(id, this, value);
              }
            } 
            this.prototypeValues.setAttributes(id, applyDescriptorToAttributeBitset(attr, desc));
            return;
          } 
        }
      } 
    } 
    super.defineOwnProperty(cx, key, desc);
  }


  
  protected ScriptableObject getOwnPropertyDescriptor(Context cx, Object id) {
    ScriptableObject desc = super.getOwnPropertyDescriptor(cx, id);
    if (desc == null && id instanceof String) {
      desc = getBuiltInDescriptor((String)id);
    }
    return desc;
  }
  
  private ScriptableObject getBuiltInDescriptor(String name) {
    Object value = null;
    int attr = 0;
    
    Scriptable scope = getParentScope();
    if (scope == null) {
      scope = this;
    }
    
    int info = findInstanceIdInfo(name);
    if (info != 0) {
      int id = info & 0xFFFF;
      value = getInstanceIdValue(id);
      attr = info >>> 16;
      return buildDataDescriptor(scope, value, attr);
    } 
    if (this.prototypeValues != null) {
      int id = this.prototypeValues.findId(name);
      if (id != 0) {
        value = this.prototypeValues.get(id);
        attr = this.prototypeValues.getAttributes(id);
        return buildDataDescriptor(scope, value, attr);
      } 
    } 
    return null;
  }


  
  private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
    stream.defaultReadObject();
    int maxPrototypeId = stream.readInt();
    if (maxPrototypeId != 0) {
      activatePrototypeMap(maxPrototypeId);
    }
  }


  
  private void writeObject(ObjectOutputStream stream) throws IOException {
    stream.defaultWriteObject();
    int maxPrototypeId = 0;
    if (this.prototypeValues != null) {
      maxPrototypeId = this.prototypeValues.getMaxId();
    }
    stream.writeInt(maxPrototypeId);
  }
}
