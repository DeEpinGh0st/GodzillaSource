package org.mozilla.javascript.xmlimpl;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.IdFunctionCall;
import org.mozilla.javascript.IdFunctionObject;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.Undefined;

class XMLCtor
  extends IdFunctionObject
{
  static final long serialVersionUID = -8708195078359817341L;
  private static final Object XMLCTOR_TAG = "XMLCtor"; private XmlProcessor options;
  private static final int Id_ignoreComments = 1;
  private static final int Id_ignoreProcessingInstructions = 2;
  private static final int Id_ignoreWhitespace = 3;
  private static final int Id_prettyIndent = 4;
  
  XMLCtor(XML xml, Object tag, int id, int arity) {
    super((IdFunctionCall)xml, tag, id, arity);
    
    this.options = xml.getProcessor();
    activatePrototypeMap(3);
  }
  private static final int Id_prettyPrinting = 5; private static final int MAX_INSTANCE_ID = 5; private static final int Id_defaultSettings = 1; private static final int Id_settings = 2; private static final int Id_setSettings = 3; private static final int MAX_FUNCTION_ID = 3;
  
  private void writeSetting(Scriptable target) {
    for (int i = 1; i <= 5; i++) {
      int id = super.getMaxInstanceId() + i;
      String name = getInstanceIdName(id);
      Object value = getInstanceIdValue(id);
      ScriptableObject.putProperty(target, name, value);
    } 
  }




















  
  private void readSettings(Scriptable source) {
    // Byte code:
    //   0: iconst_1
    //   1: istore_2
    //   2: iload_2
    //   3: iconst_5
    //   4: if_icmpgt -> 119
    //   7: aload_0
    //   8: invokespecial getMaxInstanceId : ()I
    //   11: iload_2
    //   12: iadd
    //   13: istore_3
    //   14: aload_0
    //   15: iload_3
    //   16: invokevirtual getInstanceIdName : (I)Ljava/lang/String;
    //   19: astore #4
    //   21: aload_1
    //   22: aload #4
    //   24: invokestatic getProperty : (Lorg/mozilla/javascript/Scriptable;Ljava/lang/String;)Ljava/lang/Object;
    //   27: astore #5
    //   29: aload #5
    //   31: getstatic org/mozilla/javascript/Scriptable.NOT_FOUND : Ljava/lang/Object;
    //   34: if_acmpne -> 40
    //   37: goto -> 113
    //   40: iload_2
    //   41: tableswitch default -> 98, 1 -> 76, 2 -> 76, 3 -> 76, 4 -> 87, 5 -> 76
    //   76: aload #5
    //   78: instanceof java/lang/Boolean
    //   81: ifne -> 106
    //   84: goto -> 113
    //   87: aload #5
    //   89: instanceof java/lang/Number
    //   92: ifne -> 106
    //   95: goto -> 113
    //   98: new java/lang/IllegalStateException
    //   101: dup
    //   102: invokespecial <init> : ()V
    //   105: athrow
    //   106: aload_0
    //   107: iload_3
    //   108: aload #5
    //   110: invokevirtual setInstanceIdValue : (ILjava/lang/Object;)V
    //   113: iinc #2, 1
    //   116: goto -> 2
    //   119: return
    // Line number table:
    //   Java source line number -> byte code offset
    //   #40	-> 0
    //   #41	-> 7
    //   #42	-> 14
    //   #43	-> 21
    //   #44	-> 29
    //   #45	-> 37
    //   #47	-> 40
    //   #52	-> 76
    //   #53	-> 84
    //   #57	-> 87
    //   #58	-> 95
    //   #62	-> 98
    //   #64	-> 106
    //   #40	-> 113
    //   #66	-> 119
    // Local variable table:
    //   start	length	slot	name	descriptor
    //   14	99	3	id	I
    //   21	92	4	name	Ljava/lang/String;
    //   29	84	5	value	Ljava/lang/Object;
    //   2	117	2	i	I
    //   0	120	0	this	Lorg/mozilla/javascript/xmlimpl/XMLCtor;
    //   0	120	1	source	Lorg/mozilla/javascript/Scriptable;
  }



















  
  protected int getMaxInstanceId() {
    return super.getMaxInstanceId() + 5;
  }



  
  protected int findInstanceIdInfo(String s) {
    int attr, c, id = 0; String X = null;
    switch (s.length()) { case 12:
        X = "prettyIndent"; id = 4; break;
      case 14: c = s.charAt(0);
        if (c == 105) { X = "ignoreComments"; id = 1; break; }
         if (c == 112) { X = "prettyPrinting"; id = 5; }  break;
      case 16:
        X = "ignoreWhitespace"; id = 3; break;
      case 28: X = "ignoreProcessingInstructions"; id = 2; break; }
    
    if (X != null && X != s && !X.equals(s)) id = 0;



    
    if (id == 0) return super.findInstanceIdInfo(s);

    
    switch (id) {
      case 1:
      case 2:
      case 3:
      case 4:
      case 5:
        attr = 6;


        
        return instanceIdInfo(attr, super.getMaxInstanceId() + id);
    } 
    throw new IllegalStateException();
  }


  
  protected String getInstanceIdName(int id) {
    switch (id - super.getMaxInstanceId()) { case 1:
        return "ignoreComments";
      case 2: return "ignoreProcessingInstructions";
      case 3: return "ignoreWhitespace";
      case 4: return "prettyIndent";
      case 5: return "prettyPrinting"; }
    
    return super.getInstanceIdName(id);
  }


  
  protected Object getInstanceIdValue(int id) {
    switch (id - super.getMaxInstanceId()) {
      case 1:
        return ScriptRuntime.wrapBoolean(this.options.isIgnoreComments());
      case 2:
        return ScriptRuntime.wrapBoolean(this.options.isIgnoreProcessingInstructions());
      case 3:
        return ScriptRuntime.wrapBoolean(this.options.isIgnoreWhitespace());
      case 4:
        return ScriptRuntime.wrapInt(this.options.getPrettyIndent());
      case 5:
        return ScriptRuntime.wrapBoolean(this.options.isPrettyPrinting());
    } 
    return super.getInstanceIdValue(id);
  }

  
  protected void setInstanceIdValue(int id, Object value) {
    switch (id - super.getMaxInstanceId()) {
      case 1:
        this.options.setIgnoreComments(ScriptRuntime.toBoolean(value));
        return;
      case 2:
        this.options.setIgnoreProcessingInstructions(ScriptRuntime.toBoolean(value));
        return;
      case 3:
        this.options.setIgnoreWhitespace(ScriptRuntime.toBoolean(value));
        return;
      case 4:
        this.options.setPrettyIndent(ScriptRuntime.toInt32(value));
        return;
      case 5:
        this.options.setPrettyPrinting(ScriptRuntime.toBoolean(value));
        return;
    } 
    super.setInstanceIdValue(id, value);
  }











  
  protected int findPrototypeId(String s) {
    int id = 0; String X = null;
    int s_length = s.length();
    if (s_length == 8) { X = "settings"; id = 2; }
    else if (s_length == 11) { X = "setSettings"; id = 3; }
    else if (s_length == 15) { X = "defaultSettings"; id = 1; }
     if (X != null && X != s && !X.equals(s)) id = 0;


    
    return id;
  }



  
  protected void initPrototypeId(int id) {
    String s;
    int arity;
    switch (id) { case 1:
        arity = 0; s = "defaultSettings"; break;
      case 2: arity = 0; s = "settings"; break;
      case 3: arity = 1; s = "setSettings"; break;
      default: throw new IllegalArgumentException(String.valueOf(id)); }
    
    initPrototypeMethod(XMLCTOR_TAG, id, s, arity);
  }


  
  public Object execIdCall(IdFunctionObject f, Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
    Scriptable obj;
    if (!f.hasTag(XMLCTOR_TAG)) {
      return super.execIdCall(f, cx, scope, thisObj, args);
    }
    int id = f.methodId();
    switch (id) {
      case 1:
        this.options.setDefault();
        obj = cx.newObject(scope);
        writeSetting(obj);
        return obj;
      
      case 2:
        obj = cx.newObject(scope);
        writeSetting(obj);
        return obj;
      
      case 3:
        if (args.length == 0 || args[0] == null || args[0] == Undefined.instance) {


          
          this.options.setDefault();
        } else if (args[0] instanceof Scriptable) {
          readSettings((Scriptable)args[0]);
        } 
        return Undefined.instance;
    } 
    
    throw new IllegalArgumentException(String.valueOf(id));
  }




  
  public boolean hasInstance(Scriptable instance) {
    return (instance instanceof XML || instance instanceof XMLList);
  }
}
