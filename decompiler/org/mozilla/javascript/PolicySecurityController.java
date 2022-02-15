package org.mozilla.javascript;

import java.lang.ref.SoftReference;
import java.lang.reflect.UndeclaredThrowableException;
import java.security.AccessController;
import java.security.CodeSource;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.SecureClassLoader;
import java.util.Map;
import java.util.WeakHashMap;
import org.mozilla.classfile.ClassFileWriter;


















public class PolicySecurityController
  extends SecurityController
{
  private static final byte[] secureCallerImplBytecode = loadBytecode();





  
  private static final Map<CodeSource, Map<ClassLoader, SoftReference<SecureCaller>>> callers = new WeakHashMap<CodeSource, Map<ClassLoader, SoftReference<SecureCaller>>>();


  
  public Class<?> getStaticSecurityDomainClassInternal() {
    return CodeSource.class;
  }
  
  private static class Loader
    extends SecureClassLoader
    implements GeneratedClassLoader
  {
    private final CodeSource codeSource;
    
    Loader(ClassLoader parent, CodeSource codeSource) {
      super(parent);
      this.codeSource = codeSource;
    }

    
    public Class<?> defineClass(String name, byte[] data) {
      return defineClass(name, data, 0, data.length, this.codeSource);
    }

    
    public void linkClass(Class<?> cl) {
      resolveClass(cl);
    }
  }



  
  public GeneratedClassLoader createClassLoader(final ClassLoader parent, final Object securityDomain) {
    return AccessController.<Loader>doPrivileged(new PrivilegedAction()
        {
          
          public Object run()
          {
            return new PolicySecurityController.Loader(parent, (CodeSource)securityDomain);
          }
        });
  }




  
  public Object getDynamicSecurityDomain(Object securityDomain) {
    return securityDomain;
  }




  
  public Object callWithDomain(Object securityDomain, final Context cx, Callable callable, Scriptable scope, Scriptable thisObj, Object[] args) {
    Map<ClassLoader, SoftReference<SecureCaller>> classLoaderMap;
    SecureCaller caller;
    final ClassLoader classLoader = AccessController.<ClassLoader>doPrivileged(new PrivilegedAction()
        {
          public Object run() {
            return cx.getApplicationClassLoader();
          }
        });
    final CodeSource codeSource = (CodeSource)securityDomain;
    
    synchronized (callers) {
      classLoaderMap = callers.get(codeSource);
      if (classLoaderMap == null) {
        classLoaderMap = new WeakHashMap<ClassLoader, SoftReference<SecureCaller>>();
        callers.put(codeSource, classLoaderMap);
      } 
    } 
    
    synchronized (classLoaderMap) {
      SoftReference<SecureCaller> ref = classLoaderMap.get(classLoader);
      if (ref != null) {
        caller = ref.get();
      } else {
        caller = null;
      } 
      if (caller == null) {
        
        try {


          
          caller = AccessController.<SecureCaller>doPrivileged(new PrivilegedExceptionAction()
              {
                
                public Object run() throws Exception
                {
                  PolicySecurityController.Loader loader = new PolicySecurityController.Loader(classLoader, codeSource);
                  
                  Class<?> c = loader.defineClass(PolicySecurityController.SecureCaller.class.getName() + "Impl", PolicySecurityController.secureCallerImplBytecode);

                  
                  return c.newInstance();
                }
              });
          classLoaderMap.put(classLoader, new SoftReference<SecureCaller>(caller));
        }
        catch (PrivilegedActionException ex) {
          
          throw new UndeclaredThrowableException(ex.getCause());
        } 
      }
    } 
    return caller.call(callable, cx, scope, thisObj, args);
  }


  
  public static abstract class SecureCaller
  {
    public abstract Object call(Callable param1Callable, Context param1Context, Scriptable param1Scriptable1, Scriptable param1Scriptable2, Object[] param1ArrayOfObject);
  }

  
  private static byte[] loadBytecode() {
    String secureCallerClassName = SecureCaller.class.getName();
    ClassFileWriter cfw = new ClassFileWriter(secureCallerClassName + "Impl", secureCallerClassName, "<generated>");

    
    cfw.startMethod("<init>", "()V", (short)1);
    cfw.addALoad(0);
    cfw.addInvoke(183, secureCallerClassName, "<init>", "()V");
    
    cfw.add(177);
    cfw.stopMethod((short)1);
    String callableCallSig = "Lorg/mozilla/javascript/Context;Lorg/mozilla/javascript/Scriptable;Lorg/mozilla/javascript/Scriptable;[Ljava/lang/Object;)Ljava/lang/Object;";




    
    cfw.startMethod("call", "(Lorg/mozilla/javascript/Callable;" + callableCallSig, (short)17);


    
    for (int i = 1; i < 6; i++) {
      cfw.addALoad(i);
    }
    cfw.addInvoke(185, "org/mozilla/javascript/Callable", "call", "(" + callableCallSig);

    
    cfw.add(176);
    cfw.stopMethod((short)6);
    return cfw.toByteArray();
  }
}
