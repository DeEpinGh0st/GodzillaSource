package org.mozilla.javascript;













final class NativeMath
  extends IdScriptableObject
{
  static final long serialVersionUID = -8838847185801131569L;
  private static final Object MATH_TAG = "Math"; private static final int Id_toSource = 1; private static final int Id_abs = 2; private static final int Id_acos = 3; private static final int Id_asin = 4; private static final int Id_atan = 5; private static final int Id_atan2 = 6; private static final int Id_ceil = 7; private static final int Id_cos = 8; private static final int Id_exp = 9; private static final int Id_floor = 10; private static final int Id_log = 11; private static final int Id_max = 12; private static final int Id_min = 13; private static final int Id_pow = 14; private static final int Id_random = 15; private static final int Id_round = 16; private static final int Id_sin = 17; private static final int Id_sqrt = 18; private static final int Id_tan = 19; private static final int LAST_METHOD_ID = 19; private static final int Id_E = 20; private static final int Id_PI = 21; private static final int Id_LN10 = 22; private static final int Id_LN2 = 23; private static final int Id_LOG2E = 24; private static final int Id_LOG10E = 25; private static final int Id_SQRT1_2 = 26; private static final int Id_SQRT2 = 27;
  private static final int MAX_ID = 27;
  
  static void init(Scriptable scope, boolean sealed) {
    NativeMath obj = new NativeMath();
    obj.activatePrototypeMap(27);
    obj.setPrototype(getObjectPrototype(scope));
    obj.setParentScope(scope);
    if (sealed) obj.sealObject(); 
    ScriptableObject.defineProperty(scope, "Math", obj, 2);
  }





  
  public String getClassName() {
    return "Math";
  }

  
  protected void initPrototypeId(int id) {
    if (id <= 19) {
      String name;
      int arity;
      switch (id) { case 1:
          arity = 0; name = "toSource"; break;
        case 2: arity = 1; name = "abs"; break;
        case 3: arity = 1; name = "acos"; break;
        case 4: arity = 1; name = "asin"; break;
        case 5: arity = 1; name = "atan"; break;
        case 6: arity = 2; name = "atan2"; break;
        case 7: arity = 1; name = "ceil"; break;
        case 8: arity = 1; name = "cos"; break;
        case 9: arity = 1; name = "exp"; break;
        case 10: arity = 1; name = "floor"; break;
        case 11: arity = 1; name = "log"; break;
        case 12: arity = 2; name = "max"; break;
        case 13: arity = 2; name = "min"; break;
        case 14: arity = 2; name = "pow"; break;
        case 15: arity = 0; name = "random"; break;
        case 16: arity = 1; name = "round"; break;
        case 17: arity = 1; name = "sin"; break;
        case 18: arity = 1; name = "sqrt"; break;
        case 19: arity = 1; name = "tan"; break;
        default: throw new IllegalStateException(String.valueOf(id)); }
      
      initPrototypeMethod(MATH_TAG, id, name, arity);
    } else {
      String name;
      double x;
      switch (id) { case 20:
          x = Math.E; name = "E"; break;
        case 21: x = Math.PI; name = "PI"; break;
        case 22: x = 2.302585092994046D; name = "LN10"; break;
        case 23: x = 0.6931471805599453D; name = "LN2"; break;
        case 24: x = 1.4426950408889634D; name = "LOG2E"; break;
        case 25: x = 0.4342944819032518D; name = "LOG10E"; break;
        case 26: x = 0.7071067811865476D; name = "SQRT1_2"; break;
        case 27: x = 1.4142135623730951D; name = "SQRT2"; break;
        default: throw new IllegalStateException(String.valueOf(id)); }
      
      initPrototypeValue(id, name, ScriptRuntime.wrapNumber(x), 7);
    } 
  }


  
  public Object execIdCall(IdFunctionObject f, Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
    double x;
    int i;
    if (!f.hasTag(MATH_TAG)) {
      return super.execIdCall(f, cx, scope, thisObj, args);
    }
    
    int methodId = f.methodId();
    switch (methodId) {
      case 1:
        return "Math";
      
      case 2:
        x = ScriptRuntime.toNumber(args, 0);
        
        x = (x == 0.0D) ? 0.0D : ((x < 0.0D) ? -x : x);






















































































































        
        return ScriptRuntime.wrapNumber(x);case 3: case 4: x = ScriptRuntime.toNumber(args, 0); if (x == x && -1.0D <= x && x <= 1.0D) { x = (methodId == 3) ? Math.acos(x) : Math.asin(x); } else { x = Double.NaN; }  return ScriptRuntime.wrapNumber(x);case 5: x = ScriptRuntime.toNumber(args, 0); x = Math.atan(x); return ScriptRuntime.wrapNumber(x);case 6: x = ScriptRuntime.toNumber(args, 0); x = Math.atan2(x, ScriptRuntime.toNumber(args, 1)); return ScriptRuntime.wrapNumber(x);case 7: x = ScriptRuntime.toNumber(args, 0); x = Math.ceil(x); return ScriptRuntime.wrapNumber(x);case 8: x = ScriptRuntime.toNumber(args, 0); x = (x == Double.POSITIVE_INFINITY || x == Double.NEGATIVE_INFINITY) ? Double.NaN : Math.cos(x); return ScriptRuntime.wrapNumber(x);case 9: x = ScriptRuntime.toNumber(args, 0); x = (x == Double.POSITIVE_INFINITY) ? x : ((x == Double.NEGATIVE_INFINITY) ? 0.0D : Math.exp(x)); return ScriptRuntime.wrapNumber(x);case 10: x = ScriptRuntime.toNumber(args, 0); x = Math.floor(x); return ScriptRuntime.wrapNumber(x);case 11: x = ScriptRuntime.toNumber(args, 0); x = (x < 0.0D) ? Double.NaN : Math.log(x); return ScriptRuntime.wrapNumber(x);case 12: case 13: x = (methodId == 12) ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY; for (i = 0; i != args.length; i++) { double d = ScriptRuntime.toNumber(args[i]); if (d != d) { x = d; break; }  if (methodId == 12) { x = Math.max(x, d); } else { x = Math.min(x, d); }  }  return ScriptRuntime.wrapNumber(x);case 14: x = ScriptRuntime.toNumber(args, 0); x = js_pow(x, ScriptRuntime.toNumber(args, 1)); return ScriptRuntime.wrapNumber(x);case 15: x = Math.random(); return ScriptRuntime.wrapNumber(x);case 16: x = ScriptRuntime.toNumber(args, 0); if (x == x && x != Double.POSITIVE_INFINITY && x != Double.NEGATIVE_INFINITY) { long l = Math.round(x); if (l != 0L) { x = l; } else if (x < 0.0D) { x = ScriptRuntime.negativeZero; } else if (x != 0.0D) { x = 0.0D; }  }  return ScriptRuntime.wrapNumber(x);case 17: x = ScriptRuntime.toNumber(args, 0); x = (x == Double.POSITIVE_INFINITY || x == Double.NEGATIVE_INFINITY) ? Double.NaN : Math.sin(x); return ScriptRuntime.wrapNumber(x);case 18: x = ScriptRuntime.toNumber(args, 0); x = Math.sqrt(x); return ScriptRuntime.wrapNumber(x);case 19: x = ScriptRuntime.toNumber(args, 0); x = Math.tan(x); return ScriptRuntime.wrapNumber(x);
    } 
    throw new IllegalStateException(String.valueOf(methodId));
  }
  private double js_pow(double x, double y) {
    double result;
    if (y != y) {
      
      result = y;
    } else if (y == 0.0D) {
      
      result = 1.0D;
    } else if (x == 0.0D) {
      
      if (1.0D / x > 0.0D) {
        result = (y > 0.0D) ? 0.0D : Double.POSITIVE_INFINITY;
      } else {
        
        long y_long = (long)y;
        if (y_long == y && (y_long & 0x1L) != 0L) {
          result = (y > 0.0D) ? -0.0D : Double.NEGATIVE_INFINITY;
        } else {
          result = (y > 0.0D) ? 0.0D : Double.POSITIVE_INFINITY;
        } 
      } 
    } else {
      result = Math.pow(x, y);
      if (result != result)
      {
        
        if (y == Double.POSITIVE_INFINITY) {
          if (x < -1.0D || 1.0D < x) {
            result = Double.POSITIVE_INFINITY;
          } else if (-1.0D < x && x < 1.0D) {
            result = 0.0D;
          } 
        } else if (y == Double.NEGATIVE_INFINITY) {
          if (x < -1.0D || 1.0D < x) {
            result = 0.0D;
          } else if (-1.0D < x && x < 1.0D) {
            result = Double.POSITIVE_INFINITY;
          } 
        } else if (x == Double.POSITIVE_INFINITY) {
          result = (y > 0.0D) ? Double.POSITIVE_INFINITY : 0.0D;
        } else if (x == Double.NEGATIVE_INFINITY) {
          long y_long = (long)y;
          if (y_long == y && (y_long & 0x1L) != 0L) {
            
            result = (y > 0.0D) ? Double.NEGATIVE_INFINITY : -0.0D;
          } else {
            result = (y > 0.0D) ? Double.POSITIVE_INFINITY : 0.0D;
          } 
        } 
      }
    } 
    return result;
  }






  
  protected int findPrototypeId(String s) {
    int c, id = 0; String X = null;
    switch (s.length()) { case 1:
        if (s.charAt(0) == 'E') { id = 20;







































          
          return id; }  break;case 2: if (s.charAt(0) == 'P' && s.charAt(1) == 'I') { id = 21; return id; }  break;case 3: switch (s.charAt(0)) { case 'L': if (s.charAt(2) == '2' && s.charAt(1) == 'N') { id = 23; return id; }  break;case 'a': if (s.charAt(2) == 's' && s.charAt(1) == 'b') { id = 2; return id; }  break;case 'c': if (s.charAt(2) == 's' && s.charAt(1) == 'o') { id = 8; return id; }  break;case 'e': if (s.charAt(2) == 'p' && s.charAt(1) == 'x') { id = 9; return id; }  break;case 'l': if (s.charAt(2) == 'g' && s.charAt(1) == 'o') { id = 11; return id; }  break;case 'm': c = s.charAt(2); if (c == 110) { if (s.charAt(1) == 'i') { id = 13; return id; }  break; }  if (c == 120 && s.charAt(1) == 'a') { id = 12; return id; }  break;case 'p': if (s.charAt(2) == 'w' && s.charAt(1) == 'o') { id = 14; return id; }  break;case 's': if (s.charAt(2) == 'n' && s.charAt(1) == 'i') { id = 17; return id; }  break;case 't': if (s.charAt(2) == 'n' && s.charAt(1) == 'a') { id = 19; return id; }  break; }  break;case 4: switch (s.charAt(1)) { case 'N': X = "LN10"; id = 22; break;case 'c': X = "acos"; id = 3; break;case 'e': X = "ceil"; id = 7; break;case 'q': X = "sqrt"; id = 18; break;case 's': X = "asin"; id = 4; break;case 't': X = "atan"; id = 5; break; }  break;case 5: switch (s.charAt(0)) { case 'L': X = "LOG2E"; id = 24; break;case 'S': X = "SQRT2"; id = 27; break;case 'a': X = "atan2"; id = 6; break;case 'f': X = "floor"; id = 10; break;case 'r': X = "round"; id = 16; break; }  break;case 6: c = s.charAt(0); if (c == 76) { X = "LOG10E"; id = 25; break; }  if (c == 114) { X = "random"; id = 15; }  break;case 7: X = "SQRT1_2"; id = 26; break;case 8: X = "toSource"; id = 1; break; }  if (X != null && X != s && !X.equals(s)) id = 0;  return id;
  }
}
