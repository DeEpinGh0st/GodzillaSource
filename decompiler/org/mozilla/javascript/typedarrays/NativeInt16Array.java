package org.mozilla.javascript.typedarrays;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.IdFunctionObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Undefined;
















public class NativeInt16Array
  extends NativeTypedArrayView<Short>
{
  private static final long serialVersionUID = -8592870435287581398L;
  private static final String CLASS_NAME = "Int16Array";
  private static final int BYTES_PER_ELEMENT = 2;
  
  public NativeInt16Array() {}
  
  public NativeInt16Array(NativeArrayBuffer ab, int off, int len) {
    super(ab, off, len, len * 2);
  }

  
  public NativeInt16Array(int len) {
    this(new NativeArrayBuffer(len * 2), 0, len);
  }


  
  public String getClassName() {
    return "Int16Array";
  }

  
  public static void init(Context cx, Scriptable scope, boolean sealed) {
    NativeInt16Array a = new NativeInt16Array();
    a.exportAsJSClass(4, scope, sealed);
  }


  
  protected NativeTypedArrayView construct(NativeArrayBuffer ab, int off, int len) {
    return new NativeInt16Array(ab, off, len);
  }


  
  public int getBytesPerElement() {
    return 2;
  }


  
  protected NativeTypedArrayView realThis(Scriptable thisObj, IdFunctionObject f) {
    if (!(thisObj instanceof NativeInt16Array)) {
      throw incompatibleCallError(f);
    }
    return (NativeInt16Array)thisObj;
  }


  
  protected Object js_get(int index) {
    if (checkIndex(index)) {
      return Undefined.instance;
    }
    return ByteIo.readInt16(this.arrayBuffer.buffer, index * 2 + this.offset, false);
  }


  
  protected Object js_set(int index, Object c) {
    if (checkIndex(index)) {
      return Undefined.instance;
    }
    int val = Conversions.toInt16(c);
    ByteIo.writeInt16(this.arrayBuffer.buffer, index * 2 + this.offset, val, false);
    return null;
  }


  
  public Short get(int i) {
    if (checkIndex(i)) {
      throw new IndexOutOfBoundsException();
    }
    return (Short)js_get(i);
  }


  
  public Short set(int i, Short aByte) {
    if (checkIndex(i)) {
      throw new IndexOutOfBoundsException();
    }
    return (Short)js_set(i, aByte);
  }
}
