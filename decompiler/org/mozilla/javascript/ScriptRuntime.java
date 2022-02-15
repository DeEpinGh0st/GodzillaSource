package org.mozilla.javascript;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.mozilla.javascript.v8dtoa.DoubleConversion;
import org.mozilla.javascript.v8dtoa.FastDtoa;
import org.mozilla.javascript.xml.XMLLib;
import org.mozilla.javascript.xml.XMLObject;



























public class ScriptRuntime
{
  @Deprecated
  public static BaseFunction typeErrorThrower() {
    return typeErrorThrower(Context.getCurrentContext());
  }




  
  public static BaseFunction typeErrorThrower(Context cx) {
    if (cx.typeErrorThrower == null) {
      BaseFunction thrower = new BaseFunction()
        {
          static final long serialVersionUID = -5891740962154902286L;
          
          public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
            throw ScriptRuntime.typeError0("msg.op.not.allowed");
          }
          
          public int getLength() {
            return 0;
          }
        };
      setFunctionProtoAndParent(thrower, cx.topCallScope);
      thrower.preventExtensions();
      cx.typeErrorThrower = thrower;
    } 
    return cx.typeErrorThrower;
  }
  
  static class NoSuchMethodShim
    implements Callable {
    String methodName;
    Callable noSuchMethodMethod;
    
    NoSuchMethodShim(Callable noSuchMethodMethod, String methodName) {
      this.noSuchMethodMethod = noSuchMethodMethod;
      this.methodName = methodName;
    }










    
    public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      Object[] nestedArgs = new Object[2];
      
      nestedArgs[0] = this.methodName;
      nestedArgs[1] = ScriptRuntime.newArrayLiteral(args, null, cx, scope);
      return this.noSuchMethodMethod.call(cx, scope, thisObj, nestedArgs);
    }
  }











  
  public static final Class<?> BooleanClass = Kit.classOrNull("java.lang.Boolean");
  public static final Class<?> ByteClass = Kit.classOrNull("java.lang.Byte");
  public static final Class<?> CharacterClass = Kit.classOrNull("java.lang.Character");
  public static final Class<?> ClassClass = Kit.classOrNull("java.lang.Class");
  public static final Class<?> DoubleClass = Kit.classOrNull("java.lang.Double");
  public static final Class<?> FloatClass = Kit.classOrNull("java.lang.Float");
  public static final Class<?> IntegerClass = Kit.classOrNull("java.lang.Integer");
  public static final Class<?> LongClass = Kit.classOrNull("java.lang.Long");
  public static final Class<?> NumberClass = Kit.classOrNull("java.lang.Number");
  public static final Class<?> ObjectClass = Kit.classOrNull("java.lang.Object");
  public static final Class<?> ShortClass = Kit.classOrNull("java.lang.Short");
  public static final Class<?> StringClass = Kit.classOrNull("java.lang.String");
  public static final Class<?> DateClass = Kit.classOrNull("java.util.Date");

  
  public static final Class<?> ContextClass = Kit.classOrNull("org.mozilla.javascript.Context");
  
  public static final Class<?> ContextFactoryClass = Kit.classOrNull("org.mozilla.javascript.ContextFactory");
  
  public static final Class<?> FunctionClass = Kit.classOrNull("org.mozilla.javascript.Function");
  
  public static final Class<?> ScriptableObjectClass = Kit.classOrNull("org.mozilla.javascript.ScriptableObject");
  
  public static final Class<Scriptable> ScriptableClass = Scriptable.class;


  
  public static Locale ROOT_LOCALE = new Locale("");
  
  private static final Object LIBRARY_SCOPE_KEY = "LIBRARY_SCOPE";

  
  public static boolean isRhinoRuntimeType(Class<?> cl) {
    if (cl.isPrimitive()) {
      return (cl != char.class);
    }
    return (cl == StringClass || cl == BooleanClass || NumberClass.isAssignableFrom(cl) || ScriptableClass.isAssignableFrom(cl));
  }






  
  public static ScriptableObject initSafeStandardObjects(Context cx, ScriptableObject scope, boolean sealed) {
    if (scope == null) {
      scope = new NativeObject();
    }
    scope.associateValue(LIBRARY_SCOPE_KEY, scope);
    (new ClassCache()).associate(scope);
    
    BaseFunction.init(scope, sealed);
    NativeObject.init(scope, sealed);
    
    Scriptable objectProto = ScriptableObject.getObjectPrototype(scope);

    
    Scriptable functionProto = ScriptableObject.getClassPrototype(scope, "Function");
    functionProto.setPrototype(objectProto);

    
    if (scope.getPrototype() == null) {
      scope.setPrototype(objectProto);
    }
    
    NativeError.init(scope, sealed);
    NativeGlobal.init(cx, scope, sealed);
    
    NativeArray.init(scope, sealed);
    if (cx.getOptimizationLevel() > 0)
    {

      
      NativeArray.setMaximumInitialCapacity(200000);
    }
    NativeString.init(scope, sealed);
    NativeBoolean.init(scope, sealed);
    NativeNumber.init(scope, sealed);
    NativeDate.init(scope, sealed);
    NativeMath.init(scope, sealed);
    NativeJSON.init(scope, sealed);
    
    NativeWith.init(scope, sealed);
    NativeCall.init(scope, sealed);
    NativeScript.init(scope, sealed);
    
    NativeIterator.init(scope, sealed);
    
    boolean withXml = (cx.hasFeature(6) && cx.getE4xImplementationFactory() != null);


    
    new LazilyLoadedCtor(scope, "RegExp", "org.mozilla.javascript.regexp.NativeRegExp", sealed, true);
    
    new LazilyLoadedCtor(scope, "Continuation", "org.mozilla.javascript.NativeContinuation", sealed, true);

    
    if (withXml) {
      String xmlImpl = cx.getE4xImplementationFactory().getImplementationClassName();
      new LazilyLoadedCtor(scope, "XML", xmlImpl, sealed, true);
      new LazilyLoadedCtor(scope, "XMLList", xmlImpl, sealed, true);
      new LazilyLoadedCtor(scope, "Namespace", xmlImpl, sealed, true);
      new LazilyLoadedCtor(scope, "QName", xmlImpl, sealed, true);
    } 
    
    if (cx.getLanguageVersion() >= 180 && cx.hasFeature(14)) {
      
      new LazilyLoadedCtor(scope, "ArrayBuffer", "org.mozilla.javascript.typedarrays.NativeArrayBuffer", sealed, true);

      
      new LazilyLoadedCtor(scope, "Int8Array", "org.mozilla.javascript.typedarrays.NativeInt8Array", sealed, true);

      
      new LazilyLoadedCtor(scope, "Uint8Array", "org.mozilla.javascript.typedarrays.NativeUint8Array", sealed, true);

      
      new LazilyLoadedCtor(scope, "Uint8ClampedArray", "org.mozilla.javascript.typedarrays.NativeUint8ClampedArray", sealed, true);

      
      new LazilyLoadedCtor(scope, "Int16Array", "org.mozilla.javascript.typedarrays.NativeInt16Array", sealed, true);

      
      new LazilyLoadedCtor(scope, "Uint16Array", "org.mozilla.javascript.typedarrays.NativeUint16Array", sealed, true);

      
      new LazilyLoadedCtor(scope, "Int32Array", "org.mozilla.javascript.typedarrays.NativeInt32Array", sealed, true);

      
      new LazilyLoadedCtor(scope, "Uint32Array", "org.mozilla.javascript.typedarrays.NativeUint32Array", sealed, true);

      
      new LazilyLoadedCtor(scope, "Float32Array", "org.mozilla.javascript.typedarrays.NativeFloat32Array", sealed, true);

      
      new LazilyLoadedCtor(scope, "Float64Array", "org.mozilla.javascript.typedarrays.NativeFloat64Array", sealed, true);

      
      new LazilyLoadedCtor(scope, "DataView", "org.mozilla.javascript.typedarrays.NativeDataView", sealed, true);
    } 


    
    if (scope instanceof TopLevel) {
      ((TopLevel)scope).cacheBuiltins();
    }
    
    return scope;
  }



  
  public static ScriptableObject initStandardObjects(Context cx, ScriptableObject scope, boolean sealed) {
    ScriptableObject s = initSafeStandardObjects(cx, scope, sealed);
    
    new LazilyLoadedCtor(s, "Packages", "org.mozilla.javascript.NativeJavaTopPackage", sealed, true);
    
    new LazilyLoadedCtor(s, "getClass", "org.mozilla.javascript.NativeJavaTopPackage", sealed, true);
    
    new LazilyLoadedCtor(s, "JavaAdapter", "org.mozilla.javascript.JavaAdapter", sealed, true);
    
    new LazilyLoadedCtor(s, "JavaImporter", "org.mozilla.javascript.ImporterTopLevel", sealed, true);

    
    for (String packageName : getTopPackageNames()) {
      new LazilyLoadedCtor(s, packageName, "org.mozilla.javascript.NativeJavaTopPackage", sealed, true);
    }

    
    return s;
  }

  
  static String[] getTopPackageNames() {
    (new String[7])[0] = "java"; (new String[7])[1] = "javax"; (new String[7])[2] = "org"; (new String[7])[3] = "com"; (new String[7])[4] = "edu"; (new String[7])[5] = "net"; (new String[7])[6] = "android"; (new String[6])[0] = "java"; (new String[6])[1] = "javax"; (new String[6])[2] = "org"; (new String[6])[3] = "com"; (new String[6])[4] = "edu"; (new String[6])[5] = "net"; return "Dalvik".equals(System.getProperty("java.vm.name")) ? new String[7] : new String[6];
  }




  
  public static ScriptableObject getLibraryScopeOrNull(Scriptable scope) {
    ScriptableObject libScope = (ScriptableObject)ScriptableObject.getTopScopeValue(scope, LIBRARY_SCOPE_KEY);
    
    return libScope;
  }




  
  public static boolean isJSLineTerminator(int c) {
    if ((c & 0xDFD0) != 0) {
      return false;
    }
    return (c == 10 || c == 13 || c == 8232 || c == 8233);
  }
  
  public static boolean isJSWhitespaceOrLineTerminator(int c) {
    return (isStrWhiteSpaceChar(c) || isJSLineTerminator(c));
  }
















  
  static boolean isStrWhiteSpaceChar(int c) {
    switch (c) {
      case 9:
      case 10:
      case 11:
      case 12:
      case 13:
      case 32:
      case 160:
      case 8232:
      case 8233:
      case 65279:
        return true;
    } 
    return (Character.getType(c) == 12);
  }


  
  public static Boolean wrapBoolean(boolean b) {
    return b ? Boolean.TRUE : Boolean.FALSE;
  }

  
  public static Integer wrapInt(int i) {
    return Integer.valueOf(i);
  }

  
  public static Number wrapNumber(double x) {
    if (x != x) {
      return NaNobj;
    }
    return new Double(x);
  }






  
  public static boolean toBoolean(Object val) {
    while (true) {
      if (val instanceof Boolean)
        return ((Boolean)val).booleanValue(); 
      if (val == null || val == Undefined.instance)
        return false; 
      if (val instanceof CharSequence)
        return (((CharSequence)val).length() != 0); 
      if (val instanceof Number) {
        double d = ((Number)val).doubleValue();
        return (d == d && d != 0.0D);
      } 
      if (val instanceof Scriptable) {
        if (val instanceof ScriptableObject && ((ScriptableObject)val).avoidObjectDetection())
        {
          
          return false;
        }
        if (Context.getContext().isVersionECMA1())
        {
          return true;
        }
        
        val = ((Scriptable)val).getDefaultValue(BooleanClass);
        if (val instanceof Scriptable)
          throw errorWithClassName("msg.primitive.expected", val);  continue;
      }  break;
    } 
    warnAboutNonJSObject(val);
    return true;
  }







  
  public static double toNumber(Object val) {
    while (true) {
      if (val instanceof Number)
        return ((Number)val).doubleValue(); 
      if (val == null)
        return 0.0D; 
      if (val == Undefined.instance)
        return NaN; 
      if (val instanceof String)
        return toNumber((String)val); 
      if (val instanceof CharSequence)
        return toNumber(val.toString()); 
      if (val instanceof Boolean)
        return ((Boolean)val).booleanValue() ? 1.0D : 0.0D; 
      if (val instanceof Scriptable) {
        val = ((Scriptable)val).getDefaultValue(NumberClass);
        if (val instanceof Scriptable)
          throw errorWithClassName("msg.primitive.expected", val);  continue;
      }  break;
    } 
    warnAboutNonJSObject(val);
    return NaN;
  }

  
  public static double toNumber(Object[] args, int index) {
    return (index < args.length) ? toNumber(args[index]) : NaN;
  }





  
  public static final double NaN = Double.longBitsToDouble(9221120237041090560L);


  
  public static final double negativeZero = Double.longBitsToDouble(Long.MIN_VALUE);
  
  public static final Double NaNobj = new Double(NaN);
  private static final String DEFAULT_NS_TAG = "__default_namespace__";
  public static final int ENUMERATE_KEYS = 0;
  public static final int ENUMERATE_VALUES = 1;
  
  static double stringToNumber(String s, int start, int radix) {
    char digitMax = '9';
    char lowerCaseBound = 'a';
    char upperCaseBound = 'A';
    int len = s.length();
    if (radix < 10) {
      digitMax = (char)(48 + radix - 1);
    }
    if (radix > 10) {
      lowerCaseBound = (char)(97 + radix - 10);
      upperCaseBound = (char)(65 + radix - 10);
    } 
    
    double sum = 0.0D; int end;
    for (end = start; end < len; end++) {
      int newDigit; char c = s.charAt(end);
      
      if ('0' <= c && c <= digitMax) {
        newDigit = c - 48;
      } else if ('a' <= c && c < lowerCaseBound) {
        newDigit = c - 97 + 10;
      } else if ('A' <= c && c < upperCaseBound) {
        newDigit = c - 65 + 10;
      } else {
        break;
      }  sum = sum * radix + newDigit;
    } 
    if (start == end) {
      return NaN;
    }
    if (sum >= 9.007199254740992E15D) {
      if (radix == 10)
        
        try {



          
          return Double.parseDouble(s.substring(start, end));
        } catch (NumberFormatException nfe) {
          return NaN;
        }  
      if (radix == 2 || radix == 4 || radix == 8 || radix == 16 || radix == 32) {











        
        int bitShiftInChar = 1;
        int digit = 0;
        
        int SKIP_LEADING_ZEROS = 0;
        int FIRST_EXACT_53_BITS = 1;
        int AFTER_BIT_53 = 2;
        int ZEROS_AFTER_54 = 3;
        int MIXED_AFTER_54 = 4;
        
        int state = 0;
        int exactBitsLimit = 53;
        double factor = 0.0D;
        boolean bit53 = false;
        
        boolean bit54 = false;
        
        while (true) {
          if (bitShiftInChar == 1) {
            if (start == end)
              break; 
            digit = s.charAt(start++);
            if (48 <= digit && digit <= 57) {
              digit -= 48;
            } else if (97 <= digit && digit <= 122) {
              digit -= 87;
            } else {
              digit -= 55;
            }  bitShiftInChar = radix;
          } 
          bitShiftInChar >>= 1;
          boolean bit = ((digit & bitShiftInChar) != 0);
          
          switch (state) {
            case 0:
              if (bit) {
                exactBitsLimit--;
                sum = 1.0D;
                state = 1;
              } 
            
            case 1:
              sum *= 2.0D;
              if (bit)
                sum++; 
              exactBitsLimit--;
              if (exactBitsLimit == 0) {
                bit53 = bit;
                state = 2;
              } 
            
            case 2:
              bit54 = bit;
              factor = 2.0D;
              state = 3;
            
            case 3:
              if (bit) {
                state = 4;
              }
            
            case 4:
              factor *= 2.0D;
          } 
        
        } 
        switch (state) {
          case 0:
            sum = 0.0D;
            break;





          
          case 3:
            if (bit54 & bit53)
              sum++; 
            sum *= factor;
            break;

          
          case 4:
            if (bit54)
              sum++; 
            sum *= factor;
            break;
        } 
      
      } 
    } 
    return sum;
  }
  
  public static final int ENUMERATE_ARRAY = 2;
  public static final int ENUMERATE_KEYS_NO_ITERATOR = 3;
  public static final int ENUMERATE_VALUES_NO_ITERATOR = 4;
  public static final int ENUMERATE_ARRAY_NO_ITERATOR = 5;
  
  public static double toNumber(String s) {
    char startChar;
    int len = s.length();
    int start = 0;
    
    while (true) {
      if (start == len)
      {
        return 0.0D;
      }
      startChar = s.charAt(start);
      if (!isStrWhiteSpaceChar(startChar))
        break; 
      start++;
    } 
    
    if (startChar == '0') {
      if (start + 2 < len) {
        int c1 = s.charAt(start + 1);
        if (c1 == 120 || c1 == 88)
        {
          return stringToNumber(s, start + 2, 16);
        }
      } 
    } else if ((startChar == '+' || startChar == '-') && 
      start + 3 < len && s.charAt(start + 1) == '0') {
      int c2 = s.charAt(start + 2);
      if (c2 == 120 || c2 == 88) {
        
        double val = stringToNumber(s, start + 3, 16);
        return (startChar == '-') ? -val : val;
      } 
    } 

    
    int end = len - 1;
    char endChar;
    while (isStrWhiteSpaceChar(endChar = s.charAt(end)))
      end--; 
    if (endChar == 'y') {
      
      if (startChar == '+' || startChar == '-')
        start++; 
      if (start + 7 == end && s.regionMatches(start, "Infinity", 0, 8)) {
        return (startChar == '-') ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
      }
      
      return NaN;
    } 

    
    String sub = s.substring(start, end + 1);

    
    for (int i = sub.length() - 1; i >= 0; ) {
      char c = sub.charAt(i);
      if (('0' <= c && c <= '9') || c == '.' || c == 'e' || c == 'E' || c == '+' || c == '-') {
        i--;
        continue;
      } 
      return NaN;
    } 
    try {
      return Double.parseDouble(sub);
    } catch (NumberFormatException ex) {
      return NaN;
    } 
  }






  
  public static Object[] padArguments(Object[] args, int count) {
    if (count < args.length) {
      return args;
    }
    
    Object[] result = new Object[count]; int i;
    for (i = 0; i < args.length; i++) {
      result[i] = args[i];
    }
    
    for (; i < count; i++) {
      result[i] = Undefined.instance;
    }
    
    return result;
  }

  
  public static String escapeString(String s) {
    return escapeString(s, '"');
  }





  
  public static String escapeString(String s, char escapeQuote) {
    if (escapeQuote != '"' && escapeQuote != '\'') Kit.codeBug(); 
    StringBuilder sb = null;
    
    for (int i = 0, L = s.length(); i != L; i++) {
      int c = s.charAt(i);
      
      if (32 <= c && c <= 126 && c != escapeQuote && c != 92) {

        
        if (sb != null) {
          sb.append((char)c);
        }
      } else {
        
        if (sb == null) {
          sb = new StringBuilder(L + 3);
          sb.append(s);
          sb.setLength(i);
        } 
        
        int escape = -1;
        switch (c) { case 8:
            escape = 98; break;
          case 12: escape = 102; break;
          case 10: escape = 110; break;
          case 13: escape = 114; break;
          case 9: escape = 116; break;
          case 11: escape = 118; break;
          case 32: escape = 32; break;
          case 92: escape = 92; break; }
        
        if (escape >= 0) {
          
          sb.append('\\');
          sb.append((char)escape);
        } else if (c == escapeQuote) {
          sb.append('\\');
          sb.append(escapeQuote);
        } else {
          int hexSize;
          if (c < 256) {
            
            sb.append("\\x");
            hexSize = 2;
          } else {
            
            sb.append("\\u");
            hexSize = 4;
          } 
          
          for (int shift = (hexSize - 1) * 4; shift >= 0; shift -= 4) {
            int digit = 0xF & c >> shift;
            int hc = (digit < 10) ? (48 + digit) : (87 + digit);
            sb.append((char)hc);
          } 
        } 
      } 
    }  return (sb == null) ? s : sb.toString();
  }

  
  static boolean isValidIdentifierName(String s) {
    int L = s.length();
    if (L == 0)
      return false; 
    if (!Character.isJavaIdentifierStart(s.charAt(0)))
      return false; 
    for (int i = 1; i != L; i++) {
      if (!Character.isJavaIdentifierPart(s.charAt(i)))
        return false; 
    } 
    return !TokenStream.isKeyword(s);
  }
  
  public static CharSequence toCharSequence(Object val) {
    if (val instanceof NativeString) {
      return ((NativeString)val).toCharSequence();
    }
    return (val instanceof CharSequence) ? (CharSequence)val : toString(val);
  }





  
  public static String toString(Object val) {
    while (true) {
      if (val == null) {
        return "null";
      }
      if (val == Undefined.instance) {
        return "undefined";
      }
      if (val instanceof String) {
        return (String)val;
      }
      if (val instanceof CharSequence) {
        return val.toString();
      }
      if (val instanceof Number)
      {
        
        return numberToString(((Number)val).doubleValue(), 10);
      }
      if (val instanceof Scriptable) {
        val = ((Scriptable)val).getDefaultValue(StringClass);
        if (val instanceof Scriptable)
          throw errorWithClassName("msg.primitive.expected", val);  continue;
      } 
      break;
    } 
    return val.toString();
  }


  
  static String defaultObjectToString(Scriptable obj) {
    return "[object " + obj.getClassName() + ']';
  }

  
  public static String toString(Object[] args, int index) {
    return (index < args.length) ? toString(args[index]) : "undefined";
  }



  
  public static String toString(double val) {
    return numberToString(val, 10);
  }
  
  public static String numberToString(double d, int base) {
    if (base < 2 || base > 36) {
      throw Context.reportRuntimeError1("msg.bad.radix", Integer.toString(base));
    }

    
    if (d != d)
      return "NaN"; 
    if (d == Double.POSITIVE_INFINITY)
      return "Infinity"; 
    if (d == Double.NEGATIVE_INFINITY)
      return "-Infinity"; 
    if (d == 0.0D) {
      return "0";
    }
    if (base != 10) {
      return DToA.JS_dtobasestr(base, d);
    }

    
    String result = FastDtoa.numberToString(d);
    if (result != null) {
      return result;
    }
    StringBuilder buffer = new StringBuilder();
    DToA.JS_dtostr(buffer, 0, 0, d);
    return buffer.toString();
  }



  
  static String uneval(Context cx, Scriptable scope, Object value) {
    if (value == null) {
      return "null";
    }
    if (value == Undefined.instance) {
      return "undefined";
    }
    if (value instanceof CharSequence) {
      String escaped = escapeString(value.toString());
      StringBuilder sb = new StringBuilder(escaped.length() + 2);
      sb.append('"');
      sb.append(escaped);
      sb.append('"');
      return sb.toString();
    } 
    if (value instanceof Number) {
      double d = ((Number)value).doubleValue();
      if (d == 0.0D && 1.0D / d < 0.0D) {
        return "-0";
      }
      return toString(d);
    } 
    if (value instanceof Boolean) {
      return toString(value);
    }
    if (value instanceof Scriptable) {
      Scriptable obj = (Scriptable)value;

      
      if (ScriptableObject.hasProperty(obj, "toSource")) {
        Object v = ScriptableObject.getProperty(obj, "toSource");
        if (v instanceof Function) {
          Function f = (Function)v;
          return toString(f.call(cx, scope, obj, emptyArgs));
        } 
      } 
      return toString(value);
    } 
    warnAboutNonJSObject(value);
    return value.toString();
  }


  
  static String defaultObjectToSource(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
    boolean toplevel, iterating;
    if (cx.iterating == null) {
      toplevel = true;
      iterating = false;
      cx.iterating = new ObjToIntMap(31);
    } else {
      toplevel = false;
      iterating = cx.iterating.has(thisObj);
    } 
    
    StringBuilder result = new StringBuilder(128);
    if (toplevel) {
      result.append("(");
    }
    result.append('{');


    
    try {
      if (!iterating) {
        cx.iterating.intern(thisObj);
        Object[] ids = thisObj.getIds();
        for (int i = 0; i < ids.length; i++) {
          Object value, id = ids[i];
          
          if (id instanceof Integer) {
            int intId = ((Integer)id).intValue();
            value = thisObj.get(intId, thisObj);
            if (value == Scriptable.NOT_FOUND)
              continue; 
            if (i > 0)
              result.append(", "); 
            result.append(intId);
          } else {
            String strId = (String)id;
            value = thisObj.get(strId, thisObj);
            if (value == Scriptable.NOT_FOUND)
              continue; 
            if (i > 0)
              result.append(", "); 
            if (isValidIdentifierName(strId)) {
              result.append(strId);
            } else {
              result.append('\'');
              result.append(escapeString(strId, '\''));
              
              result.append('\'');
            } 
          } 
          result.append(':');
          result.append(uneval(cx, scope, value)); continue;
        } 
      } 
    } finally {
      if (toplevel) {
        cx.iterating = null;
      }
    } 
    
    result.append('}');
    if (toplevel) {
      result.append(')');
    }
    return result.toString();
  }

  
  public static Scriptable toObject(Scriptable scope, Object val) {
    if (val instanceof Scriptable) {
      return (Scriptable)val;
    }
    return toObject(Context.getContext(), scope, val);
  }







  
  @Deprecated
  public static Scriptable toObjectOrNull(Context cx, Object obj) {
    if (obj instanceof Scriptable)
      return (Scriptable)obj; 
    if (obj != null && obj != Undefined.instance) {
      return toObject(cx, getTopCallScope(cx), obj);
    }
    return null;
  }





  
  public static Scriptable toObjectOrNull(Context cx, Object obj, Scriptable scope) {
    if (obj instanceof Scriptable)
      return (Scriptable)obj; 
    if (obj != null && obj != Undefined.instance) {
      return toObject(cx, scope, obj);
    }
    return null;
  }





  
  @Deprecated
  public static Scriptable toObject(Scriptable scope, Object val, Class<?> staticClass) {
    if (val instanceof Scriptable) {
      return (Scriptable)val;
    }
    return toObject(Context.getContext(), scope, val);
  }






  
  public static Scriptable toObject(Context cx, Scriptable scope, Object val) {
    if (val instanceof Scriptable) {
      return (Scriptable)val;
    }
    if (val instanceof CharSequence) {
      
      NativeString result = new NativeString((CharSequence)val);
      setBuiltinProtoAndParent(result, scope, TopLevel.Builtins.String);
      return result;
    } 
    if (val instanceof Number) {
      NativeNumber result = new NativeNumber(((Number)val).doubleValue());
      setBuiltinProtoAndParent(result, scope, TopLevel.Builtins.Number);
      return result;
    } 
    if (val instanceof Boolean) {
      NativeBoolean result = new NativeBoolean(((Boolean)val).booleanValue());
      setBuiltinProtoAndParent(result, scope, TopLevel.Builtins.Boolean);
      return result;
    } 
    if (val == null) {
      throw typeError0("msg.null.to.object");
    }
    if (val == Undefined.instance) {
      throw typeError0("msg.undef.to.object");
    }

    
    Object wrapped = cx.getWrapFactory().wrap(cx, scope, val, null);
    if (wrapped instanceof Scriptable)
      return (Scriptable)wrapped; 
    throw errorWithClassName("msg.invalid.type", val);
  }





  
  @Deprecated
  public static Scriptable toObject(Context cx, Scriptable scope, Object val, Class<?> staticClass) {
    return toObject(cx, scope, val);
  }





  
  @Deprecated
  public static Object call(Context cx, Object fun, Object thisArg, Object[] args, Scriptable scope) {
    if (!(fun instanceof Function)) {
      throw notFunctionError(toString(fun));
    }
    Function function = (Function)fun;
    Scriptable thisObj = toObjectOrNull(cx, thisArg, scope);
    if (thisObj == null) {
      throw undefCallError(thisObj, "function");
    }
    return function.call(cx, scope, thisObj, args);
  }


  
  public static Scriptable newObject(Context cx, Scriptable scope, String constructorName, Object[] args) {
    scope = ScriptableObject.getTopLevelScope(scope);
    Function ctor = getExistingCtor(cx, scope, constructorName);
    if (args == null) args = emptyArgs; 
    return ctor.construct(cx, scope, args);
  }



  
  public static Scriptable newBuiltinObject(Context cx, Scriptable scope, TopLevel.Builtins type, Object[] args) {
    scope = ScriptableObject.getTopLevelScope(scope);
    Function ctor = TopLevel.getBuiltinCtor(cx, scope, type);
    if (args == null) args = emptyArgs; 
    return ctor.construct(cx, scope, args);
  }


  
  static Scriptable newNativeError(Context cx, Scriptable scope, TopLevel.NativeErrors type, Object[] args) {
    scope = ScriptableObject.getTopLevelScope(scope);
    Function ctor = TopLevel.getNativeErrorCtor(cx, scope, type);
    if (args == null) args = emptyArgs; 
    return ctor.construct(cx, scope, args);
  }




  
  public static double toInteger(Object val) {
    return toInteger(toNumber(val));
  }


  
  public static double toInteger(double d) {
    if (d != d) {
      return 0.0D;
    }
    if (d == 0.0D || d == Double.POSITIVE_INFINITY || d == Double.NEGATIVE_INFINITY)
    {
      
      return d;
    }
    if (d > 0.0D) {
      return Math.floor(d);
    }
    return Math.ceil(d);
  }
  
  public static double toInteger(Object[] args, int index) {
    return (index < args.length) ? toInteger(args[index]) : 0.0D;
  }






  
  public static int toInt32(Object val) {
    if (val instanceof Integer) {
      return ((Integer)val).intValue();
    }
    return toInt32(toNumber(val));
  }
  
  public static int toInt32(Object[] args, int index) {
    return (index < args.length) ? toInt32(args[index]) : 0;
  }
  
  public static int toInt32(double d) {
    return DoubleConversion.doubleToInt32(d);
  }




  
  public static long toUint32(double d) {
    return DoubleConversion.doubleToInt32(d) & 0xFFFFFFFFL;
  }
  
  public static long toUint32(Object val) {
    return toUint32(toNumber(val));
  }




  
  public static char toUint16(Object val) {
    double d = toNumber(val);
    return (char)DoubleConversion.doubleToInt32(d);
  }





  
  public static Object setDefaultNamespace(Object namespace, Context cx) {
    Scriptable scope = cx.currentActivationCall;
    if (scope == null) {
      scope = getTopCallScope(cx);
    }
    
    XMLLib xmlLib = currentXMLLib(cx);
    Object ns = xmlLib.toDefaultXmlNamespace(cx, namespace);

    
    if (!scope.has("__default_namespace__", scope)) {
      
      ScriptableObject.defineProperty(scope, "__default_namespace__", ns, 6);
    }
    else {
      
      scope.put("__default_namespace__", scope, ns);
    } 
    
    return Undefined.instance;
  }
  
  public static Object searchDefaultNamespace(Context cx) {
    Object nsObject;
    Scriptable scope = cx.currentActivationCall;
    if (scope == null) {
      scope = getTopCallScope(cx);
    }
    
    while (true) {
      Scriptable parent = scope.getParentScope();
      if (parent == null) {
        Object object = ScriptableObject.getProperty(scope, "__default_namespace__");
        if (object == Scriptable.NOT_FOUND) {
          return null;
        }
        break;
      } 
      nsObject = scope.get("__default_namespace__", scope);
      if (nsObject != Scriptable.NOT_FOUND) {
        break;
      }
      scope = parent;
    } 
    return nsObject;
  }
  
  public static Object getTopLevelProp(Scriptable scope, String id) {
    scope = ScriptableObject.getTopLevelScope(scope);
    return ScriptableObject.getProperty(scope, id);
  }


  
  static Function getExistingCtor(Context cx, Scriptable scope, String constructorName) {
    Object ctorVal = ScriptableObject.getProperty(scope, constructorName);
    if (ctorVal instanceof Function) {
      return (Function)ctorVal;
    }
    if (ctorVal == Scriptable.NOT_FOUND) {
      throw Context.reportRuntimeError1("msg.ctor.not.found", constructorName);
    }
    
    throw Context.reportRuntimeError1("msg.not.ctor", constructorName);
  }










  
  public static long indexFromString(String str) {
    int MAX_VALUE_LENGTH = 10;
    
    int len = str.length();
    if (len > 0) {
      int i = 0;
      boolean negate = false;
      int c = str.charAt(0);
      if (c == 45 && 
        len > 1) {
        c = str.charAt(1);
        if (c == 48) return -1L; 
        i = 1;
        negate = true;
      } 
      
      c -= 48;
      if (0 <= c && c <= 9 && len <= (negate ? 11 : 10)) {




        
        int index = -c;
        int oldIndex = 0;
        i++;
        if (index != 0)
        {
          while (i != len && 0 <= (c = str.charAt(i) - 48) && c <= 9) {
            
            oldIndex = index;
            index = 10 * index - c;
            i++;
          } 
        }

        
        if (i == len && (oldIndex > -214748364 || (oldIndex == -214748364 && c <= (negate ? 8 : 7))))
        {



          
          return 0xFFFFFFFFL & (negate ? index : -index);
        }
      } 
    } 
    return -1L;
  }







  
  public static long testUint32String(String str) {
    int MAX_VALUE_LENGTH = 10;
    
    int len = str.length();
    if (1 <= len && len <= 10) {
      int c = str.charAt(0);
      c -= 48;
      if (c == 0)
      {
        return (len == 1) ? 0L : -1L;
      }
      if (1 <= c && c <= 9) {
        long v = c;
        for (int i = 1; i != len; i++) {
          c = str.charAt(i) - 48;
          if (0 > c || c > 9) {
            return -1L;
          }
          v = 10L * v + c;
        } 
        
        if (v >>> 32L == 0L) {
          return v;
        }
      } 
    } 
    return -1L;
  }





  
  static Object getIndexObject(String s) {
    long indexTest = indexFromString(s);
    if (indexTest >= 0L) {
      return Integer.valueOf((int)indexTest);
    }
    return s;
  }





  
  static Object getIndexObject(double d) {
    int i = (int)d;
    if (i == d) {
      return Integer.valueOf(i);
    }
    return toString(d);
  }





  
  static String toStringIdOrIndex(Context cx, Object id) {
    String s;
    if (id instanceof Number) {
      double d = ((Number)id).doubleValue();
      int index = (int)d;
      if (index == d) {
        storeIndexResult(cx, index);
        return null;
      } 
      return toString(id);
    } 
    
    if (id instanceof String) {
      s = (String)id;
    } else {
      s = toString(id);
    } 
    long indexTest = indexFromString(s);
    if (indexTest >= 0L) {
      storeIndexResult(cx, (int)indexTest);
      return null;
    } 
    return s;
  }







  
  @Deprecated
  public static Object getObjectElem(Object obj, Object elem, Context cx) {
    return getObjectElem(obj, elem, cx, getTopCallScope(cx));
  }




  
  public static Object getObjectElem(Object obj, Object elem, Context cx, Scriptable scope) {
    Scriptable sobj = toObjectOrNull(cx, obj, scope);
    if (sobj == null) {
      throw undefReadError(obj, elem);
    }
    return getObjectElem(sobj, elem, cx);
  }




  
  public static Object getObjectElem(Scriptable obj, Object elem, Context cx) {
    Object result;
    if (obj instanceof XMLObject) {
      result = ((XMLObject)obj).get(cx, elem);
    } else {
      String s = toStringIdOrIndex(cx, elem);
      if (s == null) {
        int index = lastIndexResult(cx);
        result = ScriptableObject.getProperty(obj, index);
      } else {
        result = ScriptableObject.getProperty(obj, s);
      } 
    } 
    
    if (result == Scriptable.NOT_FOUND) {
      result = Undefined.instance;
    }
    
    return result;
  }







  
  @Deprecated
  public static Object getObjectProp(Object obj, String property, Context cx) {
    return getObjectProp(obj, property, cx, getTopCallScope(cx));
  }







  
  public static Object getObjectProp(Object obj, String property, Context cx, Scriptable scope) {
    Scriptable sobj = toObjectOrNull(cx, obj, scope);
    if (sobj == null) {
      throw undefReadError(obj, property);
    }
    return getObjectProp(sobj, property, cx);
  }



  
  public static Object getObjectProp(Scriptable obj, String property, Context cx) {
    Object result = ScriptableObject.getProperty(obj, property);
    if (result == Scriptable.NOT_FOUND) {
      if (cx.hasFeature(11)) {
        Context.reportWarning(getMessage1("msg.ref.undefined.prop", property));
      }
      
      result = Undefined.instance;
    } 
    
    return result;
  }





  
  @Deprecated
  public static Object getObjectPropNoWarn(Object obj, String property, Context cx) {
    return getObjectPropNoWarn(obj, property, cx, getTopCallScope(cx));
  }


  
  public static Object getObjectPropNoWarn(Object obj, String property, Context cx, Scriptable scope) {
    Scriptable sobj = toObjectOrNull(cx, obj, scope);
    if (sobj == null) {
      throw undefReadError(obj, property);
    }
    Object result = ScriptableObject.getProperty(sobj, property);
    if (result == Scriptable.NOT_FOUND) {
      return Undefined.instance;
    }
    return result;
  }








  
  @Deprecated
  public static Object getObjectIndex(Object obj, double dblIndex, Context cx) {
    return getObjectIndex(obj, dblIndex, cx, getTopCallScope(cx));
  }






  
  public static Object getObjectIndex(Object obj, double dblIndex, Context cx, Scriptable scope) {
    Scriptable sobj = toObjectOrNull(cx, obj, scope);
    if (sobj == null) {
      throw undefReadError(obj, toString(dblIndex));
    }
    
    int index = (int)dblIndex;
    if (index == dblIndex) {
      return getObjectIndex(sobj, index, cx);
    }
    String s = toString(dblIndex);
    return getObjectProp(sobj, s, cx);
  }



  
  public static Object getObjectIndex(Scriptable obj, int index, Context cx) {
    Object result = ScriptableObject.getProperty(obj, index);
    if (result == Scriptable.NOT_FOUND) {
      result = Undefined.instance;
    }
    
    return result;
  }







  
  @Deprecated
  public static Object setObjectElem(Object obj, Object elem, Object value, Context cx) {
    return setObjectElem(obj, elem, value, cx, getTopCallScope(cx));
  }





  
  public static Object setObjectElem(Object obj, Object elem, Object value, Context cx, Scriptable scope) {
    Scriptable sobj = toObjectOrNull(cx, obj, scope);
    if (sobj == null) {
      throw undefWriteError(obj, elem, value);
    }
    return setObjectElem(sobj, elem, value, cx);
  }


  
  public static Object setObjectElem(Scriptable obj, Object elem, Object value, Context cx) {
    if (obj instanceof XMLObject) {
      ((XMLObject)obj).put(cx, elem, value);
    } else {
      String s = toStringIdOrIndex(cx, elem);
      if (s == null) {
        int index = lastIndexResult(cx);
        ScriptableObject.putProperty(obj, index, value);
      } else {
        ScriptableObject.putProperty(obj, s, value);
      } 
    } 
    
    return value;
  }







  
  @Deprecated
  public static Object setObjectProp(Object obj, String property, Object value, Context cx) {
    return setObjectProp(obj, property, value, cx, getTopCallScope(cx));
  }






  
  public static Object setObjectProp(Object obj, String property, Object value, Context cx, Scriptable scope) {
    Scriptable sobj = toObjectOrNull(cx, obj, scope);
    if (sobj == null) {
      throw undefWriteError(obj, property, value);
    }
    return setObjectProp(sobj, property, value, cx);
  }


  
  public static Object setObjectProp(Scriptable obj, String property, Object value, Context cx) {
    ScriptableObject.putProperty(obj, property, value);
    return value;
  }








  
  @Deprecated
  public static Object setObjectIndex(Object obj, double dblIndex, Object value, Context cx) {
    return setObjectIndex(obj, dblIndex, value, cx, getTopCallScope(cx));
  }







  
  public static Object setObjectIndex(Object obj, double dblIndex, Object value, Context cx, Scriptable scope) {
    Scriptable sobj = toObjectOrNull(cx, obj, scope);
    if (sobj == null) {
      throw undefWriteError(obj, String.valueOf(dblIndex), value);
    }
    
    int index = (int)dblIndex;
    if (index == dblIndex) {
      return setObjectIndex(sobj, index, value, cx);
    }
    String s = toString(dblIndex);
    return setObjectProp(sobj, s, value, cx);
  }



  
  public static Object setObjectIndex(Scriptable obj, int index, Object value, Context cx) {
    ScriptableObject.putProperty(obj, index, value);
    return value;
  }


  
  public static boolean deleteObjectElem(Scriptable target, Object elem, Context cx) {
    String s = toStringIdOrIndex(cx, elem);
    if (s == null) {
      int index = lastIndexResult(cx);
      target.delete(index);
      return !target.has(index, target);
    } 
    target.delete(s);
    return !target.has(s, target);
  }




  
  public static boolean hasObjectElem(Scriptable target, Object elem, Context cx) {
    boolean result;
    String s = toStringIdOrIndex(cx, elem);
    if (s == null) {
      int index = lastIndexResult(cx);
      result = ScriptableObject.hasProperty(target, index);
    } else {
      result = ScriptableObject.hasProperty(target, s);
    } 
    
    return result;
  }

  
  public static Object refGet(Ref ref, Context cx) {
    return ref.get(cx);
  }




  
  @Deprecated
  public static Object refSet(Ref ref, Object value, Context cx) {
    return refSet(ref, value, cx, getTopCallScope(cx));
  }


  
  public static Object refSet(Ref ref, Object value, Context cx, Scriptable scope) {
    return ref.set(cx, scope, value);
  }

  
  public static Object refDel(Ref ref, Context cx) {
    return wrapBoolean(ref.delete(cx));
  }

  
  static boolean isSpecialProperty(String s) {
    return (s.equals("__proto__") || s.equals("__parent__"));
  }





  
  @Deprecated
  public static Ref specialRef(Object obj, String specialProperty, Context cx) {
    return specialRef(obj, specialProperty, cx, getTopCallScope(cx));
  }


  
  public static Ref specialRef(Object obj, String specialProperty, Context cx, Scriptable scope) {
    return SpecialRef.createSpecial(cx, scope, obj, specialProperty);
  }




  
  @Deprecated
  public static Object delete(Object obj, Object id, Context cx) {
    return delete(obj, id, cx, false);
  }














  
  @Deprecated
  public static Object delete(Object obj, Object id, Context cx, boolean isName) {
    return delete(obj, id, cx, getTopCallScope(cx), isName);
  }













  
  public static Object delete(Object obj, Object id, Context cx, Scriptable scope, boolean isName) {
    Scriptable sobj = toObjectOrNull(cx, obj, scope);
    if (sobj == null) {
      if (isName) {
        return Boolean.TRUE;
      }
      throw undefDeleteError(obj, id);
    } 
    boolean result = deleteObjectElem(sobj, id, cx);
    return wrapBoolean(result);
  }




  
  public static Object name(Context cx, Scriptable scope, String name) {
    Scriptable parent = scope.getParentScope();
    if (parent == null) {
      Object result = topScopeName(cx, scope, name);
      if (result == Scriptable.NOT_FOUND) {
        throw notFoundError(scope, name);
      }
      return result;
    } 
    
    return nameOrFunction(cx, scope, parent, name, false);
  }



  
  private static Object nameOrFunction(Context cx, Scriptable scope, Scriptable parentScope, String name, boolean asFunctionCall) {
    Object result;
    Scriptable thisObj = scope;
    
    XMLObject firstXMLObject = null;
    while (true) {
      if (scope instanceof NativeWith) {
        Scriptable withObj = scope.getPrototype();
        if (withObj instanceof XMLObject) {
          XMLObject xmlObj = (XMLObject)withObj;
          if (xmlObj.has(name, (Scriptable)xmlObj)) {
            
            XMLObject xMLObject = xmlObj;
            result = xmlObj.get(name, (Scriptable)xmlObj);
            break;
          } 
          if (firstXMLObject == null) {
            firstXMLObject = xmlObj;
          }
        } else {
          result = ScriptableObject.getProperty(withObj, name);
          if (result != Scriptable.NOT_FOUND) {
            
            thisObj = withObj;
            break;
          } 
        } 
      } else if (scope instanceof NativeCall) {

        
        result = scope.get(name, scope);
        if (result != Scriptable.NOT_FOUND) {
          if (asFunctionCall)
          {
            
            thisObj = ScriptableObject.getTopLevelScope(parentScope);
          }


          
          break;
        } 
      } else {
        result = ScriptableObject.getProperty(scope, name);
        if (result != Scriptable.NOT_FOUND) {
          thisObj = scope;
          break;
        } 
      } 
      scope = parentScope;
      parentScope = parentScope.getParentScope();
      if (parentScope == null) {
        result = topScopeName(cx, scope, name);
        if (result == Scriptable.NOT_FOUND) {
          if (firstXMLObject == null || asFunctionCall) {
            throw notFoundError(scope, name);
          }



          
          result = firstXMLObject.get(name, (Scriptable)firstXMLObject);
        } 
        
        thisObj = scope;
        
        break;
      } 
    } 
    if (asFunctionCall) {
      if (!(result instanceof Callable)) {
        throw notFunctionError(result, name);
      }
      storeScriptable(cx, thisObj);
    } 
    
    return result;
  }


  
  private static Object topScopeName(Context cx, Scriptable scope, String name) {
    if (cx.useDynamicScope) {
      scope = checkDynamicScope(cx.topCallScope, scope);
    }
    return ScriptableObject.getProperty(scope, name);
  }














  
  public static Scriptable bind(Context cx, Scriptable scope, String id) {
    XMLObject xMLObject;
    Scriptable firstXMLObject = null;
    Scriptable parent = scope.getParentScope();
    if (parent != null) {
      while (true) {
        if (scope instanceof NativeWith) {
          Scriptable withObj = scope.getPrototype();
          if (withObj instanceof XMLObject) {
            XMLObject xmlObject = (XMLObject)withObj;
            if (xmlObject.has(cx, id)) {
              return (Scriptable)xmlObject;
            }
            if (firstXMLObject == null) {
              xMLObject = xmlObject;
            }
          }
          else if (ScriptableObject.hasProperty(withObj, id)) {
            return withObj;
          } 
          
          scope = parent;
          parent = parent.getParentScope();
          if (parent == null)
            break; 
          continue;
        } 
        do {
          if (ScriptableObject.hasProperty(scope, id)) {
            return scope;
          }
          scope = parent;
          parent = parent.getParentScope();
        } while (parent != null);
        
        break;
      } 
    }
    
    if (cx.useDynamicScope) {
      scope = checkDynamicScope(cx.topCallScope, scope);
    }
    if (ScriptableObject.hasProperty(scope, id)) {
      return scope;
    }

    
    return (Scriptable)xMLObject;
  }


  
  public static Object setName(Scriptable bound, Object value, Context cx, Scriptable scope, String id) {
    if (bound != null) {

      
      ScriptableObject.putProperty(bound, id, value);
    
    }
    else {
      
      if (cx.hasFeature(11) || cx.hasFeature(8))
      {
        
        Context.reportWarning(getMessage1("msg.assn.create.strict", id));
      }

      
      bound = ScriptableObject.getTopLevelScope(scope);
      if (cx.useDynamicScope) {
        bound = checkDynamicScope(cx.topCallScope, bound);
      }
      bound.put(id, bound, value);
    } 
    return value;
  }

  
  public static Object strictSetName(Scriptable bound, Object value, Context cx, Scriptable scope, String id) {
    if (bound != null) {







      
      ScriptableObject.putProperty(bound, id, value);
      return value;
    } 
    
    String msg = "Assignment to undefined \"" + id + "\" in strict mode";
    throw constructError("ReferenceError", msg);
  }



  
  public static Object setConst(Scriptable bound, Object value, Context cx, String id) {
    if (bound instanceof XMLObject) {
      bound.put(id, bound, value);
    } else {
      ScriptableObject.putConstProperty(bound, id, value);
    } 
    return value;
  }


  
  private static class IdEnumeration
    implements Serializable
  {
    private static final long serialVersionUID = 1L;

    
    Scriptable obj;

    
    Object[] ids;

    
    int index;

    
    ObjToIntMap used;

    
    Object currentId;

    
    int enumType;
    
    boolean enumNumbers;
    
    Scriptable iterator;

    
    private IdEnumeration() {}
  }

  
  public static Scriptable toIterator(Context cx, Scriptable scope, Scriptable obj, boolean keyOnly) {
    if (ScriptableObject.hasProperty(obj, "__iterator__")) {

      
      Object v = ScriptableObject.getProperty(obj, "__iterator__");
      
      if (!(v instanceof Callable)) {
        throw typeError0("msg.invalid.iterator");
      }
      Callable f = (Callable)v;
      Object[] args = { keyOnly ? Boolean.TRUE : Boolean.FALSE };
      
      v = f.call(cx, scope, obj, args);
      if (!(v instanceof Scriptable)) {
        throw typeError0("msg.iterator.primitive");
      }
      return (Scriptable)v;
    } 
    return null;
  }






  
  @Deprecated
  public static Object enumInit(Object value, Context cx, boolean enumValues) {
    return enumInit(value, cx, enumValues ? 1 : 0);
  }












  
  @Deprecated
  public static Object enumInit(Object value, Context cx, int enumType) {
    return enumInit(value, cx, getTopCallScope(cx), enumType);
  }


  
  public static Object enumInit(Object value, Context cx, Scriptable scope, int enumType) {
    IdEnumeration x = new IdEnumeration();
    x.obj = toObjectOrNull(cx, value, scope);
    if (x.obj == null)
    {
      
      return x;
    }
    x.enumType = enumType;
    x.iterator = null;
    if (enumType != 3 && enumType != 4 && enumType != 5)
    {

      
      x.iterator = toIterator(cx, x.obj.getParentScope(), x.obj, (enumType == 0));
    }
    
    if (x.iterator == null)
    {
      
      enumChangeObject(x);
    }
    
    return x;
  }
  
  public static void setEnumNumbers(Object enumObj, boolean enumNumbers) {
    ((IdEnumeration)enumObj).enumNumbers = enumNumbers;
  }

  
  public static Boolean enumNext(Object enumObj) {
    IdEnumeration x = (IdEnumeration)enumObj;
    if (x.iterator != null) {
      Object v = ScriptableObject.getProperty(x.iterator, "next");
      if (!(v instanceof Callable))
        return Boolean.FALSE; 
      Callable f = (Callable)v;
      Context cx = Context.getContext();
      try {
        x.currentId = f.call(cx, x.iterator.getParentScope(), x.iterator, emptyArgs);
        
        return Boolean.TRUE;
      } catch (JavaScriptException e) {
        if (e.getValue() instanceof NativeIterator.StopIteration) {
          return Boolean.FALSE;
        }
        throw e;
      } 
    } 
    while (true) {
      if (x.obj == null) {
        return Boolean.FALSE;
      }
      if (x.index == x.ids.length) {
        x.obj = x.obj.getPrototype();
        enumChangeObject(x);
        continue;
      } 
      Object id = x.ids[x.index++];
      if (x.used != null && x.used.has(id)) {
        continue;
      }
      if (id instanceof String) {
        String strId = (String)id;
        if (!x.obj.has(strId, x.obj))
          continue; 
        x.currentId = strId; break;
      } 
      int intId = ((Number)id).intValue();
      if (!x.obj.has(intId, x.obj))
        continue; 
      x.currentId = x.enumNumbers ? Integer.valueOf(intId) : String.valueOf(intId);
      break;
    } 
    return Boolean.TRUE;
  }

  
  public static Object enumId(Object enumObj, Context cx) {
    Object[] elements;
    IdEnumeration x = (IdEnumeration)enumObj;
    if (x.iterator != null) {
      return x.currentId;
    }
    switch (x.enumType) {
      case 0:
      case 3:
        return x.currentId;
      case 1:
      case 4:
        return enumValue(enumObj, cx);
      case 2:
      case 5:
        elements = new Object[] { x.currentId, enumValue(enumObj, cx) };
        return cx.newArray(ScriptableObject.getTopLevelScope(x.obj), elements);
    } 
    throw Kit.codeBug();
  }
  
  public static Object enumValue(Object enumObj, Context cx) {
    Object result;
    IdEnumeration x = (IdEnumeration)enumObj;


    
    String s = toStringIdOrIndex(cx, x.currentId);
    if (s == null) {
      int index = lastIndexResult(cx);
      result = x.obj.get(index, x.obj);
    } else {
      result = x.obj.get(s, x.obj);
    } 
    
    return result;
  }

  
  private static void enumChangeObject(IdEnumeration x) {
    Object[] ids = null;
    while (x.obj != null) {
      ids = x.obj.getIds();
      if (ids.length != 0) {
        break;
      }
      x.obj = x.obj.getPrototype();
    } 
    if (x.obj != null && x.ids != null) {
      Object[] previous = x.ids;
      int L = previous.length;
      if (x.used == null) {
        x.used = new ObjToIntMap(L);
      }
      for (int i = 0; i != L; i++) {
        x.used.intern(previous[i]);
      }
    } 
    x.ids = ids;
    x.index = 0;
  }










  
  public static Callable getNameFunctionAndThis(String name, Context cx, Scriptable scope) {
    Scriptable parent = scope.getParentScope();
    if (parent == null) {
      Object result = topScopeName(cx, scope, name);
      if (!(result instanceof Callable)) {
        if (result == Scriptable.NOT_FOUND) {
          throw notFoundError(scope, name);
        }
        throw notFunctionError(result, name);
      } 

      
      Scriptable thisObj = scope;
      storeScriptable(cx, thisObj);
      return (Callable)result;
    } 

    
    return (Callable)nameOrFunction(cx, scope, parent, name, true);
  }












  
  @Deprecated
  public static Callable getElemFunctionAndThis(Object obj, Object elem, Context cx) {
    return getElemFunctionAndThis(obj, elem, cx, getTopCallScope(cx));
  }









  
  public static Callable getElemFunctionAndThis(Object obj, Object elem, Context cx, Scriptable scope) {
    String str = toStringIdOrIndex(cx, elem);
    if (str != null) {
      return getPropFunctionAndThis(obj, str, cx, scope);
    }
    int index = lastIndexResult(cx);
    
    Scriptable thisObj = toObjectOrNull(cx, obj, scope);
    if (thisObj == null) {
      throw undefCallError(obj, String.valueOf(index));
    }
    
    Object value = ScriptableObject.getProperty(thisObj, index);
    if (!(value instanceof Callable)) {
      throw notFunctionError(value, elem);
    }
    
    storeScriptable(cx, thisObj);
    return (Callable)value;
  }














  
  @Deprecated
  public static Callable getPropFunctionAndThis(Object obj, String property, Context cx) {
    return getPropFunctionAndThis(obj, property, cx, getTopCallScope(cx));
  }










  
  public static Callable getPropFunctionAndThis(Object obj, String property, Context cx, Scriptable scope) {
    Scriptable thisObj = toObjectOrNull(cx, obj, scope);
    return getPropFunctionAndThisHelper(obj, property, cx, thisObj);
  }


  
  private static Callable getPropFunctionAndThisHelper(Object obj, String property, Context cx, Scriptable thisObj) {
    if (thisObj == null) {
      throw undefCallError(obj, property);
    }
    
    Object value = ScriptableObject.getProperty(thisObj, property);
    if (!(value instanceof Callable)) {
      Object noSuchMethod = ScriptableObject.getProperty(thisObj, "__noSuchMethod__");
      if (noSuchMethod instanceof Callable) {
        value = new NoSuchMethodShim((Callable)noSuchMethod, property);
      }
    } 
    if (!(value instanceof Callable)) {
      throw notFunctionError(thisObj, value, property);
    }
    
    storeScriptable(cx, thisObj);
    return (Callable)value;
  }








  
  public static Callable getValueFunctionAndThis(Object value, Context cx) {
    if (!(value instanceof Callable)) {
      throw notFunctionError(value);
    }
    
    Callable f = (Callable)value;
    Scriptable thisObj = null;
    if (f instanceof Scriptable) {
      thisObj = ((Scriptable)f).getParentScope();
    }
    if (thisObj == null) {
      if (cx.topCallScope == null) throw new IllegalStateException(); 
      thisObj = cx.topCallScope;
    } 
    if (thisObj.getParentScope() != null && 
      !(thisObj instanceof NativeWith))
    {
      
      if (thisObj instanceof NativeCall)
      {
        thisObj = ScriptableObject.getTopLevelScope(thisObj);
      }
    }
    storeScriptable(cx, thisObj);
    return f;
  }











  
  public static Ref callRef(Callable function, Scriptable thisObj, Object[] args, Context cx) {
    if (function instanceof RefCallable) {
      RefCallable rfunction = (RefCallable)function;
      Ref ref = rfunction.refCall(cx, thisObj, args);
      if (ref == null) {
        throw new IllegalStateException(rfunction.getClass().getName() + ".refCall() returned null");
      }
      return ref;
    } 
    
    String msg = getMessage1("msg.no.ref.from.function", toString(function));
    
    throw constructError("ReferenceError", msg);
  }







  
  public static Scriptable newObject(Object fun, Context cx, Scriptable scope, Object[] args) {
    if (!(fun instanceof Function)) {
      throw notFunctionError(fun);
    }
    Function function = (Function)fun;
    return function.construct(cx, scope, args);
  }





  
  public static Object callSpecial(Context cx, Callable fun, Scriptable thisObj, Object[] args, Scriptable scope, Scriptable callerThis, int callType, String filename, int lineNumber) {
    if (callType == 1) {
      if (thisObj.getParentScope() == null && NativeGlobal.isEvalFunction(fun)) {
        return evalSpecial(cx, scope, callerThis, args, filename, lineNumber);
      }
    }
    else if (callType == 2) {
      if (NativeWith.isWithFunction(fun)) {
        throw Context.reportRuntimeError1("msg.only.from.new", "With");
      }
    } else {
      
      throw Kit.codeBug();
    } 
    
    return fun.call(cx, scope, thisObj, args);
  }



  
  public static Object newSpecial(Context cx, Object fun, Object[] args, Scriptable scope, int callType) {
    if (callType == 1) {
      if (NativeGlobal.isEvalFunction(fun)) {
        throw typeError1("msg.not.ctor", "eval");
      }
    } else if (callType == 2) {
      if (NativeWith.isWithFunction(fun)) {
        return NativeWith.newWithSpecial(cx, scope, args);
      }
    } else {
      throw Kit.codeBug();
    } 
    
    return newObject(fun, cx, scope, args);
  }







  
  public static Object applyOrCall(boolean isApply, Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
    Object[] callArgs;
    int L = args.length;
    Callable function = getCallable(thisObj);
    
    Scriptable callThis = null;
    if (L != 0) {
      callThis = toObjectOrNull(cx, args[0], scope);
    }
    if (callThis == null)
    {
      callThis = getTopCallScope(cx);
    }

    
    if (isApply) {
      
      callArgs = (L <= 1) ? emptyArgs : getApplyArguments(cx, args[1]);

    
    }
    else if (L <= 1) {
      callArgs = emptyArgs;
    } else {
      callArgs = new Object[L - 1];
      System.arraycopy(args, 1, callArgs, 0, L - 1);
    } 

    
    return function.call(cx, scope, callThis, callArgs);
  }

  
  static Object[] getApplyArguments(Context cx, Object arg1) {
    if (arg1 == null || arg1 == Undefined.instance)
      return emptyArgs; 
    if (arg1 instanceof NativeArray || arg1 instanceof Arguments) {
      return cx.getElements((Scriptable)arg1);
    }
    throw typeError0("msg.arg.isnt.array");
  }


  
  static Callable getCallable(Scriptable thisObj) {
    Callable function;
    if (thisObj instanceof Callable) {
      function = (Callable)thisObj;
    } else {
      Object value = thisObj.getDefaultValue(FunctionClass);
      if (!(value instanceof Callable)) {
        throw notFunctionError(value, thisObj);
      }
      function = (Callable)value;
    } 
    return function;
  }








  
  public static Object evalSpecial(Context cx, Scriptable scope, Object thisArg, Object[] args, String filename, int lineNumber) {
    if (args.length < 1)
      return Undefined.instance; 
    Object x = args[0];
    if (!(x instanceof CharSequence)) {
      if (cx.hasFeature(11) || cx.hasFeature(9))
      {
        
        throw Context.reportRuntimeError0("msg.eval.nonstring.strict");
      }
      String message = getMessage0("msg.eval.nonstring");
      Context.reportWarning(message);
      return x;
    } 
    if (filename == null) {
      int[] linep = new int[1];
      filename = Context.getSourcePositionFromStack(linep);
      if (filename != null) {
        lineNumber = linep[0];
      } else {
        filename = "";
      } 
    } 
    String sourceName = makeUrlForGeneratedScript(true, filename, lineNumber);


    
    ErrorReporter reporter = DefaultErrorReporter.forEval(cx.getErrorReporter());
    
    Evaluator evaluator = Context.createInterpreter();
    if (evaluator == null) {
      throw new JavaScriptException("Interpreter not present", filename, lineNumber);
    }



    
    Script script = cx.compileString(x.toString(), evaluator, reporter, sourceName, 1, null);
    
    evaluator.setEvalScriptFlag(script);
    Callable c = (Callable)script;
    return c.call(cx, scope, (Scriptable)thisArg, emptyArgs);
  }




  
  public static String typeof(Object value) {
    if (value == null)
      return "object"; 
    if (value == Undefined.instance)
      return "undefined"; 
    if (value instanceof ScriptableObject)
      return ((ScriptableObject)value).getTypeOf(); 
    if (value instanceof Scriptable)
      return (value instanceof Callable) ? "function" : "object"; 
    if (value instanceof CharSequence)
      return "string"; 
    if (value instanceof Number)
      return "number"; 
    if (value instanceof Boolean)
      return "boolean"; 
    throw errorWithClassName("msg.invalid.type", value);
  }




  
  public static String typeofName(Scriptable scope, String id) {
    Context cx = Context.getContext();
    Scriptable val = bind(cx, scope, id);
    if (val == null)
      return "undefined"; 
    return typeof(getObjectProp(val, id, cx));
  }













  
  public static Object add(Object val1, Object val2, Context cx) {
    if (val1 instanceof Number && val2 instanceof Number) {
      return wrapNumber(((Number)val1).doubleValue() + ((Number)val2).doubleValue());
    }
    
    if (val1 instanceof XMLObject) {
      Object test = ((XMLObject)val1).addValues(cx, true, val2);
      if (test != Scriptable.NOT_FOUND) {
        return test;
      }
    } 
    if (val2 instanceof XMLObject) {
      Object test = ((XMLObject)val2).addValues(cx, false, val1);
      if (test != Scriptable.NOT_FOUND) {
        return test;
      }
    } 
    if (val1 instanceof Scriptable)
      val1 = ((Scriptable)val1).getDefaultValue(null); 
    if (val2 instanceof Scriptable)
      val2 = ((Scriptable)val2).getDefaultValue(null); 
    if (!(val1 instanceof CharSequence) && !(val2 instanceof CharSequence)) {
      if (val1 instanceof Number && val2 instanceof Number) {
        return wrapNumber(((Number)val1).doubleValue() + ((Number)val2).doubleValue());
      }
      
      return wrapNumber(toNumber(val1) + toNumber(val2));
    }  return new ConsString(toCharSequence(val1), toCharSequence(val2));
  }
  
  public static CharSequence add(CharSequence val1, Object val2) {
    return new ConsString(val1, toCharSequence(val2));
  }
  
  public static CharSequence add(Object val1, CharSequence val2) {
    return new ConsString(toCharSequence(val1), val2);
  }







  
  @Deprecated
  public static Object nameIncrDecr(Scriptable scopeChain, String id, int incrDecrMask) {
    return nameIncrDecr(scopeChain, id, Context.getContext(), incrDecrMask);
  }
















  
  public static Object nameIncrDecr(Scriptable scopeChain, String id, Context cx, int incrDecrMask) {
    // Byte code:
    //   0: aload_2
    //   1: getfield useDynamicScope : Z
    //   4: ifeq -> 25
    //   7: aload_0
    //   8: invokeinterface getParentScope : ()Lorg/mozilla/javascript/Scriptable;
    //   13: ifnonnull -> 25
    //   16: aload_2
    //   17: getfield topCallScope : Lorg/mozilla/javascript/Scriptable;
    //   20: aload_0
    //   21: invokestatic checkDynamicScope : (Lorg/mozilla/javascript/Scriptable;Lorg/mozilla/javascript/Scriptable;)Lorg/mozilla/javascript/Scriptable;
    //   24: astore_0
    //   25: aload_0
    //   26: astore #4
    //   28: aload #4
    //   30: instanceof org/mozilla/javascript/NativeWith
    //   33: ifeq -> 52
    //   36: aload #4
    //   38: invokeinterface getPrototype : ()Lorg/mozilla/javascript/Scriptable;
    //   43: instanceof org/mozilla/javascript/xml/XMLObject
    //   46: ifeq -> 52
    //   49: goto -> 88
    //   52: aload #4
    //   54: aload_1
    //   55: aload_0
    //   56: invokeinterface get : (Ljava/lang/String;Lorg/mozilla/javascript/Scriptable;)Ljava/lang/Object;
    //   61: astore #5
    //   63: aload #5
    //   65: getstatic org/mozilla/javascript/Scriptable.NOT_FOUND : Ljava/lang/Object;
    //   68: if_acmpeq -> 74
    //   71: goto -> 105
    //   74: aload #4
    //   76: invokeinterface getPrototype : ()Lorg/mozilla/javascript/Scriptable;
    //   81: astore #4
    //   83: aload #4
    //   85: ifnonnull -> 28
    //   88: aload_0
    //   89: invokeinterface getParentScope : ()Lorg/mozilla/javascript/Scriptable;
    //   94: astore_0
    //   95: aload_0
    //   96: ifnonnull -> 0
    //   99: aload_0
    //   100: aload_1
    //   101: invokestatic notFoundError : (Lorg/mozilla/javascript/Scriptable;Ljava/lang/String;)Ljava/lang/RuntimeException;
    //   104: athrow
    //   105: aload #4
    //   107: aload_1
    //   108: aload_0
    //   109: aload #5
    //   111: iload_3
    //   112: invokestatic doScriptableIncrDecr : (Lorg/mozilla/javascript/Scriptable;Ljava/lang/String;Lorg/mozilla/javascript/Scriptable;Ljava/lang/Object;I)Ljava/lang/Object;
    //   115: areturn
    // Line number table:
    //   Java source line number -> byte code offset
    //   #2769	-> 0
    //   #2770	-> 16
    //   #2772	-> 25
    //   #2774	-> 28
    //   #2776	-> 49
    //   #2778	-> 52
    //   #2779	-> 63
    //   #2780	-> 71
    //   #2782	-> 74
    //   #2783	-> 83
    //   #2784	-> 88
    //   #2785	-> 95
    //   #2786	-> 99
    //   #2788	-> 105
    // Local variable table:
    //   start	length	slot	name	descriptor
    //   63	25	5	value	Ljava/lang/Object;
    //   0	116	0	scopeChain	Lorg/mozilla/javascript/Scriptable;
    //   0	116	1	id	Ljava/lang/String;
    //   0	116	2	cx	Lorg/mozilla/javascript/Context;
    //   0	116	3	incrDecrMask	I
    //   28	88	4	target	Lorg/mozilla/javascript/Scriptable;
    //   105	11	5	value	Ljava/lang/Object;
  }















  
  @Deprecated
  public static Object propIncrDecr(Object obj, String id, Context cx, int incrDecrMask) {
    return propIncrDecr(obj, id, cx, getTopCallScope(cx), incrDecrMask);
  }


  
  public static Object propIncrDecr(Object obj, String id, Context cx, Scriptable scope, int incrDecrMask) {
    Object value;
    Scriptable start = toObjectOrNull(cx, obj, scope);
    if (start == null) {
      throw undefReadError(obj, id);
    }
    
    Scriptable target = start;

    
    while (true) {
      value = target.get(id, start);
      if (value != Scriptable.NOT_FOUND) {
        break;
      }
      target = target.getPrototype();
      if (target == null) {
        start.put(id, start, NaNobj);
        return NaNobj;
      } 
    }  return doScriptableIncrDecr(target, id, start, value, incrDecrMask);
  }





  
  private static Object doScriptableIncrDecr(Scriptable target, String id, Scriptable protoChainStart, Object value, int incrDecrMask) {
    double number;
    boolean post = ((incrDecrMask & 0x2) != 0);
    
    if (value instanceof Number) {
      number = ((Number)value).doubleValue();
    } else {
      number = toNumber(value);
      if (post)
      {
        value = wrapNumber(number);
      }
    } 
    if ((incrDecrMask & 0x1) == 0) {
      number++;
    } else {
      number--;
    } 
    Number result = wrapNumber(number);
    target.put(id, protoChainStart, result);
    if (post) {
      return value;
    }
    return result;
  }






  
  @Deprecated
  public static Object elemIncrDecr(Object obj, Object index, Context cx, int incrDecrMask) {
    return elemIncrDecr(obj, index, cx, getTopCallScope(cx), incrDecrMask);
  }


  
  public static Object elemIncrDecr(Object obj, Object index, Context cx, Scriptable scope, int incrDecrMask) {
    double number;
    Object value = getObjectElem(obj, index, cx, scope);
    boolean post = ((incrDecrMask & 0x2) != 0);
    
    if (value instanceof Number) {
      number = ((Number)value).doubleValue();
    } else {
      number = toNumber(value);
      if (post)
      {
        value = wrapNumber(number);
      }
    } 
    if ((incrDecrMask & 0x1) == 0) {
      number++;
    } else {
      number--;
    } 
    Number result = wrapNumber(number);
    setObjectElem(obj, index, result, cx, scope);
    if (post) {
      return value;
    }
    return result;
  }





  
  @Deprecated
  public static Object refIncrDecr(Ref ref, Context cx, int incrDecrMask) {
    return refIncrDecr(ref, cx, getTopCallScope(cx), incrDecrMask);
  }

  
  public static Object refIncrDecr(Ref ref, Context cx, Scriptable scope, int incrDecrMask) {
    double number;
    Object value = ref.get(cx);
    boolean post = ((incrDecrMask & 0x2) != 0);
    
    if (value instanceof Number) {
      number = ((Number)value).doubleValue();
    } else {
      number = toNumber(value);
      if (post)
      {
        value = wrapNumber(number);
      }
    } 
    if ((incrDecrMask & 0x1) == 0) {
      number++;
    } else {
      number--;
    } 
    Number result = wrapNumber(number);
    ref.set(cx, scope, result);
    if (post) {
      return value;
    }
    return result;
  }

  
  public static Object toPrimitive(Object val) {
    return toPrimitive(val, null);
  }

  
  public static Object toPrimitive(Object val, Class<?> typeHint) {
    if (!(val instanceof Scriptable)) {
      return val;
    }
    Scriptable s = (Scriptable)val;
    Object result = s.getDefaultValue(typeHint);
    if (result instanceof Scriptable)
      throw typeError0("msg.bad.default.value"); 
    return result;
  }






  
  public static boolean eq(Object x, Object y) {
    if (x == null || x == Undefined.instance) {
      if (y == null || y == Undefined.instance) {
        return true;
      }
      if (y instanceof ScriptableObject) {
        Object test = ((ScriptableObject)y).equivalentValues(x);
        if (test != Scriptable.NOT_FOUND) {
          return ((Boolean)test).booleanValue();
        }
      } 
      return false;
    }  if (x instanceof Number)
      return eqNumber(((Number)x).doubleValue(), y); 
    if (x == y)
      return true; 
    if (x instanceof CharSequence)
      return eqString((CharSequence)x, y); 
    if (x instanceof Boolean) {
      boolean b = ((Boolean)x).booleanValue();
      if (y instanceof Boolean) {
        return (b == ((Boolean)y).booleanValue());
      }
      if (y instanceof ScriptableObject) {
        Object test = ((ScriptableObject)y).equivalentValues(x);
        if (test != Scriptable.NOT_FOUND) {
          return ((Boolean)test).booleanValue();
        }
      } 
      return eqNumber(b ? 1.0D : 0.0D, y);
    }  if (x instanceof Scriptable) {
      if (y instanceof Scriptable) {
        if (x instanceof ScriptableObject) {
          Object test = ((ScriptableObject)x).equivalentValues(y);
          if (test != Scriptable.NOT_FOUND) {
            return ((Boolean)test).booleanValue();
          }
        } 
        if (y instanceof ScriptableObject) {
          Object test = ((ScriptableObject)y).equivalentValues(x);
          if (test != Scriptable.NOT_FOUND) {
            return ((Boolean)test).booleanValue();
          }
        } 
        if (x instanceof Wrapper && y instanceof Wrapper) {

          
          Object unwrappedX = ((Wrapper)x).unwrap();
          Object unwrappedY = ((Wrapper)y).unwrap();
          return (unwrappedX == unwrappedY || (isPrimitive(unwrappedX) && isPrimitive(unwrappedY) && eq(unwrappedX, unwrappedY)));
        } 


        
        return false;
      }  if (y instanceof Boolean) {
        if (x instanceof ScriptableObject) {
          Object test = ((ScriptableObject)x).equivalentValues(y);
          if (test != Scriptable.NOT_FOUND) {
            return ((Boolean)test).booleanValue();
          }
        } 
        double d = ((Boolean)y).booleanValue() ? 1.0D : 0.0D;
        return eqNumber(d, x);
      }  if (y instanceof Number)
        return eqNumber(((Number)y).doubleValue(), x); 
      if (y instanceof CharSequence) {
        return eqString((CharSequence)y, x);
      }
      
      return false;
    } 
    warnAboutNonJSObject(x);
    return (x == y);
  }

  
  public static boolean isPrimitive(Object obj) {
    return (obj == null || obj == Undefined.instance || obj instanceof Number || obj instanceof String || obj instanceof Boolean);
  }



  
  static boolean eqNumber(double x, Object y) {
    while (true) {
      if (y == null || y == Undefined.instance)
        return false; 
      if (y instanceof Number)
        return (x == ((Number)y).doubleValue()); 
      if (y instanceof CharSequence)
        return (x == toNumber(y)); 
      if (y instanceof Boolean)
        return (x == (((Boolean)y).booleanValue() ? 1.0D : 0.0D)); 
      if (y instanceof Scriptable) {
        if (y instanceof ScriptableObject) {
          Object xval = wrapNumber(x);
          Object test = ((ScriptableObject)y).equivalentValues(xval);
          if (test != Scriptable.NOT_FOUND) {
            return ((Boolean)test).booleanValue();
          }
        } 
        y = toPrimitive(y); continue;
      }  break;
    }  warnAboutNonJSObject(y);
    return false;
  }



  
  private static boolean eqString(CharSequence x, Object y) {
    while (true) {
      if (y == null || y == Undefined.instance)
        return false; 
      if (y instanceof CharSequence) {
        CharSequence c = (CharSequence)y;
        return (x.length() == c.length() && x.toString().equals(c.toString()));
      }  if (y instanceof Number)
        return (toNumber(x.toString()) == ((Number)y).doubleValue()); 
      if (y instanceof Boolean)
        return (toNumber(x.toString()) == (((Boolean)y).booleanValue() ? 1.0D : 0.0D)); 
      if (y instanceof Scriptable) {
        if (y instanceof ScriptableObject) {
          Object test = ((ScriptableObject)y).equivalentValues(x.toString());
          if (test != Scriptable.NOT_FOUND) {
            return ((Boolean)test).booleanValue();
          }
        } 
        y = toPrimitive(y); continue;
      }  break;
    } 
    warnAboutNonJSObject(y);
    return false;
  }


  
  public static boolean shallowEq(Object x, Object y) {
    if (x == y) {
      if (!(x instanceof Number)) {
        return true;
      }
      
      double d = ((Number)x).doubleValue();
      return (d == d);
    } 
    if (x == null || x == Undefined.instance)
      return false; 
    if (x instanceof Number) {
      if (y instanceof Number) {
        return (((Number)x).doubleValue() == ((Number)y).doubleValue());
      }
    } else if (x instanceof CharSequence) {
      if (y instanceof CharSequence) {
        return x.toString().equals(y.toString());
      }
    } else if (x instanceof Boolean) {
      if (y instanceof Boolean) {
        return x.equals(y);
      }
    } else if (x instanceof Scriptable) {
      if (x instanceof Wrapper && y instanceof Wrapper) {
        return (((Wrapper)x).unwrap() == ((Wrapper)y).unwrap());
      }
    } else {
      warnAboutNonJSObject(x);
      return (x == y);
    } 
    return false;
  }







  
  public static boolean instanceOf(Object a, Object b, Context cx) {
    if (!(b instanceof Scriptable)) {
      throw typeError0("msg.instanceof.not.object");
    }

    
    if (!(a instanceof Scriptable)) {
      return false;
    }
    return ((Scriptable)b).hasInstance((Scriptable)a);
  }





  
  public static boolean jsDelegatesTo(Scriptable lhs, Scriptable rhs) {
    Scriptable proto = lhs.getPrototype();
    
    while (proto != null) {
      if (proto.equals(rhs)) return true; 
      proto = proto.getPrototype();
    } 
    
    return false;
  }















  
  public static boolean in(Object a, Object b, Context cx) {
    if (!(b instanceof Scriptable)) {
      throw typeError0("msg.in.not.object");
    }
    
    return hasObjectElem((Scriptable)b, a, cx);
  }
  
  public static boolean cmp_LT(Object val1, Object val2) {
    double d1;
    double d2;
    if (val1 instanceof Number && val2 instanceof Number) {
      d1 = ((Number)val1).doubleValue();
      d2 = ((Number)val2).doubleValue();
    } else {
      if (val1 instanceof Scriptable)
        val1 = ((Scriptable)val1).getDefaultValue(NumberClass); 
      if (val2 instanceof Scriptable)
        val2 = ((Scriptable)val2).getDefaultValue(NumberClass); 
      if (val1 instanceof CharSequence && val2 instanceof CharSequence) {
        return (val1.toString().compareTo(val2.toString()) < 0);
      }
      d1 = toNumber(val1);
      d2 = toNumber(val2);
    } 
    return (d1 < d2);
  }
  
  public static boolean cmp_LE(Object val1, Object val2) {
    double d1;
    double d2;
    if (val1 instanceof Number && val2 instanceof Number) {
      d1 = ((Number)val1).doubleValue();
      d2 = ((Number)val2).doubleValue();
    } else {
      if (val1 instanceof Scriptable)
        val1 = ((Scriptable)val1).getDefaultValue(NumberClass); 
      if (val2 instanceof Scriptable)
        val2 = ((Scriptable)val2).getDefaultValue(NumberClass); 
      if (val1 instanceof CharSequence && val2 instanceof CharSequence) {
        return (val1.toString().compareTo(val2.toString()) <= 0);
      }
      d1 = toNumber(val1);
      d2 = toNumber(val2);
    } 
    return (d1 <= d2);
  }




  
  public static ScriptableObject getGlobal(Context cx) {
    String GLOBAL_CLASS = "org.mozilla.javascript.tools.shell.Global";
    Class<?> globalClass = Kit.classOrNull("org.mozilla.javascript.tools.shell.Global");
    if (globalClass != null) {
      try {
        Class<?>[] parm = new Class[] { ContextClass };
        Constructor<?> globalClassCtor = globalClass.getConstructor(parm);
        Object[] arg = { cx };
        return (ScriptableObject)globalClassCtor.newInstance(arg);
      }
      catch (RuntimeException e) {
        throw e;
      }
      catch (Exception e) {}
    }

    
    return new ImporterTopLevel(cx);
  }

  
  public static boolean hasTopCall(Context cx) {
    return (cx.topCallScope != null);
  }

  
  public static Scriptable getTopCallScope(Context cx) {
    Scriptable scope = cx.topCallScope;
    if (scope == null) {
      throw new IllegalStateException();
    }
    return scope;
  }


  
  public static Object doTopCall(Callable callable, Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
    Object result;
    if (scope == null)
      throw new IllegalArgumentException(); 
    if (cx.topCallScope != null) throw new IllegalStateException();

    
    cx.topCallScope = ScriptableObject.getTopLevelScope(scope);
    cx.useDynamicScope = cx.hasFeature(7);
    ContextFactory f = cx.getFactory();
    try {
      result = f.doTopCall(callable, cx, scope, thisObj, args);
    } finally {
      cx.topCallScope = null;
      
      cx.cachedXMLLib = null;
      
      if (cx.currentActivationCall != null)
      {
        
        throw new IllegalStateException();
      }
    } 
    return result;
  }









  
  static Scriptable checkDynamicScope(Scriptable possibleDynamicScope, Scriptable staticTopScope) {
    if (possibleDynamicScope == staticTopScope) {
      return possibleDynamicScope;
    }
    Scriptable proto = possibleDynamicScope;
    while (true) {
      proto = proto.getPrototype();
      if (proto == staticTopScope) {
        return possibleDynamicScope;
      }
      if (proto == null) {
        return staticTopScope;
      }
    } 
  }

  
  public static void addInstructionCount(Context cx, int instructionsToAdd) {
    cx.instructionCount += instructionsToAdd;
    if (cx.instructionCount > cx.instructionThreshold) {
      
      cx.observeInstructionCount(cx.instructionCount);
      cx.instructionCount = 0;
    } 
  }



  
  public static void initScript(NativeFunction funObj, Scriptable thisObj, Context cx, Scriptable scope, boolean evalScript) {
    if (cx.topCallScope == null) {
      throw new IllegalStateException();
    }
    int varCount = funObj.getParamAndVarCount();
    if (varCount != 0) {
      
      Scriptable varScope = scope;

      
      while (varScope instanceof NativeWith) {
        varScope = varScope.getParentScope();
      }
      
      for (int i = varCount; i-- != 0; ) {
        String name = funObj.getParamOrVarName(i);
        boolean isConst = funObj.getParamOrVarConst(i);

        
        if (!ScriptableObject.hasProperty(scope, name)) {
          if (isConst) {
            ScriptableObject.defineConstProperty(varScope, name); continue;
          }  if (!evalScript) {
            
            ScriptableObject.defineProperty(varScope, name, Undefined.instance, 4);
            
            continue;
          } 
          varScope.put(name, varScope, Undefined.instance);
          continue;
        } 
        ScriptableObject.redefineProperty(scope, name, isConst);
      } 
    } 
  }




  
  public static Scriptable createFunctionActivation(NativeFunction funObj, Scriptable scope, Object[] args) {
    return new NativeCall(funObj, scope, args);
  }



  
  public static void enterActivationFunction(Context cx, Scriptable scope) {
    if (cx.topCallScope == null)
      throw new IllegalStateException(); 
    NativeCall call = (NativeCall)scope;
    call.parentActivationCall = cx.currentActivationCall;
    cx.currentActivationCall = call;
  }

  
  public static void exitActivationFunction(Context cx) {
    NativeCall call = cx.currentActivationCall;
    cx.currentActivationCall = call.parentActivationCall;
    call.parentActivationCall = null;
  }

  
  static NativeCall findFunctionActivation(Context cx, Function f) {
    NativeCall call = cx.currentActivationCall;
    while (call != null) {
      if (call.function == f)
        return call; 
      call = call.parentActivationCall;
    } 
    return null;
  }






  
  public static Scriptable newCatchScope(Throwable t, Scriptable lastCatchScope, String exceptionName, Context cx, Scriptable scope) {
    Object obj;
    boolean cacheObj;
    if (t instanceof JavaScriptException) {
      cacheObj = false;
      obj = ((JavaScriptException)t).getValue();
    } else {
      cacheObj = true;



      
      if (lastCatchScope != null) {
        NativeObject last = (NativeObject)lastCatchScope;
        obj = last.getAssociatedValue(t);
        if (obj == null) Kit.codeBug();
      
      } else {
        RhinoException re;
        TopLevel.NativeErrors type;
        String errorMsg;
        Object[] args;
        Throwable javaException = null;
        
        if (t instanceof EcmaError) {
          EcmaError ee = (EcmaError)t;
          re = ee;
          type = TopLevel.NativeErrors.valueOf(ee.getName());
          errorMsg = ee.getErrorMessage();
        } else if (t instanceof WrappedException) {
          WrappedException we = (WrappedException)t;
          re = we;
          javaException = we.getWrappedException();
          type = TopLevel.NativeErrors.JavaException;
          errorMsg = javaException.getClass().getName() + ": " + javaException.getMessage();
        }
        else if (t instanceof EvaluatorException) {
          
          EvaluatorException ee = (EvaluatorException)t;
          re = ee;
          type = TopLevel.NativeErrors.InternalError;
          errorMsg = ee.getMessage();
        } else if (cx.hasFeature(13)) {

          
          re = new WrappedException(t);
          type = TopLevel.NativeErrors.JavaException;
          errorMsg = t.toString();
        }
        else {
          
          throw Kit.codeBug();
        } 
        
        String sourceUri = re.sourceName();
        if (sourceUri == null) {
          sourceUri = "";
        }
        int line = re.lineNumber();
        
        if (line > 0) {
          args = new Object[] { errorMsg, sourceUri, Integer.valueOf(line) };
        } else {
          args = new Object[] { errorMsg, sourceUri };
        } 
        
        Scriptable errorObject = newNativeError(cx, scope, type, args);
        
        if (errorObject instanceof NativeError) {
          ((NativeError)errorObject).setStackProvider(re);
        }
        
        if (javaException != null && isVisible(cx, javaException)) {
          Object wrap = cx.getWrapFactory().wrap(cx, scope, javaException, null);
          
          ScriptableObject.defineProperty(errorObject, "javaException", wrap, 5);
        } 

        
        if (isVisible(cx, re)) {
          Object wrap = cx.getWrapFactory().wrap(cx, scope, re, null);
          ScriptableObject.defineProperty(errorObject, "rhinoException", wrap, 5);
        } 

        
        obj = errorObject;
      } 
    } 
    NativeObject catchScopeObject = new NativeObject();
    
    catchScopeObject.defineProperty(exceptionName, obj, 4);

    
    if (isVisible(cx, t))
    {

      
      catchScopeObject.defineProperty("__exception__", Context.javaToJS(t, scope), 6);
    }


    
    if (cacheObj) {
      catchScopeObject.associateValue(t, obj);
    }
    return catchScopeObject;
  }


  
  public static Scriptable wrapException(Throwable t, Scriptable scope, Context cx) {
    RhinoException re;
    String errorName, errorMsg;
    Object[] args;
    Throwable javaException = null;
    
    if (t instanceof EcmaError) {
      EcmaError ee = (EcmaError)t;
      re = ee;
      errorName = ee.getName();
      errorMsg = ee.getErrorMessage();
    } else if (t instanceof WrappedException) {
      WrappedException we = (WrappedException)t;
      re = we;
      javaException = we.getWrappedException();
      errorName = "JavaException";
      errorMsg = javaException.getClass().getName() + ": " + javaException.getMessage();
    }
    else if (t instanceof EvaluatorException) {
      
      EvaluatorException ee = (EvaluatorException)t;
      re = ee;
      errorName = "InternalError";
      errorMsg = ee.getMessage();
    } else if (cx.hasFeature(13)) {

      
      re = new WrappedException(t);
      errorName = "JavaException";
      errorMsg = t.toString();
    }
    else {
      
      throw Kit.codeBug();
    } 
    
    String sourceUri = re.sourceName();
    if (sourceUri == null) {
      sourceUri = "";
    }
    int line = re.lineNumber();
    
    if (line > 0) {
      args = new Object[] { errorMsg, sourceUri, Integer.valueOf(line) };
    } else {
      args = new Object[] { errorMsg, sourceUri };
    } 
    
    Scriptable errorObject = cx.newObject(scope, errorName, args);
    ScriptableObject.putProperty(errorObject, "name", errorName);
    
    if (errorObject instanceof NativeError) {
      ((NativeError)errorObject).setStackProvider(re);
    }
    
    if (javaException != null && isVisible(cx, javaException)) {
      Object wrap = cx.getWrapFactory().wrap(cx, scope, javaException, null);
      
      ScriptableObject.defineProperty(errorObject, "javaException", wrap, 5);
    } 

    
    if (isVisible(cx, re)) {
      Object wrap = cx.getWrapFactory().wrap(cx, scope, re, null);
      ScriptableObject.defineProperty(errorObject, "rhinoException", wrap, 5);
    } 

    
    return errorObject;
  }
  
  private static boolean isVisible(Context cx, Object obj) {
    ClassShutter shutter = cx.getClassShutter();
    return (shutter == null || shutter.visibleToScripts(obj.getClass().getName()));
  }



  
  public static Scriptable enterWith(Object obj, Context cx, Scriptable scope) {
    Scriptable sobj = toObjectOrNull(cx, obj, scope);
    if (sobj == null) {
      throw typeError1("msg.undef.with", toString(obj));
    }
    if (sobj instanceof XMLObject) {
      XMLObject xmlObject = (XMLObject)sobj;
      return xmlObject.enterWith(scope);
    } 
    return new NativeWith(scope, sobj);
  }

  
  public static Scriptable leaveWith(Scriptable scope) {
    NativeWith nw = (NativeWith)scope;
    return nw.getParentScope();
  }

  
  public static Scriptable enterDotQuery(Object value, Scriptable scope) {
    if (!(value instanceof XMLObject)) {
      throw notXmlError(value);
    }
    XMLObject object = (XMLObject)value;
    return object.enterDotQuery(scope);
  }


  
  public static Object updateDotQuery(boolean value, Scriptable scope) {
    NativeWith nw = (NativeWith)scope;
    return nw.updateDotQuery(value);
  }

  
  public static Scriptable leaveDotQuery(Scriptable scope) {
    NativeWith nw = (NativeWith)scope;
    return nw.getParentScope();
  }


  
  public static void setFunctionProtoAndParent(BaseFunction fn, Scriptable scope) {
    fn.setParentScope(scope);
    fn.setPrototype(ScriptableObject.getFunctionPrototype(scope));
  }



  
  public static void setObjectProtoAndParent(ScriptableObject object, Scriptable scope) {
    scope = ScriptableObject.getTopLevelScope(scope);
    object.setParentScope(scope);
    Scriptable proto = ScriptableObject.getClassPrototype(scope, object.getClassName());
    
    object.setPrototype(proto);
  }



  
  public static void setBuiltinProtoAndParent(ScriptableObject object, Scriptable scope, TopLevel.Builtins type) {
    scope = ScriptableObject.getTopLevelScope(scope);
    object.setParentScope(scope);
    object.setPrototype(TopLevel.getBuiltinPrototype(scope, type));
  }




  
  public static void initFunction(Context cx, Scriptable scope, NativeFunction function, int type, boolean fromEvalCode) {
    if (type == 1) {
      String name = function.getFunctionName();
      if (name != null && name.length() != 0) {
        if (!fromEvalCode) {

          
          ScriptableObject.defineProperty(scope, name, function, 4);
        } else {
          
          scope.put(name, scope, function);
        } 
      }
    } else if (type == 3) {
      String name = function.getFunctionName();
      if (name != null && name.length() != 0) {


        
        while (scope instanceof NativeWith) {
          scope = scope.getParentScope();
        }
        scope.put(name, scope, function);
      } 
    } else {
      throw Kit.codeBug();
    } 
  }



  
  public static Scriptable newArrayLiteral(Object[] objects, int[] skipIndices, Context cx, Scriptable scope) {
    int SKIP_DENSITY = 2;
    int count = objects.length;
    int skipCount = 0;
    if (skipIndices != null) {
      skipCount = skipIndices.length;
    }
    int length = count + skipCount;
    if (length > 1 && skipCount * 2 < length) {
      Object[] sparse;
      
      if (skipCount == 0) {
        sparse = objects;
      } else {
        sparse = new Object[length];
        int k = 0;
        for (int m = 0, n = 0; m != length; m++) {
          if (k != skipCount && skipIndices[k] == m) {
            sparse[m] = Scriptable.NOT_FOUND;
            k++;
          } else {
            
            sparse[m] = objects[n];
            n++;
          } 
        } 
      }  return cx.newArray(scope, sparse);
    } 
    
    Scriptable array = cx.newArray(scope, length);
    
    int skip = 0;
    for (int i = 0, j = 0; i != length; i++) {
      if (skip != skipCount && skipIndices[skip] == i) {
        skip++;
      } else {
        
        ScriptableObject.putProperty(array, i, objects[j]);
        j++;
      } 
    }  return array;
  }











  
  @Deprecated
  public static Scriptable newObjectLiteral(Object[] propertyIds, Object[] propertyValues, Context cx, Scriptable scope) {
    return newObjectLiteral(propertyIds, propertyValues, null, cx, scope);
  }




  
  public static Scriptable newObjectLiteral(Object[] propertyIds, Object[] propertyValues, int[] getterSetters, Context cx, Scriptable scope) {
    Scriptable object = cx.newObject(scope);
    for (int i = 0, end = propertyIds.length; i != end; i++) {
      Object id = propertyIds[i];
      int getterSetter = (getterSetters == null) ? 0 : getterSetters[i];
      Object value = propertyValues[i];
      if (id instanceof String) {
        if (getterSetter == 0) {
          if (isSpecialProperty((String)id)) {
            Ref ref = specialRef(object, (String)id, cx, scope);
            ref.set(cx, scope, value);
          } else {
            object.put((String)id, object, value);
          } 
        } else {
          ScriptableObject so = (ScriptableObject)object;
          Callable getterOrSetter = (Callable)value;
          boolean isSetter = (getterSetter == 1);
          so.setGetterOrSetter((String)id, 0, getterOrSetter, isSetter);
        } 
      } else {
        int index = ((Integer)id).intValue();
        object.put(index, object, value);
      } 
    } 
    return object;
  }

  
  public static boolean isArrayObject(Object obj) {
    return (obj instanceof NativeArray || obj instanceof Arguments);
  }

  
  public static Object[] getArrayElements(Scriptable object) {
    Context cx = Context.getContext();
    long longLen = NativeArray.getLengthProperty(cx, object);
    if (longLen > 2147483647L)
    {
      throw new IllegalArgumentException();
    }
    int len = (int)longLen;
    if (len == 0) {
      return emptyArgs;
    }
    Object[] result = new Object[len];
    for (int i = 0; i < len; i++) {
      Object elem = ScriptableObject.getProperty(object, i);
      result[i] = (elem == Scriptable.NOT_FOUND) ? Undefined.instance : elem;
    } 
    
    return result;
  }

  
  static void checkDeprecated(Context cx, String name) {
    int version = cx.getLanguageVersion();
    if (version >= 140 || version == 0) {
      String msg = getMessage1("msg.deprec.ctor", name);
      if (version == 0) {
        Context.reportWarning(msg);
      } else {
        throw Context.reportRuntimeError(msg);
      } 
    } 
  }
  
  public static String getMessage0(String messageId) {
    return getMessage(messageId, null);
  }

  
  public static String getMessage1(String messageId, Object arg1) {
    Object[] arguments = { arg1 };
    return getMessage(messageId, arguments);
  }


  
  public static String getMessage2(String messageId, Object arg1, Object arg2) {
    Object[] arguments = { arg1, arg2 };
    return getMessage(messageId, arguments);
  }


  
  public static String getMessage3(String messageId, Object arg1, Object arg2, Object arg3) {
    Object[] arguments = { arg1, arg2, arg3 };
    return getMessage(messageId, arguments);
  }


  
  public static String getMessage4(String messageId, Object arg1, Object arg2, Object arg3, Object arg4) {
    Object[] arguments = { arg1, arg2, arg3, arg4 };
    return getMessage(messageId, arguments);
  }


















  
  public static MessageProvider messageProvider = new DefaultMessageProvider();

  
  public static String getMessage(String messageId, Object[] arguments) {
    return messageProvider.getMessage(messageId, arguments);
  }
  
  public static interface MessageProvider {
    String getMessage(String param1String, Object[] param1ArrayOfObject); }
  
  private static class DefaultMessageProvider implements MessageProvider { private DefaultMessageProvider() {}
    
    public String getMessage(String messageId, Object[] arguments) {
      String formatString, defaultResource = "org.mozilla.javascript.resources.Messages";

      
      Context cx = Context.getCurrentContext();
      Locale locale = (cx != null) ? cx.getLocale() : Locale.getDefault();

      
      ResourceBundle rb = ResourceBundle.getBundle("org.mozilla.javascript.resources.Messages", locale);

      
      try {
        formatString = rb.getString(messageId);
      } catch (MissingResourceException mre) {
        throw new RuntimeException("no message resource found for message property " + messageId);
      } 






      
      MessageFormat formatter = new MessageFormat(formatString);
      return formatter.format(arguments);
    } }


  
  public static EcmaError constructError(String error, String message) {
    int[] linep = new int[1];
    String filename = Context.getSourcePositionFromStack(linep);
    return constructError(error, message, filename, linep[0], null, 0);
  }



  
  public static EcmaError constructError(String error, String message, int lineNumberDelta) {
    int[] linep = new int[1];
    String filename = Context.getSourcePositionFromStack(linep);
    if (linep[0] != 0) {
      linep[0] = linep[0] + lineNumberDelta;
    }
    return constructError(error, message, filename, linep[0], null, 0);
  }






  
  public static EcmaError constructError(String error, String message, String sourceName, int lineNumber, String lineSource, int columnNumber) {
    return new EcmaError(error, message, sourceName, lineNumber, lineSource, columnNumber);
  }


  
  public static EcmaError typeError(String message) {
    return constructError("TypeError", message);
  }

  
  public static EcmaError typeError0(String messageId) {
    String msg = getMessage0(messageId);
    return typeError(msg);
  }

  
  public static EcmaError typeError1(String messageId, String arg1) {
    String msg = getMessage1(messageId, arg1);
    return typeError(msg);
  }


  
  public static EcmaError typeError2(String messageId, String arg1, String arg2) {
    String msg = getMessage2(messageId, arg1, arg2);
    return typeError(msg);
  }


  
  public static EcmaError typeError3(String messageId, String arg1, String arg2, String arg3) {
    String msg = getMessage3(messageId, arg1, arg2, arg3);
    return typeError(msg);
  }

  
  public static RuntimeException undefReadError(Object object, Object id) {
    return typeError2("msg.undef.prop.read", toString(object), toString(id));
  }

  
  public static RuntimeException undefCallError(Object object, Object id) {
    return typeError2("msg.undef.method.call", toString(object), toString(id));
  }



  
  public static RuntimeException undefWriteError(Object object, Object id, Object value) {
    return typeError3("msg.undef.prop.write", toString(object), toString(id), toString(value));
  }


  
  private static RuntimeException undefDeleteError(Object object, Object id) {
    throw typeError2("msg.undef.prop.delete", toString(object), toString(id));
  }



  
  public static RuntimeException notFoundError(Scriptable object, String property) {
    String msg = getMessage1("msg.is.not.defined", property);
    throw constructError("ReferenceError", msg);
  }

  
  public static RuntimeException notFunctionError(Object value) {
    return notFunctionError(value, value);
  }



  
  public static RuntimeException notFunctionError(Object value, Object messageHelper) {
    String msg = (messageHelper == null) ? "null" : messageHelper.toString();
    
    if (value == Scriptable.NOT_FOUND) {
      return typeError1("msg.function.not.found", msg);
    }
    return typeError2("msg.isnt.function", msg, typeof(value));
  }



  
  public static RuntimeException notFunctionError(Object obj, Object value, String propertyName) {
    String objString = toString(obj);
    if (obj instanceof NativeFunction) {
      
      int paren = objString.indexOf(')');
      int curly = objString.indexOf('{', paren);
      if (curly > -1) {
        objString = objString.substring(0, curly + 1) + "...}";
      }
    } 
    if (value == Scriptable.NOT_FOUND) {
      return typeError2("msg.function.not.found.in", propertyName, objString);
    }
    
    return typeError3("msg.isnt.function.in", propertyName, objString, typeof(value));
  }


  
  private static RuntimeException notXmlError(Object value) {
    throw typeError1("msg.isnt.xml.object", toString(value));
  }

  
  private static void warnAboutNonJSObject(Object nonJSObject) {
    String message = "RHINO USAGE WARNING: Missed Context.javaToJS() conversion:\nRhino runtime detected object " + nonJSObject + " of class " + nonJSObject.getClass().getName() + " where it expected String, Number, Boolean or Scriptable instance. Please check your code for missing Context.javaToJS() call.";

    
    Context.reportWarning(message);
    
    System.err.println(message);
  }

  
  public static RegExpProxy getRegExpProxy(Context cx) {
    return cx.getRegExpProxy();
  }

  
  public static void setRegExpProxy(Context cx, RegExpProxy proxy) {
    if (proxy == null) throw new IllegalArgumentException(); 
    cx.regExpProxy = proxy;
  }

  
  public static RegExpProxy checkRegExpProxy(Context cx) {
    RegExpProxy result = getRegExpProxy(cx);
    if (result == null) {
      throw Context.reportRuntimeError0("msg.no.regexp");
    }
    return result;
  }

  
  public static Scriptable wrapRegExp(Context cx, Scriptable scope, Object compiled) {
    return cx.getRegExpProxy().wrapRegExp(cx, scope, compiled);
  }


  
  private static XMLLib currentXMLLib(Context cx) {
    if (cx.topCallScope == null) {
      throw new IllegalStateException();
    }
    XMLLib xmlLib = cx.cachedXMLLib;
    if (xmlLib == null) {
      xmlLib = XMLLib.extractFromScope(cx.topCallScope);
      if (xmlLib == null)
        throw new IllegalStateException(); 
      cx.cachedXMLLib = xmlLib;
    } 
    
    return xmlLib;
  }







  
  public static String escapeAttributeValue(Object value, Context cx) {
    XMLLib xmlLib = currentXMLLib(cx);
    return xmlLib.escapeAttributeValue(value);
  }







  
  public static String escapeTextValue(Object value, Context cx) {
    XMLLib xmlLib = currentXMLLib(cx);
    return xmlLib.escapeTextValue(value);
  }


  
  public static Ref memberRef(Object obj, Object elem, Context cx, int memberTypeFlags) {
    if (!(obj instanceof XMLObject)) {
      throw notXmlError(obj);
    }
    XMLObject xmlObject = (XMLObject)obj;
    return xmlObject.memberRef(cx, elem, memberTypeFlags);
  }


  
  public static Ref memberRef(Object obj, Object namespace, Object elem, Context cx, int memberTypeFlags) {
    if (!(obj instanceof XMLObject)) {
      throw notXmlError(obj);
    }
    XMLObject xmlObject = (XMLObject)obj;
    return xmlObject.memberRef(cx, namespace, elem, memberTypeFlags);
  }


  
  public static Ref nameRef(Object name, Context cx, Scriptable scope, int memberTypeFlags) {
    XMLLib xmlLib = currentXMLLib(cx);
    return xmlLib.nameRef(cx, name, scope, memberTypeFlags);
  }


  
  public static Ref nameRef(Object namespace, Object name, Context cx, Scriptable scope, int memberTypeFlags) {
    XMLLib xmlLib = currentXMLLib(cx);
    return xmlLib.nameRef(cx, namespace, name, scope, memberTypeFlags);
  }

  
  private static void storeIndexResult(Context cx, int index) {
    cx.scratchIndex = index;
  }

  
  static int lastIndexResult(Context cx) {
    return cx.scratchIndex;
  }

  
  public static void storeUint32Result(Context cx, long value) {
    if (value >>> 32L != 0L)
      throw new IllegalArgumentException(); 
    cx.scratchUint32 = value;
  }

  
  public static long lastUint32Result(Context cx) {
    long value = cx.scratchUint32;
    if (value >>> 32L != 0L)
      throw new IllegalStateException(); 
    return value;
  }


  
  private static void storeScriptable(Context cx, Scriptable value) {
    if (cx.scratchScriptable != null)
      throw new IllegalStateException(); 
    cx.scratchScriptable = value;
  }

  
  public static Scriptable lastStoredScriptable(Context cx) {
    Scriptable result = cx.scratchScriptable;
    cx.scratchScriptable = null;
    return result;
  }


  
  static String makeUrlForGeneratedScript(boolean isEval, String masterScriptUrl, int masterScriptLine) {
    if (isEval) {
      return masterScriptUrl + '#' + masterScriptLine + "(eval)";
    }
    return masterScriptUrl + '#' + masterScriptLine + "(Function)";
  }



  
  static boolean isGeneratedScript(String sourceUrl) {
    return (sourceUrl.indexOf("(eval)") >= 0 || sourceUrl.indexOf("(Function)") >= 0);
  }


  
  private static RuntimeException errorWithClassName(String msg, Object val) {
    return Context.reportRuntimeError1(msg, val.getClass().getName());
  }








  
  public static JavaScriptException throwError(Context cx, Scriptable scope, String message) {
    int[] linep = { 0 };
    String filename = Context.getSourcePositionFromStack(linep);
    Scriptable error = newBuiltinObject(cx, scope, TopLevel.Builtins.Error, new Object[] { message, filename, Integer.valueOf(linep[0]) });
    
    return new JavaScriptException(error, filename, linep[0]);
  }
  
  public static final Object[] emptyArgs = new Object[0];
  public static final String[] emptyStrings = new String[0];
}
