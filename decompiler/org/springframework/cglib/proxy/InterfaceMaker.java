package org.springframework.cglib.proxy;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.springframework.asm.ClassVisitor;
import org.springframework.asm.Type;
import org.springframework.cglib.core.AbstractClassGenerator;
import org.springframework.cglib.core.ClassEmitter;
import org.springframework.cglib.core.ReflectUtils;
import org.springframework.cglib.core.Signature;


















public class InterfaceMaker
  extends AbstractClassGenerator
{
  private static final AbstractClassGenerator.Source SOURCE = new AbstractClassGenerator.Source(InterfaceMaker.class.getName());
  private Map signatures = new HashMap<Object, Object>();





  
  public InterfaceMaker() {
    super(SOURCE);
  }





  
  public void add(Signature sig, Type[] exceptions) {
    this.signatures.put(sig, exceptions);
  }





  
  public void add(Method method) {
    add(ReflectUtils.getSignature(method), 
        ReflectUtils.getExceptionTypes(method));
  }






  
  public void add(Class clazz) {
    Method[] methods = clazz.getMethods();
    for (int i = 0; i < methods.length; i++) {
      Method m = methods[i];
      if (!m.getDeclaringClass().getName().equals("java.lang.Object")) {
        add(m);
      }
    } 
  }



  
  public Class create() {
    setUseCache(false);
    return (Class)create(this);
  }
  
  protected ClassLoader getDefaultClassLoader() {
    return null;
  }
  
  protected Object firstInstance(Class type) {
    return type;
  }
  
  protected Object nextInstance(Object instance) {
    throw new IllegalStateException("InterfaceMaker does not cache");
  }
  
  public void generateClass(ClassVisitor v) throws Exception {
    ClassEmitter ce = new ClassEmitter(v);
    ce.begin_class(52, 1537, 
        
        getClassName(), null, null, "<generated>");


    
    for (Iterator<Signature> it = this.signatures.keySet().iterator(); it.hasNext(); ) {
      Signature sig = it.next();
      Type[] exceptions = (Type[])this.signatures.get(sig);
      ce.begin_method(1025, sig, exceptions)
        
        .end_method();
    } 
    ce.end_class();
  }
}
