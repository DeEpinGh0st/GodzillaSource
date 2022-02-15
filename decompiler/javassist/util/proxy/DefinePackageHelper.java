package javassist.util.proxy;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import javassist.CannotCompileException;
import javassist.bytecode.ClassFile;


























public class DefinePackageHelper
{
  private static abstract class Helper
  {
    private Helper() {}
    
    abstract Package definePackage(ClassLoader param1ClassLoader, String param1String1, String param1String2, String param1String3, String param1String4, String param1String5, String param1String6, String param1String7, URL param1URL) throws IllegalArgumentException;
  }
  
  private static class Java9
    extends Helper
  {
    private Java9() {}
    
    Package definePackage(ClassLoader loader, String name, String specTitle, String specVersion, String specVendor, String implTitle, String implVersion, String implVendor, URL sealBase) throws IllegalArgumentException {
      throw new RuntimeException("define package has been disabled for jigsaw");
    } }
  private static class Java7 extends Helper { private final SecurityActions stack; private final MethodHandle definePackage;
    
    private Java7() {
      this.stack = SecurityActions.stack;
      this.definePackage = getDefinePackageMethodHandle();
    }
    private MethodHandle getDefinePackageMethodHandle() {
      if (this.stack.getCallerClass() != getClass())
        throw new IllegalAccessError("Access denied for caller."); 
      try {
        return SecurityActions.getMethodHandle(ClassLoader.class, "definePackage", new Class[] { String.class, String.class, String.class, String.class, String.class, String.class, String.class, URL.class });


      
      }
      catch (NoSuchMethodException e) {
        throw new RuntimeException("cannot initialize", e);
      } 
    }





    
    Package definePackage(ClassLoader loader, String name, String specTitle, String specVersion, String specVendor, String implTitle, String implVersion, String implVendor, URL sealBase) throws IllegalArgumentException {
      if (this.stack.getCallerClass() != DefinePackageHelper.class)
        throw new IllegalAccessError("Access denied for caller."); 
      try {
        return (Package)this.definePackage.invokeWithArguments(new Object[] { loader, name, specTitle, specVersion, specVendor, implTitle, implVersion, implVendor, sealBase });
      }
      catch (Throwable e) {
        if (e instanceof IllegalArgumentException) throw (IllegalArgumentException)e; 
        if (e instanceof RuntimeException) throw (RuntimeException)e;
        
        return null;
      } 
    } }
  private static class JavaOther extends Helper { private final SecurityActions stack;
    private JavaOther() {
      this.stack = SecurityActions.stack;
      this.definePackage = getDefinePackageMethod();
    } private final Method definePackage;
    private Method getDefinePackageMethod() {
      if (this.stack.getCallerClass() != getClass())
        throw new IllegalAccessError("Access denied for caller."); 
      try {
        return SecurityActions.getDeclaredMethod(ClassLoader.class, "definePackage", new Class[] { String.class, String.class, String.class, String.class, String.class, String.class, String.class, URL.class });


      
      }
      catch (NoSuchMethodException e) {
        throw new RuntimeException("cannot initialize", e);
      } 
    }





    
    Package definePackage(ClassLoader loader, String name, String specTitle, String specVersion, String specVendor, String implTitle, String implVersion, String implVendor, URL sealBase) throws IllegalArgumentException {
      if (this.stack.getCallerClass() != DefinePackageHelper.class)
        throw new IllegalAccessError("Access denied for caller."); 
      try {
        this.definePackage.setAccessible(true);
        return (Package)this.definePackage.invoke(loader, new Object[] { name, specTitle, specVersion, specVendor, implTitle, implVersion, implVendor, sealBase });

      
      }
      catch (Throwable e) {
        if (e instanceof InvocationTargetException) {
          Throwable t = ((InvocationTargetException)e).getTargetException();
          if (t instanceof IllegalArgumentException)
            throw (IllegalArgumentException)t; 
        } 
        if (e instanceof RuntimeException) throw (RuntimeException)e;
        
        return null;
      } 
    } }

  
  private static final Helper privileged = (ClassFile.MAJOR_VERSION >= 53) ? 
    new Java9() : ((ClassFile.MAJOR_VERSION >= 51) ? 
    new Java7() : new JavaOther());























  
  public static void definePackage(String className, ClassLoader loader) throws CannotCompileException {
    try {
      privileged.definePackage(loader, className, null, null, null, null, null, null, null);
    
    }
    catch (IllegalArgumentException e) {

      
      return;
    }
    catch (Exception e) {
      throw new CannotCompileException(e);
    } 
  }
}
