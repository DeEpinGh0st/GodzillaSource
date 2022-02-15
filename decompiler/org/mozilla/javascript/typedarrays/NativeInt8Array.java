package org.mozilla.javascript.typedarrays;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.IdFunctionObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Undefined;
















public class NativeInt8Array
  extends NativeTypedArrayView<Byte>
{
  private static final long serialVersionUID = -3349419704390398895L;
  private static final String CLASS_NAME = "Int8Array";
  
  public NativeInt8Array() {}
  
  public NativeInt8Array(NativeArrayBuffer ab, int off, int len) {
    super(ab, off, len, len);
  }

  
  public NativeInt8Array(int len) {
    this(new NativeArrayBuffer(len), 0, len);
  }


  
  public String getClassName() {
    return "Int8Array";
  }

  
  public static void init(Context cx, Scriptable scope, boolean sealed) {
    NativeInt8Array a = new NativeInt8Array();
    a.exportAsJSClass(4, scope, sealed);
  }


  
  protected NativeTypedArrayView construct(NativeArrayBuffer ab, int off, int len) {
    return new NativeInt8Array(ab, off, len);
  }


  
  public int getBytesPerElement() {
    return 1;
  }


  
  protected NativeTypedArrayView realThis(Scriptable thisObj, IdFunctionObject f) {
    if (!(thisObj instanceof NativeInt8Array)) {
      throw incompatibleCallError(f);
    }
    return (NativeInt8Array)thisObj;
  }


  
  protected Object js_get(int index) {
    if (checkIndex(index)) {
      return Undefined.instance;
    }
    return ByteIo.readInt8(this.arrayBuffer.buffer, index + this.offset);
  }


  
  protected Object js_set(int index, Object c) {
    if (checkIndex(index)) {
      return Undefined.instance;
    }
    int val = Conversions.toInt8(c);
    ByteIo.writeInt8(this.arrayBuffer.buffer, index + this.offset, val);
    return null;
  }




  
  public Byte get(int i) {
    if (checkIndex(i)) {
      throw new IndexOutOfBoundsException();
    }
    return (Byte)js_get(i);
  }


  
  public Byte set(int i, Byte aByte) {
    if (checkIndex(i)) {
      throw new IndexOutOfBoundsException();
    }
    return (Byte)js_set(i, aByte);
  }
}
