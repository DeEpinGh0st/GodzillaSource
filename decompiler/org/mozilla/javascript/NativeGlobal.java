package org.mozilla.javascript;

import java.io.Serializable;
import org.mozilla.javascript.xml.XMLLib;

















public class NativeGlobal
  implements Serializable, IdFunctionCall
{
  static final long serialVersionUID = 6080442165748707530L;
  private static final String URI_DECODE_RESERVED = ";/?:@&=+$,#";
  private static final int INVALID_UTF8 = 2147483647;
  
  public static void init(Context cx, Scriptable scope, boolean sealed) {
    NativeGlobal obj = new NativeGlobal();
    
    for (int id = 1; id <= 13; id++) {
      String name;
      int arity = 1;
      switch (id) {
        case 1:
          name = "decodeURI";
          break;
        case 2:
          name = "decodeURIComponent";
          break;
        case 3:
          name = "encodeURI";
          break;
        case 4:
          name = "encodeURIComponent";
          break;
        case 5:
          name = "escape";
          break;
        case 6:
          name = "eval";
          break;
        case 7:
          name = "isFinite";
          break;
        case 8:
          name = "isNaN";
          break;
        case 9:
          name = "isXMLName";
          break;
        case 10:
          name = "parseFloat";
          break;
        case 11:
          name = "parseInt";
          arity = 2;
          break;
        case 12:
          name = "unescape";
          break;
        case 13:
          name = "uneval";
          break;
        default:
          throw Kit.codeBug();
      } 
      IdFunctionObject f = new IdFunctionObject(obj, FTAG, id, name, arity, scope);
      
      if (sealed) {
        f.sealObject();
      }
      f.exportAsScopeProperty();
    } 
    
    ScriptableObject.defineProperty(scope, "NaN", ScriptRuntime.NaNobj, 7);

    
    ScriptableObject.defineProperty(scope, "Infinity", ScriptRuntime.wrapNumber(Double.POSITIVE_INFINITY), 7);


    
    ScriptableObject.defineProperty(scope, "undefined", Undefined.instance, 7);






    
    for (TopLevel.NativeErrors error : TopLevel.NativeErrors.values()) {
      if (error != TopLevel.NativeErrors.Error) {


        
        String name = error.name();
        ScriptableObject errorProto = (ScriptableObject)ScriptRuntime.newBuiltinObject(cx, scope, TopLevel.Builtins.Error, ScriptRuntime.emptyArgs);


        
        errorProto.put("name", errorProto, name);
        errorProto.put("message", errorProto, "");
        IdFunctionObject ctor = new IdFunctionObject(obj, FTAG, 14, name, 1, scope);

        
        ctor.markAsConstructor(errorProto);
        errorProto.put("constructor", errorProto, ctor);
        errorProto.setAttributes("constructor", 2);
        if (sealed) {
          errorProto.sealObject();
          ctor.sealObject();
        } 
        ctor.exportAsScopeProperty();
      } 
    } 
  }

  
  public Object execIdCall(IdFunctionObject f, Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
    if (f.hasTag(FTAG)) {
      String str; boolean result; Object name, value; XMLLib xmlLib; int methodId = f.methodId();
      switch (methodId) {
        case 1:
        case 2:
          str = ScriptRuntime.toString(args, 0);
          return decode(str, (methodId == 1));

        
        case 3:
        case 4:
          str = ScriptRuntime.toString(args, 0);
          return encode(str, (methodId == 3));

        
        case 5:
          return js_escape(args);
        
        case 6:
          return js_eval(cx, scope, args);

        
        case 7:
          if (args.length < 1) {
            result = false;
          } else {
            double d = ScriptRuntime.toNumber(args[0]);
            result = (d == d && d != Double.POSITIVE_INFINITY && d != Double.NEGATIVE_INFINITY);
          } 

          
          return ScriptRuntime.wrapBoolean(result);



        
        case 8:
          if (args.length < 1) {
            result = true;
          } else {
            double d = ScriptRuntime.toNumber(args[0]);
            result = (d != d);
          } 
          return ScriptRuntime.wrapBoolean(result);

        
        case 9:
          name = (args.length == 0) ? Undefined.instance : args[0];
          
          xmlLib = XMLLib.extractFromScope(scope);
          return ScriptRuntime.wrapBoolean(xmlLib.isXMLName(cx, name));


        
        case 10:
          return js_parseFloat(args);
        
        case 11:
          return js_parseInt(args);
        
        case 12:
          return js_unescape(args);
        
        case 13:
          value = (args.length != 0) ? args[0] : Undefined.instance;
          
          return ScriptRuntime.uneval(cx, scope, value);



        
        case 14:
          return NativeError.make(cx, scope, f, args);
      } 
    } 
    throw f.unknown();
  }



























  
  private Object js_parseInt(Object[] args) {
    // Byte code:
    //   0: aload_1
    //   1: iconst_0
    //   2: invokestatic toString : ([Ljava/lang/Object;I)Ljava/lang/String;
    //   5: astore_2
    //   6: aload_1
    //   7: iconst_1
    //   8: invokestatic toInt32 : ([Ljava/lang/Object;I)I
    //   11: istore_3
    //   12: aload_2
    //   13: invokevirtual length : ()I
    //   16: istore #4
    //   18: iload #4
    //   20: ifne -> 27
    //   23: getstatic org/mozilla/javascript/ScriptRuntime.NaNobj : Ljava/lang/Double;
    //   26: areturn
    //   27: iconst_0
    //   28: istore #5
    //   30: iconst_0
    //   31: istore #6
    //   33: aload_2
    //   34: iload #6
    //   36: invokevirtual charAt : (I)C
    //   39: istore #7
    //   41: iload #7
    //   43: invokestatic isStrWhiteSpaceChar : (I)Z
    //   46: ifne -> 52
    //   49: goto -> 62
    //   52: iinc #6, 1
    //   55: iload #6
    //   57: iload #4
    //   59: if_icmplt -> 33
    //   62: iload #7
    //   64: bipush #43
    //   66: if_icmpeq -> 87
    //   69: iload #7
    //   71: bipush #45
    //   73: if_icmpne -> 80
    //   76: iconst_1
    //   77: goto -> 81
    //   80: iconst_0
    //   81: dup
    //   82: istore #5
    //   84: ifeq -> 90
    //   87: iinc #6, 1
    //   90: iconst_m1
    //   91: istore #8
    //   93: iload_3
    //   94: ifne -> 102
    //   97: iconst_m1
    //   98: istore_3
    //   99: goto -> 170
    //   102: iload_3
    //   103: iconst_2
    //   104: if_icmplt -> 113
    //   107: iload_3
    //   108: bipush #36
    //   110: if_icmple -> 117
    //   113: getstatic org/mozilla/javascript/ScriptRuntime.NaNobj : Ljava/lang/Double;
    //   116: areturn
    //   117: iload_3
    //   118: bipush #16
    //   120: if_icmpne -> 170
    //   123: iload #4
    //   125: iload #6
    //   127: isub
    //   128: iconst_1
    //   129: if_icmple -> 170
    //   132: aload_2
    //   133: iload #6
    //   135: invokevirtual charAt : (I)C
    //   138: bipush #48
    //   140: if_icmpne -> 170
    //   143: aload_2
    //   144: iload #6
    //   146: iconst_1
    //   147: iadd
    //   148: invokevirtual charAt : (I)C
    //   151: istore #7
    //   153: iload #7
    //   155: bipush #120
    //   157: if_icmpeq -> 167
    //   160: iload #7
    //   162: bipush #88
    //   164: if_icmpne -> 170
    //   167: iinc #6, 2
    //   170: iload_3
    //   171: iconst_m1
    //   172: if_icmpne -> 251
    //   175: bipush #10
    //   177: istore_3
    //   178: iload #4
    //   180: iload #6
    //   182: isub
    //   183: iconst_1
    //   184: if_icmple -> 251
    //   187: aload_2
    //   188: iload #6
    //   190: invokevirtual charAt : (I)C
    //   193: bipush #48
    //   195: if_icmpne -> 251
    //   198: aload_2
    //   199: iload #6
    //   201: iconst_1
    //   202: iadd
    //   203: invokevirtual charAt : (I)C
    //   206: istore #7
    //   208: iload #7
    //   210: bipush #120
    //   212: if_icmpeq -> 222
    //   215: iload #7
    //   217: bipush #88
    //   219: if_icmpne -> 231
    //   222: bipush #16
    //   224: istore_3
    //   225: iinc #6, 2
    //   228: goto -> 251
    //   231: bipush #48
    //   233: iload #7
    //   235: if_icmpgt -> 251
    //   238: iload #7
    //   240: bipush #57
    //   242: if_icmpgt -> 251
    //   245: bipush #8
    //   247: istore_3
    //   248: iinc #6, 1
    //   251: aload_2
    //   252: iload #6
    //   254: iload_3
    //   255: invokestatic stringToNumber : (Ljava/lang/String;II)D
    //   258: dstore #9
    //   260: iload #5
    //   262: ifeq -> 271
    //   265: dload #9
    //   267: dneg
    //   268: goto -> 273
    //   271: dload #9
    //   273: invokestatic wrapNumber : (D)Ljava/lang/Number;
    //   276: areturn
    // Line number table:
    //   Java source line number -> byte code offset
    //   #213	-> 0
    //   #214	-> 6
    //   #216	-> 12
    //   #217	-> 18
    //   #218	-> 23
    //   #220	-> 27
    //   #221	-> 30
    //   #224	-> 33
    //   #225	-> 41
    //   #226	-> 49
    //   #227	-> 52
    //   #228	-> 55
    //   #230	-> 62
    //   #231	-> 87
    //   #233	-> 90
    //   #234	-> 93
    //   #235	-> 97
    //   #236	-> 102
    //   #237	-> 113
    //   #238	-> 117
    //   #239	-> 143
    //   #240	-> 153
    //   #241	-> 167
    //   #244	-> 170
    //   #245	-> 175
    //   #246	-> 178
    //   #247	-> 198
    //   #248	-> 208
    //   #249	-> 222
    //   #250	-> 225
    //   #251	-> 231
    //   #252	-> 245
    //   #253	-> 248
    //   #258	-> 251
    //   #259	-> 260
    // Local variable table:
    //   start	length	slot	name	descriptor
    //   0	277	0	this	Lorg/mozilla/javascript/NativeGlobal;
    //   0	277	1	args	[Ljava/lang/Object;
    //   6	271	2	s	Ljava/lang/String;
    //   12	265	3	radix	I
    //   18	259	4	len	I
    //   30	247	5	negative	Z
    //   33	244	6	start	I
    //   41	236	7	c	C
    //   93	184	8	NO_RADIX	I
    //   260	17	9	d	D
  }



























  
  private Object js_parseFloat(Object[] args) {
    char c;
    if (args.length < 1) {
      return ScriptRuntime.NaNobj;
    }
    String s = ScriptRuntime.toString(args[0]);
    int len = s.length();
    int start = 0;

    
    while (true) {
      if (start == len) {
        return ScriptRuntime.NaNobj;
      }
      c = s.charAt(start);
      if (!ScriptRuntime.isStrWhiteSpaceChar(c)) {
        break;
      }
      start++;
    } 
    
    int i = start;
    if (c == '+' || c == '-') {
      i++;
      if (i == len) {
        return ScriptRuntime.NaNobj;
      }
      c = s.charAt(i);
    } 
    
    if (c == 'I') {
      
      if (i + 8 <= len && s.regionMatches(i, "Infinity", 0, 8)) {
        double d;
        if (s.charAt(start) == '-') {
          d = Double.NEGATIVE_INFINITY;
        } else {
          d = Double.POSITIVE_INFINITY;
        } 
        return ScriptRuntime.wrapNumber(d);
      } 
      return ScriptRuntime.NaNobj;
    } 

    
    int decimal = -1;
    int exponent = -1;
    boolean exponentValid = false;
    for (; i < len; i++) {
      switch (s.charAt(i)) {
        case '.':
          if (decimal != -1)
            break; 
          decimal = i;
          break;
        
        case 'E':
        case 'e':
          if (exponent != -1)
            break; 
          if (i == len - 1) {
            break;
          }
          exponent = i;
          break;

        
        case '+':
        case '-':
          if (exponent != i - 1)
            break; 
          if (i == len - 1)
          { i--; break; }  break;
        case '0': case '1': case '2': case '3': case '4':
        case '5':
        case '6':
        case '7':
        case '8':
        case '9':
          if (exponent != -1) {
            exponentValid = true;
          }
          break;
        
        default:
          break;
      } 
    
    } 
    if (exponent != -1 && !exponentValid) {
      i = exponent;
    }
    s = s.substring(start, i);
    try {
      return Double.valueOf(s);
    }
    catch (NumberFormatException ex) {
      return ScriptRuntime.NaNobj;
    } 
  }









  
  private Object js_escape(Object[] args) {
    int URL_XALPHAS = 1;
    int URL_XPALPHAS = 2;
    int URL_PATH = 4;
    
    String s = ScriptRuntime.toString(args, 0);
    
    int mask = 7;
    if (args.length > 1) {
      double d = ScriptRuntime.toNumber(args[1]);
      if (d != d || (mask = (int)d) != d || 0 != (mask & 0xFFFFFFF8))
      {
        
        throw Context.reportRuntimeError0("msg.bad.esc.mask");
      }
    } 
    
    StringBuilder sb = null;
    int k = 0, L = s.length(); while (true) { if (k != L) {
        int c = s.charAt(k);
        if (mask != 0 && ((c >= 48 && c <= 57) || (c >= 65 && c <= 90) || (c >= 97 && c <= 122) || c == 64 || c == 42 || c == 95 || c == 45 || c == 46 || (0 != (mask & 0x4) && (c == 47 || c == 43)))) {




          
          if (sb != null) {
            sb.append((char)c);
          }
        } else {
          if (sb == null) {
            sb = new StringBuilder(L + 3);
            sb.append(s);
            sb.setLength(k);
          } 

          
          if (c < 256)
          { if (c == 32 && mask == 2)
            { sb.append('+'); }
            else
            
            { sb.append('%');
              int hexSize = 2;






              
              int shift = (hexSize - 1) * 4; }  } else { sb.append('%'); sb.append('u'); int hexSize = 4; int i = (hexSize - 1) * 4; }
        
        } 
      } else {
        break;
      } 
      k++; }
    
    return (sb == null) ? s : sb.toString();
  }





  
  private Object js_unescape(Object[] args) {
    String s = ScriptRuntime.toString(args, 0);
    int firstEscapePos = s.indexOf('%');
    if (firstEscapePos >= 0) {
      int L = s.length();
      char[] buf = s.toCharArray();
      int destination = firstEscapePos;
      for (int k = firstEscapePos; k != L; ) {
        char c = buf[k];
        k++;
        if (c == '%' && k != L) {
          int end; int start;
          if (buf[k] == 'u') {
            start = k + 1;
            end = k + 5;
          } else {
            start = k;
            end = k + 2;
          } 
          if (end <= L) {
            int x = 0;
            for (int i = start; i != end; i++) {
              x = Kit.xDigitToInt(buf[i], x);
            }
            if (x >= 0) {
              c = (char)x;
              k = end;
            } 
          } 
        } 
        buf[destination] = c;
        destination++;
      } 
      s = new String(buf, 0, destination);
    } 
    return s;
  }





  
  private Object js_eval(Context cx, Scriptable scope, Object[] args) {
    Scriptable global = ScriptableObject.getTopLevelScope(scope);
    return ScriptRuntime.evalSpecial(cx, global, global, args, "eval code", 1);
  }

  
  static boolean isEvalFunction(Object functionObj) {
    if (functionObj instanceof IdFunctionObject) {
      IdFunctionObject function = (IdFunctionObject)functionObj;
      if (function.hasTag(FTAG) && function.methodId() == 6) {
        return true;
      }
    } 
    return false;
  }








  
  @Deprecated
  public static EcmaError constructError(Context cx, String error, String message, Scriptable scope) {
    return ScriptRuntime.constructError(error, message);
  }













  
  @Deprecated
  public static EcmaError constructError(Context cx, String error, String message, Scriptable scope, String sourceName, int lineNumber, int columnNumber, String lineSource) {
    return ScriptRuntime.constructError(error, message, sourceName, lineNumber, lineSource, columnNumber);
  }









  
  private static String encode(String str, boolean fullUri) {
    byte[] utf8buf = null;
    StringBuilder sb = null;
    
    for (int k = 0, length = str.length(); k != length; k++) {
      char C = str.charAt(k);
      if (encodeUnescaped(C, fullUri)) {
        if (sb != null)
          sb.append(C); 
      } else {
        int V;
        if (sb == null) {
          sb = new StringBuilder(length + 3);
          sb.append(str);
          sb.setLength(k);
          utf8buf = new byte[6];
        } 
        if ('?' <= C && C <= '?') {
          throw uriError();
        }
        
        if (C < '?' || '?' < C) {
          V = C;
        } else {
          k++;
          if (k == length) {
            throw uriError();
          }
          char C2 = str.charAt(k);
          if ('?' > C2 || C2 > '?') {
            throw uriError();
          }
          V = (C - 55296 << 10) + C2 - 56320 + 65536;
        } 
        int L = oneUcs4ToUtf8Char(utf8buf, V);
        for (int j = 0; j < L; j++) {
          int d = 0xFF & utf8buf[j];
          sb.append('%');
          sb.append(toHexChar(d >>> 4));
          sb.append(toHexChar(d & 0xF));
        } 
      } 
    } 
    return (sb == null) ? str : sb.toString();
  }
  
  private static char toHexChar(int i) {
    if (i >> 4 != 0) Kit.codeBug(); 
    return (char)((i < 10) ? (i + 48) : (i - 10 + 65));
  }
  
  private static int unHex(char c) {
    if ('A' <= c && c <= 'F')
      return c - 65 + 10; 
    if ('a' <= c && c <= 'f')
      return c - 97 + 10; 
    if ('0' <= c && c <= '9') {
      return c - 48;
    }
    return -1;
  }

  
  private static int unHex(char c1, char c2) {
    int i1 = unHex(c1);
    int i2 = unHex(c2);
    if (i1 >= 0 && i2 >= 0) {
      return i1 << 4 | i2;
    }
    return -1;
  }
  
  private static String decode(String str, boolean fullUri) {
    char[] buf = null;
    int bufTop = 0;
    
    for (int k = 0, length = str.length(); k != length; ) {
      char C = str.charAt(k);
      if (C != '%') {
        if (buf != null) {
          buf[bufTop++] = C;
        }
        k++; continue;
      } 
      if (buf == null) {

        
        buf = new char[length];
        str.getChars(0, k, buf, 0);
        bufTop = k;
      } 
      int start = k;
      if (k + 3 > length)
        throw uriError(); 
      int B = unHex(str.charAt(k + 1), str.charAt(k + 2));
      if (B < 0) throw uriError(); 
      k += 3;
      if ((B & 0x80) == 0) {
        C = (char)B;
      } else {
        int utf8Tail;
        int ucs4Char;
        int minUcs4Char;
        if ((B & 0xC0) == 128)
        {
          throw uriError(); } 
        if ((B & 0x20) == 0) {
          utf8Tail = 1; ucs4Char = B & 0x1F;
          minUcs4Char = 128;
        } else if ((B & 0x10) == 0) {
          utf8Tail = 2; ucs4Char = B & 0xF;
          minUcs4Char = 2048;
        } else if ((B & 0x8) == 0) {
          utf8Tail = 3; ucs4Char = B & 0x7;
          minUcs4Char = 65536;
        } else if ((B & 0x4) == 0) {
          utf8Tail = 4; ucs4Char = B & 0x3;
          minUcs4Char = 2097152;
        } else if ((B & 0x2) == 0) {
          utf8Tail = 5; ucs4Char = B & 0x1;
          minUcs4Char = 67108864;
        } else {
          
          throw uriError();
        } 
        if (k + 3 * utf8Tail > length)
          throw uriError(); 
        for (int j = 0; j != utf8Tail; j++) {
          if (str.charAt(k) != '%')
            throw uriError(); 
          B = unHex(str.charAt(k + 1), str.charAt(k + 2));
          if (B < 0 || (B & 0xC0) != 128)
            throw uriError(); 
          ucs4Char = ucs4Char << 6 | B & 0x3F;
          k += 3;
        } 
        
        if (ucs4Char < minUcs4Char || (ucs4Char >= 55296 && ucs4Char <= 57343)) {
          
          ucs4Char = Integer.MAX_VALUE;
        } else if (ucs4Char == 65534 || ucs4Char == 65535) {
          ucs4Char = 65533;
        } 
        if (ucs4Char >= 65536) {
          ucs4Char -= 65536;
          if (ucs4Char > 1048575) {
            throw uriError();
          }
          char H = (char)((ucs4Char >>> 10) + 55296);
          C = (char)((ucs4Char & 0x3FF) + 56320);
          buf[bufTop++] = H;
        } else {
          C = (char)ucs4Char;
        } 
      } 
      if (fullUri && ";/?:@&=+$,#".indexOf(C) >= 0) {
        for (int x = start; x != k; x++)
          buf[bufTop++] = str.charAt(x); 
        continue;
      } 
      buf[bufTop++] = C;
    } 

    
    return (buf == null) ? str : new String(buf, 0, bufTop);
  }
  
  private static boolean encodeUnescaped(char c, boolean fullUri) {
    if (('A' <= c && c <= 'Z') || ('a' <= c && c <= 'z') || ('0' <= c && c <= '9'))
    {
      return true;
    }
    if ("-_.!~*'()".indexOf(c) >= 0) {
      return true;
    }
    if (fullUri) {
      return (";/?:@&=+$,#".indexOf(c) >= 0);
    }
    return false;
  }
  
  private static EcmaError uriError() {
    return ScriptRuntime.constructError("URIError", ScriptRuntime.getMessage0("msg.bad.uri"));
  }







  
  private static int oneUcs4ToUtf8Char(byte[] utf8Buffer, int ucs4Char) {
    int utf8Length = 1;

    
    if ((ucs4Char & 0xFFFFFF80) == 0) {
      utf8Buffer[0] = (byte)ucs4Char;
    } else {
      
      int a = ucs4Char >>> 11;
      utf8Length = 2;
      while (a != 0) {
        a >>>= 5;
        utf8Length++;
      } 
      int i = utf8Length;
      while (--i > 0) {
        utf8Buffer[i] = (byte)(ucs4Char & 0x3F | 0x80);
        ucs4Char >>>= 6;
      } 
      utf8Buffer[0] = (byte)(256 - (1 << 8 - utf8Length) + ucs4Char);
    } 
    return utf8Length;
  }
  
  private static final Object FTAG = "Global";
  private static final int Id_decodeURI = 1;
  private static final int Id_decodeURIComponent = 2;
  private static final int Id_encodeURI = 3;
  private static final int Id_encodeURIComponent = 4;
  private static final int Id_escape = 5;
  private static final int Id_eval = 6;
  private static final int Id_isFinite = 7;
  private static final int Id_isNaN = 8;
  private static final int Id_isXMLName = 9;
  private static final int Id_parseFloat = 10;
  private static final int Id_parseInt = 11;
  private static final int Id_unescape = 12;
  private static final int Id_uneval = 13;
  private static final int LAST_SCOPE_FUNCTION_ID = 13;
  private static final int Id_new_CommonError = 14;
}
