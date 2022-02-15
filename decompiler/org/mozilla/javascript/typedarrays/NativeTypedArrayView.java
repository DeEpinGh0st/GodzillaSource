package org.mozilla.javascript.typedarrays;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.RandomAccess;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ExternalArrayData;
import org.mozilla.javascript.IdFunctionObject;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Undefined;








public abstract class NativeTypedArrayView<T>
  extends NativeArrayBufferView
  implements List<T>, RandomAccess, ExternalArrayData
{
  protected final int length;
  private static final int Id_constructor = 1;
  private static final int Id_get = 2;
  private static final int Id_set = 3;
  private static final int Id_subarray = 4;
  protected static final int MAX_PROTOTYPE_ID = 4;
  private static final int Id_length = 10;
  private static final int Id_BYTES_PER_ELEMENT = 11;
  private static final int MAX_INSTANCE_ID = 11;
  
  protected NativeTypedArrayView() {
    this.length = 0;
  }

  
  protected NativeTypedArrayView(NativeArrayBuffer ab, int off, int len, int byteLen) {
    super(ab, off, byteLen);
    this.length = len;
  }




  
  public Object get(int index, Scriptable start) {
    return js_get(index);
  }


  
  public boolean has(int index, Scriptable start) {
    return (index > 0 && index < this.length);
  }


  
  public void put(int index, Scriptable start, Object val) {
    js_set(index, val);
  }



  
  public void delete(int index) {}


  
  public Object[] getIds() {
    Object[] ret = new Object[this.length];
    for (int i = 0; i < this.length; i++) {
      ret[i] = Integer.valueOf(i);
    }
    return ret;
  }



  
  protected boolean checkIndex(int index) {
    return (index < 0 || index >= this.length);
  }

  
  public abstract int getBytesPerElement();

  
  protected abstract NativeTypedArrayView construct(NativeArrayBuffer paramNativeArrayBuffer, int paramInt1, int paramInt2);
  
  protected abstract Object js_get(int paramInt);
  
  protected abstract Object js_set(int paramInt, Object paramObject);
  
  protected abstract NativeTypedArrayView realThis(Scriptable paramScriptable, IdFunctionObject paramIdFunctionObject);
  
  private NativeArrayBuffer makeArrayBuffer(Context cx, Scriptable scope, int length) {
    return (NativeArrayBuffer)cx.newObject(scope, "ArrayBuffer", new Object[] { Integer.valueOf(length) });
  }


  
  private NativeTypedArrayView js_constructor(Context cx, Scriptable scope, Object[] args) {
    if (!isArg(args, 0)) {
      return construct(NativeArrayBuffer.EMPTY_BUFFER, 0, 0);
    }
    if (args[0] instanceof Number || args[0] instanceof String) {
      
      int length = ScriptRuntime.toInt32(args[0]);
      NativeArrayBuffer buffer = makeArrayBuffer(cx, scope, length * getBytesPerElement());
      return construct(buffer, 0, length);
    } 
    if (args[0] instanceof NativeTypedArrayView) {
      
      NativeTypedArrayView src = (NativeTypedArrayView)args[0];
      NativeArrayBuffer na = makeArrayBuffer(cx, scope, src.length * getBytesPerElement());
      NativeTypedArrayView v = construct(na, 0, src.length);
      
      for (int i = 0; i < src.length; i++) {
        v.js_set(i, src.js_get(i));
      }
      return v;
    } 
    if (args[0] instanceof NativeArrayBuffer) {
      int byteLen;
      NativeArrayBuffer na = (NativeArrayBuffer)args[0];
      int byteOff = isArg(args, 1) ? ScriptRuntime.toInt32(args[1]) : 0;

      
      if (isArg(args, 2)) {
        byteLen = ScriptRuntime.toInt32(args[2]) * getBytesPerElement();
      } else {
        byteLen = na.getLength() - byteOff;
      } 
      
      if (byteOff < 0 || byteOff > na.buffer.length) {
        throw ScriptRuntime.constructError("RangeError", "offset out of range");
      }
      if (byteLen < 0 || byteOff + byteLen > na.buffer.length) {
        throw ScriptRuntime.constructError("RangeError", "length out of range");
      }
      if (byteOff % getBytesPerElement() != 0) {
        throw ScriptRuntime.constructError("RangeError", "offset must be a multiple of the byte size");
      }
      if (byteLen % getBytesPerElement() != 0) {
        throw ScriptRuntime.constructError("RangeError", "offset and buffer must be a multiple of the byte size");
      }
      
      return construct(na, byteOff, byteLen / getBytesPerElement());
    } 
    if (args[0] instanceof NativeArray) {
      
      List l = (List)args[0];
      NativeArrayBuffer na = makeArrayBuffer(cx, scope, l.size() * getBytesPerElement());
      NativeTypedArrayView v = construct(na, 0, l.size());
      int p = 0;
      for (Object o : l) {
        v.js_set(p, o);
        p++;
      } 
      return v;
    } 
    
    throw ScriptRuntime.constructError("Error", "invalid argument");
  }


  
  private void setRange(NativeTypedArrayView v, int off) {
    if (off >= this.length) {
      throw ScriptRuntime.constructError("RangeError", "offset out of range");
    }
    
    if (v.length > this.length - off) {
      throw ScriptRuntime.constructError("RangeError", "source array too long");
    }
    
    if (v.arrayBuffer == this.arrayBuffer) {
      
      Object[] tmp = new Object[v.length]; int i;
      for (i = 0; i < v.length; i++) {
        tmp[i] = v.js_get(i);
      }
      for (i = 0; i < v.length; i++) {
        js_set(i + off, tmp[i]);
      }
    } else {
      for (int i = 0; i < v.length; i++) {
        js_set(i + off, v.js_get(i));
      }
    } 
  }

  
  private void setRange(NativeArray a, int off) {
    if (off > this.length) {
      throw ScriptRuntime.constructError("RangeError", "offset out of range");
    }
    if (off + a.size() > this.length) {
      throw ScriptRuntime.constructError("RangeError", "offset + length out of range");
    }
    
    int pos = off;
    for (Object val : a) {
      js_set(pos, val);
      pos++;
    } 
  }

  
  private Object js_subarray(Context cx, Scriptable scope, int s, int e) {
    int start = (s < 0) ? (this.length + s) : s;
    int end = (e < 0) ? (this.length + e) : e;

    
    start = Math.max(0, start);
    end = Math.min(this.length, end);
    int len = Math.max(0, end - start);
    int byteOff = Math.min(start * getBytesPerElement(), this.arrayBuffer.getLength());
    
    return cx.newObject(scope, getClassName(), new Object[] { this.arrayBuffer, Integer.valueOf(byteOff), Integer.valueOf(len) });
  }







  
  public Object execIdCall(IdFunctionObject f, Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
    if (!f.hasTag(getClassName())) {
      return super.execIdCall(f, cx, scope, thisObj, args);
    }
    int id = f.methodId();
    switch (id) {
      case 1:
        return js_constructor(cx, scope, args);
      
      case 2:
        if (args.length > 0) {
          return realThis(thisObj, f).js_get(ScriptRuntime.toInt32(args[0]));
        }
        throw ScriptRuntime.constructError("Error", "invalid arguments");

      
      case 3:
        if (args.length > 0) {
          NativeTypedArrayView self = realThis(thisObj, f);
          if (args[0] instanceof NativeTypedArrayView) {
            int offset = isArg(args, 1) ? ScriptRuntime.toInt32(args[1]) : 0;
            self.setRange((NativeTypedArrayView)args[0], offset);
            return Undefined.instance;
          } 
          if (args[0] instanceof NativeArray) {
            int offset = isArg(args, 1) ? ScriptRuntime.toInt32(args[1]) : 0;
            self.setRange((NativeArray)args[0], offset);
            return Undefined.instance;
          } 
          if (args[0] instanceof Scriptable)
          {
            return Undefined.instance;
          }
          if (isArg(args, 2)) {
            return self.js_set(ScriptRuntime.toInt32(args[0]), args[1]);
          }
        } 
        throw ScriptRuntime.constructError("Error", "invalid arguments");
      
      case 4:
        if (args.length > 0) {
          NativeTypedArrayView self = realThis(thisObj, f);
          int start = ScriptRuntime.toInt32(args[0]);
          int end = isArg(args, 1) ? ScriptRuntime.toInt32(args[1]) : self.length;
          return self.js_subarray(cx, scope, start, end);
        } 
        throw ScriptRuntime.constructError("Error", "invalid arguments");
    } 
    
    throw new IllegalArgumentException(String.valueOf(id));
  }


  
  protected void initPrototypeId(int id) {
    String s;
    int arity;
    switch (id) { case 1:
        arity = 1; s = "constructor"; break;
      case 2: arity = 1; s = "get"; break;
      case 3: arity = 2; s = "set"; break;
      case 4: arity = 2; s = "subarray"; break;
      default: throw new IllegalArgumentException(String.valueOf(id)); }
    
    initPrototypeMethod(getClassName(), id, s, arity);
  }






  
  protected int findPrototypeId(String s) {
    int id = 0; String X = null;
    int s_length = s.length();
    if (s_length == 3)
    { int c = s.charAt(0);
      if (c == 103) { if (s.charAt(2) == 't' && s.charAt(1) == 'e') { id = 2;







          
          return id; }  } else if (c == 115 && s.charAt(2) == 't' && s.charAt(1) == 'e') { id = 3; return id; }  } else if (s_length == 8) { X = "subarray"; id = 4; } else if (s_length == 11) { X = "constructor"; id = 1; }  if (X != null && X != s && !X.equals(s)) id = 0;  return id;
  }
















  
  protected void fillConstructorProperties(IdFunctionObject ctor) {
    ctor.put("BYTES_PER_ELEMENT", (Scriptable)ctor, ScriptRuntime.wrapInt(getBytesPerElement()));
  }




  
  protected int getMaxInstanceId() {
    return 11;
  }


  
  protected String getInstanceIdName(int id) {
    switch (id) { case 10:
        return "length";
      case 11: return "BYTES_PER_ELEMENT"; }
     return super.getInstanceIdName(id);
  }



  
  protected Object getInstanceIdValue(int id) {
    switch (id) {
      case 10:
        return ScriptRuntime.wrapInt(this.length);
      case 11:
        return ScriptRuntime.wrapInt(getBytesPerElement());
    } 
    return super.getInstanceIdValue(id);
  }







  
  protected int findInstanceIdInfo(String s) {
    int id = 0; String X = null;
    int s_length = s.length();
    if (s_length == 6) { X = "length"; id = 10; }
    else if (s_length == 17) { X = "BYTES_PER_ELEMENT"; id = 11; }
     if (X != null && X != s && !X.equals(s)) id = 0;


    
    if (id == 0) {
      return super.findInstanceIdInfo(s);
    }
    return instanceIdInfo(5, id);
  }














  
  public Object getArrayElement(int index) {
    return js_get(index);
  }


  
  public void setArrayElement(int index, Object value) {
    js_set(index, value);
  }

  
  public int getArrayLength() {
    return this.length;
  }




  
  public int size() {
    return this.length;
  }


  
  public boolean isEmpty() {
    return (this.length == 0);
  }


  
  public boolean contains(Object o) {
    return (indexOf(o) >= 0);
  }


  
  public boolean containsAll(Collection<?> objects) {
    for (Object o : objects) {
      if (!contains(o)) {
        return false;
      }
    } 
    return true;
  }


  
  public int indexOf(Object o) {
    for (int i = 0; i < this.length; i++) {
      if (o.equals(js_get(i))) {
        return i;
      }
    } 
    return -1;
  }


  
  public int lastIndexOf(Object o) {
    for (int i = this.length - 1; i >= 0; i--) {
      if (o.equals(js_get(i))) {
        return i;
      }
    } 
    return -1;
  }


  
  public Object[] toArray() {
    Object[] a = new Object[this.length];
    for (int i = 0; i < this.length; i++) {
      a[i] = js_get(i);
    }
    return a;
  }



  
  public <U> U[] toArray(U[] ts) {
    U[] a;
    if (ts.length >= this.length) {
      a = ts;
    } else {
      a = (U[])Array.newInstance(ts.getClass().getComponentType(), this.length);
    } 
    
    for (int i = 0; i < this.length; i++) {
      try {
        a[i] = (U)js_get(i);
      } catch (ClassCastException cce) {
        throw new ArrayStoreException();
      } 
    } 
    return a;
  }


  
  public boolean equals(Object o) {
    try {
      NativeTypedArrayView<T> v = (NativeTypedArrayView<T>)o;
      if (this.length != v.length) {
        return false;
      }
      for (int i = 0; i < this.length; i++) {
        if (!js_get(i).equals(v.js_get(i))) {
          return false;
        }
      } 
      return true;
    } catch (ClassCastException cce) {
      return false;
    } 
  }


  
  public int hashCode() {
    int hc = 0;
    for (int i = 0; i < this.length; i++) {
      hc += js_get(i).hashCode();
    }
    return 0;
  }


  
  public Iterator<T> iterator() {
    return new NativeTypedArrayIterator<T>(this, 0);
  }


  
  public ListIterator<T> listIterator() {
    return new NativeTypedArrayIterator<T>(this, 0);
  }


  
  public ListIterator<T> listIterator(int start) {
    if (checkIndex(start)) {
      throw new IndexOutOfBoundsException();
    }
    return new NativeTypedArrayIterator<T>(this, start);
  }


  
  public List<T> subList(int i, int i2) {
    throw new UnsupportedOperationException();
  }


  
  public boolean add(T aByte) {
    throw new UnsupportedOperationException();
  }


  
  public void add(int i, T aByte) {
    throw new UnsupportedOperationException();
  }


  
  public boolean addAll(Collection<? extends T> bytes) {
    throw new UnsupportedOperationException();
  }


  
  public boolean addAll(int i, Collection<? extends T> bytes) {
    throw new UnsupportedOperationException();
  }


  
  public void clear() {
    throw new UnsupportedOperationException();
  }


  
  public T remove(int i) {
    throw new UnsupportedOperationException();
  }


  
  public boolean remove(Object o) {
    throw new UnsupportedOperationException();
  }


  
  public boolean removeAll(Collection<?> objects) {
    throw new UnsupportedOperationException();
  }


  
  public boolean retainAll(Collection<?> objects) {
    throw new UnsupportedOperationException();
  }
}
