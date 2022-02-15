package org.mozilla.javascript.typedarrays;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.IdFunctionObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Undefined;
















public class NativeUint8Array
  extends NativeTypedArrayView<Integer>
{
  private static final long serialVersionUID = -3349419704390398895L;
  private static final String CLASS_NAME = "Uint8Array";
  
  public NativeUint8Array() {}
  
  public NativeUint8Array(NativeArrayBuffer ab, int off, int len) {
    super(ab, off, len, len);
  }

  
  public NativeUint8Array(int len) {
    this(new NativeArrayBuffer(len), 0, len);
  }


  
  public String getClassName() {
    return "Uint8Array";
  }

  
  public static void init(Context cx, Scriptable scope, boolean sealed) {
    NativeUint8Array a = new NativeUint8Array();
    a.exportAsJSClass(4, scope, sealed);
  }


  
  protected NativeTypedArrayView construct(NativeArrayBuffer ab, int off, int len) {
    return new NativeUint8Array(ab, off, len);
  }


  
  public int getBytesPerElement() {
    return 1;
  }


  
  protected NativeTypedArrayView realThis(Scriptable thisObj, IdFunctionObject f) {
    if (!(thisObj instanceof NativeUint8Array)) {
      throw incompatibleCallError(f);
    }
    return (NativeUint8Array)thisObj;
  }


  
  protected Object js_get(int index) {
    if (checkIndex(index)) {
      return Undefined.instance;
    }
    return ByteIo.readUint8(this.arrayBuffer.buffer, index + this.offset);
  }


  
  protected Object js_set(int index, Object c) {
    if (checkIndex(index)) {
      return Undefined.instance;
    }
    int val = Conversions.toUint8(c);
    ByteIo.writeUint8(this.arrayBuffer.buffer, index + this.offset, val);
    return null;
  }


  
  public Integer get(int i) {
    if (checkIndex(i)) {
      throw new IndexOutOfBoundsException();
    }
    return (Integer)js_get(i);
  }


  
  public Integer set(int i, Integer aByte) {
    if (checkIndex(i)) {
      throw new IndexOutOfBoundsException();
    }
    return (Integer)js_set(i, aByte);
  }
}
