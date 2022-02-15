package org.mozilla.javascript.typedarrays;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.IdFunctionObject;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Undefined;
















public class NativeFloat32Array
  extends NativeTypedArrayView<Float>
{
  private static final long serialVersionUID = -8963461831950499340L;
  private static final String CLASS_NAME = "Float32Array";
  private static final int BYTES_PER_ELEMENT = 4;
  
  public NativeFloat32Array() {}
  
  public NativeFloat32Array(NativeArrayBuffer ab, int off, int len) {
    super(ab, off, len, len * 4);
  }

  
  public NativeFloat32Array(int len) {
    this(new NativeArrayBuffer(len * 4), 0, len);
  }


  
  public String getClassName() {
    return "Float32Array";
  }

  
  public static void init(Context cx, Scriptable scope, boolean sealed) {
    NativeFloat32Array a = new NativeFloat32Array();
    a.exportAsJSClass(4, scope, sealed);
  }


  
  protected NativeTypedArrayView construct(NativeArrayBuffer ab, int off, int len) {
    return new NativeFloat32Array(ab, off, len);
  }


  
  public int getBytesPerElement() {
    return 4;
  }


  
  protected NativeTypedArrayView realThis(Scriptable thisObj, IdFunctionObject f) {
    if (!(thisObj instanceof NativeFloat32Array)) {
      throw incompatibleCallError(f);
    }
    return (NativeFloat32Array)thisObj;
  }


  
  protected Object js_get(int index) {
    if (checkIndex(index)) {
      return Undefined.instance;
    }
    return ByteIo.readFloat32(this.arrayBuffer.buffer, index * 4 + this.offset, false);
  }


  
  protected Object js_set(int index, Object c) {
    if (checkIndex(index)) {
      return Undefined.instance;
    }
    double val = ScriptRuntime.toNumber(c);
    ByteIo.writeFloat32(this.arrayBuffer.buffer, index * 4 + this.offset, val, false);
    return null;
  }


  
  public Float get(int i) {
    if (checkIndex(i)) {
      throw new IndexOutOfBoundsException();
    }
    return (Float)js_get(i);
  }


  
  public Float set(int i, Float aByte) {
    if (checkIndex(i)) {
      throw new IndexOutOfBoundsException();
    }
    return (Float)js_set(i, aByte);
  }
}
