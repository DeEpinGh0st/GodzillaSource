package org.mozilla.javascript;

import java.security.AccessController;
import java.security.PrivilegedAction;
import org.mozilla.javascript.xml.XMLLib;








































































































public class ContextFactory
{
  private static volatile boolean hasCustomGlobal;
  private static ContextFactory global = new ContextFactory();
  
  private volatile boolean sealed;
  
  private final Object listenersLock = new Object();





  
  private volatile Object listeners;





  
  private boolean disabledListening;





  
  private ClassLoader applicationClassLoader;






  
  public static ContextFactory getGlobal() {
    return global;
  }










  
  public static boolean hasExplicitGlobal() {
    return hasCustomGlobal;
  }








  
  public static synchronized void initGlobal(ContextFactory factory) {
    if (factory == null) {
      throw new IllegalArgumentException();
    }
    if (hasCustomGlobal) {
      throw new IllegalStateException();
    }
    hasCustomGlobal = true;
    global = factory;
  }





  
  public static synchronized GlobalSetter getGlobalSetter() {
    if (hasCustomGlobal) {
      throw new IllegalStateException();
    }
    hasCustomGlobal = true;
    class GlobalSetterImpl implements GlobalSetter {
      public void setContextFactoryGlobal(ContextFactory factory) {
        ContextFactory.global = (factory == null) ? new ContextFactory() : factory;
      }
      public ContextFactory getContextFactoryGlobal() {
        return ContextFactory.global;
      }
    };
    return new GlobalSetterImpl();
  }










  
  protected Context makeContext() {
    return new Context(this);
  }






  
  protected boolean hasFeature(Context cx, int featureIndex) {
    int version;
    switch (featureIndex) {










      
      case 1:
        version = cx.getLanguageVersion();
        return (version == 100 || version == 110 || version == 120);


      
      case 2:
        return false;
      
      case 3:
        return true;
      
      case 4:
        version = cx.getLanguageVersion();
        return (version == 120);
      
      case 5:
        return true;
      
      case 6:
        version = cx.getLanguageVersion();
        return (version == 0 || version >= 160);

      
      case 7:
        return false;
      
      case 8:
        return false;
      
      case 9:
        return false;
      
      case 10:
        return false;
      
      case 11:
        return false;
      
      case 12:
        return false;
      
      case 13:
        return false;
      
      case 14:
        return true;
    } 
    
    throw new IllegalArgumentException(String.valueOf(featureIndex));
  }
  
  private boolean isDom3Present() {
    Class<?> nodeClass = Kit.classOrNull("org.w3c.dom.Node");
    if (nodeClass == null) return false;

    
    try {
      nodeClass.getMethod("getUserData", new Class[] { String.class });
      return true;
    } catch (NoSuchMethodException e) {
      return false;
    } 
  }



















  
  protected XMLLib.Factory getE4xImplementationFactory() {
    if (isDom3Present()) {
      return XMLLib.Factory.create("org.mozilla.javascript.xmlimpl.XMLLibImpl");
    }

    
    return null;
  }











  
  protected GeneratedClassLoader createClassLoader(final ClassLoader parent) {
    return AccessController.<GeneratedClassLoader>doPrivileged((PrivilegedAction)new PrivilegedAction<DefiningClassLoader>() {
          public DefiningClassLoader run() {
            return new DefiningClassLoader(parent);
          }
        });
  }
  
  public static interface GlobalSetter
  {
    void setContextFactoryGlobal(ContextFactory param1ContextFactory);
    
    ContextFactory getContextFactoryGlobal();
  }
  
  public final ClassLoader getApplicationClassLoader() {
    return this.applicationClassLoader;
  }
  
  public static interface Listener {
    void contextCreated(Context param1Context);
    
    void contextReleased(Context param1Context);
  }
  
  public final void initApplicationClassLoader(ClassLoader loader) {
    if (loader == null)
      throw new IllegalArgumentException("loader is null"); 
    if (!Kit.testIfCanLoadRhinoClasses(loader)) {
      throw new IllegalArgumentException("Loader can not resolve Rhino classes");
    }
    
    if (this.applicationClassLoader != null) {
      throw new IllegalStateException("applicationClassLoader can only be set once");
    }
    checkNotSealed();
    
    this.applicationClassLoader = loader;
  }










  
  protected Object doTopCall(Callable callable, Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
    Object result = callable.call(cx, scope, thisObj, args);
    return (result instanceof ConsString) ? result.toString() : result;
  }




  
  protected void observeInstructionCount(Context cx, int instructionCount) {}




  
  protected void onContextCreated(Context cx) {
    Object listeners = this.listeners;
    for (int i = 0;; i++) {
      Listener l = (Listener)Kit.getListener(listeners, i);
      if (l == null)
        break; 
      l.contextCreated(cx);
    } 
  }

  
  protected void onContextReleased(Context cx) {
    Object listeners = this.listeners;
    for (int i = 0;; i++) {
      Listener l = (Listener)Kit.getListener(listeners, i);
      if (l == null)
        break; 
      l.contextReleased(cx);
    } 
  }

  
  public final void addListener(Listener listener) {
    checkNotSealed();
    synchronized (this.listenersLock) {
      if (this.disabledListening) {
        throw new IllegalStateException();
      }
      this.listeners = Kit.addListener(this.listeners, listener);
    } 
  }

  
  public final void removeListener(Listener listener) {
    checkNotSealed();
    synchronized (this.listenersLock) {
      if (this.disabledListening) {
        throw new IllegalStateException();
      }
      this.listeners = Kit.removeListener(this.listeners, listener);
    } 
  }





  
  final void disableContextListening() {
    checkNotSealed();
    synchronized (this.listenersLock) {
      this.disabledListening = true;
      this.listeners = null;
    } 
  }





  
  public final boolean isSealed() {
    return this.sealed;
  }






  
  public final void seal() {
    checkNotSealed();
    this.sealed = true;
  }

  
  protected final void checkNotSealed() {
    if (this.sealed) throw new IllegalStateException();
  
  }













  
  public final Object call(ContextAction action) {
    return Context.call(this, action);
  }








































  
  public Context enterContext() {
    return enterContext(null);
  }





  
  @Deprecated
  public final Context enter() {
    return enterContext(null);
  }




  
  @Deprecated
  public final void exit() {
    Context.exit();
  }
















  
  public final Context enterContext(Context cx) {
    return Context.enter(cx, this);
  }
}
