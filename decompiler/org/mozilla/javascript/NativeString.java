package org.mozilla.javascript;

import java.text.Collator;



















final class NativeString
  extends IdScriptableObject
{
  static final long serialVersionUID = 920268368584188687L;
  private static final Object STRING_TAG = "String"; private static final int Id_length = 1; private static final int MAX_INSTANCE_ID = 1; private static final int ConstructorId_fromCharCode = -1; private static final int Id_constructor = 1; private static final int Id_toString = 2; private static final int Id_toSource = 3; private static final int Id_valueOf = 4; private static final int Id_charAt = 5; private static final int Id_charCodeAt = 6; private static final int Id_indexOf = 7; private static final int Id_lastIndexOf = 8; private static final int Id_split = 9; private static final int Id_substring = 10; private static final int Id_toLowerCase = 11; private static final int Id_toUpperCase = 12; private static final int Id_substr = 13; private static final int Id_concat = 14; private static final int Id_slice = 15; private static final int Id_bold = 16; private static final int Id_italics = 17; private static final int Id_fixed = 18; private static final int Id_strike = 19; private static final int Id_small = 20; private static final int Id_big = 21; private static final int Id_blink = 22; private static final int Id_sup = 23; private static final int Id_sub = 24; private static final int Id_fontsize = 25; private static final int Id_fontcolor = 26; private static final int Id_link = 27; private static final int Id_anchor = 28; private static final int Id_equals = 29; private static final int Id_equalsIgnoreCase = 30; private static final int Id_match = 31; private static final int Id_search = 32; private static final int Id_replace = 33; private static final int Id_localeCompare = 34; private static final int Id_toLocaleLowerCase = 35; private static final int Id_toLocaleUpperCase = 36; private static final int Id_trim = 37; private static final int Id_trimLeft = 38; private static final int Id_trimRight = 39; private static final int MAX_PROTOTYPE_ID = 39; private static final int ConstructorId_charAt = -5; private static final int ConstructorId_charCodeAt = -6; private static final int ConstructorId_indexOf = -7; private static final int ConstructorId_lastIndexOf = -8; private static final int ConstructorId_split = -9; private static final int ConstructorId_substring = -10; private static final int ConstructorId_toLowerCase = -11; private static final int ConstructorId_toUpperCase = -12; private static final int ConstructorId_substr = -13; private static final int ConstructorId_concat = -14; private static final int ConstructorId_slice = -15; private static final int ConstructorId_equalsIgnoreCase = -30; private static final int ConstructorId_match = -31; private static final int ConstructorId_search = -32; private static final int ConstructorId_replace = -33; private static final int ConstructorId_localeCompare = -34; private static final int ConstructorId_toLocaleLowerCase = -35;
  private CharSequence string;
  
  static void init(Scriptable scope, boolean sealed) {
    NativeString obj = new NativeString("");
    obj.exportAsJSClass(39, scope, sealed);
  }
  
  NativeString(CharSequence s) {
    this.string = s;
  }

  
  public String getClassName() {
    return "String";
  }






  
  protected int getMaxInstanceId() {
    return 1;
  }


  
  protected int findInstanceIdInfo(String s) {
    if (s.equals("length")) {
      return instanceIdInfo(7, 1);
    }
    return super.findInstanceIdInfo(s);
  }


  
  protected String getInstanceIdName(int id) {
    if (id == 1) return "length"; 
    return super.getInstanceIdName(id);
  }


  
  protected Object getInstanceIdValue(int id) {
    if (id == 1) {
      return ScriptRuntime.wrapInt(this.string.length());
    }
    return super.getInstanceIdValue(id);
  }


  
  protected void fillConstructorProperties(IdFunctionObject ctor) {
    addIdFunctionProperty(ctor, STRING_TAG, -1, "fromCharCode", 1);
    
    addIdFunctionProperty(ctor, STRING_TAG, -5, "charAt", 2);
    
    addIdFunctionProperty(ctor, STRING_TAG, -6, "charCodeAt", 2);
    
    addIdFunctionProperty(ctor, STRING_TAG, -7, "indexOf", 2);
    
    addIdFunctionProperty(ctor, STRING_TAG, -8, "lastIndexOf", 2);
    
    addIdFunctionProperty(ctor, STRING_TAG, -9, "split", 3);
    
    addIdFunctionProperty(ctor, STRING_TAG, -10, "substring", 3);
    
    addIdFunctionProperty(ctor, STRING_TAG, -11, "toLowerCase", 1);
    
    addIdFunctionProperty(ctor, STRING_TAG, -12, "toUpperCase", 1);
    
    addIdFunctionProperty(ctor, STRING_TAG, -13, "substr", 3);
    
    addIdFunctionProperty(ctor, STRING_TAG, -14, "concat", 2);
    
    addIdFunctionProperty(ctor, STRING_TAG, -15, "slice", 3);
    
    addIdFunctionProperty(ctor, STRING_TAG, -30, "equalsIgnoreCase", 2);
    
    addIdFunctionProperty(ctor, STRING_TAG, -31, "match", 2);
    
    addIdFunctionProperty(ctor, STRING_TAG, -32, "search", 2);
    
    addIdFunctionProperty(ctor, STRING_TAG, -33, "replace", 2);
    
    addIdFunctionProperty(ctor, STRING_TAG, -34, "localeCompare", 2);
    
    addIdFunctionProperty(ctor, STRING_TAG, -35, "toLocaleLowerCase", 1);
    
    super.fillConstructorProperties(ctor);
  }


  
  protected void initPrototypeId(int id) {
    String s;
    int arity;
    switch (id) { case 1:
        arity = 1; s = "constructor"; break;
      case 2: arity = 0; s = "toString"; break;
      case 3: arity = 0; s = "toSource"; break;
      case 4: arity = 0; s = "valueOf"; break;
      case 5: arity = 1; s = "charAt"; break;
      case 6: arity = 1; s = "charCodeAt"; break;
      case 7: arity = 1; s = "indexOf"; break;
      case 8: arity = 1; s = "lastIndexOf"; break;
      case 9: arity = 2; s = "split"; break;
      case 10: arity = 2; s = "substring"; break;
      case 11: arity = 0; s = "toLowerCase"; break;
      case 12: arity = 0; s = "toUpperCase"; break;
      case 13: arity = 2; s = "substr"; break;
      case 14: arity = 1; s = "concat"; break;
      case 15: arity = 2; s = "slice"; break;
      case 16: arity = 0; s = "bold"; break;
      case 17: arity = 0; s = "italics"; break;
      case 18: arity = 0; s = "fixed"; break;
      case 19: arity = 0; s = "strike"; break;
      case 20: arity = 0; s = "small"; break;
      case 21: arity = 0; s = "big"; break;
      case 22: arity = 0; s = "blink"; break;
      case 23: arity = 0; s = "sup"; break;
      case 24: arity = 0; s = "sub"; break;
      case 25: arity = 0; s = "fontsize"; break;
      case 26: arity = 0; s = "fontcolor"; break;
      case 27: arity = 0; s = "link"; break;
      case 28: arity = 0; s = "anchor"; break;
      case 29: arity = 1; s = "equals"; break;
      case 30: arity = 1; s = "equalsIgnoreCase"; break;
      case 31: arity = 1; s = "match"; break;
      case 32: arity = 1; s = "search"; break;
      case 33: arity = 2; s = "replace"; break;
      case 34: arity = 1; s = "localeCompare"; break;
      case 35: arity = 0; s = "toLocaleLowerCase"; break;
      case 36: arity = 0; s = "toLocaleUpperCase"; break;
      case 37: arity = 0; s = "trim"; break;
      case 38: arity = 0; s = "trimLeft"; break;
      case 39: arity = 0; s = "trimRight"; break;
      default: throw new IllegalArgumentException(String.valueOf(id)); }
    
    initPrototypeMethod(STRING_TAG, id, s, arity);
  }



  
  public Object execIdCall(IdFunctionObject f, Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
    if (!f.hasTag(STRING_TAG)) {
      return super.execIdCall(f, cx, scope, thisObj, args);
    }
    int id = f.methodId(); while (true) {
      int N; CharSequence s; CharSequence cs; StringBuilder sb; CharSequence charSequence1; CharSequence target; String s1; int actionType; Collator collator; String str; int i; double pos; String s2; char[] chars; int start; char c;
      int end;
      switch (id) {
        case -35:
        case -34:
        case -33:
        case -32:
        case -31:
        case -30:
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
            thisObj = ScriptRuntime.toObject(cx, scope, ScriptRuntime.toCharSequence(args[0]));
            
            Object[] newArgs = new Object[args.length - 1];
            for (int j = 0; j < newArgs.length; j++)
              newArgs[j] = args[j + 1]; 
            args = newArgs;
          } else {
            thisObj = ScriptRuntime.toObject(cx, scope, ScriptRuntime.toCharSequence(thisObj));
          } 
          
          id = -id;
          continue;

        
        case -1:
          N = args.length;
          if (N < 1)
            return ""; 
          sb = new StringBuilder(N);
          for (i = 0; i != N; i++) {
            sb.append(ScriptRuntime.toUint16(args[i]));
          }
          return sb.toString();

        
        case 1:
          s = (args.length >= 1) ? ScriptRuntime.toCharSequence(args[0]) : "";
          
          if (thisObj == null)
          {
            return new NativeString(s);
          }
          
          return (s instanceof String) ? s : s.toString();


        
        case 2:
        case 4:
          cs = (realThis(thisObj, f)).string;
          return (cs instanceof String) ? cs : cs.toString();
        
        case 3:
          charSequence1 = (realThis(thisObj, f)).string;
          return "(new String(\"" + ScriptRuntime.escapeString(charSequence1.toString()) + "\"))";


        
        case 5:
        case 6:
          target = ScriptRuntime.toCharSequence(thisObj);
          pos = ScriptRuntime.toInteger(args, 0);
          if (pos < 0.0D || pos >= target.length()) {
            if (id == 5) return ""; 
            return ScriptRuntime.NaNobj;
          } 
          c = target.charAt((int)pos);
          if (id == 5) return String.valueOf(c); 
          return ScriptRuntime.wrapInt(c);

        
        case 7:
          return ScriptRuntime.wrapInt(js_indexOf(ScriptRuntime.toString(thisObj), args));

        
        case 8:
          return ScriptRuntime.wrapInt(js_lastIndexOf(ScriptRuntime.toString(thisObj), args));

        
        case 9:
          return ScriptRuntime.checkRegExpProxy(cx).js_split(cx, scope, ScriptRuntime.toString(thisObj), args);


        
        case 10:
          return js_substring(cx, ScriptRuntime.toCharSequence(thisObj), args);

        
        case 11:
          return ScriptRuntime.toString(thisObj).toLowerCase(ScriptRuntime.ROOT_LOCALE);


        
        case 12:
          return ScriptRuntime.toString(thisObj).toUpperCase(ScriptRuntime.ROOT_LOCALE);

        
        case 13:
          return js_substr(ScriptRuntime.toCharSequence(thisObj), args);
        
        case 14:
          return js_concat(ScriptRuntime.toString(thisObj), args);
        
        case 15:
          return js_slice(ScriptRuntime.toCharSequence(thisObj), args);
        
        case 16:
          return tagify(thisObj, "b", (String)null, (Object[])null);
        
        case 17:
          return tagify(thisObj, "i", (String)null, (Object[])null);
        
        case 18:
          return tagify(thisObj, "tt", (String)null, (Object[])null);
        
        case 19:
          return tagify(thisObj, "strike", (String)null, (Object[])null);
        
        case 20:
          return tagify(thisObj, "small", (String)null, (Object[])null);
        
        case 21:
          return tagify(thisObj, "big", (String)null, (Object[])null);
        
        case 22:
          return tagify(thisObj, "blink", (String)null, (Object[])null);
        
        case 23:
          return tagify(thisObj, "sup", (String)null, (Object[])null);
        
        case 24:
          return tagify(thisObj, "sub", (String)null, (Object[])null);
        
        case 25:
          return tagify(thisObj, "font", "size", args);
        
        case 26:
          return tagify(thisObj, "font", "color", args);
        
        case 27:
          return tagify(thisObj, "a", "href", args);
        
        case 28:
          return tagify(thisObj, "a", "name", args);
        
        case 29:
        case 30:
          s1 = ScriptRuntime.toString(thisObj);
          s2 = ScriptRuntime.toString(args, 0);
          return ScriptRuntime.wrapBoolean((id == 29) ? s1.equals(s2) : s1.equalsIgnoreCase(s2));





        
        case 31:
        case 32:
        case 33:
          if (id == 31) {
            actionType = 1;
          } else if (id == 32) {
            actionType = 3;
          } else {
            actionType = 2;
          } 
          return ScriptRuntime.checkRegExpProxy(cx).action(cx, scope, thisObj, args, actionType);







        
        case 34:
          collator = Collator.getInstance(cx.getLocale());
          collator.setStrength(3);
          collator.setDecomposition(1);
          return ScriptRuntime.wrapNumber(collator.compare(ScriptRuntime.toString(thisObj), ScriptRuntime.toString(args, 0)));



        
        case 35:
          return ScriptRuntime.toString(thisObj).toLowerCase(cx.getLocale());


        
        case 36:
          return ScriptRuntime.toString(thisObj).toUpperCase(cx.getLocale());


        
        case 37:
          str = ScriptRuntime.toString(thisObj);
          chars = str.toCharArray();
          
          start = 0;
          while (start < chars.length && ScriptRuntime.isJSWhitespaceOrLineTerminator(chars[start])) {
            start++;
          }
          end = chars.length;
          while (end > start && ScriptRuntime.isJSWhitespaceOrLineTerminator(chars[end - 1])) {
            end--;
          }
          
          return str.substring(start, end);

        
        case 38:
          str = ScriptRuntime.toString(thisObj);
          chars = str.toCharArray();
          
          start = 0;
          while (start < chars.length && ScriptRuntime.isJSWhitespaceOrLineTerminator(chars[start])) {
            start++;
          }
          end = chars.length;
          
          return str.substring(start, end);

        
        case 39:
          str = ScriptRuntime.toString(thisObj);
          chars = str.toCharArray();
          
          start = 0;
          
          end = chars.length;
          while (end > start && ScriptRuntime.isJSWhitespaceOrLineTerminator(chars[end - 1])) {
            end--;
          }
          
          return str.substring(start, end);
      }  break;
    } 
    throw new IllegalArgumentException(String.valueOf(id));
  }


  
  private static NativeString realThis(Scriptable thisObj, IdFunctionObject f) {
    if (!(thisObj instanceof NativeString))
      throw incompatibleCallError(f); 
    return (NativeString)thisObj;
  }





  
  private static String tagify(Object thisObj, String tag, String attribute, Object[] args) {
    String str = ScriptRuntime.toString(thisObj);
    StringBuilder result = new StringBuilder();
    result.append('<');
    result.append(tag);
    if (attribute != null) {
      result.append(' ');
      result.append(attribute);
      result.append("=\"");
      result.append(ScriptRuntime.toString(args, 0));
      result.append('"');
    } 
    result.append('>');
    result.append(str);
    result.append("</");
    result.append(tag);
    result.append('>');
    return result.toString();
  }
  
  public CharSequence toCharSequence() {
    return this.string;
  }

  
  public String toString() {
    return (this.string instanceof String) ? (String)this.string : this.string.toString();
  }




  
  public Object get(int index, Scriptable start) {
    if (0 <= index && index < this.string.length()) {
      return String.valueOf(this.string.charAt(index));
    }
    return super.get(index, start);
  }

  
  public void put(int index, Scriptable start, Object value) {
    if (0 <= index && index < this.string.length()) {
      return;
    }
    super.put(index, start, value);
  }





  
  private static int js_indexOf(String target, Object[] args) {
    String search = ScriptRuntime.toString(args, 0);
    double begin = ScriptRuntime.toInteger(args, 1);
    
    if (begin > target.length()) {
      return -1;
    }
    if (begin < 0.0D)
      begin = 0.0D; 
    return target.indexOf(search, (int)begin);
  }






  
  private static int js_lastIndexOf(String target, Object[] args) {
    String search = ScriptRuntime.toString(args, 0);
    double end = ScriptRuntime.toNumber(args, 1);
    
    if (end != end || end > target.length()) {
      end = target.length();
    } else if (end < 0.0D) {
      end = 0.0D;
    } 
    return target.lastIndexOf(search, (int)end);
  }





  
  private static CharSequence js_substring(Context cx, CharSequence target, Object[] args) {
    double end;
    int length = target.length();
    double start = ScriptRuntime.toInteger(args, 0);

    
    if (start < 0.0D) {
      start = 0.0D;
    } else if (start > length) {
      start = length;
    } 
    if (args.length <= 1 || args[1] == Undefined.instance) {
      end = length;
    } else {
      end = ScriptRuntime.toInteger(args[1]);
      if (end < 0.0D) {
        end = 0.0D;
      } else if (end > length) {
        end = length;
      } 
      
      if (end < start) {
        if (cx.getLanguageVersion() != 120) {
          double temp = start;
          start = end;
          end = temp;
        } else {
          
          end = start;
        } 
      }
    } 
    return target.subSequence((int)start, (int)end);
  }
  
  int getLength() {
    return this.string.length();
  }


  
  private static CharSequence js_substr(CharSequence target, Object[] args) {
    double end;
    if (args.length < 1) {
      return target;
    }
    double begin = ScriptRuntime.toInteger(args[0]);
    
    int length = target.length();
    
    if (begin < 0.0D) {
      begin += length;
      if (begin < 0.0D)
        begin = 0.0D; 
    } else if (begin > length) {
      begin = length;
    } 
    
    if (args.length == 1) {
      end = length;
    } else {
      end = ScriptRuntime.toInteger(args[1]);
      if (end < 0.0D)
        end = 0.0D; 
      end += begin;
      if (end > length) {
        end = length;
      }
    } 
    return target.subSequence((int)begin, (int)end);
  }



  
  private static String js_concat(String target, Object[] args) {
    int N = args.length;
    if (N == 0) return target; 
    if (N == 1) {
      String arg = ScriptRuntime.toString(args[0]);
      return target.concat(arg);
    } 


    
    int size = target.length();
    String[] argsAsStrings = new String[N];
    for (int i = 0; i != N; i++) {
      String s = ScriptRuntime.toString(args[i]);
      argsAsStrings[i] = s;
      size += s.length();
    } 
    
    StringBuilder result = new StringBuilder(size);
    result.append(target);
    for (int j = 0; j != N; j++) {
      result.append(argsAsStrings[j]);
    }
    return result.toString();
  }
  
  private static CharSequence js_slice(CharSequence target, Object[] args) {
    double end, begin = (args.length < 1) ? 0.0D : ScriptRuntime.toInteger(args[0]);
    
    int length = target.length();
    if (begin < 0.0D) {
      begin += length;
      if (begin < 0.0D)
        begin = 0.0D; 
    } else if (begin > length) {
      begin = length;
    } 
    
    if (args.length < 2 || args[1] == Undefined.instance) {
      end = length;
    } else {
      end = ScriptRuntime.toInteger(args[1]);
      if (end < 0.0D) {
        end += length;
        if (end < 0.0D)
          end = 0.0D; 
      } else if (end > length) {
        end = length;
      } 
      if (end < begin)
        end = begin; 
    } 
    return target.subSequence((int)begin, (int)end);
  }






  
  protected int findPrototypeId(String s) {
    int c, id = 0; String X = null;
    switch (s.length()) { case 3:
        c = s.charAt(2);
        if (c == 98) { if (s.charAt(0) == 's' && s.charAt(1) == 'u') { id = 24;



























































            
            return id; }  break; }  if (c == 103) { if (s.charAt(0) == 'b' && s.charAt(1) == 'i') { id = 21; return id; }  break; }  if (c == 112 && s.charAt(0) == 's' && s.charAt(1) == 'u') { id = 23; return id; }  break;case 4: c = s.charAt(0); if (c == 98) { X = "bold"; id = 16; break; }  if (c == 108) { X = "link"; id = 27; break; }  if (c == 116) { X = "trim"; id = 37; }  break;case 5: switch (s.charAt(4)) { case 'd': X = "fixed"; id = 18; break;case 'e': X = "slice"; id = 15; break;case 'h': X = "match"; id = 31; break;case 'k': X = "blink"; id = 22; break;case 'l': X = "small"; id = 20; break;case 't': X = "split"; id = 9; break; }  break;case 6: switch (s.charAt(1)) { case 'e': X = "search"; id = 32; break;case 'h': X = "charAt"; id = 5; break;case 'n': X = "anchor"; id = 28; break;case 'o': X = "concat"; id = 14; break;case 'q': X = "equals"; id = 29; break;case 't': X = "strike"; id = 19; break;case 'u': X = "substr"; id = 13; break; }  break;case 7: switch (s.charAt(1)) { case 'a': X = "valueOf"; id = 4; break;case 'e': X = "replace"; id = 33; break;case 'n': X = "indexOf"; id = 7; break;case 't': X = "italics"; id = 17; break; }  break;case 8: switch (s.charAt(4)) { case 'L': X = "trimLeft"; id = 38; break;case 'r': X = "toString"; id = 2; break;case 's': X = "fontsize"; id = 25; break;case 'u': X = "toSource"; id = 3; break; }  break;case 9: c = s.charAt(0); if (c == 102) { X = "fontcolor"; id = 26; break; }  if (c == 115) { X = "substring"; id = 10; break; }  if (c == 116) { X = "trimRight"; id = 39; }  break;case 10: X = "charCodeAt"; id = 6; break;case 11: switch (s.charAt(2)) { case 'L': X = "toLowerCase"; id = 11; break;case 'U': X = "toUpperCase"; id = 12; break;case 'n': X = "constructor"; id = 1; break;case 's': X = "lastIndexOf"; id = 8; break; }  break;case 13: X = "localeCompare"; id = 34; break;case 16: X = "equalsIgnoreCase"; id = 30; break;case 17: c = s.charAt(8); if (c == 76) { X = "toLocaleLowerCase"; id = 35; break; }  if (c == 85) { X = "toLocaleUpperCase"; id = 36; }  break; }  if (X != null && X != s && !X.equals(s)) id = 0;  return id;
  }
}
