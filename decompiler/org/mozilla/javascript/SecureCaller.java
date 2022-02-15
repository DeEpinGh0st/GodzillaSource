package org.mozilla.javascript;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.lang.reflect.UndeclaredThrowableException;
import java.net.URL;
import java.security.AccessController;
import java.security.CodeSource;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.SecureClassLoader;
import java.util.Map;
import java.util.WeakHashMap;








public abstract class SecureCaller
{
  private static final byte[] secureCallerImplBytecode = loadBytecode();





  
  private static final Map<CodeSource, Map<ClassLoader, SoftReference<SecureCaller>>> callers = new WeakHashMap<CodeSource, Map<ClassLoader, SoftReference<SecureCaller>>>();








  
  static Object callSecurely(final CodeSource codeSource, Callable callable, Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
    Map<ClassLoader, SoftReference<SecureCaller>> classLoaderMap;
    SecureCaller caller;
    final Thread thread = Thread.currentThread();

    
    final ClassLoader classLoader = AccessController.<ClassLoader>doPrivileged(new PrivilegedAction()
        {
          public Object run() {
            return thread.getContextClassLoader();
          }
        });
    
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
                  ClassLoader effectiveClassLoader;
                  
                  Class<?> thisClass = getClass();
                  if (classLoader.loadClass(thisClass.getName()) != thisClass) {
                    effectiveClassLoader = thisClass.getClassLoader();
                  } else {
                    effectiveClassLoader = classLoader;
                  } 
                  SecureCaller.SecureClassLoaderImpl secCl = new SecureCaller.SecureClassLoaderImpl(effectiveClassLoader);
                  
                  Class<?> c = secCl.defineAndLinkClass(SecureCaller.class.getName() + "Impl", SecureCaller.secureCallerImplBytecode, codeSource);

                  
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
  
  private static class SecureClassLoaderImpl
    extends SecureClassLoader
  {
    SecureClassLoaderImpl(ClassLoader parent) {
      super(parent);
    }

    
    Class<?> defineAndLinkClass(String name, byte[] bytes, CodeSource cs) {
      Class<?> cl = defineClass(name, bytes, 0, bytes.length, cs);
      resolveClass(cl);
      return cl;
    }
  }

  
  private static byte[] loadBytecode() {
    return AccessController.<byte[]>doPrivileged(new PrivilegedAction()
        {
          public Object run()
          {
            return SecureCaller.loadBytecodePrivileged();
          }
        });
  }

  
  private static byte[] loadBytecodePrivileged() {
    URL url = SecureCaller.class.getResource("SecureCallerImpl.clazz");
    
    try {
      InputStream in = url.openStream();
      
      try {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        
        while (true) {
          int r = in.read();
          if (r == -1)
          {
            return bout.toByteArray();
          }
          bout.write(r);
        }
      
      } finally {
        
        in.close();
      }
    
    } catch (IOException e) {
      
      throw new UndeclaredThrowableException(e);
    } 
  }
  
  public abstract Object call(Callable paramCallable, Context paramContext, Scriptable paramScriptable1, Scriptable paramScriptable2, Object[] paramArrayOfObject);
}
