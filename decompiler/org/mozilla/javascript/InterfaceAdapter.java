package org.mozilla.javascript;

import java.lang.reflect.Method;




















public class InterfaceAdapter
{
  private final Object proxyHelper;
  
  static Object create(Context cx, Class<?> cl, ScriptableObject object) {
    if (!cl.isInterface()) throw new IllegalArgumentException();
    
    Scriptable topScope = ScriptRuntime.getTopCallScope(cx);
    ClassCache cache = ClassCache.get(topScope);
    
    InterfaceAdapter adapter = (InterfaceAdapter)cache.getInterfaceAdapter(cl);
    ContextFactory cf = cx.getFactory();
    if (adapter == null) {
      Method[] methods = cl.getMethods();
      if (object instanceof Callable) {



        
        int length = methods.length;
        if (length == 0) {
          throw Context.reportRuntimeError1("msg.no.empty.interface.conversion", cl.getName());
        }
        
        if (length > 1) {
          String methodName = methods[0].getName();
          for (int i = 1; i < length; i++) {
            if (!methodName.equals(methods[i].getName())) {
              throw Context.reportRuntimeError1("msg.no.function.interface.conversion", cl.getName());
            }
          } 
        } 
      } 

      
      adapter = new InterfaceAdapter(cf, cl);
      cache.cacheInterfaceAdapter(cl, adapter);
    } 
    return VMBridge.instance.newInterfaceProxy(adapter.proxyHelper, cf, adapter, object, topScope);
  }


  
  private InterfaceAdapter(ContextFactory cf, Class<?> cl) {
    this.proxyHelper = VMBridge.instance.getInterfaceProxyHelper(cf, new Class[] { cl });
  }








  
  public Object invoke(ContextFactory cf, final Object target, final Scriptable topScope, final Object thisObject, final Method method, final Object[] args) {
    ContextAction action = new ContextAction()
      {
        public Object run(Context cx) {
          return InterfaceAdapter.this.invokeImpl(cx, target, topScope, thisObject, method, args);
        }
      };
    return cf.call(action);
  }






  
  Object invokeImpl(Context cx, Object target, Scriptable topScope, Object thisObject, Method method, Object[] args) {
    Callable function;
    if (target instanceof Callable) {
      function = (Callable)target;
    } else {
      Scriptable s = (Scriptable)target;
      String methodName = method.getName();
      Object value = ScriptableObject.getProperty(s, methodName);
      if (value == ScriptableObject.NOT_FOUND) {


        
        Context.reportWarning(ScriptRuntime.getMessage1("msg.undefined.function.interface", methodName));
        
        Class<?> resultType = method.getReturnType();
        if (resultType == void.class) {
          return null;
        }
        return Context.jsToJava(null, resultType);
      } 
      
      if (!(value instanceof Callable)) {
        throw Context.reportRuntimeError1("msg.not.function.interface", methodName);
      }
      
      function = (Callable)value;
    } 
    WrapFactory wf = cx.getWrapFactory();
    if (args == null) {
      args = ScriptRuntime.emptyArgs;
    } else {
      for (int i = 0, N = args.length; i != N; i++) {
        Object arg = args[i];
        
        if (!(arg instanceof String) && !(arg instanceof Number) && !(arg instanceof Boolean))
        {
          args[i] = wf.wrap(cx, topScope, arg, null);
        }
      } 
    } 
    Scriptable thisObj = wf.wrapAsJavaObject(cx, topScope, thisObject, null);
    
    Object result = function.call(cx, topScope, thisObj, args);
    Class<?> javaResultType = method.getReturnType();
    if (javaResultType == void.class) {
      result = null;
    } else {
      result = Context.jsToJava(result, javaResultType);
    } 
    return result;
  }
}
