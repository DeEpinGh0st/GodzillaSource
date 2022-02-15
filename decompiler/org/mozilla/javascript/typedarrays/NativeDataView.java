package org.mozilla.javascript.typedarrays;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.IdFunctionObject;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Undefined;

public class NativeDataView
  extends NativeArrayBufferView {
  private static final long serialVersionUID = 1427967607557438968L;
  public static final String CLASS_NAME = "DataView";
  private static final int Id_constructor = 1;
  private static final int Id_getInt8 = 2;
  private static final int Id_getUint8 = 3;
  private static final int Id_getInt16 = 4;
  private static final int Id_getUint16 = 5;
  private static final int Id_getInt32 = 6;
  private static final int Id_getUint32 = 7;
  private static final int Id_getFloat32 = 8;
  private static final int Id_getFloat64 = 9;
  private static final int Id_setInt8 = 10;
  private static final int Id_setUint8 = 11;
  private static final int Id_setInt16 = 12;
  private static final int Id_setUint16 = 13;
  private static final int Id_setInt32 = 14;
  private static final int Id_setUint32 = 15;
  private static final int Id_setFloat32 = 16;
  private static final int Id_setFloat64 = 17;
  private static final int MAX_PROTOTYPE_ID = 17;
  
  public NativeDataView() {}
  
  public NativeDataView(NativeArrayBuffer ab, int offset, int length) {
    super(ab, offset, length);
  }


  
  public String getClassName() {
    return "DataView";
  }

  
  public static void init(Context cx, Scriptable scope, boolean sealed) {
    NativeDataView dv = new NativeDataView();
    dv.exportAsJSClass(17, scope, sealed);
  }

  
  private void rangeCheck(int offset, int len) {
    if (offset < 0 || offset + len > this.byteLength) {
      throw ScriptRuntime.constructError("RangeError", "offset out of range");
    }
  }

  
  private void checkOffset(Object[] args, int pos) {
    if (args.length <= pos) {
      throw ScriptRuntime.constructError("TypeError", "missing required offset parameter");
    }
    if (Undefined.instance.equals(args[pos])) {
      throw ScriptRuntime.constructError("RangeError", "invalid offset");
    }
  }

  
  private void checkValue(Object[] args, int pos) {
    if (args.length <= pos) {
      throw ScriptRuntime.constructError("TypeError", "missing required value parameter");
    }
    if (Undefined.instance.equals(args[pos])) {
      throw ScriptRuntime.constructError("RangeError", "invalid value parameter");
    }
  }

  
  private static NativeDataView realThis(Scriptable thisObj, IdFunctionObject f) {
    if (!(thisObj instanceof NativeDataView))
      throw incompatibleCallError(f); 
    return (NativeDataView)thisObj;
  }

  
  private NativeDataView js_constructor(NativeArrayBuffer ab, int offset, int length) {
    if (length < 0) {
      throw ScriptRuntime.constructError("RangeError", "length out of range");
    }
    if (offset < 0 || offset + length > ab.getLength()) {
      throw ScriptRuntime.constructError("RangeError", "offset out of range");
    }
    return new NativeDataView(ab, offset, length);
  }

  
  private Object js_getInt(int bytes, boolean signed, Object[] args) {
    checkOffset(args, 0);
    
    int offset = ScriptRuntime.toInt32(args[0]);
    rangeCheck(offset, bytes);
    
    boolean littleEndian = (isArg(args, 1) && bytes > 1 && ScriptRuntime.toBoolean(args[1]));

    
    switch (bytes) {
      case 1:
        return signed ? ByteIo.readInt8(this.arrayBuffer.buffer, offset) : ByteIo.readUint8(this.arrayBuffer.buffer, offset);
      
      case 2:
        return signed ? ByteIo.readInt16(this.arrayBuffer.buffer, offset, littleEndian) : ByteIo.readUint16(this.arrayBuffer.buffer, offset, littleEndian);
      
      case 4:
        return signed ? ByteIo.readInt32(this.arrayBuffer.buffer, offset, littleEndian) : ByteIo.readUint32(this.arrayBuffer.buffer, offset, littleEndian);
    } 
    
    throw new AssertionError();
  }


  
  private Object js_getFloat(int bytes, Object[] args) {
    checkOffset(args, 0);
    
    int offset = ScriptRuntime.toInt32(args[0]);
    rangeCheck(offset, bytes);
    
    boolean littleEndian = (isArg(args, 1) && bytes > 1 && ScriptRuntime.toBoolean(args[1]));

    
    switch (bytes) {
      case 4:
        return ByteIo.readFloat32(this.arrayBuffer.buffer, offset, littleEndian);
      case 8:
        return ByteIo.readFloat64(this.arrayBuffer.buffer, offset, littleEndian);
    } 
    throw new AssertionError();
  }


  
  private void js_setInt(int bytes, boolean signed, Object[] args) {
    checkOffset(args, 0);
    checkValue(args, 1);
    
    int offset = ScriptRuntime.toInt32(args[0]);
    rangeCheck(offset, bytes);
    
    boolean littleEndian = (isArg(args, 2) && bytes > 1 && ScriptRuntime.toBoolean(args[2]));

    
    switch (bytes) {
      case 1:
        if (signed) {
          ByteIo.writeInt8(this.arrayBuffer.buffer, offset, Conversions.toInt8(args[1]));
        } else {
          ByteIo.writeUint8(this.arrayBuffer.buffer, offset, Conversions.toUint8(args[1]));
        } 
        return;
      case 2:
        if (signed) {
          ByteIo.writeInt16(this.arrayBuffer.buffer, offset, Conversions.toInt16(args[1]), littleEndian);
        } else {
          ByteIo.writeUint16(this.arrayBuffer.buffer, offset, Conversions.toUint16(args[1]), littleEndian);
        } 
        return;
      case 4:
        if (signed) {
          ByteIo.writeInt32(this.arrayBuffer.buffer, offset, Conversions.toInt32(args[1]), littleEndian);
        } else {
          ByteIo.writeUint32(this.arrayBuffer.buffer, offset, Conversions.toUint32(args[1]), littleEndian);
        } 
        return;
    } 
    throw new AssertionError();
  }


  
  private void js_setFloat(int bytes, Object[] args) {
    checkOffset(args, 0);
    checkValue(args, 1);
    
    int offset = ScriptRuntime.toInt32(args[0]);
    rangeCheck(offset, bytes);
    
    boolean littleEndian = (isArg(args, 2) && bytes > 1 && ScriptRuntime.toBoolean(args[2]));
    
    double val = ScriptRuntime.toNumber(args[1]);
    
    switch (bytes) {
      case 4:
        ByteIo.writeFloat32(this.arrayBuffer.buffer, offset, val, littleEndian);
        return;
      case 8:
        ByteIo.writeFloat64(this.arrayBuffer.buffer, offset, val, littleEndian);
        return;
    } 
    throw new AssertionError();
  }






  
  public Object execIdCall(IdFunctionObject f, Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
    if (!f.hasTag(getClassName())) {
      return super.execIdCall(f, cx, scope, thisObj, args);
    }
    int id = f.methodId();
    switch (id) {
      case 1:
        if (isArg(args, 0) && args[0] instanceof NativeArrayBuffer) {
          NativeArrayBuffer ab = (NativeArrayBuffer)args[0];
          int off = isArg(args, 1) ? ScriptRuntime.toInt32(args[1]) : 0;
          int len = isArg(args, 2) ? ScriptRuntime.toInt32(args[2]) : (ab.getLength() - off);
          return js_constructor(ab, off, len);
        } 
        throw ScriptRuntime.constructError("TypeError", "Missing parameters");
      
      case 2:
        return realThis(thisObj, f).js_getInt(1, true, args);
      case 3:
        return realThis(thisObj, f).js_getInt(1, false, args);
      case 4:
        return realThis(thisObj, f).js_getInt(2, true, args);
      case 5:
        return realThis(thisObj, f).js_getInt(2, false, args);
      case 6:
        return realThis(thisObj, f).js_getInt(4, true, args);
      case 7:
        return realThis(thisObj, f).js_getInt(4, false, args);
      case 8:
        return realThis(thisObj, f).js_getFloat(4, args);
      case 9:
        return realThis(thisObj, f).js_getFloat(8, args);
      case 10:
        realThis(thisObj, f).js_setInt(1, true, args);
        return Undefined.instance;
      case 11:
        realThis(thisObj, f).js_setInt(1, false, args);
        return Undefined.instance;
      case 12:
        realThis(thisObj, f).js_setInt(2, true, args);
        return Undefined.instance;
      case 13:
        realThis(thisObj, f).js_setInt(2, false, args);
        return Undefined.instance;
      case 14:
        realThis(thisObj, f).js_setInt(4, true, args);
        return Undefined.instance;
      case 15:
        realThis(thisObj, f).js_setInt(4, false, args);
        return Undefined.instance;
      case 16:
        realThis(thisObj, f).js_setFloat(4, args);
        return Undefined.instance;
      case 17:
        realThis(thisObj, f).js_setFloat(8, args);
        return Undefined.instance;
    } 
    throw new IllegalArgumentException(String.valueOf(id));
  }


  
  protected void initPrototypeId(int id) {
    String s;
    int arity;
    switch (id) { case 1:
        arity = 1; s = "constructor"; break;
      case 2: arity = 1; s = "getInt8"; break;
      case 3: arity = 1; s = "getUint8"; break;
      case 4: arity = 1; s = "getInt16"; break;
      case 5: arity = 1; s = "getUint16"; break;
      case 6: arity = 1; s = "getInt32"; break;
      case 7: arity = 1; s = "getUint32"; break;
      case 8: arity = 1; s = "getFloat32"; break;
      case 9: arity = 1; s = "getFloat64"; break;
      case 10: arity = 2; s = "setInt8"; break;
      case 11: arity = 2; s = "setUint8"; break;
      case 12: arity = 2; s = "setInt16"; break;
      case 13: arity = 2; s = "setUint16"; break;
      case 14: arity = 2; s = "setInt32"; break;
      case 15: arity = 2; s = "setUint32"; break;
      case 16: arity = 2; s = "setFloat32"; break;
      case 17: arity = 2; s = "setFloat64"; break;
      default: throw new IllegalArgumentException(String.valueOf(id)); }
    
    initPrototypeMethod(getClassName(), id, s, arity);
  }






  
  protected int findPrototypeId(String s) {
    int c, id = 0; String X = null;
    switch (s.length()) { case 7:
        c = s.charAt(0);
        if (c == 103) { X = "getInt8"; id = 2; break; }
         if (c == 115) { X = "setInt8"; id = 10; }  break;
      case 8:
        c = s.charAt(6);
        if (c == 49) {
          c = s.charAt(0);
          if (c == 103) { X = "getInt16"; id = 4; break; }
           if (c == 115) { X = "setInt16"; id = 12; }
           break;
        }  if (c == 51) {
          c = s.charAt(0);
          if (c == 103) { X = "getInt32"; id = 6; break; }
           if (c == 115) { X = "setInt32"; id = 14; }
           break;
        }  if (c == 116) {
          c = s.charAt(0);
          if (c == 103) { X = "getUint8"; id = 3; break; }
           if (c == 115) { X = "setUint8"; id = 11; } 
        }  break;
      case 9:
        c = s.charAt(0);
        if (c == 103) {
          c = s.charAt(8);
          if (c == 50) { X = "getUint32"; id = 7; break; }
           if (c == 54) { X = "getUint16"; id = 5; }
           break;
        }  if (c == 115) {
          c = s.charAt(8);
          if (c == 50) { X = "setUint32"; id = 15; break; }
           if (c == 54) { X = "setUint16"; id = 13; } 
        }  break;
      case 10:
        c = s.charAt(0);
        if (c == 103) {
          c = s.charAt(9);
          if (c == 50) { X = "getFloat32"; id = 8; break; }
           if (c == 52) { X = "getFloat64"; id = 9; }
           break;
        }  if (c == 115) {
          c = s.charAt(9);
          if (c == 50) { X = "setFloat32"; id = 16; break; }
           if (c == 52) { X = "setFloat64"; id = 17; } 
        }  break;
      case 11:
        X = "constructor"; id = 1; break; }
    
    if (X != null && X != s && !X.equals(s)) id = 0;


    
    return id;
  }
}
