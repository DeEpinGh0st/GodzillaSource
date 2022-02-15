package org.mozilla.javascript;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Set;























public class NativeArray
  extends IdScriptableObject
  implements List
{
  static final long serialVersionUID = 7331366857676127338L;
  private static final Object ARRAY_TAG = "Array";
  private static final Integer NEGATIVE_ONE = Integer.valueOf(-1); private static final int Id_length = 1; private static final int MAX_INSTANCE_ID = 1; private static final int Id_constructor = 1; private static final int Id_toString = 2; private static final int Id_toLocaleString = 3; private static final int Id_toSource = 4; private static final int Id_join = 5; private static final int Id_reverse = 6; private static final int Id_sort = 7; private static final int Id_push = 8; private static final int Id_pop = 9; private static final int Id_shift = 10; private static final int Id_unshift = 11; private static final int Id_splice = 12; private static final int Id_concat = 13; private static final int Id_slice = 14; private static final int Id_indexOf = 15; private static final int Id_lastIndexOf = 16; private static final int Id_every = 17; private static final int Id_filter = 18; private static final int Id_forEach = 19; private static final int Id_map = 20; private static final int Id_some = 21; private static final int Id_find = 22; private static final int Id_findIndex = 23; private static final int Id_reduce = 24;
  private static final int Id_reduceRight = 25;
  
  static void init(Scriptable scope, boolean sealed) {
    NativeArray obj = new NativeArray(0L);
    obj.exportAsJSClass(25, scope, sealed);
  }
  private static final int MAX_PROTOTYPE_ID = 25; private static final int ConstructorId_join = -5; private static final int ConstructorId_reverse = -6; private static final int ConstructorId_sort = -7; private static final int ConstructorId_push = -8; private static final int ConstructorId_pop = -9; private static final int ConstructorId_shift = -10; private static final int ConstructorId_unshift = -11; private static final int ConstructorId_splice = -12; private static final int ConstructorId_concat = -13; private static final int ConstructorId_slice = -14; private static final int ConstructorId_indexOf = -15; private static final int ConstructorId_lastIndexOf = -16; private static final int ConstructorId_every = -17; private static final int ConstructorId_filter = -18; private static final int ConstructorId_forEach = -19; private static final int ConstructorId_map = -20; private static final int ConstructorId_some = -21; private static final int ConstructorId_find = -22; private static final int ConstructorId_findIndex = -23; private static final int ConstructorId_reduce = -24; private static final int ConstructorId_reduceRight = -25; private static final int ConstructorId_isArray = -26; private long length; private int lengthAttr; private Object[] dense; private boolean denseOnly;
  static int getMaximumInitialCapacity() {
    return maximumInitialCapacity;
  }
  
  static void setMaximumInitialCapacity(int maximumInitialCapacity) {
    NativeArray.maximumInitialCapacity = maximumInitialCapacity;
  }


















































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































  
  public NativeArray(long lengthArg)
  {
    this.lengthAttr = 6; this.denseOnly = (lengthArg <= maximumInitialCapacity); if (this.denseOnly) { int intLength = (int)lengthArg; if (intLength < 10) intLength = 10;  this.dense = new Object[intLength]; Arrays.fill(this.dense, Scriptable.NOT_FOUND); }  this.length = lengthArg; } public NativeArray(Object[] array) { this.lengthAttr = 6;
    this.denseOnly = true;
    this.dense = array;
    this.length = array.length; } public String getClassName() { return "Array"; } protected int getMaxInstanceId() { return 1; } protected void setInstanceIdAttributes(int id, int attr) {
    if (id == 1)
      this.lengthAttr = attr; 
  } protected int findInstanceIdInfo(String s) {
    if (s.equals("length"))
      return instanceIdInfo(this.lengthAttr, 1); 
    return super.findInstanceIdInfo(s);
  }
  protected String getInstanceIdName(int id) {
    if (id == 1)
      return "length"; 
    return super.getInstanceIdName(id);
  }
  private static int maximumInitialCapacity = 10000;
  
  protected Object getInstanceIdValue(int id) {
    if (id == 1)
      return ScriptRuntime.wrapNumber(this.length); 
    return super.getInstanceIdValue(id);
  }
  
  protected void setInstanceIdValue(int id, Object value) {
    if (id == 1) {
      setLength(value);
      return;
    } 
    super.setInstanceIdValue(id, value);
  }
  
  protected void fillConstructorProperties(IdFunctionObject ctor) {
    addIdFunctionProperty(ctor, ARRAY_TAG, -5, "join", 1);
    addIdFunctionProperty(ctor, ARRAY_TAG, -6, "reverse", 0);
    addIdFunctionProperty(ctor, ARRAY_TAG, -7, "sort", 1);
    addIdFunctionProperty(ctor, ARRAY_TAG, -8, "push", 1);
    addIdFunctionProperty(ctor, ARRAY_TAG, -9, "pop", 0);
    addIdFunctionProperty(ctor, ARRAY_TAG, -10, "shift", 0);
    addIdFunctionProperty(ctor, ARRAY_TAG, -11, "unshift", 1);
    addIdFunctionProperty(ctor, ARRAY_TAG, -12, "splice", 2);
    addIdFunctionProperty(ctor, ARRAY_TAG, -13, "concat", 1);
    addIdFunctionProperty(ctor, ARRAY_TAG, -14, "slice", 2);
    addIdFunctionProperty(ctor, ARRAY_TAG, -15, "indexOf", 1);
    addIdFunctionProperty(ctor, ARRAY_TAG, -16, "lastIndexOf", 1);
    addIdFunctionProperty(ctor, ARRAY_TAG, -17, "every", 1);
    addIdFunctionProperty(ctor, ARRAY_TAG, -18, "filter", 1);
    addIdFunctionProperty(ctor, ARRAY_TAG, -19, "forEach", 1);
    addIdFunctionProperty(ctor, ARRAY_TAG, -20, "map", 1);
    addIdFunctionProperty(ctor, ARRAY_TAG, -21, "some", 1);
    addIdFunctionProperty(ctor, ARRAY_TAG, -22, "find", 1);
    addIdFunctionProperty(ctor, ARRAY_TAG, -23, "findIndex", 1);
    addIdFunctionProperty(ctor, ARRAY_TAG, -24, "reduce", 1);
    addIdFunctionProperty(ctor, ARRAY_TAG, -25, "reduceRight", 1);
    addIdFunctionProperty(ctor, ARRAY_TAG, -26, "isArray", 1);
    super.fillConstructorProperties(ctor);
  }
  
  protected void initPrototypeId(int id) {
    String s;
    int arity;
    switch (id) {
      case 1:
        arity = 1;
        s = "constructor";
        break;
      case 2:
        arity = 0;
        s = "toString";
        break;
      case 3:
        arity = 0;
        s = "toLocaleString";
        break;
      case 4:
        arity = 0;
        s = "toSource";
        break;
      case 5:
        arity = 1;
        s = "join";
        break;
      case 6:
        arity = 0;
        s = "reverse";
        break;
      case 7:
        arity = 1;
        s = "sort";
        break;
      case 8:
        arity = 1;
        s = "push";
        break;
      case 9:
        arity = 0;
        s = "pop";
        break;
      case 10:
        arity = 0;
        s = "shift";
        break;
      case 11:
        arity = 1;
        s = "unshift";
        break;
      case 12:
        arity = 2;
        s = "splice";
        break;
      case 13:
        arity = 1;
        s = "concat";
        break;
      case 14:
        arity = 2;
        s = "slice";
        break;
      case 15:
        arity = 1;
        s = "indexOf";
        break;
      case 16:
        arity = 1;
        s = "lastIndexOf";
        break;
      case 17:
        arity = 1;
        s = "every";
        break;
      case 18:
        arity = 1;
        s = "filter";
        break;
      case 19:
        arity = 1;
        s = "forEach";
        break;
      case 20:
        arity = 1;
        s = "map";
        break;
      case 21:
        arity = 1;
        s = "some";
        break;
      case 22:
        arity = 1;
        s = "find";
        break;
      case 23:
        arity = 1;
        s = "findIndex";
        break;
      case 24:
        arity = 1;
        s = "reduce";
        break;
      case 25:
        arity = 1;
        s = "reduceRight";
        break;
      default:
        throw new IllegalArgumentException(String.valueOf(id));
    } 
    initPrototypeMethod(ARRAY_TAG, id, s, arity);
  }
  
  public Object execIdCall(IdFunctionObject f, Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
    if (!f.hasTag(ARRAY_TAG))
      return super.execIdCall(f, cx, scope, thisObj, args); 
    int id = f.methodId();
    while (true) {
      boolean inNewExpr;
      switch (id) {
        case -25:
        case -24:
        case -23:
        case -22:
        case -21:
        case -20:
        case -19:
        case -18:
        case -17:
        case -16:
        case -15:
        case -14:
        case -13:
        case -12:
        case -11:
        case -10:
        case -9:
        case -8:
        case -7:
        case -6:
        case -5:
          if (args.length > 0) {
            thisObj = ScriptRuntime.toObject(cx, scope, args[0]);
            Object[] newArgs = new Object[args.length - 1];
            for (int i = 0; i < newArgs.length; i++)
              newArgs[i] = args[i + 1]; 
            args = newArgs;
          } 
          id = -id;
          continue;
        case -26:
          return Boolean.valueOf((args.length > 0 && js_isArray(args[0])));
        case 1:
          inNewExpr = (thisObj == null);
          if (!inNewExpr)
            return f.construct(cx, scope, args); 
          return jsConstructor(cx, scope, args);
        case 2:
          return toStringHelper(cx, scope, thisObj, cx.hasFeature(4), false);
        case 3:
          return toStringHelper(cx, scope, thisObj, false, true);
        case 4:
          return toStringHelper(cx, scope, thisObj, true, false);
        case 5:
          return js_join(cx, thisObj, args);
        case 6:
          return js_reverse(cx, thisObj, args);
        case 7:
          return js_sort(cx, scope, thisObj, args);
        case 8:
          return js_push(cx, thisObj, args);
        case 9:
          return js_pop(cx, thisObj, args);
        case 10:
          return js_shift(cx, thisObj, args);
        case 11:
          return js_unshift(cx, thisObj, args);
        case 12:
          return js_splice(cx, scope, thisObj, args);
        case 13:
          return js_concat(cx, scope, thisObj, args);
        case 14:
          return js_slice(cx, thisObj, args);
        case 15:
          return js_indexOf(cx, thisObj, args);
        case 16:
          return js_lastIndexOf(cx, thisObj, args);
        case 17:
        case 18:
        case 19:
        case 20:
        case 21:
        case 22:
        case 23:
          return iterativeMethod(cx, id, scope, thisObj, args);
        case 24:
        case 25:
          return reduceMethod(cx, id, scope, thisObj, args);
      } 
      break;
    } 
    throw new IllegalArgumentException(String.valueOf(id));
  }
  
  public Object get(int index, Scriptable start) {
    if (!this.denseOnly && isGetterOrSetter((String)null, index, false))
      return super.get(index, start); 
    if (this.dense != null && 0 <= index && index < this.dense.length)
      return this.dense[index]; 
    return super.get(index, start);
  }
  
  public boolean has(int index, Scriptable start) {
    if (!this.denseOnly && isGetterOrSetter((String)null, index, false))
      return super.has(index, start); 
    if (this.dense != null && 0 <= index && index < this.dense.length)
      return (this.dense[index] != NOT_FOUND); 
    return super.has(index, start);
  }
  
  private static long toArrayIndex(Object id) {
    if (id instanceof String)
      return toArrayIndex((String)id); 
    if (id instanceof Number)
      return toArrayIndex(((Number)id).doubleValue()); 
    return -1L;
  }
  
  private static long toArrayIndex(String id) {
    long index = toArrayIndex(ScriptRuntime.toNumber(id));
    if (Long.toString(index).equals(id))
      return index; 
    return -1L;
  }
  
  private static long toArrayIndex(double d) {
    if (d == d) {
      long index = ScriptRuntime.toUint32(d);
      if (index == d && index != 4294967295L)
        return index; 
    } 
    return -1L;
  }
  
  private static int toDenseIndex(Object id) {
    long index = toArrayIndex(id);
    return (0L <= index && index < 2147483647L) ? (int)index : -1;
  }
  
  public void put(String id, Scriptable start, Object value) {
    super.put(id, start, value);
    if (start == this) {
      long index = toArrayIndex(id);
      if (index >= this.length) {
        this.length = index + 1L;
        this.denseOnly = false;
      } 
    } 
  }
  
  private boolean ensureCapacity(int capacity) {
    if (capacity > this.dense.length) {
      if (capacity > 1431655764) {
        this.denseOnly = false;
        return false;
      } 
      capacity = Math.max(capacity, (int)(this.dense.length * 1.5D));
      Object[] newDense = new Object[capacity];
      System.arraycopy(this.dense, 0, newDense, 0, this.dense.length);
      Arrays.fill(newDense, this.dense.length, newDense.length, Scriptable.NOT_FOUND);
      this.dense = newDense;
    } 
    return true;
  }
  
  public void put(int index, Scriptable start, Object value) {
    if (start == this && !isSealed() && this.dense != null && 0 <= index && (this.denseOnly || !isGetterOrSetter((String)null, index, true))) {
      if (!isExtensible() && this.length <= index)
        return; 
      if (index < this.dense.length) {
        this.dense[index] = value;
        if (this.length <= index)
          this.length = index + 1L; 
        return;
      } 
      if (this.denseOnly && index < this.dense.length * 1.5D && ensureCapacity(index + 1)) {
        this.dense[index] = value;
        this.length = index + 1L;
        return;
      } 
      this.denseOnly = false;
    } 
    super.put(index, start, value);
    if (start == this && (this.lengthAttr & 0x1) == 0)
      if (this.length <= index)
        this.length = index + 1L;  
  }
  
  public void delete(int index) {
    if (this.dense != null && 0 <= index && index < this.dense.length && !isSealed() && (this.denseOnly || !isGetterOrSetter((String)null, index, true))) {
      this.dense[index] = NOT_FOUND;
    } else {
      super.delete(index);
    } 
  }
  
  public Object[] getIds() {
    Object[] superIds = super.getIds();
    if (this.dense == null)
      return superIds; 
    int N = this.dense.length;
    long currentLength = this.length;
    if (N > currentLength)
      N = (int)currentLength; 
    if (N == 0)
      return superIds; 
    int superLength = superIds.length;
    Object[] ids = new Object[N + superLength];
    int presentCount = 0;
    for (int i = 0; i != N; i++) {
      if (this.dense[i] != NOT_FOUND) {
        ids[presentCount] = Integer.valueOf(i);
        presentCount++;
      } 
    } 
    if (presentCount != N) {
      Object[] tmp = new Object[presentCount + superLength];
      System.arraycopy(ids, 0, tmp, 0, presentCount);
      ids = tmp;
    } 
    System.arraycopy(superIds, 0, ids, presentCount, superLength);
    return ids;
  }
  
  public Object[] getAllIds() {
    Set<Object> allIds = new LinkedHashSet(Arrays.asList(getIds()));
    allIds.addAll(Arrays.asList(super.getAllIds()));
    return allIds.toArray();
  }
  
  public Integer[] getIndexIds() {
    Object[] ids = getIds();
    List<Integer> indices = new ArrayList<Integer>(ids.length);
    for (Object id : ids) {
      int int32Id = ScriptRuntime.toInt32(id);
      if (int32Id >= 0 && ScriptRuntime.toString(int32Id).equals(ScriptRuntime.toString(id)))
        indices.add(Integer.valueOf(int32Id)); 
    } 
    return indices.<Integer>toArray(new Integer[indices.size()]);
  }
  
  public Object getDefaultValue(Class<?> hint) {
    if (hint == ScriptRuntime.NumberClass) {
      Context cx = Context.getContext();
      if (cx.getLanguageVersion() == 120)
        return Long.valueOf(this.length); 
    } 
    return super.getDefaultValue(hint);
  }
  
  private ScriptableObject defaultIndexPropertyDescriptor(Object value) {
    Scriptable scope = getParentScope();
    if (scope == null)
      scope = this; 
    ScriptableObject desc = new NativeObject();
    ScriptRuntime.setBuiltinProtoAndParent(desc, scope, TopLevel.Builtins.Object);
    desc.defineProperty("value", value, 0);
    desc.defineProperty("writable", Boolean.valueOf(true), 0);
    desc.defineProperty("enumerable", Boolean.valueOf(true), 0);
    desc.defineProperty("configurable", Boolean.valueOf(true), 0);
    return desc;
  }
  
  public int getAttributes(int index) {
    if (this.dense != null && index >= 0 && index < this.dense.length && this.dense[index] != NOT_FOUND)
      return 0; 
    return super.getAttributes(index);
  }
  
  protected ScriptableObject getOwnPropertyDescriptor(Context cx, Object id) {
    if (this.dense != null) {
      int index = toDenseIndex(id);
      if (0 <= index && index < this.dense.length && this.dense[index] != NOT_FOUND) {
        Object value = this.dense[index];
        return defaultIndexPropertyDescriptor(value);
      } 
    } 
    return super.getOwnPropertyDescriptor(cx, id);
  }
  
  protected void defineOwnProperty(Context cx, Object id, ScriptableObject desc, boolean checkValid) {
    if (this.dense != null) {
      Object[] values = this.dense;
      this.dense = null;
      this.denseOnly = false;
      for (int i = 0; i < values.length; i++) {
        if (values[i] != NOT_FOUND)
          put(i, this, values[i]); 
      } 
    } 
    long index = toArrayIndex(id);
    if (index >= this.length)
      this.length = index + 1L; 
    super.defineOwnProperty(cx, id, desc, checkValid);
  }
  
  private static Object jsConstructor(Context cx, Scriptable scope, Object[] args) {
    if (args.length == 0)
      return new NativeArray(0L); 
    if (cx.getLanguageVersion() == 120)
      return new NativeArray(args); 
    Object arg0 = args[0];
    if (args.length > 1 || !(arg0 instanceof Number))
      return new NativeArray(args); 
    long len = ScriptRuntime.toUint32(arg0);
    if (len != ((Number)arg0).doubleValue()) {
      String msg = ScriptRuntime.getMessage0("msg.arraylength.bad");
      throw ScriptRuntime.constructError("RangeError", msg);
    } 
    return new NativeArray(len);
  }
  
  public long getLength() {
    return this.length;
  }
  
  @Deprecated
  public long jsGet_length() {
    return getLength();
  }
  
  void setDenseOnly(boolean denseOnly) {
    if (denseOnly && !this.denseOnly)
      throw new IllegalArgumentException(); 
    this.denseOnly = denseOnly;
  }
  
  private void setLength(Object val) {
    if ((this.lengthAttr & 0x1) != 0)
      return; 
    double d = ScriptRuntime.toNumber(val);
    long longVal = ScriptRuntime.toUint32(d);
    if (longVal != d) {
      String msg = ScriptRuntime.getMessage0("msg.arraylength.bad");
      throw ScriptRuntime.constructError("RangeError", msg);
    } 
    if (this.denseOnly) {
      if (longVal < this.length) {
        Arrays.fill(this.dense, (int)longVal, this.dense.length, NOT_FOUND);
        this.length = longVal;
        return;
      } 
      if (longVal < 1431655764L && longVal < this.length * 1.5D && ensureCapacity((int)longVal)) {
        this.length = longVal;
        return;
      } 
      this.denseOnly = false;
    } 
    if (longVal < this.length)
      if (this.length - longVal > 4096L) {
        Object[] e = getIds();
        for (int i = 0; i < e.length; i++) {
          Object id = e[i];
          if (id instanceof String) {
            String strId = (String)id;
            long index = toArrayIndex(strId);
            if (index >= longVal)
              delete(strId); 
          } else {
            int index = ((Integer)id).intValue();
            if (index >= longVal)
              delete(index); 
          } 
        } 
      } else {
        long i;
        for (i = longVal; i < this.length; i++)
          deleteElem(this, i); 
      }  
    this.length = longVal;
  }
  
  static long getLengthProperty(Context cx, Scriptable obj) {
    if (obj instanceof NativeString)
      return ((NativeString)obj).getLength(); 
    if (obj instanceof NativeArray)
      return ((NativeArray)obj).getLength(); 
    Object len = ScriptableObject.getProperty(obj, "length");
    if (len == Scriptable.NOT_FOUND)
      return 0L; 
    return ScriptRuntime.toUint32(len);
  }
  
  private static Object setLengthProperty(Context cx, Scriptable target, long length) {
    Object len = ScriptRuntime.wrapNumber(length);
    ScriptableObject.putProperty(target, "length", len);
    return len;
  }
  
  private static void deleteElem(Scriptable target, long index) {
    int i = (int)index;
    if (i == index) {
      target.delete(i);
    } else {
      target.delete(Long.toString(index));
    } 
  }
  
  private static Object getElem(Context cx, Scriptable target, long index) {
    Object elem = getRawElem(target, index);
    return (elem != Scriptable.NOT_FOUND) ? elem : Undefined.instance;
  }
  
  private static Object getRawElem(Scriptable target, long index) {
    if (index > 2147483647L)
      return ScriptableObject.getProperty(target, Long.toString(index)); 
    return ScriptableObject.getProperty(target, (int)index);
  }
  
  private static void defineElem(Context cx, Scriptable target, long index, Object value) {
    if (index > 2147483647L) {
      String id = Long.toString(index);
      target.put(id, target, value);
    } else {
      target.put((int)index, target, value);
    } 
  }
  
  private static void setElem(Context cx, Scriptable target, long index, Object value) {
    if (index > 2147483647L) {
      String id = Long.toString(index);
      ScriptableObject.putProperty(target, id, value);
    } else {
      ScriptableObject.putProperty(target, (int)index, value);
    } 
  }
  
  private static void setRawElem(Context cx, Scriptable target, long index, Object value) {
    if (value == NOT_FOUND) {
      deleteElem(target, index);
    } else {
      setElem(cx, target, index, value);
    } 
  }
  
  private static String toStringHelper(Context cx, Scriptable scope, Scriptable thisObj, boolean toSource, boolean toLocale) {
    String separator;
    boolean toplevel, iterating;
    long length = getLengthProperty(cx, thisObj);
    StringBuilder result = new StringBuilder(256);
    if (toSource) {
      result.append('[');
      separator = ", ";
    } else {
      separator = ",";
    } 
    boolean haslast = false;
    long i = 0L;
    if (cx.iterating == null) {
      toplevel = true;
      iterating = false;
      cx.iterating = new ObjToIntMap(31);
    } else {
      toplevel = false;
      iterating = cx.iterating.has(thisObj);
    } 
    try {
      if (!iterating) {
        cx.iterating.put(thisObj, 0);
        boolean skipUndefinedAndNull = (!toSource || cx.getLanguageVersion() < 150);
        for (i = 0L; i < length; i++) {
          if (i > 0L)
            result.append(separator); 
          Object elem = getRawElem(thisObj, i);
          if (elem == NOT_FOUND || (skipUndefinedAndNull && (elem == null || elem == Undefined.instance))) {
            haslast = false;
          } else {
            haslast = true;
            if (toSource) {
              result.append(ScriptRuntime.uneval(cx, scope, elem));
            } else if (elem instanceof String) {
              String s = (String)elem;
              if (toSource) {
                result.append('"');
                result.append(ScriptRuntime.escapeString(s));
                result.append('"');
              } else {
                result.append(s);
              } 
            } else {
              if (toLocale) {
                Callable fun = ScriptRuntime.getPropFunctionAndThis(elem, "toLocaleString", cx, scope);
                Scriptable funThis = ScriptRuntime.lastStoredScriptable(cx);
                elem = fun.call(cx, scope, funThis, ScriptRuntime.emptyArgs);
              } 
              result.append(ScriptRuntime.toString(elem));
            } 
          } 
        } 
      } 
    } finally {
      if (toplevel)
        cx.iterating = null; 
    } 
    if (toSource)
      if (!haslast && i > 0L) {
        result.append(", ]");
      } else {
        result.append(']');
      }  
    return result.toString();
  }
  
  private static String js_join(Context cx, Scriptable thisObj, Object[] args) {
    long llength = getLengthProperty(cx, thisObj);
    int length = (int)llength;
    if (llength != length)
      throw Context.reportRuntimeError1("msg.arraylength.too.big", String.valueOf(llength)); 
    String separator = (args.length < 1 || args[0] == Undefined.instance) ? "," : ScriptRuntime.toString(args[0]);
    if (thisObj instanceof NativeArray) {
      NativeArray na = (NativeArray)thisObj;
      if (na.denseOnly) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int k = 0; k < length; k++) {
          if (k != 0)
            stringBuilder.append(separator); 
          if (k < na.dense.length) {
            Object temp = na.dense[k];
            if (temp != null && temp != Undefined.instance && temp != Scriptable.NOT_FOUND)
              stringBuilder.append(ScriptRuntime.toString(temp)); 
          } 
        } 
        return stringBuilder.toString();
      } 
    } 
    if (length == 0)
      return ""; 
    String[] buf = new String[length];
    int total_size = 0;
    for (int i = 0; i != length; i++) {
      Object temp = getElem(cx, thisObj, i);
      if (temp != null && temp != Undefined.instance) {
        String str = ScriptRuntime.toString(temp);
        total_size += str.length();
        buf[i] = str;
      } 
    } 
    total_size += (length - 1) * separator.length();
    StringBuilder sb = new StringBuilder(total_size);
    for (int j = 0; j != length; j++) {
      if (j != 0)
        sb.append(separator); 
      String str = buf[j];
      if (str != null)
        sb.append(str); 
    } 
    return sb.toString();
  }
  
  private static Scriptable js_reverse(Context cx, Scriptable thisObj, Object[] args) {
    if (thisObj instanceof NativeArray) {
      NativeArray na = (NativeArray)thisObj;
      if (na.denseOnly) {
        for (int k = 0, j = (int)na.length - 1; k < j; k++, j--) {
          Object temp = na.dense[k];
          na.dense[k] = na.dense[j];
          na.dense[j] = temp;
        } 
        return thisObj;
      } 
    } 
    long len = getLengthProperty(cx, thisObj);
    long half = len / 2L;
    long i;
    for (i = 0L; i < half; i++) {
      long j = len - i - 1L;
      Object temp1 = getRawElem(thisObj, i);
      Object temp2 = getRawElem(thisObj, j);
      setRawElem(cx, thisObj, i, temp2);
      setRawElem(cx, thisObj, j, temp1);
    } 
    return thisObj;
  }
  
  private static Scriptable js_sort(final Context cx, final Scriptable scope, Scriptable thisObj, Object[] args) {
    Comparator<Object> comparator;
    if (args.length > 0 && Undefined.instance != args[0]) {
      final Callable jsCompareFunction = ScriptRuntime.getValueFunctionAndThis(args[0], cx);
      final Scriptable funThis = ScriptRuntime.lastStoredScriptable(cx);
      final Object[] cmpBuf = new Object[2];
      comparator = new Comparator() {
          public int compare(Object x, Object y) {
            if (x == Scriptable.NOT_FOUND)
              return (y == Scriptable.NOT_FOUND) ? 0 : 1; 
            if (y == Scriptable.NOT_FOUND)
              return -1; 
            if (x == Undefined.instance)
              return (y == Undefined.instance) ? 0 : 1; 
            if (y == Undefined.instance)
              return -1; 
            cmpBuf[0] = x;
            cmpBuf[1] = y;
            Object ret = jsCompareFunction.call(cx, scope, funThis, cmpBuf);
            double d = ScriptRuntime.toNumber(ret);
            if (d < 0.0D)
              return -1; 
            if (d > 0.0D)
              return 1; 
            return 0;
          }
        };
    } else {
      comparator = new Comparator() {
          public int compare(Object x, Object y) {
            if (x == Scriptable.NOT_FOUND)
              return (y == Scriptable.NOT_FOUND) ? 0 : 1; 
            if (y == Scriptable.NOT_FOUND)
              return -1; 
            if (x == Undefined.instance)
              return (y == Undefined.instance) ? 0 : 1; 
            if (y == Undefined.instance)
              return -1; 
            String a = ScriptRuntime.toString(x);
            String b = ScriptRuntime.toString(y);
            return a.compareTo(b);
          }
        };
    } 
    long llength = getLengthProperty(cx, thisObj);
    int length = (int)llength;
    if (llength != length)
      throw Context.reportRuntimeError1("msg.arraylength.too.big", String.valueOf(llength)); 
    Object[] working = new Object[length];
    int i;
    for (i = 0; i != length; i++)
      working[i] = getRawElem(thisObj, i); 
    Arrays.sort(working, comparator);
    for (i = 0; i < length; i++)
      setRawElem(cx, thisObj, i, working[i]); 
    return thisObj;
  }
  
  private static Object js_push(Context cx, Scriptable thisObj, Object[] args) {
    if (thisObj instanceof NativeArray) {
      NativeArray na = (NativeArray)thisObj;
      if (na.denseOnly && na.ensureCapacity((int)na.length + args.length)) {
        for (int j = 0; j < args.length; j++)
          na.dense[(int)na.length++] = args[j]; 
        return ScriptRuntime.wrapNumber(na.length);
      } 
    } 
    long length = getLengthProperty(cx, thisObj);
    for (int i = 0; i < args.length; i++)
      setElem(cx, thisObj, length + i, args[i]); 
    length += args.length;
    Object lengthObj = setLengthProperty(cx, thisObj, length);
    if (cx.getLanguageVersion() == 120)
      return (args.length == 0) ? Undefined.instance : args[args.length - 1]; 
    return lengthObj;
  }
  
  private static Object js_pop(Context cx, Scriptable thisObj, Object[] args) {
    Object result;
    if (thisObj instanceof NativeArray) {
      NativeArray na = (NativeArray)thisObj;
      if (na.denseOnly && na.length > 0L) {
        na.length--;
        result = na.dense[(int)na.length];
        na.dense[(int)na.length] = NOT_FOUND;
        return result;
      } 
    } 
    long length = getLengthProperty(cx, thisObj);
    if (length > 0L) {
      length--;
      result = getElem(cx, thisObj, length);
      deleteElem(thisObj, length);
    } else {
      result = Undefined.instance;
    } 
    setLengthProperty(cx, thisObj, length);
    return result;
  }
  
  private static Object js_shift(Context cx, Scriptable thisObj, Object[] args) {
    Object result;
    if (thisObj instanceof NativeArray) {
      NativeArray na = (NativeArray)thisObj;
      if (na.denseOnly && na.length > 0L) {
        na.length--;
        Object object = na.dense[0];
        System.arraycopy(na.dense, 1, na.dense, 0, (int)na.length);
        na.dense[(int)na.length] = NOT_FOUND;
        return (object == NOT_FOUND) ? Undefined.instance : object;
      } 
    } 
    long length = getLengthProperty(cx, thisObj);
    if (length > 0L) {
      long i = 0L;
      length--;
      result = getElem(cx, thisObj, i);
      if (length > 0L)
        for (i = 1L; i <= length; i++) {
          Object temp = getRawElem(thisObj, i);
          setRawElem(cx, thisObj, i - 1L, temp);
        }  
      deleteElem(thisObj, length);
    } else {
      result = Undefined.instance;
    } 
    setLengthProperty(cx, thisObj, length);
    return result;
  }
  
  private static Object js_unshift(Context cx, Scriptable thisObj, Object[] args) {
    if (thisObj instanceof NativeArray) {
      NativeArray na = (NativeArray)thisObj;
      if (na.denseOnly && na.ensureCapacity((int)na.length + args.length)) {
        System.arraycopy(na.dense, 0, na.dense, args.length, (int)na.length);
        for (int i = 0; i < args.length; i++)
          na.dense[i] = args[i]; 
        na.length += args.length;
        return ScriptRuntime.wrapNumber(na.length);
      } 
    } 
    long length = getLengthProperty(cx, thisObj);
    int argc = args.length;
    if (args.length > 0) {
      if (length > 0L) {
        long last;
        for (last = length - 1L; last >= 0L; last--) {
          Object temp = getRawElem(thisObj, last);
          setRawElem(cx, thisObj, last + argc, temp);
        } 
      } 
      for (int i = 0; i < args.length; i++)
        setElem(cx, thisObj, i, args[i]); 
    } 
    length += args.length;
    return setLengthProperty(cx, thisObj, length);
  }
  
  private static Object js_splice(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
    long count;
    Object result;
    NativeArray na = null;
    boolean denseMode = false;
    if (thisObj instanceof NativeArray) {
      na = (NativeArray)thisObj;
      denseMode = na.denseOnly;
    } 
    scope = getTopLevelScope(scope);
    int argc = args.length;
    if (argc == 0)
      return cx.newArray(scope, 0); 
    long length = getLengthProperty(cx, thisObj);
    long begin = toSliceIndex(ScriptRuntime.toInteger(args[0]), length);
    argc--;
    if (args.length == 1) {
      count = length - begin;
    } else {
      double dcount = ScriptRuntime.toInteger(args[1]);
      if (dcount < 0.0D) {
        count = 0L;
      } else if (dcount > (length - begin)) {
        count = length - begin;
      } else {
        count = (long)dcount;
      } 
      argc--;
    } 
    long end = begin + count;
    if (count != 0L) {
      if (count == 1L && cx.getLanguageVersion() == 120) {
        result = getElem(cx, thisObj, begin);
      } else if (denseMode) {
        int intLen = (int)(end - begin);
        Object[] copy = new Object[intLen];
        System.arraycopy(na.dense, (int)begin, copy, 0, intLen);
        result = cx.newArray(scope, copy);
      } else {
        Scriptable resultArray = cx.newArray(scope, 0);
        long last;
        for (last = begin; last != end; last++) {
          Object temp = getRawElem(thisObj, last);
          if (temp != NOT_FOUND)
            setElem(cx, resultArray, last - begin, temp); 
        } 
        setLengthProperty(cx, resultArray, end - begin);
        result = resultArray;
      } 
    } else if (cx.getLanguageVersion() == 120) {
      result = Undefined.instance;
    } else {
      result = cx.newArray(scope, 0);
    } 
    long delta = argc - count;
    if (denseMode && length + delta < 2147483647L && na.ensureCapacity((int)(length + delta))) {
      System.arraycopy(na.dense, (int)end, na.dense, (int)(begin + argc), (int)(length - end));
      if (argc > 0)
        System.arraycopy(args, 2, na.dense, (int)begin, argc); 
      if (delta < 0L)
        Arrays.fill(na.dense, (int)(length + delta), (int)length, NOT_FOUND); 
      na.length = length + delta;
      return result;
    } 
    if (delta > 0L) {
      long last;
      for (last = length - 1L; last >= end; last--) {
        Object temp = getRawElem(thisObj, last);
        setRawElem(cx, thisObj, last + delta, temp);
      } 
    } else if (delta < 0L) {
      long last;
      for (last = end; last < length; last++) {
        Object temp = getRawElem(thisObj, last);
        setRawElem(cx, thisObj, last + delta, temp);
      } 
      long k;
      for (k = length + delta; k < length; k++)
        deleteElem(thisObj, k); 
    } 
    int argoffset = args.length - argc;
    for (int i = 0; i < argc; i++)
      setElem(cx, thisObj, begin + i, args[i + argoffset]); 
    setLengthProperty(cx, thisObj, length + delta);
    return result;
  }
  
  private static Scriptable js_concat(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
    scope = getTopLevelScope(scope);
    Scriptable result = cx.newArray(scope, 0);
    if (thisObj instanceof NativeArray && result instanceof NativeArray) {
      NativeArray denseThis = (NativeArray)thisObj;
      NativeArray denseResult = (NativeArray)result;
      if (denseThis.denseOnly && denseResult.denseOnly) {
        boolean canUseDense = true;
        int length = (int)denseThis.length;
        for (int j = 0; j < args.length && canUseDense; j++) {
          if (args[j] instanceof NativeArray) {
            NativeArray arg = (NativeArray)args[j];
            canUseDense = arg.denseOnly;
            length = (int)(length + arg.length);
          } else {
            length++;
          } 
        } 
        if (canUseDense && denseResult.ensureCapacity(length)) {
          System.arraycopy(denseThis.dense, 0, denseResult.dense, 0, (int)denseThis.length);
          int cursor = (int)denseThis.length;
          for (int k = 0; k < args.length && canUseDense; k++) {
            if (args[k] instanceof NativeArray) {
              NativeArray arg = (NativeArray)args[k];
              System.arraycopy(arg.dense, 0, denseResult.dense, cursor, (int)arg.length);
              cursor += (int)arg.length;
            } else {
              denseResult.dense[cursor++] = args[k];
            } 
          } 
          denseResult.length = length;
          return result;
        } 
      } 
    } 
    long slot = 0L;
    if (js_isArray(thisObj)) {
      long length = getLengthProperty(cx, thisObj);
      for (slot = 0L; slot < length; slot++) {
        Object temp = getRawElem(thisObj, slot);
        if (temp != NOT_FOUND)
          defineElem(cx, result, slot, temp); 
      } 
    } else {
      defineElem(cx, result, slot++, thisObj);
    } 
    for (int i = 0; i < args.length; i++) {
      if (js_isArray(args[i])) {
        Scriptable arg = (Scriptable)args[i];
        long length = getLengthProperty(cx, arg);
        for (long j = 0L; j < length; j++, slot++) {
          Object temp = getRawElem(arg, j);
          if (temp != NOT_FOUND)
            defineElem(cx, result, slot, temp); 
        } 
      } else {
        defineElem(cx, result, slot++, args[i]);
      } 
    } 
    setLengthProperty(cx, result, slot);
    return result;
  }
  
  private Scriptable js_slice(Context cx, Scriptable thisObj, Object[] args) {
    long begin, end;
    Scriptable scope = getTopLevelScope(this);
    Scriptable result = cx.newArray(scope, 0);
    long length = getLengthProperty(cx, thisObj);
    if (args.length == 0) {
      begin = 0L;
      end = length;
    } else {
      begin = toSliceIndex(ScriptRuntime.toInteger(args[0]), length);
      if (args.length == 1 || args[1] == Undefined.instance) {
        end = length;
      } else {
        end = toSliceIndex(ScriptRuntime.toInteger(args[1]), length);
      } 
    } 
    long slot;
    for (slot = begin; slot < end; slot++) {
      Object temp = getRawElem(thisObj, slot);
      if (temp != NOT_FOUND)
        defineElem(cx, result, slot - begin, temp); 
    } 
    setLengthProperty(cx, result, Math.max(0L, end - begin));
    return result;
  }
  
  private static long toSliceIndex(double value, long length) {
    long result;
    if (value < 0.0D) {
      if (value + length < 0.0D) {
        result = 0L;
      } else {
        result = (long)(value + length);
      } 
    } else if (value > length) {
      result = length;
    } else {
      result = (long)value;
    } 
    return result;
  }
  
  private static Object js_indexOf(Context cx, Scriptable thisObj, Object[] args) {
    long start;
    Object compareTo = (args.length > 0) ? args[0] : Undefined.instance;
    long length = getLengthProperty(cx, thisObj);
    if (args.length < 2) {
      start = 0L;
    } else {
      start = (long)ScriptRuntime.toInteger(args[1]);
      if (start < 0L) {
        start += length;
        if (start < 0L)
          start = 0L; 
      } 
      if (start > length - 1L)
        return NEGATIVE_ONE; 
    } 
    if (thisObj instanceof NativeArray) {
      NativeArray na = (NativeArray)thisObj;
      if (na.denseOnly) {
        Scriptable proto = na.getPrototype();
        for (int j = (int)start; j < length; j++) {
          Object val = na.dense[j];
          if (val == NOT_FOUND && proto != null)
            val = ScriptableObject.getProperty(proto, j); 
          if (val != NOT_FOUND && ScriptRuntime.shallowEq(val, compareTo))
            return Long.valueOf(j); 
        } 
        return NEGATIVE_ONE;
      } 
    } 
    long i;
    for (i = start; i < length; i++) {
      Object val = getRawElem(thisObj, i);
      if (val != NOT_FOUND && ScriptRuntime.shallowEq(val, compareTo))
        return Long.valueOf(i); 
    } 
    return NEGATIVE_ONE;
  }
  
  private static Object js_lastIndexOf(Context cx, Scriptable thisObj, Object[] args) {
    long start;
    Object compareTo = (args.length > 0) ? args[0] : Undefined.instance;
    long length = getLengthProperty(cx, thisObj);
    if (args.length < 2) {
      start = length - 1L;
    } else {
      start = (long)ScriptRuntime.toInteger(args[1]);
      if (start >= length) {
        start = length - 1L;
      } else if (start < 0L) {
        start += length;
      } 
      if (start < 0L)
        return NEGATIVE_ONE; 
    } 
    if (thisObj instanceof NativeArray) {
      NativeArray na = (NativeArray)thisObj;
      if (na.denseOnly) {
        Scriptable proto = na.getPrototype();
        for (int j = (int)start; j >= 0; j--) {
          Object val = na.dense[j];
          if (val == NOT_FOUND && proto != null)
            val = ScriptableObject.getProperty(proto, j); 
          if (val != NOT_FOUND && ScriptRuntime.shallowEq(val, compareTo))
            return Long.valueOf(j); 
        } 
        return NEGATIVE_ONE;
      } 
    } 
    long i;
    for (i = start; i >= 0L; i--) {
      Object val = getRawElem(thisObj, i);
      if (val != NOT_FOUND && ScriptRuntime.shallowEq(val, compareTo))
        return Long.valueOf(i); 
    } 
    return NEGATIVE_ONE;
  }
  
  private static Object iterativeMethod(Context cx, int id, Scriptable scope, Scriptable thisObj, Object[] args) {
    Scriptable thisArg;
    long length = getLengthProperty(cx, thisObj);
    Object callbackArg = (args.length > 0) ? args[0] : Undefined.instance;
    if (callbackArg == null || !(callbackArg instanceof Function))
      throw ScriptRuntime.notFunctionError(callbackArg); 
    if ((id == 22 || id == 23) && !(callbackArg instanceof NativeFunction))
      throw ScriptRuntime.notFunctionError(callbackArg); 
    Function f = (Function)callbackArg;
    Scriptable parent = ScriptableObject.getTopLevelScope(f);
    if (args.length < 2 || args[1] == null || args[1] == Undefined.instance) {
      thisArg = parent;
    } else {
      thisArg = ScriptRuntime.toObject(cx, scope, args[1]);
    } 
    if ((22 == id || 23 == id) && thisArg == thisObj)
      throw ScriptRuntime.typeError("Array.prototype method called on null or undefined"); 
    Scriptable array = null;
    if (id == 18 || id == 20) {
      int resultLength = (id == 20) ? (int)length : 0;
      array = cx.newArray(scope, resultLength);
    } 
    long j = 0L;
    long i;
    for (i = 0L; i < length; i++) {
      Object[] innerArgs = new Object[3];
      Object elem = getRawElem(thisObj, i);
      if (elem != Scriptable.NOT_FOUND) {
        innerArgs[0] = elem;
        innerArgs[1] = Long.valueOf(i);
        innerArgs[2] = thisObj;
        Object result = f.call(cx, parent, thisArg, innerArgs);
        switch (id) {
          case 17:
            if (!ScriptRuntime.toBoolean(result))
              return Boolean.FALSE; 
            break;
          case 18:
            if (ScriptRuntime.toBoolean(result))
              defineElem(cx, array, j++, innerArgs[0]); 
            break;
          case 20:
            defineElem(cx, array, i, result);
            break;
          case 21:
            if (ScriptRuntime.toBoolean(result))
              return Boolean.TRUE; 
            break;
          case 22:
            if (ScriptRuntime.toBoolean(result))
              return elem; 
            break;
          case 23:
            if (ScriptRuntime.toBoolean(result))
              return ScriptRuntime.wrapNumber(i); 
            break;
        } 
      } 
    } 
    switch (id) {
      case 17:
        return Boolean.TRUE;
      case 18:
      case 20:
        return array;
      case 21:
        return Boolean.FALSE;
      case 23:
        return ScriptRuntime.wrapNumber(-1.0D);
    } 
    return Undefined.instance;
  }
  
  private static Object reduceMethod(Context cx, int id, Scriptable scope, Scriptable thisObj, Object[] args) {
    long length = getLengthProperty(cx, thisObj);
    Object callbackArg = (args.length > 0) ? args[0] : Undefined.instance;
    if (callbackArg == null || !(callbackArg instanceof Function))
      throw ScriptRuntime.notFunctionError(callbackArg); 
    Function f = (Function)callbackArg;
    Scriptable parent = ScriptableObject.getTopLevelScope(f);
    boolean movingLeft = (id == 24);
    Object value = (args.length > 1) ? args[1] : Scriptable.NOT_FOUND;
    long i;
    for (i = 0L; i < length; i++) {
      long index = movingLeft ? i : (length - 1L - i);
      Object elem = getRawElem(thisObj, index);
      if (elem != Scriptable.NOT_FOUND)
        if (value == Scriptable.NOT_FOUND) {
          value = elem;
        } else {
          Object[] innerArgs = { value, elem, Long.valueOf(index), thisObj };
          value = f.call(cx, parent, parent, innerArgs);
        }  
    } 
    if (value == Scriptable.NOT_FOUND)
      throw ScriptRuntime.typeError0("msg.empty.array.reduce"); 
    return value;
  }
  
  private static boolean js_isArray(Object o) {
    if (!(o instanceof Scriptable))
      return false; 
    return "Array".equals(((Scriptable)o).getClassName());
  }
  
  public boolean contains(Object o) {
    return (indexOf(o) > -1);
  }
  
  public Object[] toArray() {
    return toArray(ScriptRuntime.emptyArgs);
  }
  
  public Object[] toArray(Object[] a) {
    long longLen = this.length;
    if (longLen > 2147483647L)
      throw new IllegalStateException(); 
    int len = (int)longLen;
    Object[] array = (a.length >= len) ? a : (Object[])Array.newInstance(a.getClass().getComponentType(), len);
    for (int i = 0; i < len; i++)
      array[i] = get(i); 
    return array;
  }
  
  public boolean containsAll(Collection c) {
    for (Object aC : c) {
      if (!contains(aC))
        return false; 
    } 
    return true;
  }
  
  public int size() {
    long longLen = this.length;
    if (longLen > 2147483647L)
      throw new IllegalStateException(); 
    return (int)longLen;
  }
  
  public boolean isEmpty() {
    return (this.length == 0L);
  }
  
  public Object get(long index) {
    if (index < 0L || index >= this.length)
      throw new IndexOutOfBoundsException(); 
    Object value = getRawElem(this, index);
    if (value == Scriptable.NOT_FOUND || value == Undefined.instance)
      return null; 
    if (value instanceof Wrapper)
      return ((Wrapper)value).unwrap(); 
    return value;
  }
  
  public Object get(int index) {
    return get(index);
  }
  
  public int indexOf(Object o) {
    long longLen = this.length;
    if (longLen > 2147483647L)
      throw new IllegalStateException(); 
    int len = (int)longLen;
    if (o == null) {
      for (int i = 0; i < len; i++) {
        if (get(i) == null)
          return i; 
      } 
    } else {
      for (int i = 0; i < len; i++) {
        if (o.equals(get(i)))
          return i; 
      } 
    } 
    return -1;
  }
  
  public int lastIndexOf(Object o) {
    long longLen = this.length;
    if (longLen > 2147483647L)
      throw new IllegalStateException(); 
    int len = (int)longLen;
    if (o == null) {
      for (int i = len - 1; i >= 0; i--) {
        if (get(i) == null)
          return i; 
      } 
    } else {
      for (int i = len - 1; i >= 0; i--) {
        if (o.equals(get(i)))
          return i; 
      } 
    } 
    return -1;
  }
  
  public Iterator iterator() {
    return listIterator(0);
  }
  
  public ListIterator listIterator() {
    return listIterator(0);
  }
  
  public ListIterator listIterator(final int start) {
    long longLen = this.length;
    if (longLen > 2147483647L)
      throw new IllegalStateException(); 
    final int len = (int)longLen;
    if (start < 0 || start > len)
      throw new IndexOutOfBoundsException("Index: " + start); 
    return new ListIterator() {
        int cursor = start;
        
        public boolean hasNext() {
          return (this.cursor < len);
        }
        
        public Object next() {
          if (this.cursor == len)
            throw new NoSuchElementException(); 
          return NativeArray.this.get(this.cursor++);
        }
        
        public boolean hasPrevious() {
          return (this.cursor > 0);
        }
        
        public Object previous() {
          if (this.cursor == 0)
            throw new NoSuchElementException(); 
          return NativeArray.this.get(--this.cursor);
        }
        
        public int nextIndex() {
          return this.cursor;
        }
        
        public int previousIndex() {
          return this.cursor - 1;
        }
        
        public void remove() {
          throw new UnsupportedOperationException();
        }
        
        public void add(Object o) {
          throw new UnsupportedOperationException();
        }
        
        public void set(Object o) {
          throw new UnsupportedOperationException();
        }
      };
  }
  
  public boolean add(Object o) {
    throw new UnsupportedOperationException();
  }
  
  public boolean remove(Object o) {
    throw new UnsupportedOperationException();
  }
  
  public boolean addAll(Collection c) {
    throw new UnsupportedOperationException();
  }
  
  public boolean removeAll(Collection c) {
    throw new UnsupportedOperationException();
  }
  
  public boolean retainAll(Collection c) {
    throw new UnsupportedOperationException();
  }
  
  public void clear() {
    throw new UnsupportedOperationException();
  }
  
  public void add(int index, Object element) {
    throw new UnsupportedOperationException();
  }
  
  public boolean addAll(int index, Collection c) {
    throw new UnsupportedOperationException();
  }
  
  public Object set(int index, Object element) {
    throw new UnsupportedOperationException();
  }
  
  public Object remove(int index) {
    throw new UnsupportedOperationException();
  }
  
  public List subList(int fromIndex, int toIndex) {
    throw new UnsupportedOperationException();
  }
  
  protected int findPrototypeId(String s) {
    int c, id = 0;
    String X = null;
    switch (s.length()) {
      case 3:
        c = s.charAt(0);
        if (c == 109) {
          if (s.charAt(2) == 'p' && s.charAt(1) == 'a') {
            id = 20;
            return id;
          } 
          break;
        } 
        if (c == 112 && s.charAt(2) == 'p' && s.charAt(1) == 'o') {
          id = 9;
          return id;
        } 
        break;
      case 4:
        switch (s.charAt(2)) {
          case 'i':
            X = "join";
            id = 5;
            break;
          case 'm':
            X = "some";
            id = 21;
            break;
          case 'n':
            X = "find";
            id = 22;
            break;
          case 'r':
            X = "sort";
            id = 7;
            break;
          case 's':
            X = "push";
            id = 8;
            break;
        } 
        break;
      case 5:
        c = s.charAt(1);
        if (c == 104) {
          X = "shift";
          id = 10;
          break;
        } 
        if (c == 108) {
          X = "slice";
          id = 14;
          break;
        } 
        if (c == 118) {
          X = "every";
          id = 17;
        } 
        break;
      case 6:
        switch (s.charAt(0)) {
          case 'c':
            X = "concat";
            id = 13;
            break;
          case 'f':
            X = "filter";
            id = 18;
            break;
          case 'r':
            X = "reduce";
            id = 24;
            break;
          case 's':
            X = "splice";
            id = 12;
            break;
        } 
        break;
      case 7:
        switch (s.charAt(0)) {
          case 'f':
            X = "forEach";
            id = 19;
            break;
          case 'i':
            X = "indexOf";
            id = 15;
            break;
          case 'r':
            X = "reverse";
            id = 6;
            break;
          case 'u':
            X = "unshift";
            id = 11;
            break;
        } 
        break;
      case 8:
        c = s.charAt(3);
        if (c == 111) {
          X = "toSource";
          id = 4;
          break;
        } 
        if (c == 116) {
          X = "toString";
          id = 2;
        } 
        break;
      case 9:
        X = "findIndex";
        id = 23;
        break;
      case 11:
        c = s.charAt(0);
        if (c == 99) {
          X = "constructor";
          id = 1;
          break;
        } 
        if (c == 108) {
          X = "lastIndexOf";
          id = 16;
          break;
        } 
        if (c == 114) {
          X = "reduceRight";
          id = 25;
        } 
        break;
      case 14:
        X = "toLocaleString";
        id = 3;
        break;
    } 
    if (X != null && X != s && !X.equals(s))
      id = 0; 
    return id;
  }
  
  private static final int DEFAULT_INITIAL_CAPACITY = 10;
  private static final double GROW_FACTOR = 1.5D;
  private static final int MAX_PRE_GROW_SIZE = 1431655764;
}
