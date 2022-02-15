package org.mozilla.javascript;














public class BoundFunction
  extends BaseFunction
{
  static final long serialVersionUID = 2118137342826470729L;
  private final Callable targetFunction;
  private final Scriptable boundThis;
  private final Object[] boundArgs;
  private final int length;
  
  public BoundFunction(Context cx, Scriptable scope, Callable targetFunction, Scriptable boundThis, Object[] boundArgs) {
    this.targetFunction = targetFunction;
    this.boundThis = boundThis;
    this.boundArgs = boundArgs;
    if (targetFunction instanceof BaseFunction) {
      this.length = Math.max(0, ((BaseFunction)targetFunction).getLength() - boundArgs.length);
    } else {
      this.length = 0;
    } 
    
    ScriptRuntime.setFunctionProtoAndParent(this, scope);
    
    Function thrower = ScriptRuntime.typeErrorThrower(cx);
    NativeObject throwing = new NativeObject();
    throwing.put("get", throwing, thrower);
    throwing.put("set", throwing, thrower);
    throwing.put("enumerable", throwing, Boolean.valueOf(false));
    throwing.put("configurable", throwing, Boolean.valueOf(false));
    throwing.preventExtensions();
    
    defineOwnProperty(cx, "caller", throwing, false);
    defineOwnProperty(cx, "arguments", throwing, false);
  }


  
  public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] extraArgs) {
    Scriptable callThis = (this.boundThis != null) ? this.boundThis : ScriptRuntime.getTopCallScope(cx);
    return this.targetFunction.call(cx, scope, callThis, concat(this.boundArgs, extraArgs));
  }

  
  public Scriptable construct(Context cx, Scriptable scope, Object[] extraArgs) {
    if (this.targetFunction instanceof Function) {
      return ((Function)this.targetFunction).construct(cx, scope, concat(this.boundArgs, extraArgs));
    }
    throw ScriptRuntime.typeError0("msg.not.ctor");
  }

  
  public boolean hasInstance(Scriptable instance) {
    if (this.targetFunction instanceof Function) {
      return ((Function)this.targetFunction).hasInstance(instance);
    }
    throw ScriptRuntime.typeError0("msg.not.ctor");
  }

  
  public int getLength() {
    return this.length;
  }
  
  private Object[] concat(Object[] first, Object[] second) {
    Object[] args = new Object[first.length + second.length];
    System.arraycopy(first, 0, args, 0, first.length);
    System.arraycopy(second, 0, args, first.length, second.length);
    return args;
  }
}
