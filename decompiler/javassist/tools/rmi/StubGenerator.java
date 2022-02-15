package javassist.tools.rmi;

import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.Map;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewConstructor;
import javassist.CtNewMethod;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.Translator;

















































public class StubGenerator
  implements Translator
{
  private static final String fieldImporter = "importer";
  private static final String fieldObjectId = "objectId";
  private static final String accessorObjectId = "_getObjectId";
  private static final String sampleClass = "javassist.tools.rmi.Sample";
  private ClassPool classPool;
  private Map<String, CtClass> proxyClasses = new Hashtable<>();
  
  private CtMethod forwardMethod;
  
  private CtMethod forwardStaticMethod;
  
  private CtClass[] proxyConstructorParamTypes;
  private CtClass[] interfacesForProxy;
  private CtClass[] exceptionForProxy;
  
  public void start(ClassPool pool) throws NotFoundException {
    this.classPool = pool;
    CtClass c = pool.get("javassist.tools.rmi.Sample");
    this.forwardMethod = c.getDeclaredMethod("forward");
    this.forwardStaticMethod = c.getDeclaredMethod("forwardStatic");
    
    this
      .proxyConstructorParamTypes = pool.get(new String[] { "javassist.tools.rmi.ObjectImporter", "int" });
    
    this
      .interfacesForProxy = pool.get(new String[] { "java.io.Serializable", "javassist.tools.rmi.Proxy" });
    
    this
      .exceptionForProxy = new CtClass[] { pool.get("javassist.tools.rmi.RemoteException") };
  }






  
  public void onLoad(ClassPool pool, String classname) {}






  
  public boolean isProxyClass(String name) {
    return (this.proxyClasses.get(name) != null);
  }











  
  public synchronized boolean makeProxyClass(Class<?> clazz) throws CannotCompileException, NotFoundException {
    String classname = clazz.getName();
    if (this.proxyClasses.get(classname) != null)
      return false; 
    CtClass ctclazz = produceProxyClass(this.classPool.get(classname), clazz);
    
    this.proxyClasses.put(classname, ctclazz);
    modifySuperclass(ctclazz);
    return true;
  }


  
  private CtClass produceProxyClass(CtClass orgclass, Class<?> orgRtClass) throws CannotCompileException, NotFoundException {
    int modify = orgclass.getModifiers();
    if (Modifier.isAbstract(modify) || Modifier.isNative(modify) || 
      !Modifier.isPublic(modify)) {
      throw new CannotCompileException(orgclass.getName() + " must be public, non-native, and non-abstract.");
    }
    
    CtClass proxy = this.classPool.makeClass(orgclass.getName(), orgclass
        .getSuperclass());
    
    proxy.setInterfaces(this.interfacesForProxy);

    
    CtField f = new CtField(this.classPool.get("javassist.tools.rmi.ObjectImporter"), "importer", proxy);
    
    f.setModifiers(2);
    proxy.addField(f, CtField.Initializer.byParameter(0));
    
    f = new CtField(CtClass.intType, "objectId", proxy);
    f.setModifiers(2);
    proxy.addField(f, CtField.Initializer.byParameter(1));
    
    proxy.addMethod(CtNewMethod.getter("_getObjectId", f));
    
    proxy.addConstructor(CtNewConstructor.defaultConstructor(proxy));
    
    CtConstructor cons = CtNewConstructor.skeleton(this.proxyConstructorParamTypes, null, proxy);
    
    proxy.addConstructor(cons);
    
    try {
      addMethods(proxy, orgRtClass.getMethods());
      return proxy;
    }
    catch (SecurityException e) {
      throw new CannotCompileException(e);
    } 
  }
  
  private CtClass toCtClass(Class<?> rtclass) throws NotFoundException {
    String name;
    if (!rtclass.isArray())
    { name = rtclass.getName(); }
    else
    { StringBuffer sbuf = new StringBuffer();
      while (true)
      { sbuf.append("[]");
        rtclass = rtclass.getComponentType();
        if (!rtclass.isArray())
        { sbuf.insert(0, rtclass.getName());
          name = sbuf.toString();

          
          return this.classPool.get(name); }  }  }  return this.classPool.get(name);
  }
  
  private CtClass[] toCtClass(Class<?>[] rtclasses) throws NotFoundException {
    int n = rtclasses.length;
    CtClass[] ctclasses = new CtClass[n];
    for (int i = 0; i < n; i++) {
      ctclasses[i] = toCtClass(rtclasses[i]);
    }
    return ctclasses;
  }






  
  private void addMethods(CtClass proxy, Method[] ms) throws CannotCompileException, NotFoundException {
    for (int i = 0; i < ms.length; i++) {
      Method m = ms[i];
      int mod = m.getModifiers();
      if (m.getDeclaringClass() != Object.class && 
        !Modifier.isFinal(mod)) {
        if (Modifier.isPublic(mod)) {
          CtMethod body;
          if (Modifier.isStatic(mod)) {
            body = this.forwardStaticMethod;
          } else {
            body = this.forwardMethod;
          } 
          
          CtMethod wmethod = CtNewMethod.wrapped(toCtClass(m.getReturnType()), m
              .getName(), 
              toCtClass(m.getParameterTypes()), this.exceptionForProxy, body, 

              
              CtMethod.ConstParameter.integer(i), proxy);
          
          wmethod.setModifiers(mod);
          proxy.addMethod(wmethod);
        }
        else if (!Modifier.isProtected(mod) && 
          !Modifier.isPrivate(mod)) {
          
          throw new CannotCompileException("the methods must be public, protected, or private.");
        } 
      }
    } 
  }





  
  private void modifySuperclass(CtClass orgclass) throws CannotCompileException, NotFoundException {
    for (;; orgclass = superclazz) {
      CtClass superclazz = orgclass.getSuperclass();
      if (superclazz == null) {
        break;
      }
      try {
        superclazz.getDeclaredConstructor(null);
        
        break;
      } catch (NotFoundException notFoundException) {

        
        superclazz.addConstructor(
            CtNewConstructor.defaultConstructor(superclazz));
      } 
    } 
  }
}
