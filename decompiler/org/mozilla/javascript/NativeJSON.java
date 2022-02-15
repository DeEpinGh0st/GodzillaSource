package org.mozilla.javascript;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import org.mozilla.javascript.json.JsonParser;













public final class NativeJSON
  extends IdScriptableObject
{
  static final long serialVersionUID = -4567599697595654984L;
  private static final Object JSON_TAG = "JSON";
  private static final int MAX_STRINGIFY_GAP_LENGTH = 10;
  private static final int Id_toSource = 1;
  private static final int Id_parse = 2;
  
  static void init(Scriptable scope, boolean sealed) {
    NativeJSON obj = new NativeJSON();
    obj.activatePrototypeMap(3);
    obj.setPrototype(getObjectPrototype(scope));
    obj.setParentScope(scope);
    if (sealed) obj.sealObject(); 
    ScriptableObject.defineProperty(scope, "JSON", obj, 2);
  }

  
  private static final int Id_stringify = 3;
  private static final int LAST_METHOD_ID = 3;
  private static final int MAX_ID = 3;
  
  public String getClassName() {
    return "JSON";
  }

  
  protected void initPrototypeId(int id) {
    if (id <= 3) {
      String name;
      int arity;
      switch (id) { case 1:
          arity = 0; name = "toSource"; break;
        case 2: arity = 2; name = "parse"; break;
        case 3: arity = 3; name = "stringify"; break;
        default: throw new IllegalStateException(String.valueOf(id)); }
      
      initPrototypeMethod(JSON_TAG, id, name, arity);
    } else {
      throw new IllegalStateException(String.valueOf(id));
    } 
  }

  
  public Object execIdCall(IdFunctionObject f, Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
    String jtext;
    Object value, reviver, replacer, space;
    if (!f.hasTag(JSON_TAG)) {
      return super.execIdCall(f, cx, scope, thisObj, args);
    }
    int methodId = f.methodId();
    switch (methodId) {
      case 1:
        return "JSON";
      
      case 2:
        jtext = ScriptRuntime.toString(args, 0);
        reviver = null;
        if (args.length > 1) {
          reviver = args[1];
        }
        if (reviver instanceof Callable) {
          return parse(cx, scope, jtext, (Callable)reviver);
        }
        return parse(cx, scope, jtext);


      
      case 3:
        value = null; replacer = null; space = null;
        switch (args.length) {
          default:
            space = args[2];
          case 2: replacer = args[1];
          case 1: value = args[0]; break;
          case 0:
            break;
        }  return stringify(cx, scope, value, replacer, space);
    } 
    
    throw new IllegalStateException(String.valueOf(methodId));
  }

  
  private static Object parse(Context cx, Scriptable scope, String jtext) {
    try {
      return (new JsonParser(cx, scope)).parseValue(jtext);
    } catch (org.mozilla.javascript.json.JsonParser.ParseException ex) {
      throw ScriptRuntime.constructError("SyntaxError", ex.getMessage());
    } 
  }


  
  public static Object parse(Context cx, Scriptable scope, String jtext, Callable reviver) {
    Object unfiltered = parse(cx, scope, jtext);
    Scriptable root = cx.newObject(scope);
    root.put("", root, unfiltered);
    return walk(cx, scope, reviver, root, "");
  }


  
  private static Object walk(Context cx, Scriptable scope, Callable reviver, Scriptable holder, Object name) {
    Object property;
    if (name instanceof Number) {
      property = holder.get(((Number)name).intValue(), holder);
    } else {
      property = holder.get((String)name, holder);
    } 
    
    if (property instanceof Scriptable) {
      Scriptable val = (Scriptable)property;
      if (val instanceof NativeArray) {
        long len = ((NativeArray)val).getLength(); long i;
        for (i = 0L; i < len; i++) {
          
          if (i > 2147483647L) {
            String id = Long.toString(i);
            Object newElement = walk(cx, scope, reviver, val, id);
            if (newElement == Undefined.instance) {
              val.delete(id);
            } else {
              val.put(id, val, newElement);
            } 
          } else {
            int idx = (int)i;
            Object newElement = walk(cx, scope, reviver, val, Integer.valueOf(idx));
            if (newElement == Undefined.instance) {
              val.delete(idx);
            } else {
              val.put(idx, val, newElement);
            } 
          } 
        } 
      } else {
        Object[] keys = val.getIds();
        for (Object p : keys) {
          Object newElement = walk(cx, scope, reviver, val, p);
          if (newElement == Undefined.instance) {
            if (p instanceof Number) {
              val.delete(((Number)p).intValue());
            } else {
              val.delete((String)p);
            } 
          } else if (p instanceof Number) {
            val.put(((Number)p).intValue(), val, newElement);
          } else {
            val.put((String)p, val, newElement);
          } 
        } 
      } 
    } 
    
    return reviver.call(cx, scope, holder, new Object[] { name, property });
  }
  
  private static String repeat(char c, int count) {
    char[] chars = new char[count];
    Arrays.fill(chars, c);
    return new String(chars);
  }

  
  private static class StringifyState
  {
    Stack<Scriptable> stack;
    
    String indent;
    
    String gap;
    Callable replacer;
    List<Object> propertyList;
    Object space;
    Context cx;
    Scriptable scope;
    
    StringifyState(Context cx, Scriptable scope, String indent, String gap, Callable replacer, List<Object> propertyList, Object space) {
      this.stack = new Stack<Scriptable>();
      this.cx = cx;
      this.scope = scope;
      this.indent = indent;
      this.gap = gap;
      this.replacer = replacer;
      this.propertyList = propertyList;
      this.space = space;
    }
  }


  
  public static Object stringify(Context cx, Scriptable scope, Object value, Object replacer, Object space) {
    String indent = "";
    String gap = "";
    
    List<Object> propertyList = null;
    Callable replacerFunction = null;
    
    if (replacer instanceof Callable) {
      replacerFunction = (Callable)replacer;
    } else if (replacer instanceof NativeArray) {
      propertyList = new LinkedList();
      NativeArray replacerArray = (NativeArray)replacer; Integer[] arr$; int len$, i$;
      for (arr$ = replacerArray.getIndexIds(), len$ = arr$.length, i$ = 0; i$ < len$; ) { int i = arr$[i$].intValue();
        Object v = replacerArray.get(i, replacerArray);
        if (v instanceof String || v instanceof Number) {
          propertyList.add(v);
        } else if (v instanceof NativeString || v instanceof NativeNumber) {
          propertyList.add(ScriptRuntime.toString(v));
        } 
        i$++; }
    
    } 
    if (space instanceof NativeNumber) {
      space = Double.valueOf(ScriptRuntime.toNumber(space));
    } else if (space instanceof NativeString) {
      space = ScriptRuntime.toString(space);
    } 
    
    if (space instanceof Number) {
      int gapLength = (int)ScriptRuntime.toInteger(space);
      gapLength = Math.min(10, gapLength);
      gap = (gapLength > 0) ? repeat(' ', gapLength) : "";
      space = Integer.valueOf(gapLength);
    } else if (space instanceof String) {
      gap = (String)space;
      if (gap.length() > 10) {
        gap = gap.substring(0, 10);
      }
    } 
    
    StringifyState state = new StringifyState(cx, scope, indent, gap, replacerFunction, propertyList, space);





    
    ScriptableObject wrapper = new NativeObject();
    wrapper.setParentScope(scope);
    wrapper.setPrototype(ScriptableObject.getObjectPrototype(scope));
    wrapper.defineProperty("", value, 0);
    return str("", wrapper, state);
  }


  
  private static Object str(Object key, Scriptable holder, StringifyState state) {
    Object value = null;
    if (key instanceof String) {
      value = getProperty(holder, (String)key);
    } else {
      value = getProperty(holder, ((Number)key).intValue());
    } 
    
    if (value instanceof Scriptable) {
      Object toJSON = getProperty((Scriptable)value, "toJSON");
      if (toJSON instanceof Callable) {
        value = callMethod(state.cx, (Scriptable)value, "toJSON", new Object[] { key });
      }
    } 

    
    if (state.replacer != null) {
      value = state.replacer.call(state.cx, state.scope, holder, new Object[] { key, value });
    }


    
    if (value instanceof NativeNumber) {
      value = Double.valueOf(ScriptRuntime.toNumber(value));
    } else if (value instanceof NativeString) {
      value = ScriptRuntime.toString(value);
    } else if (value instanceof NativeBoolean) {
      value = ((NativeBoolean)value).getDefaultValue(ScriptRuntime.BooleanClass);
    } 
    
    if (value == null) return "null"; 
    if (value.equals(Boolean.TRUE)) return "true"; 
    if (value.equals(Boolean.FALSE)) return "false";
    
    if (value instanceof CharSequence) {
      return quote(value.toString());
    }
    
    if (value instanceof Number) {
      double d = ((Number)value).doubleValue();
      if (d == d && d != Double.POSITIVE_INFINITY && d != Double.NEGATIVE_INFINITY)
      {
        
        return ScriptRuntime.toString(value);
      }
      return "null";
    } 

    
    if (value instanceof Scriptable && !(value instanceof Callable)) {
      if (value instanceof NativeArray) {
        return ja((NativeArray)value, state);
      }
      return jo((Scriptable)value, state);
    } 
    
    return Undefined.instance;
  }
  
  private static String join(Collection<Object> objs, String delimiter) {
    if (objs == null || objs.isEmpty()) {
      return "";
    }
    Iterator<Object> iter = objs.iterator();
    if (!iter.hasNext()) return ""; 
    StringBuilder builder = new StringBuilder(iter.next().toString());
    while (iter.hasNext()) {
      builder.append(delimiter).append(iter.next().toString());
    }
    return builder.toString();
  }
  private static String jo(Scriptable value, StringifyState state) {
    String finalValue;
    if (state.stack.search(value) != -1) {
      throw ScriptRuntime.typeError0("msg.cyclic.value");
    }
    state.stack.push(value);
    
    String stepback = state.indent;
    state.indent += state.gap;
    Object[] k = null;
    if (state.propertyList != null) {
      k = state.propertyList.toArray();
    } else {
      k = value.getIds();
    } 
    
    List<Object> partial = new LinkedList();
    
    for (Object p : k) {
      Object strP = str(p, value, state);
      if (strP != Undefined.instance) {
        String member = quote(p.toString()) + ":";
        if (state.gap.length() > 0) {
          member = member + " ";
        }
        member = member + strP;
        partial.add(member);
      } 
    } 


    
    if (partial.isEmpty()) {
      finalValue = "{}";
    }
    else if (state.gap.length() == 0) {
      finalValue = '{' + join(partial, ",") + '}';
    } else {
      String separator = ",\n" + state.indent;
      String properties = join(partial, separator);
      finalValue = "{\n" + state.indent + properties + '\n' + stepback + '}';
    } 


    
    state.stack.pop();
    state.indent = stepback;
    return finalValue;
  }
  private static String ja(NativeArray value, StringifyState state) {
    String finalValue;
    if (state.stack.search(value) != -1) {
      throw ScriptRuntime.typeError0("msg.cyclic.value");
    }
    state.stack.push(value);
    
    String stepback = state.indent;
    state.indent += state.gap;
    List<Object> partial = new LinkedList();
    
    long len = value.getLength(); long index;
    for (index = 0L; index < len; index++) {
      Object strP;
      if (index > 2147483647L) {
        strP = str(Long.toString(index), value, state);
      } else {
        strP = str(Integer.valueOf((int)index), value, state);
      } 
      if (strP == Undefined.instance) {
        partial.add("null");
      } else {
        partial.add(strP);
      } 
    } 


    
    if (partial.isEmpty()) {
      finalValue = "[]";
    }
    else if (state.gap.length() == 0) {
      finalValue = '[' + join(partial, ",") + ']';
    } else {
      String separator = ",\n" + state.indent;
      String properties = join(partial, separator);
      finalValue = "[\n" + state.indent + properties + '\n' + stepback + ']';
    } 

    
    state.stack.pop();
    state.indent = stepback;
    return finalValue;
  }
  
  private static String quote(String string) {
    StringBuilder product = new StringBuilder(string.length() + 2);
    product.append('"');
    int length = string.length();
    for (int i = 0; i < length; i++) {
      char c = string.charAt(i);
      switch (c) {
        case '"':
          product.append("\\\"");
          break;
        case '\\':
          product.append("\\\\");
          break;
        case '\b':
          product.append("\\b");
          break;
        case '\f':
          product.append("\\f");
          break;
        case '\n':
          product.append("\\n");
          break;
        case '\r':
          product.append("\\r");
          break;
        case '\t':
          product.append("\\t");
          break;
        default:
          if (c < ' ') {
            product.append("\\u");
            String hex = String.format("%04x", new Object[] { Integer.valueOf(c) });
            product.append(hex);
            break;
          } 
          product.append(c);
          break;
      } 
    
    } 
    product.append('"');
    return product.toString();
  }






  
  protected int findPrototypeId(String s) {
    int id = 0; String X = null;
    switch (s.length()) { case 5:
        X = "parse"; id = 2; break;
      case 8: X = "toSource"; id = 1; break;
      case 9: X = "stringify"; id = 3; break; }
    
    if (X != null && X != s && !X.equals(s)) id = 0;

    
    return id;
  }
}
