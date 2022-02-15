package org.mozilla.javascript;

public class BaseFunction
  extends IdScriptableObject implements Function {
  static final long serialVersionUID = 5311394446546053859L;
  private static final int Id_length = 1;
  private static final int Id_arity = 2;
  private static final int Id_name = 3;
  private static final int Id_prototype = 4;
  private static final int Id_arguments = 5;
  private static final int MAX_INSTANCE_ID = 5;
  private static final int Id_constructor = 1;
  private static final int Id_toString = 2;
  private static final int Id_toSource = 3;
  private static final int Id_apply = 4;
  private static final int Id_call = 5;
  private static final int Id_bind = 6;
  private static final int MAX_PROTOTYPE_ID = 6;
  private static final Object FUNCTION_TAG = "Function"; private Object prototypeProperty; private Object argumentsObj; private int prototypePropertyAttributes;
  private int argumentsAttributes;
  
  static void init(Scriptable scope, boolean sealed) {
    BaseFunction obj = new BaseFunction();
    
    obj.prototypePropertyAttributes = 7;
    obj.exportAsJSClass(6, scope, sealed);
  }


























































































































































































































































































































































































































































































































































































  
  public BaseFunction()
  {
    this.argumentsObj = NOT_FOUND;



    
    this.prototypePropertyAttributes = 6;
    this.argumentsAttributes = 6; } public BaseFunction(Scriptable scope, Scriptable prototype) { super(scope, prototype); this.argumentsObj = NOT_FOUND; this.prototypePropertyAttributes = 6; this.argumentsAttributes = 6; }

  
  public String getClassName() {
    return "Function";
  }
  
  public String getTypeOf() {
    return avoidObjectDetection() ? "undefined" : "function";
  }
  
  public boolean hasInstance(Scriptable instance) {
    Object protoProp = ScriptableObject.getProperty(this, "prototype");
    if (protoProp instanceof Scriptable)
      return ScriptRuntime.jsDelegatesTo(instance, (Scriptable)protoProp); 
    throw ScriptRuntime.typeError1("msg.instanceof.bad.prototype", getFunctionName());
  }
  
  protected int getMaxInstanceId() {
    return 5;
  }
  
  protected int findInstanceIdInfo(String s) {
    int attr, c, id = 0;
    String X = null;
    switch (s.length()) {
      case 4:
        X = "name";
        id = 3;
        break;
      case 5:
        X = "arity";
        id = 2;
        break;
      case 6:
        X = "length";
        id = 1;
        break;
      case 9:
        c = s.charAt(0);
        if (c == 97) {
          X = "arguments";
          id = 5;
          break;
        } 
        if (c == 112) {
          X = "prototype";
          id = 4;
        } 
        break;
    } 
    if (X != null && X != s && !X.equals(s))
      id = 0; 
    if (id == 0)
      return super.findInstanceIdInfo(s); 
    switch (id) {
      case 1:
      case 2:
      case 3:
        attr = 7;
        return instanceIdInfo(attr, id);
      case 4:
        if (!hasPrototypeProperty())
          return 0; 
        attr = this.prototypePropertyAttributes;
        return instanceIdInfo(attr, id);
      case 5:
        attr = this.argumentsAttributes;
        return instanceIdInfo(attr, id);
    } 
    throw new IllegalStateException();
  }
  
  protected String getInstanceIdName(int id) {
    switch (id) {
      case 1:
        return "length";
      case 2:
        return "arity";
      case 3:
        return "name";
      case 4:
        return "prototype";
      case 5:
        return "arguments";
    } 
    return super.getInstanceIdName(id);
  }
  
  protected Object getInstanceIdValue(int id) {
    switch (id) {
      case 1:
        return ScriptRuntime.wrapInt(getLength());
      case 2:
        return ScriptRuntime.wrapInt(getArity());
      case 3:
        return getFunctionName();
      case 4:
        return getPrototypeProperty();
      case 5:
        return getArguments();
    } 
    return super.getInstanceIdValue(id);
  }
  
  protected void setInstanceIdValue(int id, Object value) {
    switch (id) {
      case 4:
        if ((this.prototypePropertyAttributes & 0x1) == 0)
          this.prototypeProperty = (value != null) ? value : UniqueTag.NULL_VALUE; 
        return;
      case 5:
        if (value == NOT_FOUND)
          Kit.codeBug(); 
        if (defaultHas("arguments")) {
          defaultPut("arguments", value);
        } else if ((this.argumentsAttributes & 0x1) == 0) {
          this.argumentsObj = value;
        } 
        return;
      case 1:
      case 2:
      case 3:
        return;
    } 
    super.setInstanceIdValue(id, value);
  }
  
  protected void setInstanceIdAttributes(int id, int attr) {
    switch (id) {
      case 4:
        this.prototypePropertyAttributes = attr;
        return;
      case 5:
        this.argumentsAttributes = attr;
        return;
    } 
    super.setInstanceIdAttributes(id, attr);
  }
  
  protected void fillConstructorProperties(IdFunctionObject ctor) {
    ctor.setPrototype(this);
    super.fillConstructorProperties(ctor);
  }
  
  protected void initPrototypeId(int id) {
    String s;
    int arity;
    switch (id) {
      case 1:
        arity = 1;
        s = "constructor";
        break;
      case 2:
        arity = 0;
        s = "toString";
        break;
      case 3:
        arity = 1;
        s = "toSource";
        break;
      case 4:
        arity = 2;
        s = "apply";
        break;
      case 5:
        arity = 1;
        s = "call";
        break;
      case 6:
        arity = 1;
        s = "bind";
        break;
      default:
        throw new IllegalArgumentException(String.valueOf(id));
    } 
    initPrototypeMethod(FUNCTION_TAG, id, s, arity);
  }
  
  static boolean isApply(IdFunctionObject f) {
    return (f.hasTag(FUNCTION_TAG) && f.methodId() == 4);
  }
  
  static boolean isApplyOrCall(IdFunctionObject f) {
    if (f.hasTag(FUNCTION_TAG))
      switch (f.methodId()) {
        case 4:
        case 5:
          return true;
      }  
    return false;
  }
  
  public Object execIdCall(IdFunctionObject f, Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
    BaseFunction realf;
    Callable targetFunction;
    int indent, argc, flags;
    Scriptable boundThis;
    Object[] boundArgs;
    if (!f.hasTag(FUNCTION_TAG))
      return super.execIdCall(f, cx, scope, thisObj, args); 
    int id = f.methodId();
    switch (id) {
      case 1:
        return jsConstructor(cx, scope, args);
      case 2:
        realf = realFunction(thisObj, f);
        indent = ScriptRuntime.toInt32(args, 0);
        return realf.decompile(indent, 0);
      case 3:
        realf = realFunction(thisObj, f);
        indent = 0;
        flags = 2;
        if (args.length != 0) {
          indent = ScriptRuntime.toInt32(args[0]);
          if (indent >= 0) {
            flags = 0;
          } else {
            indent = 0;
          } 
        } 
        return realf.decompile(indent, flags);
      case 4:
      case 5:
        return ScriptRuntime.applyOrCall((id == 4), cx, scope, thisObj, args);
      case 6:
        if (!(thisObj instanceof Callable))
          throw ScriptRuntime.notFunctionError(thisObj); 
        targetFunction = (Callable)thisObj;
        argc = args.length;
        if (argc > 0) {
          boundThis = ScriptRuntime.toObjectOrNull(cx, args[0], scope);
          boundArgs = new Object[argc - 1];
          System.arraycopy(args, 1, boundArgs, 0, argc - 1);
        } else {
          boundThis = null;
          boundArgs = ScriptRuntime.emptyArgs;
        } 
        return new BoundFunction(cx, scope, targetFunction, boundThis, boundArgs);
    } 
    throw new IllegalArgumentException(String.valueOf(id));
  }
  
  private BaseFunction realFunction(Scriptable thisObj, IdFunctionObject f) {
    Object x = thisObj.getDefaultValue(ScriptRuntime.FunctionClass);
    if (x instanceof Delegator)
      x = ((Delegator)x).getDelegee(); 
    if (x instanceof BaseFunction)
      return (BaseFunction)x; 
    throw ScriptRuntime.typeError1("msg.incompat.call", f.getFunctionName());
  }
  
  public void setImmunePrototypeProperty(Object value) {
    if ((this.prototypePropertyAttributes & 0x1) != 0)
      throw new IllegalStateException(); 
    this.prototypeProperty = (value != null) ? value : UniqueTag.NULL_VALUE;
    this.prototypePropertyAttributes = 7;
  }
  
  protected Scriptable getClassPrototype() {
    Object protoVal = getPrototypeProperty();
    if (protoVal instanceof Scriptable)
      return (Scriptable)protoVal; 
    return ScriptableObject.getObjectPrototype(this);
  }
  
  public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
    return Undefined.instance;
  }
  
  public Scriptable construct(Context cx, Scriptable scope, Object[] args) {
    Scriptable result = createObject(cx, scope);
    if (result != null) {
      Object val = call(cx, scope, result, args);
      if (val instanceof Scriptable)
        result = (Scriptable)val; 
    } else {
      Object val = call(cx, scope, (Scriptable)null, args);
      if (!(val instanceof Scriptable))
        throw new IllegalStateException("Bad implementaion of call as constructor, name=" + getFunctionName() + " in " + getClass().getName()); 
      result = (Scriptable)val;
      if (result.getPrototype() == null) {
        Scriptable proto = getClassPrototype();
        if (result != proto)
          result.setPrototype(proto); 
      } 
      if (result.getParentScope() == null) {
        Scriptable parent = getParentScope();
        if (result != parent)
          result.setParentScope(parent); 
      } 
    } 
    return result;
  }
  
  public Scriptable createObject(Context cx, Scriptable scope) {
    Scriptable newInstance = new NativeObject();
    newInstance.setPrototype(getClassPrototype());
    newInstance.setParentScope(getParentScope());
    return newInstance;
  }
  
  String decompile(int indent, int flags) {
    StringBuilder sb = new StringBuilder();
    boolean justbody = (0 != (flags & 0x1));
    if (!justbody) {
      sb.append("function ");
      sb.append(getFunctionName());
      sb.append("() {\n\t");
    } 
    sb.append("[native code, arity=");
    sb.append(getArity());
    sb.append("]\n");
    if (!justbody)
      sb.append("}\n"); 
    return sb.toString();
  }
  
  public int getArity() {
    return 0;
  }
  
  public int getLength() {
    return 0;
  }
  
  public String getFunctionName() {
    return "";
  }
  
  protected boolean hasPrototypeProperty() {
    return (this.prototypeProperty != null || this instanceof NativeFunction);
  }
  
  protected Object getPrototypeProperty() {
    Object result = this.prototypeProperty;
    if (result == null) {
      if (this instanceof NativeFunction) {
        result = setupDefaultPrototype();
      } else {
        result = Undefined.instance;
      } 
    } else if (result == UniqueTag.NULL_VALUE) {
      result = null;
    } 
    return result;
  }
  
  private synchronized Object setupDefaultPrototype() {
    if (this.prototypeProperty != null)
      return this.prototypeProperty; 
    NativeObject obj = new NativeObject();
    int attr = 2;
    obj.defineProperty("constructor", this, 2);
    this.prototypeProperty = obj;
    Scriptable proto = getObjectPrototype(this);
    if (proto != obj)
      obj.setPrototype(proto); 
    return obj;
  }
  
  private Object getArguments() {
    Object value = defaultHas("arguments") ? defaultGet("arguments") : this.argumentsObj;
    if (value != NOT_FOUND)
      return value; 
    Context cx = Context.getContext();
    NativeCall activation = ScriptRuntime.findFunctionActivation(cx, this);
    return (activation == null) ? null : activation.get("arguments", activation);
  }
  
  private static Object jsConstructor(Context cx, Scriptable scope, Object[] args) {
    int arglen = args.length;
    StringBuilder sourceBuf = new StringBuilder();
    sourceBuf.append("function ");
    if (cx.getLanguageVersion() != 120)
      sourceBuf.append("anonymous"); 
    sourceBuf.append('(');
    for (int i = 0; i < arglen - 1; i++) {
      if (i > 0)
        sourceBuf.append(','); 
      sourceBuf.append(ScriptRuntime.toString(args[i]));
    } 
    sourceBuf.append(") {");
    if (arglen != 0) {
      String funBody = ScriptRuntime.toString(args[arglen - 1]);
      sourceBuf.append(funBody);
    } 
    sourceBuf.append("\n}");
    String source = sourceBuf.toString();
    int[] linep = new int[1];
    String filename = Context.getSourcePositionFromStack(linep);
    if (filename == null) {
      filename = "<eval'ed string>";
      linep[0] = 1;
    } 
    String sourceURI = ScriptRuntime.makeUrlForGeneratedScript(false, filename, linep[0]);
    Scriptable global = ScriptableObject.getTopLevelScope(scope);
    ErrorReporter reporter = DefaultErrorReporter.forEval(cx.getErrorReporter());
    Evaluator evaluator = Context.createInterpreter();
    if (evaluator == null)
      throw new JavaScriptException("Interpreter not present", filename, linep[0]); 
    return cx.compileFunction(global, source, evaluator, reporter, sourceURI, 1, null);
  }
  
  protected int findPrototypeId(String s) {
    int c, id = 0;
    String X = null;
    switch (s.length()) {
      case 4:
        c = s.charAt(0);
        if (c == 98) {
          X = "bind";
          id = 6;
          break;
        } 
        if (c == 99) {
          X = "call";
          id = 5;
        } 
        break;
      case 5:
        X = "apply";
        id = 4;
        break;
      case 8:
        c = s.charAt(3);
        if (c == 111) {
          X = "toSource";
          id = 3;
          break;
        } 
        if (c == 116) {
          X = "toString";
          id = 2;
        } 
        break;
      case 11:
        X = "constructor";
        id = 1;
        break;
    } 
    if (X != null && X != s && !X.equals(s))
      id = 0; 
    return id;
  }
}
