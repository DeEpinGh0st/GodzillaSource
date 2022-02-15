package org.mozilla.javascript.typedarrays;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.IdFunctionObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Undefined;
















public class NativeUint32Array
  extends NativeTypedArrayView<Long>
{
  private static final long serialVersionUID = -7987831421954144244L;
  private static final String CLASS_NAME = "Uint32Array";
  private static final int BYTES_PER_ELEMENT = 4;
  
  public NativeUint32Array() {}
  
  public NativeUint32Array(NativeArrayBuffer ab, int off, int len) {
    super(ab, off, len, len * 4);
  }

  
  public NativeUint32Array(int len) {
    this(new NativeArrayBuffer(len * 4), 0, len);
  }


  
  public String getClassName() {
    return "Uint32Array";
  }

  
  public static void init(Context cx, Scriptable scope, boolean sealed) {
    NativeUint32Array a = new NativeUint32Array();
    a.exportAsJSClass(4, scope, sealed);
  }


  
  protected NativeTypedArrayView construct(NativeArrayBuffer ab, int off, int len) {
    return new NativeUint32Array(ab, off, len);
  }


  
  public int getBytesPerElement() {
    return 4;
  }


  
  protected NativeTypedArrayView realThis(Scriptable thisObj, IdFunctionObject f) {
    if (!(thisObj instanceof NativeUint32Array)) {
      throw incompatibleCallError(f);
    }
    return (NativeUint32Array)thisObj;
  }


  
  protected Object js_get(int index) {
    if (checkIndex(index)) {
      return Undefined.instance;
    }
    return ByteIo.readUint32(this.arrayBuffer.buffer, index * 4 + this.offset, false);
  }


  
  protected Object js_set(int index, Object c) {
    if (checkIndex(index)) {
      return Undefined.instance;
    }
    long val = Conversions.toUint32(c);
    ByteIo.writeUint32(this.arrayBuffer.buffer, index * 4 + this.offset, val, false);
    return null;
  }


  
  public Long get(int i) {
    if (checkIndex(i)) {
      throw new IndexOutOfBoundsException();
    }
    return (Long)js_get(i);
  }


  
  public Long set(int i, Long aByte) {
    if (checkIndex(i)) {
      throw new IndexOutOfBoundsException();
    }
    return (Long)js_set(i, aByte);
  }
}
