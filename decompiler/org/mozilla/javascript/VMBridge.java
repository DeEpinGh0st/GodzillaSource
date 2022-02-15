package org.mozilla.javascript;

import java.lang.reflect.Member;
import java.util.Iterator;











public abstract class VMBridge
{
  static final VMBridge instance = makeInstance();

  
  private static VMBridge makeInstance() {
    String[] classNames = { "org.mozilla.javascript.VMBridge_custom", "org.mozilla.javascript.jdk15.VMBridge_jdk15", "org.mozilla.javascript.jdk13.VMBridge_jdk13", "org.mozilla.javascript.jdk11.VMBridge_jdk11" };




    
    for (int i = 0; i != classNames.length; i++) {
      String className = classNames[i];
      Class<?> cl = Kit.classOrNull(className);
      if (cl != null) {
        VMBridge bridge = (VMBridge)Kit.newInstanceOrNull(cl);
        if (bridge != null) {
          return bridge;
        }
      } 
    } 
    throw new IllegalStateException("Failed to create VMBridge instance");
  }









  
  protected abstract Object getThreadContextHelper();









  
  protected abstract Context getContext(Object paramObject);








  
  protected abstract void setContext(Object paramObject, Context paramContext);








  
  protected abstract ClassLoader getCurrentThreadClassLoader();








  
  protected abstract boolean tryToMakeAccessible(Object paramObject);








  
  protected Object getInterfaceProxyHelper(ContextFactory cf, Class<?>[] interfaces) {
    throw Context.reportRuntimeError("VMBridge.getInterfaceProxyHelper is not supported");
  }
















  
  protected Object newInterfaceProxy(Object proxyHelper, ContextFactory cf, InterfaceAdapter adapter, Object target, Scriptable topScope) {
    throw Context.reportRuntimeError("VMBridge.newInterfaceProxy is not supported");
  }






  
  protected abstract boolean isVarArgs(Member paramMember);






  
  public Iterator<?> getJavaIterator(Context cx, Scriptable scope, Object obj) {
    if (obj instanceof Wrapper) {
      Object unwrapped = ((Wrapper)obj).unwrap();
      Iterator<?> iterator = null;
      if (unwrapped instanceof Iterator)
        iterator = (Iterator)unwrapped; 
      return iterator;
    } 
    return null;
  }
}
