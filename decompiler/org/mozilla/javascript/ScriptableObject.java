package org.mozilla.javascript;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import org.mozilla.javascript.annotations.JSConstructor;
import org.mozilla.javascript.annotations.JSFunction;
import org.mozilla.javascript.annotations.JSGetter;
import org.mozilla.javascript.annotations.JSSetter;
import org.mozilla.javascript.annotations.JSStaticFunction;
import org.mozilla.javascript.debug.DebuggableObject;


























































































public abstract class ScriptableObject
  implements Scriptable, Serializable, DebuggableObject, ConstProperties
{
  static final long serialVersionUID = 2829861078851942586L;
  public static final int EMPTY = 0;
  public static final int READONLY = 1;
  public static final int DONTENUM = 2;
  public static final int PERMANENT = 4;
  public static final int UNINITIALIZED_CONST = 8;
  public static final int CONST = 13;
  private Scriptable prototypeObject;
  private Scriptable parentScopeObject;
  private transient Slot[] slots;
  private int count;
  private transient ExternalArrayData externalData;
  private transient Slot firstAdded;
  private transient Slot lastAdded;
  private volatile Map<Object, Object> associatedValues;
  private static final int SLOT_QUERY = 1;
  private static final int SLOT_MODIFY = 2;
  private static final int SLOT_MODIFY_CONST = 3;
  private static final int SLOT_MODIFY_GETTER_SETTER = 4;
  private static final int SLOT_CONVERT_ACCESSOR_TO_DATA = 5;
  private static final int INITIAL_SLOT_SIZE = 4;
  private boolean isExtensible = true;
  private static final Method GET_ARRAY_LENGTH;
  
  static {
    try {
      GET_ARRAY_LENGTH = ScriptableObject.class.getMethod("getExternalArrayLength", new Class[0]);
    } catch (NoSuchMethodException nsm) {
      throw new RuntimeException(nsm);
    } 
  }
  
  private static class Slot
    implements Serializable
  {
    private static final long serialVersionUID = -6090581677123995491L;
    String name;
    int indexOrHash;
    private volatile short attributes;
    volatile transient boolean wasDeleted;
    volatile Object value;
    transient Slot next;
    volatile transient Slot orderedNext;
    
    Slot(String name, int indexOrHash, int attributes) {
      this.name = name;
      this.indexOrHash = indexOrHash;
      this.attributes = (short)attributes;
    }


    
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
      in.defaultReadObject();
      if (this.name != null) {
        this.indexOrHash = this.name.hashCode();
      }
    }
    
    boolean setValue(Object value, Scriptable owner, Scriptable start) {
      if ((this.attributes & 0x1) != 0) {
        return true;
      }
      if (owner == start) {
        this.value = value;
        return true;
      } 
      return false;
    }

    
    Object getValue(Scriptable start) {
      return this.value;
    }

    
    int getAttributes() {
      return this.attributes;
    }

    
    synchronized void setAttributes(int value) {
      ScriptableObject.checkValidAttributes(value);
      this.attributes = (short)value;
    }
    
    void markDeleted() {
      this.wasDeleted = true;
      this.value = null;
      this.name = null;
    }
    
    ScriptableObject getPropertyDescriptor(Context cx, Scriptable scope) {
      return ScriptableObject.buildDataDescriptor(scope, this.value, this.attributes);
    }
  }



  
  protected static ScriptableObject buildDataDescriptor(Scriptable scope, Object value, int attributes) {
    ScriptableObject desc = new NativeObject();
    ScriptRuntime.setBuiltinProtoAndParent(desc, scope, TopLevel.Builtins.Object);
    desc.defineProperty("value", value, 0);
    desc.defineProperty("writable", Boolean.valueOf(((attributes & 0x1) == 0)), 0);
    desc.defineProperty("enumerable", Boolean.valueOf(((attributes & 0x2) == 0)), 0);
    desc.defineProperty("configurable", Boolean.valueOf(((attributes & 0x4) == 0)), 0);
    return desc;
  }

  
  private static final class GetterSlot
    extends Slot
  {
    static final long serialVersionUID = -4900574849788797588L;
    Object getter;
    Object setter;
    
    GetterSlot(String name, int indexOrHash, int attributes) {
      super(name, indexOrHash, attributes);
    }

    
    ScriptableObject getPropertyDescriptor(Context cx, Scriptable scope) {
      int attr = getAttributes();
      ScriptableObject desc = new NativeObject();
      ScriptRuntime.setBuiltinProtoAndParent(desc, scope, TopLevel.Builtins.Object);
      desc.defineProperty("enumerable", Boolean.valueOf(((attr & 0x2) == 0)), 0);
      desc.defineProperty("configurable", Boolean.valueOf(((attr & 0x4) == 0)), 0);
      if (this.getter != null) desc.defineProperty("get", this.getter, 0); 
      if (this.setter != null) desc.defineProperty("set", this.setter, 0); 
      return desc;
    }

    
    boolean setValue(Object value, Scriptable owner, Scriptable start) {
      if (this.setter == null) {
        if (this.getter != null) {
          if (Context.getContext().hasFeature(11))
          {
            
            throw ScriptRuntime.typeError1("msg.set.prop.no.setter", this.name);
          }

          
          return true;
        } 
      } else {
        Context cx = Context.getContext();
        if (this.setter instanceof MemberBox) {
          Object setterThis, args[]; MemberBox nativeSetter = (MemberBox)this.setter;
          Class<?>[] pTypes = nativeSetter.argTypes;

          
          Class<?> valueType = pTypes[pTypes.length - 1];
          int tag = FunctionObject.getTypeTag(valueType);
          Object actualArg = FunctionObject.convertArg(cx, start, value, tag);


          
          if (nativeSetter.delegateTo == null) {
            setterThis = start;
            args = new Object[] { actualArg };
          } else {
            setterThis = nativeSetter.delegateTo;
            args = new Object[] { start, actualArg };
          } 
          nativeSetter.invoke(setterThis, args);
        } else if (this.setter instanceof Function) {
          Function f = (Function)this.setter;
          f.call(cx, f.getParentScope(), start, new Object[] { value });
        } 
        
        return true;
      } 
      return super.setValue(value, owner, start);
    }

    
    Object getValue(Scriptable start) {
      if (this.getter != null) {
        if (this.getter instanceof MemberBox) {
          Object getterThis, args[]; MemberBox nativeGetter = (MemberBox)this.getter;

          
          if (nativeGetter.delegateTo == null) {
            getterThis = start;
            args = ScriptRuntime.emptyArgs;
          } else {
            getterThis = nativeGetter.delegateTo;
            args = new Object[] { start };
          } 
          return nativeGetter.invoke(getterThis, args);
        }  if (this.getter instanceof Function) {
          Function f = (Function)this.getter;
          Context cx = Context.getContext();
          return f.call(cx, f.getParentScope(), start, ScriptRuntime.emptyArgs);
        } 
      } 
      
      Object val = this.value;
      if (val instanceof LazilyLoadedCtor) {
        LazilyLoadedCtor initializer = (LazilyLoadedCtor)val;
        try {
          initializer.init();
        } finally {
          this.value = val = initializer.getValue();
        } 
      } 
      return val;
    }

    
    void markDeleted() {
      super.markDeleted();
      this.getter = null;
      this.setter = null;
    }
  }



  
  private static class RelinkedSlot
    extends Slot
  {
    final ScriptableObject.Slot slot;



    
    RelinkedSlot(ScriptableObject.Slot slot) {
      super(slot.name, slot.indexOrHash, slot.attributes);
      
      this.slot = ScriptableObject.unwrapSlot(slot);
    }

    
    boolean setValue(Object value, Scriptable owner, Scriptable start) {
      return this.slot.setValue(value, owner, start);
    }

    
    Object getValue(Scriptable start) {
      return this.slot.getValue(start);
    }

    
    ScriptableObject getPropertyDescriptor(Context cx, Scriptable scope) {
      return this.slot.getPropertyDescriptor(cx, scope);
    }

    
    int getAttributes() {
      return this.slot.getAttributes();
    }

    
    void setAttributes(int value) {
      this.slot.setAttributes(value);
    }

    
    void markDeleted() {
      super.markDeleted();
      this.slot.markDeleted();
    }
    
    private void writeObject(ObjectOutputStream out) throws IOException {
      out.writeObject(this.slot);
    }
  }


  
  static void checkValidAttributes(int attributes) {
    int mask = 15;
    if ((attributes & 0xFFFFFFF0) != 0) {
      throw new IllegalArgumentException(String.valueOf(attributes));
    }
  }





  
  public ScriptableObject(Scriptable scope, Scriptable prototype) {
    if (scope == null) {
      throw new IllegalArgumentException();
    }
    this.parentScopeObject = scope;
    this.prototypeObject = prototype;
  }





  
  public String getTypeOf() {
    return avoidObjectDetection() ? "undefined" : "object";
  }

















  
  public boolean has(String name, Scriptable start) {
    return (null != getSlot(name, 0, 1));
  }








  
  public boolean has(int index, Scriptable start) {
    if (this.externalData != null) {
      return (index < this.externalData.getArrayLength());
    }
    return (null != getSlot((String)null, index, 1));
  }











  
  public Object get(String name, Scriptable start) {
    Slot slot = getSlot(name, 0, 1);
    if (slot == null) {
      return Scriptable.NOT_FOUND;
    }
    return slot.getValue(start);
  }








  
  public Object get(int index, Scriptable start) {
    if (this.externalData != null) {
      if (index < this.externalData.getArrayLength()) {
        return this.externalData.getArrayElement(index);
      }
      return Scriptable.NOT_FOUND;
    } 
    
    Slot slot = getSlot((String)null, index, 1);
    if (slot == null) {
      return Scriptable.NOT_FOUND;
    }
    return slot.getValue(start);
  }
















  
  public void put(String name, Scriptable start, Object value) {
    if (putImpl(name, 0, start, value)) {
      return;
    }
    if (start == this) throw Kit.codeBug(); 
    start.put(name, start, value);
  }








  
  public void put(int index, Scriptable start, Object value) {
    if (this.externalData != null) {
      if (index < this.externalData.getArrayLength()) {
        this.externalData.setArrayElement(index, value);
      } else {
        throw new JavaScriptException(ScriptRuntime.newNativeError(Context.getCurrentContext(), this, TopLevel.NativeErrors.RangeError, new Object[] { "External array index out of bounds " }), null, 0);
      } 


      
      return;
    } 

    
    if (putImpl(null, index, start, value)) {
      return;
    }
    if (start == this) throw Kit.codeBug(); 
    start.put(index, start, value);
  }









  
  public void delete(String name) {
    checkNotSealed(name, 0);
    removeSlot(name, 0);
  }









  
  public void delete(int index) {
    checkNotSealed(null, index);
    removeSlot(null, index);
  }
















  
  public void putConst(String name, Scriptable start, Object value) {
    if (putConstImpl(name, 0, start, value, 1)) {
      return;
    }
    if (start == this) throw Kit.codeBug(); 
    if (start instanceof ConstProperties) {
      ((ConstProperties)start).putConst(name, start, value);
    } else {
      start.put(name, start, value);
    } 
  }
  
  public void defineConst(String name, Scriptable start) {
    if (putConstImpl(name, 0, start, Undefined.instance, 8)) {
      return;
    }
    if (start == this) throw Kit.codeBug(); 
    if (start instanceof ConstProperties) {
      ((ConstProperties)start).defineConst(name, start);
    }
  }






  
  public boolean isConst(String name) {
    Slot slot = getSlot(name, 0, 1);
    if (slot == null) {
      return false;
    }
    return ((slot.getAttributes() & 0x5) == 5);
  }







  
  @Deprecated
  public final int getAttributes(String name, Scriptable start) {
    return getAttributes(name);
  }





  
  @Deprecated
  public final int getAttributes(int index, Scriptable start) {
    return getAttributes(index);
  }






  
  @Deprecated
  public final void setAttributes(String name, Scriptable start, int attributes) {
    setAttributes(name, attributes);
  }






  
  @Deprecated
  public void setAttributes(int index, Scriptable start, int attributes) {
    setAttributes(index, attributes);
  }
















  
  public int getAttributes(String name) {
    return findAttributeSlot(name, 0, 1).getAttributes();
  }














  
  public int getAttributes(int index) {
    return findAttributeSlot(null, index, 1).getAttributes();
  }






















  
  public void setAttributes(String name, int attributes) {
    checkNotSealed(name, 0);
    findAttributeSlot(name, 0, 2).setAttributes(attributes);
  }













  
  public void setAttributes(int index, int attributes) {
    checkNotSealed(null, index);
    findAttributeSlot(null, index, 2).setAttributes(attributes);
  }





  
  public void setGetterOrSetter(String name, int index, Callable getterOrSetter, boolean isSetter) {
    setGetterOrSetter(name, index, getterOrSetter, isSetter, false);
  }

  
  private void setGetterOrSetter(String name, int index, Callable getterOrSetter, boolean isSetter, boolean force) {
    GetterSlot gslot;
    if (name != null && index != 0) {
      throw new IllegalArgumentException(name);
    }
    if (!force) {
      checkNotSealed(name, index);
    }

    
    if (isExtensible()) {
      gslot = (GetterSlot)getSlot(name, index, 4);
    } else {
      Slot slot = unwrapSlot(getSlot(name, index, 1));
      if (!(slot instanceof GetterSlot))
        return; 
      gslot = (GetterSlot)slot;
    } 
    
    if (!force) {
      int attributes = gslot.getAttributes();
      if ((attributes & 0x1) != 0) {
        throw Context.reportRuntimeError1("msg.modify.readonly", name);
      }
    } 
    if (isSetter) {
      gslot.setter = getterOrSetter;
    } else {
      gslot.getter = getterOrSetter;
    } 
    gslot.value = Undefined.instance;
  }














  
  public Object getGetterOrSetter(String name, int index, boolean isSetter) {
    if (name != null && index != 0)
      throw new IllegalArgumentException(name); 
    Slot slot = unwrapSlot(getSlot(name, index, 1));
    if (slot == null)
      return null; 
    if (slot instanceof GetterSlot) {
      GetterSlot gslot = (GetterSlot)slot;
      Object result = isSetter ? gslot.setter : gslot.getter;
      return (result != null) ? result : Undefined.instance;
    } 
    return Undefined.instance;
  }







  
  protected boolean isGetterOrSetter(String name, int index, boolean setter) {
    Slot slot = unwrapSlot(getSlot(name, index, 1));
    if (slot instanceof GetterSlot) {
      if (setter && ((GetterSlot)slot).setter != null) return true; 
      if (!setter && ((GetterSlot)slot).getter != null) return true; 
    } 
    return false;
  }


  
  void addLazilyInitializedValue(String name, int index, LazilyLoadedCtor init, int attributes) {
    if (name != null && index != 0)
      throw new IllegalArgumentException(name); 
    checkNotSealed(name, index);
    GetterSlot gslot = (GetterSlot)getSlot(name, index, 4);
    
    gslot.setAttributes(attributes);
    gslot.getter = null;
    gslot.setter = null;
    gslot.value = init;
  }











  
  public void setExternalArrayData(ExternalArrayData array) {
    this.externalData = array;
    
    if (array == null) {
      delete("length");
    } else {
      
      defineProperty("length", null, GET_ARRAY_LENGTH, null, 3);
    } 
  }








  
  public ExternalArrayData getExternalArrayData() {
    return this.externalData;
  }




  
  public Object getExternalArrayLength() {
    return Integer.valueOf((this.externalData == null) ? 0 : this.externalData.getArrayLength());
  }




  
  public Scriptable getPrototype() {
    return this.prototypeObject;
  }




  
  public void setPrototype(Scriptable m) {
    this.prototypeObject = m;
  }




  
  public Scriptable getParentScope() {
    return this.parentScopeObject;
  }




  
  public void setParentScope(Scriptable m) {
    this.parentScopeObject = m;
  }











  
  public Object[] getIds() {
    return getIds(false);
  }











  
  public Object[] getAllIds() {
    return getIds(true);
  }















  
  public Object getDefaultValue(Class<?> typeHint) {
    return getDefaultValue(this, typeHint);
  }

  
  public static Object getDefaultValue(Scriptable object, Class<?> typeHint) {
    Context cx = null;
    for (int i = 0; i < 2; i++) {
      boolean tryToString; String methodName; Object[] args;
      if (typeHint == ScriptRuntime.StringClass) {
        tryToString = (i == 0);
      } else {
        tryToString = (i == 1);
      } 


      
      if (tryToString) {
        methodName = "toString";
        args = ScriptRuntime.emptyArgs;
      } else {
        String hint; methodName = "valueOf";
        args = new Object[1];
        
        if (typeHint == null) {
          hint = "undefined";
        } else if (typeHint == ScriptRuntime.StringClass) {
          hint = "string";
        } else if (typeHint == ScriptRuntime.ScriptableClass) {
          hint = "object";
        } else if (typeHint == ScriptRuntime.FunctionClass) {
          hint = "function";
        } else if (typeHint == ScriptRuntime.BooleanClass || typeHint == boolean.class) {

          
          hint = "boolean";
        } else if (typeHint == ScriptRuntime.NumberClass || typeHint == ScriptRuntime.ByteClass || typeHint == byte.class || typeHint == ScriptRuntime.ShortClass || typeHint == short.class || typeHint == ScriptRuntime.IntegerClass || typeHint == int.class || typeHint == ScriptRuntime.FloatClass || typeHint == float.class || typeHint == ScriptRuntime.DoubleClass || typeHint == double.class) {










          
          hint = "number";
        } else {
          throw Context.reportRuntimeError1("msg.invalid.type", typeHint.toString());
        } 
        
        args[0] = hint;
      } 
      Object v = getProperty(object, methodName);
      if (v instanceof Function) {
        
        Function fun = (Function)v;
        if (cx == null)
          cx = Context.getContext(); 
        Object object1 = fun.call(cx, fun.getParentScope(), object, args);
        if (object1 != null) {
          if (!(object1 instanceof Scriptable)) {
            return object1;
          }
          if (typeHint == ScriptRuntime.ScriptableClass || typeHint == ScriptRuntime.FunctionClass)
          {
            
            return object1;
          }
          if (tryToString && object1 instanceof Wrapper) {

            
            Object u = ((Wrapper)object1).unwrap();
            if (u instanceof String)
              return u; 
          } 
        } 
      } 
    } 
    String arg = (typeHint == null) ? "undefined" : typeHint.getName();
    throw ScriptRuntime.typeError1("msg.default.value", arg);
  }














  
  public boolean hasInstance(Scriptable instance) {
    return ScriptRuntime.jsDelegatesTo(instance, this);
  }











  
  public boolean avoidObjectDetection() {
    return false;
  }














  
  protected Object equivalentValues(Object value) {
    return (this == value) ? Boolean.TRUE : Scriptable.NOT_FOUND;
  }

































































































  
  public static <T extends Scriptable> void defineClass(Scriptable scope, Class<T> clazz) throws IllegalAccessException, InstantiationException, InvocationTargetException {
    defineClass(scope, clazz, false, false);
  }



























  
  public static <T extends Scriptable> void defineClass(Scriptable scope, Class<T> clazz, boolean sealed) throws IllegalAccessException, InstantiationException, InvocationTargetException {
    defineClass(scope, clazz, sealed, false);
  }
































  
  public static <T extends Scriptable> String defineClass(Scriptable scope, Class<T> clazz, boolean sealed, boolean mapInheritance) throws IllegalAccessException, InstantiationException, InvocationTargetException {
    BaseFunction ctor = buildClassCtor(scope, clazz, sealed, mapInheritance);
    
    if (ctor == null)
      return null; 
    String name = ctor.getClassPrototype().getClassName();
    defineProperty(scope, name, ctor, 2);
    return name;
  }






  
  static <T extends Scriptable> BaseFunction buildClassCtor(Scriptable scope, Class<T> clazz, boolean sealed, boolean mapInheritance) throws IllegalAccessException, InstantiationException, InvocationTargetException {
    Method[] methods = FunctionObject.getMethodList(clazz);
    for (int i = 0; i < methods.length; i++) {
      Method method = methods[i];
      if (method.getName().equals("init")) {
        
        Class<?>[] parmTypes = method.getParameterTypes();
        if (parmTypes.length == 3 && parmTypes[0] == ScriptRuntime.ContextClass && parmTypes[1] == ScriptRuntime.ScriptableClass && parmTypes[2] == boolean.class && Modifier.isStatic(method.getModifiers())) {




          
          Object[] args = { Context.getContext(), scope, sealed ? Boolean.TRUE : Boolean.FALSE };
          
          method.invoke((Object)null, args);
          return null;
        } 
        if (parmTypes.length == 1 && parmTypes[0] == ScriptRuntime.ScriptableClass && Modifier.isStatic(method.getModifiers())) {


          
          Object[] args = { scope };
          method.invoke((Object)null, args);
          return null;
        } 
      } 
    } 



    
    Constructor[] arrayOfConstructor = (Constructor[])clazz.getConstructors();
    Constructor<?> protoCtor = null;
    for (int j = 0; j < arrayOfConstructor.length; j++) {
      if ((arrayOfConstructor[j].getParameterTypes()).length == 0) {
        protoCtor = arrayOfConstructor[j];
        break;
      } 
    } 
    if (protoCtor == null) {
      throw Context.reportRuntimeError1("msg.zero.arg.ctor", clazz.getName());
    }

    
    Scriptable proto = (Scriptable)protoCtor.newInstance(ScriptRuntime.emptyArgs);
    String className = proto.getClassName();

    
    Object existing = getProperty(getTopLevelScope(scope), className);
    if (existing instanceof BaseFunction) {
      Object existingProto = ((BaseFunction)existing).getPrototypeProperty();
      if (existingProto != null && clazz.equals(existingProto.getClass())) {
        return (BaseFunction)existing;
      }
    } 


    
    Scriptable superProto = null;
    if (mapInheritance) {
      Class<? super T> superClass = clazz.getSuperclass();
      if (ScriptRuntime.ScriptableClass.isAssignableFrom(superClass) && !Modifier.isAbstract(superClass.getModifiers())) {

        
        Class<? extends Scriptable> superScriptable = extendsScriptable(superClass);
        
        String name = defineClass(scope, superScriptable, sealed, mapInheritance);
        
        if (name != null) {
          superProto = getClassPrototype(scope, name);
        }
      } 
    } 
    if (superProto == null) {
      superProto = getObjectPrototype(scope);
    }
    proto.setPrototype(superProto);



    
    String functionPrefix = "jsFunction_";
    String staticFunctionPrefix = "jsStaticFunction_";
    String getterPrefix = "jsGet_";
    String setterPrefix = "jsSet_";
    String ctorName = "jsConstructor";
    
    Member ctorMember = findAnnotatedMember((AccessibleObject[])methods, (Class)JSConstructor.class);
    if (ctorMember == null) {
      ctorMember = findAnnotatedMember((AccessibleObject[])arrayOfConstructor, (Class)JSConstructor.class);
    }
    if (ctorMember == null) {
      ctorMember = FunctionObject.findSingleMethod(methods, "jsConstructor");
    }
    if (ctorMember == null) {
      if (arrayOfConstructor.length == 1) {
        ctorMember = arrayOfConstructor[0];
      } else if (arrayOfConstructor.length == 2) {
        if ((arrayOfConstructor[0].getParameterTypes()).length == 0) {
          ctorMember = arrayOfConstructor[1];
        } else if ((arrayOfConstructor[1].getParameterTypes()).length == 0) {
          ctorMember = arrayOfConstructor[0];
        } 
      }  if (ctorMember == null) {
        throw Context.reportRuntimeError1("msg.ctor.multiple.parms", clazz.getName());
      }
    } 

    
    FunctionObject ctor = new FunctionObject(className, ctorMember, scope);
    if (ctor.isVarArgsMethod()) {
      throw Context.reportRuntimeError1("msg.varargs.ctor", ctorMember.getName());
    }
    
    ctor.initAsConstructor(scope, proto);
    
    Method finishInit = null;
    HashSet<String> staticNames = new HashSet<String>();
    HashSet<String> instanceNames = new HashSet<String>();
    for (Method method : methods) {
      if (method == ctorMember) {
        continue;
      }
      String name = method.getName();
      if (name.equals("finishInit")) {
        Class<?>[] parmTypes = method.getParameterTypes();
        if (parmTypes.length == 3 && parmTypes[0] == ScriptRuntime.ScriptableClass && parmTypes[1] == FunctionObject.class && parmTypes[2] == ScriptRuntime.ScriptableClass && Modifier.isStatic(method.getModifiers())) {




          
          finishInit = method;
          
          continue;
        } 
      } 
      if (name.indexOf('$') != -1)
        continue; 
      if (name.equals("jsConstructor")) {
        continue;
      }
      Annotation annotation = null;
      String prefix = null;
      if (method.isAnnotationPresent((Class)JSFunction.class)) {
        annotation = method.getAnnotation(JSFunction.class);
      } else if (method.isAnnotationPresent((Class)JSStaticFunction.class)) {
        annotation = method.getAnnotation(JSStaticFunction.class);
      } else if (method.isAnnotationPresent((Class)JSGetter.class)) {
        annotation = method.getAnnotation(JSGetter.class);
      } else if (method.isAnnotationPresent((Class)JSSetter.class)) {
        continue;
      } 
      
      if (annotation == null) {
        if (name.startsWith("jsFunction_")) {
          prefix = "jsFunction_";
        } else if (name.startsWith("jsStaticFunction_")) {
          prefix = "jsStaticFunction_";
        } else if (name.startsWith("jsGet_")) {
          prefix = "jsGet_";
        } else if (annotation == null) {
          continue;
        } 
      }


      
      boolean isStatic = (annotation instanceof JSStaticFunction || prefix == "jsStaticFunction_");
      
      HashSet<String> names = isStatic ? staticNames : instanceNames;
      String propName = getPropertyName(name, prefix, annotation);
      if (names.contains(propName)) {
        throw Context.reportRuntimeError2("duplicate.defineClass.name", name, propName);
      }
      
      names.add(propName);
      name = propName;
      
      if (annotation instanceof JSGetter || prefix == "jsGet_") {
        if (!(proto instanceof ScriptableObject)) {
          throw Context.reportRuntimeError2("msg.extend.scriptable", proto.getClass().toString(), name);
        }

        
        Method setter = findSetterMethod(methods, name, "jsSet_");
        int attr = 0x6 | ((setter != null) ? 0 : 1);


        
        ((ScriptableObject)proto).defineProperty(name, null, method, setter, attr);
      
      }
      else {

        
        if (isStatic && !Modifier.isStatic(method.getModifiers())) {
          throw Context.reportRuntimeError("jsStaticFunction must be used with static method.");
        }

        
        FunctionObject f = new FunctionObject(name, method, proto);
        if (f.isVarArgsConstructor()) {
          throw Context.reportRuntimeError1("msg.varargs.fun", ctorMember.getName());
        }
        
        defineProperty(isStatic ? ctor : proto, name, f, 2);
        if (sealed) {
          f.sealObject();
        }
      } 
      continue;
    } 
    if (finishInit != null) {
      Object[] finishArgs = { scope, ctor, proto };
      finishInit.invoke((Object)null, finishArgs);
    } 

    
    if (sealed) {
      ctor.sealObject();
      if (proto instanceof ScriptableObject) {
        ((ScriptableObject)proto).sealObject();
      }
    } 
    
    return ctor;
  }

  
  private static Member findAnnotatedMember(AccessibleObject[] members, Class<? extends Annotation> annotation) {
    for (AccessibleObject member : members) {
      if (member.isAnnotationPresent(annotation)) {
        return (Member)member;
      }
    } 
    return null;
  }


  
  private static Method findSetterMethod(Method[] methods, String name, String prefix) {
    String newStyleName = "set" + Character.toUpperCase(name.charAt(0)) + name.substring(1);

    
    for (Method method : methods) {
      JSSetter annotation = method.<JSSetter>getAnnotation(JSSetter.class);
      if (annotation != null && (
        name.equals(annotation.value()) || ("".equals(annotation.value()) && newStyleName.equals(method.getName()))))
      {
        return method;
      }
    } 
    
    String oldStyleName = prefix + name;
    for (Method method : methods) {
      if (oldStyleName.equals(method.getName())) {
        return method;
      }
    } 
    return null;
  }


  
  private static String getPropertyName(String methodName, String prefix, Annotation annotation) {
    if (prefix != null) {
      return methodName.substring(prefix.length());
    }
    String propName = null;
    if (annotation instanceof JSGetter) {
      propName = ((JSGetter)annotation).value();
      if ((propName == null || propName.length() == 0) && 
        methodName.length() > 3 && methodName.startsWith("get")) {
        propName = methodName.substring(3);
        if (Character.isUpperCase(propName.charAt(0))) {
          if (propName.length() == 1) {
            propName = propName.toLowerCase();
          } else if (!Character.isUpperCase(propName.charAt(1))) {
            propName = Character.toLowerCase(propName.charAt(0)) + propName.substring(1);
          }
        
        }
      }
    
    } else if (annotation instanceof JSFunction) {
      propName = ((JSFunction)annotation).value();
    } else if (annotation instanceof JSStaticFunction) {
      propName = ((JSStaticFunction)annotation).value();
    } 
    if (propName == null || propName.length() == 0) {
      propName = methodName;
    }
    return propName;
  }


  
  private static <T extends Scriptable> Class<T> extendsScriptable(Class<?> c) {
    if (ScriptRuntime.ScriptableClass.isAssignableFrom(c))
      return (Class)c; 
    return null;
  }












  
  public void defineProperty(String propertyName, Object value, int attributes) {
    checkNotSealed(propertyName, 0);
    put(propertyName, this, value);
    setAttributes(propertyName, attributes);
  }









  
  public static void defineProperty(Scriptable destination, String propertyName, Object value, int attributes) {
    if (!(destination instanceof ScriptableObject)) {
      destination.put(propertyName, destination, value);
      return;
    } 
    ScriptableObject so = (ScriptableObject)destination;
    so.defineProperty(propertyName, value, attributes);
  }








  
  public static void defineConstProperty(Scriptable destination, String propertyName) {
    if (destination instanceof ConstProperties) {
      ConstProperties cp = (ConstProperties)destination;
      cp.defineConst(propertyName, destination);
    } else {
      defineProperty(destination, propertyName, Undefined.instance, 13);
    } 
  }



















  
  public void defineProperty(String propertyName, Class<?> clazz, int attributes) {
    int length = propertyName.length();
    if (length == 0) throw new IllegalArgumentException(); 
    char[] buf = new char[3 + length];
    propertyName.getChars(0, length, buf, 3);
    buf[3] = Character.toUpperCase(buf[3]);
    buf[0] = 'g';
    buf[1] = 'e';
    buf[2] = 't';
    String getterName = new String(buf);
    buf[0] = 's';
    String setterName = new String(buf);
    
    Method[] methods = FunctionObject.getMethodList(clazz);
    Method getter = FunctionObject.findSingleMethod(methods, getterName);
    Method setter = FunctionObject.findSingleMethod(methods, setterName);
    if (setter == null)
      attributes |= 0x1; 
    defineProperty(propertyName, null, getter, (setter == null) ? null : setter, attributes);
  }












































  
  public void defineProperty(String propertyName, Object delegateTo, Method getter, Method setter, int attributes) {
    MemberBox getterBox = null;
    if (getter != null) {
      boolean delegatedForm; getterBox = new MemberBox(getter);

      
      if (!Modifier.isStatic(getter.getModifiers())) {
        delegatedForm = (delegateTo != null);
        getterBox.delegateTo = delegateTo;
      } else {
        delegatedForm = true;

        
        getterBox.delegateTo = void.class;
      } 
      
      String errorId = null;
      Class<?>[] parmTypes = getter.getParameterTypes();
      if (parmTypes.length == 0) {
        if (delegatedForm) {
          errorId = "msg.obj.getter.parms";
        }
      } else if (parmTypes.length == 1) {
        Object<?> argType = (Object<?>)parmTypes[0];
        
        if (argType != ScriptRuntime.ScriptableClass && argType != ScriptRuntime.ScriptableObjectClass) {

          
          errorId = "msg.bad.getter.parms";
        } else if (!delegatedForm) {
          errorId = "msg.bad.getter.parms";
        } 
      } else {
        errorId = "msg.bad.getter.parms";
      } 
      if (errorId != null) {
        throw Context.reportRuntimeError1(errorId, getter.toString());
      }
    } 
    
    MemberBox setterBox = null;
    if (setter != null) {
      boolean delegatedForm; if (setter.getReturnType() != void.class) {
        throw Context.reportRuntimeError1("msg.setter.return", setter.toString());
      }
      
      setterBox = new MemberBox(setter);

      
      if (!Modifier.isStatic(setter.getModifiers())) {
        delegatedForm = (delegateTo != null);
        setterBox.delegateTo = delegateTo;
      } else {
        delegatedForm = true;

        
        setterBox.delegateTo = void.class;
      } 
      
      String errorId = null;
      Class<?>[] parmTypes = setter.getParameterTypes();
      if (parmTypes.length == 1) {
        if (delegatedForm) {
          errorId = "msg.setter2.expected";
        }
      } else if (parmTypes.length == 2) {
        Object<?> argType = (Object<?>)parmTypes[0];
        
        if (argType != ScriptRuntime.ScriptableClass && argType != ScriptRuntime.ScriptableObjectClass) {

          
          errorId = "msg.setter2.parms";
        } else if (!delegatedForm) {
          errorId = "msg.setter1.parms";
        } 
      } else {
        errorId = "msg.setter.parms";
      } 
      if (errorId != null) {
        throw Context.reportRuntimeError1(errorId, setter.toString());
      }
    } 
    
    GetterSlot gslot = (GetterSlot)getSlot(propertyName, 0, 4);
    
    gslot.setAttributes(attributes);
    gslot.getter = getterBox;
    gslot.setter = setterBox;
  }






  
  public void defineOwnProperties(Context cx, ScriptableObject props) {
    Object[] ids = props.getIds();
    ScriptableObject[] descs = new ScriptableObject[ids.length]; int i, len;
    for (i = 0, len = ids.length; i < len; i++) {
      Object descObj = ScriptRuntime.getObjectElem(props, ids[i], cx);
      ScriptableObject desc = ensureScriptableObject(descObj);
      checkPropertyDefinition(desc);
      descs[i] = desc;
    } 
    for (i = 0, len = ids.length; i < len; i++) {
      defineOwnProperty(cx, ids[i], descs[i]);
    }
  }







  
  public void defineOwnProperty(Context cx, Object id, ScriptableObject desc) {
    checkPropertyDefinition(desc);
    defineOwnProperty(cx, id, desc, true);
  }











  
  protected void defineOwnProperty(Context cx, Object id, ScriptableObject desc, boolean checkValid) {
    int attributes;
    Slot slot = getSlot(cx, id, 1);
    boolean isNew = (slot == null);
    
    if (checkValid) {
      ScriptableObject current = (slot == null) ? null : slot.getPropertyDescriptor(cx, this);
      
      String name = ScriptRuntime.toString(id);
      checkPropertyChange(name, current, desc);
    } 
    
    boolean isAccessor = isAccessorDescriptor(desc);

    
    if (slot == null) {
      slot = getSlot(cx, id, isAccessor ? 4 : 2);
      attributes = applyDescriptorToAttributeBitset(7, desc);
    } else {
      attributes = applyDescriptorToAttributeBitset(slot.getAttributes(), desc);
    } 
    
    slot = unwrapSlot(slot);
    
    if (isAccessor) {
      if (!(slot instanceof GetterSlot)) {
        slot = getSlot(cx, id, 4);
      }
      
      GetterSlot gslot = (GetterSlot)slot;
      
      Object getter = getProperty(desc, "get");
      if (getter != NOT_FOUND) {
        gslot.getter = getter;
      }
      Object setter = getProperty(desc, "set");
      if (setter != NOT_FOUND) {
        gslot.setter = setter;
      }
      
      gslot.value = Undefined.instance;
      gslot.setAttributes(attributes);
    } else {
      if (slot instanceof GetterSlot && isDataDescriptor(desc)) {
        slot = getSlot(cx, id, 5);
      }
      
      Object value = getProperty(desc, "value");
      if (value != NOT_FOUND) {
        slot.value = value;
      } else if (isNew) {
        slot.value = Undefined.instance;
      } 
      slot.setAttributes(attributes);
    } 
  }
  
  protected void checkPropertyDefinition(ScriptableObject desc) {
    Object getter = getProperty(desc, "get");
    if (getter != NOT_FOUND && getter != Undefined.instance && !(getter instanceof Callable))
    {
      throw ScriptRuntime.notFunctionError(getter);
    }
    Object setter = getProperty(desc, "set");
    if (setter != NOT_FOUND && setter != Undefined.instance && !(setter instanceof Callable))
    {
      throw ScriptRuntime.notFunctionError(setter);
    }
    if (isDataDescriptor(desc) && isAccessorDescriptor(desc)) {
      throw ScriptRuntime.typeError0("msg.both.data.and.accessor.desc");
    }
  }

  
  protected void checkPropertyChange(String id, ScriptableObject current, ScriptableObject desc) {
    if (current == null) {
      if (!isExtensible()) throw ScriptRuntime.typeError0("msg.not.extensible");
    
    } else if (isFalse(current.get("configurable", current))) {
      if (isTrue(getProperty(desc, "configurable"))) {
        throw ScriptRuntime.typeError1("msg.change.configurable.false.to.true", id);
      }
      if (isTrue(current.get("enumerable", current)) != isTrue(getProperty(desc, "enumerable"))) {
        throw ScriptRuntime.typeError1("msg.change.enumerable.with.configurable.false", id);
      }
      boolean isData = isDataDescriptor(desc);
      boolean isAccessor = isAccessorDescriptor(desc);
      if (isData || isAccessor)
      {
        if (isData && isDataDescriptor(current)) {
          if (isFalse(current.get("writable", current))) {
            if (isTrue(getProperty(desc, "writable"))) {
              throw ScriptRuntime.typeError1("msg.change.writable.false.to.true.with.configurable.false", id);
            }
            
            if (!sameValue(getProperty(desc, "value"), current.get("value", current))) {
              throw ScriptRuntime.typeError1("msg.change.value.with.writable.false", id);
            }
          } 
        } else if (isAccessor && isAccessorDescriptor(current)) {
          if (!sameValue(getProperty(desc, "set"), current.get("set", current))) {
            throw ScriptRuntime.typeError1("msg.change.setter.with.configurable.false", id);
          }
          
          if (!sameValue(getProperty(desc, "get"), current.get("get", current))) {
            throw ScriptRuntime.typeError1("msg.change.getter.with.configurable.false", id);
          }
        } else {
          if (isDataDescriptor(current)) {
            throw ScriptRuntime.typeError1("msg.change.property.data.to.accessor.with.configurable.false", id);
          }
          
          throw ScriptRuntime.typeError1("msg.change.property.accessor.to.data.with.configurable.false", id);
        } 
      }
    } 
  }

  
  protected static boolean isTrue(Object value) {
    return (value != NOT_FOUND && ScriptRuntime.toBoolean(value));
  }
  
  protected static boolean isFalse(Object value) {
    return !isTrue(value);
  }







  
  protected boolean sameValue(Object newValue, Object currentValue) {
    if (newValue == NOT_FOUND) {
      return true;
    }
    if (currentValue == NOT_FOUND) {
      currentValue = Undefined.instance;
    }

    
    if (currentValue instanceof Number && newValue instanceof Number) {
      double d1 = ((Number)currentValue).doubleValue();
      double d2 = ((Number)newValue).doubleValue();
      if (Double.isNaN(d1) && Double.isNaN(d2)) {
        return true;
      }
      if (d1 == 0.0D && Double.doubleToLongBits(d1) != Double.doubleToLongBits(d2)) {
        return false;
      }
    } 
    return ScriptRuntime.shallowEq(currentValue, newValue);
  }


  
  protected int applyDescriptorToAttributeBitset(int attributes, ScriptableObject desc) {
    Object enumerable = getProperty(desc, "enumerable");
    if (enumerable != NOT_FOUND) {
      attributes = ScriptRuntime.toBoolean(enumerable) ? (attributes & 0xFFFFFFFD) : (attributes | 0x2);
    }

    
    Object writable = getProperty(desc, "writable");
    if (writable != NOT_FOUND) {
      attributes = ScriptRuntime.toBoolean(writable) ? (attributes & 0xFFFFFFFE) : (attributes | 0x1);
    }

    
    Object configurable = getProperty(desc, "configurable");
    if (configurable != NOT_FOUND) {
      attributes = ScriptRuntime.toBoolean(configurable) ? (attributes & 0xFFFFFFFB) : (attributes | 0x4);
    }

    
    return attributes;
  }





  
  protected boolean isDataDescriptor(ScriptableObject desc) {
    return (hasProperty(desc, "value") || hasProperty(desc, "writable"));
  }





  
  protected boolean isAccessorDescriptor(ScriptableObject desc) {
    return (hasProperty(desc, "get") || hasProperty(desc, "set"));
  }





  
  protected boolean isGenericDescriptor(ScriptableObject desc) {
    return (!isDataDescriptor(desc) && !isAccessorDescriptor(desc));
  }
  
  protected static Scriptable ensureScriptable(Object arg) {
    if (!(arg instanceof Scriptable))
      throw ScriptRuntime.typeError1("msg.arg.not.object", ScriptRuntime.typeof(arg)); 
    return (Scriptable)arg;
  }
  
  protected static ScriptableObject ensureScriptableObject(Object arg) {
    if (!(arg instanceof ScriptableObject))
      throw ScriptRuntime.typeError1("msg.arg.not.object", ScriptRuntime.typeof(arg)); 
    return (ScriptableObject)arg;
  }















  
  public void defineFunctionProperties(String[] names, Class<?> clazz, int attributes) {
    Method[] methods = FunctionObject.getMethodList(clazz);
    for (int i = 0; i < names.length; i++) {
      String name = names[i];
      Method m = FunctionObject.findSingleMethod(methods, name);
      if (m == null) {
        throw Context.reportRuntimeError2("msg.method.not.found", name, clazz.getName());
      }
      
      FunctionObject f = new FunctionObject(name, m, this);
      defineProperty(name, f, attributes);
    } 
  }




  
  public static Scriptable getObjectPrototype(Scriptable scope) {
    return TopLevel.getBuiltinPrototype(getTopLevelScope(scope), TopLevel.Builtins.Object);
  }





  
  public static Scriptable getFunctionPrototype(Scriptable scope) {
    return TopLevel.getBuiltinPrototype(getTopLevelScope(scope), TopLevel.Builtins.Function);
  }

  
  public static Scriptable getArrayPrototype(Scriptable scope) {
    return TopLevel.getBuiltinPrototype(getTopLevelScope(scope), TopLevel.Builtins.Array);
  }

















  
  public static Scriptable getClassPrototype(Scriptable scope, String className) {
    Object proto;
    scope = getTopLevelScope(scope);
    Object ctor = getProperty(scope, className);
    
    if (ctor instanceof BaseFunction) {
      proto = ((BaseFunction)ctor).getPrototypeProperty();
    } else if (ctor instanceof Scriptable) {
      Scriptable ctorObj = (Scriptable)ctor;
      proto = ctorObj.get("prototype", ctorObj);
    } else {
      return null;
    } 
    if (proto instanceof Scriptable) {
      return (Scriptable)proto;
    }
    return null;
  }










  
  public static Scriptable getTopLevelScope(Scriptable obj) {
    while (true) {
      Scriptable parent = obj.getParentScope();
      if (parent == null) {
        return obj;
      }
      obj = parent;
    } 
  }
  
  public boolean isExtensible() {
    return this.isExtensible;
  }
  
  public void preventExtensions() {
    this.isExtensible = false;
  }









  
  public synchronized void sealObject() {
    if (this.count >= 0) {
      
      Slot slot = this.firstAdded;
      while (slot != null) {
        Object value = slot.value;
        if (value instanceof LazilyLoadedCtor) {
          LazilyLoadedCtor initializer = (LazilyLoadedCtor)value;
          try {
            initializer.init();
          } finally {
            slot.value = initializer.getValue();
          } 
        } 
        slot = slot.orderedNext;
      } 
      this.count ^= 0xFFFFFFFF;
    } 
  }







  
  public final boolean isSealed() {
    return (this.count < 0);
  }

  
  private void checkNotSealed(String name, int index) {
    if (!isSealed()) {
      return;
    }
    String str = (name != null) ? name : Integer.toString(index);
    throw Context.reportRuntimeError1("msg.modify.sealed", str);
  }












  
  public static Object getProperty(Scriptable obj, String name) {
    Object result;
    Scriptable start = obj;
    
    do {
      result = obj.get(name, start);
      if (result != Scriptable.NOT_FOUND)
        break; 
      obj = obj.getPrototype();
    } while (obj != null);
    return result;
  }



















  
  public static <T> T getTypedProperty(Scriptable s, int index, Class<T> type) {
    Object val = getProperty(s, index);
    if (val == Scriptable.NOT_FOUND) {
      val = null;
    }
    return type.cast(Context.jsToJava(val, type));
  }















  
  public static Object getProperty(Scriptable obj, int index) {
    Object result;
    Scriptable start = obj;
    
    do {
      result = obj.get(index, start);
      if (result != Scriptable.NOT_FOUND)
        break; 
      obj = obj.getPrototype();
    } while (obj != null);
    return result;
  }
















  
  public static <T> T getTypedProperty(Scriptable s, String name, Class<T> type) {
    Object val = getProperty(s, name);
    if (val == Scriptable.NOT_FOUND) {
      val = null;
    }
    return type.cast(Context.jsToJava(val, type));
  }












  
  public static boolean hasProperty(Scriptable obj, String name) {
    return (null != getBase(obj, name));
  }











  
  public static void redefineProperty(Scriptable obj, String name, boolean isConst) {
    Scriptable base = getBase(obj, name);
    if (base == null)
      return; 
    if (base instanceof ConstProperties) {
      ConstProperties cp = (ConstProperties)base;
      
      if (cp.isConst(name))
        throw ScriptRuntime.typeError1("msg.const.redecl", name); 
    } 
    if (isConst) {
      throw ScriptRuntime.typeError1("msg.var.redecl", name);
    }
  }










  
  public static boolean hasProperty(Scriptable obj, int index) {
    return (null != getBase(obj, index));
  }
















  
  public static void putProperty(Scriptable obj, String name, Object value) {
    Scriptable base = getBase(obj, name);
    if (base == null)
      base = obj; 
    base.put(name, obj, value);
  }
















  
  public static void putConstProperty(Scriptable obj, String name, Object value) {
    Scriptable base = getBase(obj, name);
    if (base == null)
      base = obj; 
    if (base instanceof ConstProperties) {
      ((ConstProperties)base).putConst(name, obj, value);
    }
  }















  
  public static void putProperty(Scriptable obj, int index, Object value) {
    Scriptable base = getBase(obj, index);
    if (base == null)
      base = obj; 
    base.put(index, obj, value);
  }












  
  public static boolean deleteProperty(Scriptable obj, String name) {
    Scriptable base = getBase(obj, name);
    if (base == null)
      return true; 
    base.delete(name);
    return !base.has(name, obj);
  }












  
  public static boolean deleteProperty(Scriptable obj, int index) {
    Scriptable base = getBase(obj, index);
    if (base == null)
      return true; 
    base.delete(index);
    return !base.has(index, obj);
  }










  
  public static Object[] getPropertyIds(Scriptable obj) {
    if (obj == null) {
      return ScriptRuntime.emptyArgs;
    }
    Object[] result = obj.getIds();
    ObjToIntMap map = null;
    while (true) {
      obj = obj.getPrototype();
      if (obj == null) {
        break;
      }
      Object[] ids = obj.getIds();
      if (ids.length == 0) {
        continue;
      }
      if (map == null) {
        if (result.length == 0) {
          result = ids;
          continue;
        } 
        map = new ObjToIntMap(result.length + ids.length);
        for (int j = 0; j != result.length; j++) {
          map.intern(result[j]);
        }
        result = null;
      } 
      for (int i = 0; i != ids.length; i++) {
        map.intern(ids[i]);
      }
    } 
    if (map != null) {
      result = map.getKeys();
    }
    return result;
  }










  
  public static Object callMethod(Scriptable obj, String methodName, Object[] args) {
    return callMethod(null, obj, methodName, args);
  }










  
  public static Object callMethod(Context cx, Scriptable obj, String methodName, Object[] args) {
    Object funObj = getProperty(obj, methodName);
    if (!(funObj instanceof Function)) {
      throw ScriptRuntime.notFunctionError(obj, methodName);
    }
    Function fun = (Function)funObj;






    
    Scriptable scope = getTopLevelScope(obj);
    if (cx != null) {
      return fun.call(cx, scope, obj, args);
    }
    return Context.call(null, fun, scope, obj, args);
  }



  
  private static Scriptable getBase(Scriptable obj, String name) {
    while (!obj.has(name, obj))
    
    { obj = obj.getPrototype();
      if (obj == null)
        break;  }  return obj;
  }


  
  private static Scriptable getBase(Scriptable obj, int index) {
    while (!obj.has(index, obj))
    
    { obj = obj.getPrototype();
      if (obj == null)
        break;  }  return obj;
  }






  
  public final Object getAssociatedValue(Object key) {
    Map<Object, Object> h = this.associatedValues;
    if (h == null)
      return null; 
    return h.get(key);
  }












  
  public static Object getTopScopeValue(Scriptable scope, Object key) {
    scope = getTopLevelScope(scope);
    while (true) {
      if (scope instanceof ScriptableObject) {
        ScriptableObject so = (ScriptableObject)scope;
        Object value = so.getAssociatedValue(key);
        if (value != null) {
          return value;
        }
      } 
      scope = scope.getPrototype();
      if (scope == null) {
        return null;
      }
    } 
  }













  
  public final synchronized Object associateValue(Object key, Object value) {
    if (value == null) throw new IllegalArgumentException(); 
    Map<Object, Object> h = this.associatedValues;
    if (h == null) {
      h = new HashMap<Object, Object>();
      this.associatedValues = h;
    } 
    return Kit.initHash(h, key, value);
  }













  
  private boolean putImpl(String name, int index, Scriptable start, Object value) {
    Slot slot;
    if (this != start) {
      slot = getSlot(name, index, 1);
      if (slot == null) {
        return false;
      }
    } else if (!this.isExtensible) {
      slot = getSlot(name, index, 1);
      if (slot == null) {
        return true;
      }
    } else {
      if (this.count < 0) checkNotSealed(name, index); 
      slot = getSlot(name, index, 2);
    } 
    return slot.setValue(value, this, start);
  }













  
  private boolean putConstImpl(String name, int index, Scriptable start, Object value, int constFlag) {
    Slot slot;
    assert constFlag != 0;
    
    if (this != start) {
      slot = getSlot(name, index, 1);
      if (slot == null) {
        return false;
      }
    } else if (!isExtensible()) {
      slot = getSlot(name, index, 1);
      if (slot == null) {
        return true;
      }
    } else {
      checkNotSealed(name, index);
      
      slot = unwrapSlot(getSlot(name, index, 3));
      int attr = slot.getAttributes();
      if ((attr & 0x1) == 0)
        throw Context.reportRuntimeError1("msg.var.redecl", name); 
      if ((attr & 0x8) != 0) {
        slot.value = value;
        
        if (constFlag != 8)
          slot.setAttributes(attr & 0xFFFFFFF7); 
      } 
      return true;
    } 
    return slot.setValue(value, this, start);
  }

  
  private Slot findAttributeSlot(String name, int index, int accessType) {
    Slot slot = getSlot(name, index, accessType);
    if (slot == null) {
      String str = (name != null) ? name : Integer.toString(index);
      throw Context.reportRuntimeError1("msg.prop.not.found", str);
    } 
    return slot;
  }
  
  private static Slot unwrapSlot(Slot slot) {
    return (slot instanceof RelinkedSlot) ? ((RelinkedSlot)slot).slot : slot;
  }









  
  private Slot getSlot(String name, int index, int accessType) {
    Slot[] slotsLocalRef = this.slots;
    if (slotsLocalRef == null && accessType == 1) {
      return null;
    }
    
    int indexOrHash = (name != null) ? name.hashCode() : index;
    if (slotsLocalRef != null) {
      
      int slotIndex = getSlotIndex(slotsLocalRef.length, indexOrHash);
      Slot slot = slotsLocalRef[slotIndex];
      for (; slot != null; 
        slot = slot.next) {
        Object sname = slot.name;
        if (indexOrHash == slot.indexOrHash && (sname == name || (name != null && name.equals(sname)))) {
          break;
        }
      } 

      
      switch (accessType) {
        case 1:
          return slot;
        case 2:
        case 3:
          if (slot != null)
            return slot; 
          break;
        case 4:
          slot = unwrapSlot(slot);
          if (slot instanceof GetterSlot)
            return slot; 
          break;
        case 5:
          slot = unwrapSlot(slot);
          if (!(slot instanceof GetterSlot)) {
            return slot;
          }
          break;
      } 

    
    } 
    return createSlot(name, indexOrHash, accessType);
  }
  private synchronized Slot createSlot(String name, int indexOrHash, int accessType) {
    int insertPos;
    Slot[] slotsLocalRef = this.slots;
    
    if (this.count == 0) {
      
      slotsLocalRef = new Slot[4];
      this.slots = slotsLocalRef;
      insertPos = getSlotIndex(slotsLocalRef.length, indexOrHash);
    } else {
      int tableSize = slotsLocalRef.length;
      insertPos = getSlotIndex(tableSize, indexOrHash);
      Slot prev = slotsLocalRef[insertPos];
      Slot slot = prev;
      while (slot != null && (
        slot.indexOrHash != indexOrHash || (slot.name != name && (name == null || !name.equals(slot.name))))) {




        
        prev = slot;
        slot = slot.next;
      } 
      
      if (slot != null) {





        
        Slot slot1, inner = unwrapSlot(slot);

        
        if (accessType == 4 && !(inner instanceof GetterSlot))
        
        { slot1 = new GetterSlot(name, indexOrHash, inner.getAttributes()); }
        else if (accessType == 5 && inner instanceof GetterSlot)
        
        { slot1 = new Slot(name, indexOrHash, inner.getAttributes()); }
        else { if (accessType == 3) {
            return null;
          }
          return inner; }

        
        slot1.value = inner.value;
        slot1.next = slot.next;
        
        if (this.lastAdded != null) {
          this.lastAdded.orderedNext = slot1;
        }
        if (this.firstAdded == null) {
          this.firstAdded = slot1;
        }
        this.lastAdded = slot1;
        
        if (prev == slot) {
          slotsLocalRef[insertPos] = slot1;
        } else {
          prev.next = slot1;
        } 
        
        slot.markDeleted();
        return slot1;
      } 
      
      if (4 * (this.count + 1) > 3 * slotsLocalRef.length) {
        
        slotsLocalRef = new Slot[slotsLocalRef.length * 2];
        copyTable(this.slots, slotsLocalRef, this.count);
        this.slots = slotsLocalRef;
        insertPos = getSlotIndex(slotsLocalRef.length, indexOrHash);
      } 
    } 

    
    Slot newSlot = (accessType == 4) ? new GetterSlot(name, indexOrHash, 0) : new Slot(name, indexOrHash, 0);

    
    if (accessType == 3)
      newSlot.setAttributes(13); 
    this.count++;
    
    if (this.lastAdded != null)
      this.lastAdded.orderedNext = newSlot; 
    if (this.firstAdded == null)
      this.firstAdded = newSlot; 
    this.lastAdded = newSlot;
    
    addKnownAbsentSlot(slotsLocalRef, newSlot, insertPos);
    return newSlot;
  }
  
  private synchronized void removeSlot(String name, int index) {
    int indexOrHash = (name != null) ? name.hashCode() : index;
    
    Slot[] slotsLocalRef = this.slots;
    if (this.count != 0) {
      int tableSize = slotsLocalRef.length;
      int slotIndex = getSlotIndex(tableSize, indexOrHash);
      Slot prev = slotsLocalRef[slotIndex];
      Slot slot = prev;
      while (slot != null && (
        slot.indexOrHash != indexOrHash || (slot.name != name && (name == null || !name.equals(slot.name))))) {




        
        prev = slot;
        slot = slot.next;
      } 
      if (slot != null && (slot.getAttributes() & 0x4) == 0) {
        this.count--;
        
        if (prev == slot) {
          slotsLocalRef[slotIndex] = slot.next;
        } else {
          prev.next = slot.next;
        } 





        
        Slot deleted = unwrapSlot(slot);
        if (deleted == this.firstAdded) {
          prev = null;
          this.firstAdded = deleted.orderedNext;
        } else {
          prev = this.firstAdded;
          while (prev.orderedNext != deleted) {
            prev = prev.orderedNext;
          }
          prev.orderedNext = deleted.orderedNext;
        } 
        if (deleted == this.lastAdded) {
          this.lastAdded = prev;
        }

        
        slot.markDeleted();
      } 
    } 
  }


  
  private static int getSlotIndex(int tableSize, int indexOrHash) {
    return indexOrHash & tableSize - 1;
  }













  
  private static void copyTable(Slot[] oldSlots, Slot[] newSlots, int count) {
    // Byte code:
    //   0: iload_2
    //   1: ifne -> 8
    //   4: invokestatic codeBug : ()Ljava/lang/RuntimeException;
    //   7: athrow
    //   8: aload_1
    //   9: arraylength
    //   10: istore_3
    //   11: aload_0
    //   12: arraylength
    //   13: istore #4
    //   15: iinc #4, -1
    //   18: aload_0
    //   19: iload #4
    //   21: aaload
    //   22: astore #5
    //   24: aload #5
    //   26: ifnull -> 90
    //   29: iload_3
    //   30: aload #5
    //   32: getfield indexOrHash : I
    //   35: invokestatic getSlotIndex : (II)I
    //   38: istore #6
    //   40: aload #5
    //   42: getfield next : Lorg/mozilla/javascript/ScriptableObject$Slot;
    //   45: ifnonnull -> 53
    //   48: aload #5
    //   50: goto -> 62
    //   53: new org/mozilla/javascript/ScriptableObject$RelinkedSlot
    //   56: dup
    //   57: aload #5
    //   59: invokespecial <init> : (Lorg/mozilla/javascript/ScriptableObject$Slot;)V
    //   62: astore #7
    //   64: aload_1
    //   65: aload #7
    //   67: iload #6
    //   69: invokestatic addKnownAbsentSlot : ([Lorg/mozilla/javascript/ScriptableObject$Slot;Lorg/mozilla/javascript/ScriptableObject$Slot;I)V
    //   72: aload #5
    //   74: getfield next : Lorg/mozilla/javascript/ScriptableObject$Slot;
    //   77: astore #5
    //   79: iinc #2, -1
    //   82: iload_2
    //   83: ifne -> 87
    //   86: return
    //   87: goto -> 24
    //   90: goto -> 15
    // Line number table:
    //   Java source line number -> byte code offset
    //   #3001	-> 0
    //   #3003	-> 8
    //   #3004	-> 11
    //   #3006	-> 15
    //   #3007	-> 18
    //   #3008	-> 24
    //   #3009	-> 29
    //   #3012	-> 40
    //   #3013	-> 64
    //   #3014	-> 72
    //   #3015	-> 79
    //   #3016	-> 86
    //   #3017	-> 87
    //   #3018	-> 90
    // Local variable table:
    //   start	length	slot	name	descriptor
    //   40	47	6	insertPos	I
    //   64	23	7	insSlot	Lorg/mozilla/javascript/ScriptableObject$Slot;
    //   24	66	5	slot	Lorg/mozilla/javascript/ScriptableObject$Slot;
    //   0	93	0	oldSlots	[Lorg/mozilla/javascript/ScriptableObject$Slot;
    //   0	93	1	newSlots	[Lorg/mozilla/javascript/ScriptableObject$Slot;
    //   0	93	2	count	I
    //   11	82	3	tableSize	I
    //   15	78	4	i	I
  }













  
  private static void addKnownAbsentSlot(Slot[] slots, Slot slot, int insertPos) {
    if (slots[insertPos] == null) {
      slots[insertPos] = slot;
    } else {
      Slot prev = slots[insertPos];
      Slot next = prev.next;
      while (next != null) {
        prev = next;
        next = prev.next;
      } 
      prev.next = slot;
    } 
  }
  Object[] getIds(boolean getAll) {
    Object[] a;
    Slot[] s = this.slots;
    
    int externalLen = (this.externalData == null) ? 0 : this.externalData.getArrayLength();
    
    if (externalLen == 0) {
      a = ScriptRuntime.emptyArgs;
    } else {
      a = new Object[externalLen];
      for (int i = 0; i < externalLen; i++) {
        a[i] = Integer.valueOf(i);
      }
    } 
    if (s == null) {
      return a;
    }
    
    int c = externalLen;
    Slot slot = this.firstAdded;
    while (slot != null && slot.wasDeleted)
    {


      
      slot = slot.orderedNext;
    }
    while (slot != null) {
      if (getAll || (slot.getAttributes() & 0x2) == 0) {
        if (c == externalLen) {
          Object[] oldA = a;
          a = new Object[s.length + externalLen];
          if (oldA != null) {
            System.arraycopy(oldA, 0, a, 0, externalLen);
          }
        } 
        a[c++] = (slot.name != null) ? slot.name : Integer.valueOf(slot.indexOrHash);
      } 

      
      slot = slot.orderedNext;
      while (slot != null && slot.wasDeleted)
      {
        slot = slot.orderedNext;
      }
    } 
    if (c == a.length + externalLen) {
      return a;
    }
    Object[] result = new Object[c];
    System.arraycopy(a, 0, result, 0, c);
    return result;
  }


  
  private synchronized void writeObject(ObjectOutputStream out) throws IOException {
    out.defaultWriteObject();
    int objectsCount = this.count;
    if (objectsCount < 0)
    {
      objectsCount ^= 0xFFFFFFFF;
    }
    if (objectsCount == 0) {
      out.writeInt(0);
    } else {
      out.writeInt(this.slots.length);
      Slot slot = this.firstAdded;
      while (slot != null && slot.wasDeleted)
      {
        
        slot = slot.orderedNext;
      }
      this.firstAdded = slot;
      while (slot != null) {
        out.writeObject(slot);
        Slot next = slot.orderedNext;
        while (next != null && next.wasDeleted)
        {
          next = next.orderedNext;
        }
        slot.orderedNext = next;
        slot = next;
      } 
    } 
  }


  
  private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
    in.defaultReadObject();
    
    int tableSize = in.readInt();
    if (tableSize != 0) {

      
      if ((tableSize & tableSize - 1) != 0) {
        if (tableSize > 1073741824)
          throw new RuntimeException("Property table overflow"); 
        int newSize = 4;
        while (newSize < tableSize)
          newSize <<= 1; 
        tableSize = newSize;
      } 
      this.slots = new Slot[tableSize];
      int objectsCount = this.count;
      if (objectsCount < 0)
      {
        objectsCount ^= 0xFFFFFFFF;
      }
      Slot prev = null;
      for (int i = 0; i != objectsCount; i++) {
        this.lastAdded = (Slot)in.readObject();
        if (i == 0) {
          this.firstAdded = this.lastAdded;
        } else {
          prev.orderedNext = this.lastAdded;
        } 
        int slotIndex = getSlotIndex(tableSize, this.lastAdded.indexOrHash);
        addKnownAbsentSlot(this.slots, this.lastAdded, slotIndex);
        prev = this.lastAdded;
      } 
    } 
  }
  
  protected ScriptableObject getOwnPropertyDescriptor(Context cx, Object id) {
    Slot slot = getSlot(cx, id, 1);
    if (slot == null) return null; 
    Scriptable scope = getParentScope();
    return slot.getPropertyDescriptor(cx, (scope == null) ? this : scope);
  }
  
  protected Slot getSlot(Context cx, Object id, int accessType) {
    String name = ScriptRuntime.toStringIdOrIndex(cx, id);
    if (name == null) {
      return getSlot((String)null, ScriptRuntime.lastIndexResult(cx), accessType);
    }
    return getSlot(name, 0, accessType);
  }




  
  public int size() {
    return (this.count < 0) ? (this.count ^ 0xFFFFFFFF) : this.count;
  }
  
  public boolean isEmpty() {
    return (this.count == 0 || this.count == -1);
  }

  
  public Object get(Object key) {
    Object value = null;
    if (key instanceof String) {
      value = get((String)key, this);
    } else if (key instanceof Number) {
      value = get(((Number)key).intValue(), this);
    } 
    if (value == Scriptable.NOT_FOUND || value == Undefined.instance)
      return null; 
    if (value instanceof Wrapper) {
      return ((Wrapper)value).unwrap();
    }
    return value;
  }
  
  public ScriptableObject() {}
  
  public abstract String getClassName();
}
