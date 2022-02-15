package org.springframework.cglib.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.ProtectionDomain;
import org.springframework.asm.ClassVisitor;
import org.springframework.asm.Type;
import org.springframework.cglib.core.AbstractClassGenerator;
import org.springframework.cglib.core.Constants;
import org.springframework.cglib.core.ReflectUtils;
import org.springframework.cglib.core.Signature;













public abstract class FastClass
{
  private Class type;
  
  protected FastClass() {
    throw new Error("Using the FastClass empty constructor--please report to the cglib-devel mailing list");
  }
  
  protected FastClass(Class type) {
    this.type = type;
  }

  
  public static FastClass create(Class type) {
    return create(type.getClassLoader(), type);
  }
  
  public static FastClass create(ClassLoader loader, Class type) {
    Generator gen = new Generator();
    gen.setType(type);
    gen.setClassLoader(loader);
    return gen.create();
  }
  
  public static class Generator
    extends AbstractClassGenerator {
    private static final AbstractClassGenerator.Source SOURCE = new AbstractClassGenerator.Source(FastClass.class.getName());
    private Class type;
    
    public Generator() {
      super(SOURCE);
    }
    
    public void setType(Class type) {
      this.type = type;
    }
    
    public FastClass create() {
      setNamePrefix(this.type.getName());
      return (FastClass)create(this.type.getName());
    }
    
    protected ClassLoader getDefaultClassLoader() {
      return this.type.getClassLoader();
    }
    
    protected ProtectionDomain getProtectionDomain() {
      return ReflectUtils.getProtectionDomain(this.type);
    }
    
    public void generateClass(ClassVisitor v) throws Exception {
      new FastClassEmitter(v, getClassName(), this.type);
    }
    
    protected Object firstInstance(Class type) {
      return ReflectUtils.newInstance(type, new Class[] { Class.class }, new Object[] { this.type });
    }


    
    protected Object nextInstance(Object instance) {
      return instance;
    }
  }
  
  public Object invoke(String name, Class[] parameterTypes, Object obj, Object[] args) throws InvocationTargetException {
    return invoke(getIndex(name, parameterTypes), obj, args);
  }
  
  public Object newInstance() throws InvocationTargetException {
    return newInstance(getIndex(Constants.EMPTY_CLASS_ARRAY), (Object[])null);
  }
  
  public Object newInstance(Class[] parameterTypes, Object[] args) throws InvocationTargetException {
    return newInstance(getIndex(parameterTypes), args);
  }
  
  public FastMethod getMethod(Method method) {
    return new FastMethod(this, method);
  }
  
  public FastConstructor getConstructor(Constructor constructor) {
    return new FastConstructor(this, constructor);
  }
  
  public FastMethod getMethod(String name, Class[] parameterTypes) {
    try {
      return getMethod(this.type.getMethod(name, parameterTypes));
    } catch (NoSuchMethodException e) {
      throw new NoSuchMethodError(e.getMessage());
    } 
  }
  
  public FastConstructor getConstructor(Class[] parameterTypes) {
    try {
      return getConstructor(this.type.getConstructor(parameterTypes));
    } catch (NoSuchMethodException e) {
      throw new NoSuchMethodError(e.getMessage());
    } 
  }
  
  public String getName() {
    return this.type.getName();
  }
  
  public Class getJavaClass() {
    return this.type;
  }
  
  public String toString() {
    return this.type.toString();
  }
  
  public int hashCode() {
    return this.type.hashCode();
  }
  
  public boolean equals(Object o) {
    if (o == null || !(o instanceof FastClass)) {
      return false;
    }
    return this.type.equals(((FastClass)o).type);
  }





  
  public abstract int getIndex(String paramString, Class[] paramArrayOfClass);





  
  public abstract int getIndex(Class[] paramArrayOfClass);





  
  public abstract Object invoke(int paramInt, Object paramObject, Object[] paramArrayOfObject) throws InvocationTargetException;





  
  public abstract Object newInstance(int paramInt, Object[] paramArrayOfObject) throws InvocationTargetException;





  
  public abstract int getIndex(Signature paramSignature);





  
  public abstract int getMaxIndex();





  
  protected static String getSignatureWithoutReturnType(String name, Class[] parameterTypes) {
    StringBuffer sb = new StringBuffer();
    sb.append(name);
    sb.append('(');
    for (int i = 0; i < parameterTypes.length; i++) {
      sb.append(Type.getDescriptor(parameterTypes[i]));
    }
    sb.append(')');
    return sb.toString();
  }
}
