package org.springframework.cglib.beans;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Modifier;
import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.Map;
import org.springframework.asm.ClassVisitor;
import org.springframework.asm.Type;
import org.springframework.cglib.core.AbstractClassGenerator;
import org.springframework.cglib.core.ClassEmitter;
import org.springframework.cglib.core.CodeEmitter;
import org.springframework.cglib.core.Constants;
import org.springframework.cglib.core.Converter;
import org.springframework.cglib.core.EmitUtils;
import org.springframework.cglib.core.KeyFactory;
import org.springframework.cglib.core.Local;
import org.springframework.cglib.core.MethodInfo;
import org.springframework.cglib.core.ReflectUtils;
import org.springframework.cglib.core.Signature;
import org.springframework.cglib.core.TypeUtils;








public abstract class BeanCopier
{
  private static final BeanCopierKey KEY_FACTORY = (BeanCopierKey)KeyFactory.create(BeanCopierKey.class);
  
  private static final Type CONVERTER = TypeUtils.parseType("org.springframework.cglib.core.Converter");
  
  private static final Type BEAN_COPIER = TypeUtils.parseType("org.springframework.cglib.beans.BeanCopier");
  private static final Signature COPY = new Signature("copy", Type.VOID_TYPE, new Type[] { Constants.TYPE_OBJECT, Constants.TYPE_OBJECT, CONVERTER });

  
  private static final Signature CONVERT = TypeUtils.parseSignature("Object convert(Object, Class, Object)");

  
  public abstract void copy(Object paramObject1, Object paramObject2, Converter paramConverter);

  
  public static BeanCopier create(Class source, Class target, boolean useConverter) {
    Generator gen = new Generator();
    gen.setSource(source);
    gen.setTarget(target);
    gen.setUseConverter(useConverter);
    return gen.create();
  }
  
  public static class Generator
    extends AbstractClassGenerator
  {
    private static final AbstractClassGenerator.Source SOURCE = new AbstractClassGenerator.Source(BeanCopier.class.getName());
    private Class source;
    private Class target;
    private boolean useConverter;
    
    public Generator() {
      super(SOURCE);
    }
    
    public void setSource(Class source) {
      if (!Modifier.isPublic(source.getModifiers())) {
        setNamePrefix(source.getName());
      }
      this.source = source;
    }
    
    public void setTarget(Class target) {
      if (!Modifier.isPublic(target.getModifiers())) {
        setNamePrefix(target.getName());
      }
      
      this.target = target;
    }
    
    public void setUseConverter(boolean useConverter) {
      this.useConverter = useConverter;
    }
    
    protected ClassLoader getDefaultClassLoader() {
      return this.source.getClassLoader();
    }
    
    protected ProtectionDomain getProtectionDomain() {
      return ReflectUtils.getProtectionDomain(this.source);
    }
    
    public BeanCopier create() {
      Object key = BeanCopier.KEY_FACTORY.newInstance(this.source.getName(), this.target.getName(), this.useConverter);
      return (BeanCopier)create(key);
    }
    
    public void generateClass(ClassVisitor v) {
      Type sourceType = Type.getType(this.source);
      Type targetType = Type.getType(this.target);
      ClassEmitter ce = new ClassEmitter(v);
      ce.begin_class(52, 1, 
          
          getClassName(), BeanCopier
          .BEAN_COPIER, null, "<generated>");


      
      EmitUtils.null_constructor(ce);
      CodeEmitter e = ce.begin_method(1, BeanCopier.COPY, null);
      PropertyDescriptor[] getters = ReflectUtils.getBeanGetters(this.source);
      PropertyDescriptor[] setters = ReflectUtils.getBeanSetters(this.target);
      
      Map<Object, Object> names = new HashMap<Object, Object>();
      for (int i = 0; i < getters.length; i++) {
        names.put(getters[i].getName(), getters[i]);
      }
      Local targetLocal = e.make_local();
      Local sourceLocal = e.make_local();
      if (this.useConverter) {
        e.load_arg(1);
        e.checkcast(targetType);
        e.store_local(targetLocal);
        e.load_arg(0);
        e.checkcast(sourceType);
        e.store_local(sourceLocal);
      } else {
        e.load_arg(1);
        e.checkcast(targetType);
        e.load_arg(0);
        e.checkcast(sourceType);
      } 
      for (int j = 0; j < setters.length; j++) {
        PropertyDescriptor setter = setters[j];
        PropertyDescriptor getter = (PropertyDescriptor)names.get(setter.getName());
        if (getter != null) {
          MethodInfo read = ReflectUtils.getMethodInfo(getter.getReadMethod());
          MethodInfo write = ReflectUtils.getMethodInfo(setter.getWriteMethod());
          if (this.useConverter) {
            Type setterType = write.getSignature().getArgumentTypes()[0];
            e.load_local(targetLocal);
            e.load_arg(2);
            e.load_local(sourceLocal);
            e.invoke(read);
            e.box(read.getSignature().getReturnType());
            EmitUtils.load_class(e, setterType);
            e.push(write.getSignature().getName());
            e.invoke_interface(BeanCopier.CONVERTER, BeanCopier.CONVERT);
            e.unbox_or_zero(setterType);
            e.invoke(write);
          } else if (compatible(getter, setter)) {
            e.dup2();
            e.invoke(read);
            e.invoke(write);
          } 
        } 
      } 
      e.return_value();
      e.end_method();
      ce.end_class();
    }

    
    private static boolean compatible(PropertyDescriptor getter, PropertyDescriptor setter) {
      return setter.getPropertyType().isAssignableFrom(getter.getPropertyType());
    }
    
    protected Object firstInstance(Class type) {
      return ReflectUtils.newInstance(type);
    }
    
    protected Object nextInstance(Object instance) {
      return instance;
    }
  }
  
  static interface BeanCopierKey {
    Object newInstance(String param1String1, String param1String2, boolean param1Boolean);
  }
}
