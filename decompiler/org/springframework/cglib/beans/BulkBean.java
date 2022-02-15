package org.springframework.cglib.beans;

import java.security.ProtectionDomain;
import org.springframework.asm.ClassVisitor;
import org.springframework.cglib.core.AbstractClassGenerator;
import org.springframework.cglib.core.KeyFactory;
import org.springframework.cglib.core.ReflectUtils;






















public abstract class BulkBean
{
  private static final BulkBeanKey KEY_FACTORY = (BulkBeanKey)KeyFactory.create(BulkBeanKey.class);

  
  protected Class target;

  
  protected String[] getters;

  
  protected String[] setters;

  
  protected Class[] types;

  
  public Object[] getPropertyValues(Object bean) {
    Object[] values = new Object[this.getters.length];
    getPropertyValues(bean, values);
    return values;
  }
  
  public Class[] getPropertyTypes() {
    return (Class[])this.types.clone();
  }
  
  public String[] getGetters() {
    return (String[])this.getters.clone();
  }
  
  public String[] getSetters() {
    return (String[])this.setters.clone();
  }
  public abstract void getPropertyValues(Object paramObject, Object[] paramArrayOfObject);
  public static BulkBean create(Class target, String[] getters, String[] setters, Class[] types) {
    Generator gen = new Generator();
    gen.setTarget(target);
    gen.setGetters(getters);
    gen.setSetters(setters);
    gen.setTypes(types);
    return gen.create();
  }
  public abstract void setPropertyValues(Object paramObject, Object[] paramArrayOfObject);
  
  public static class Generator extends AbstractClassGenerator { private static final AbstractClassGenerator.Source SOURCE = new AbstractClassGenerator.Source(BulkBean.class.getName());
    private Class target;
    private String[] getters;
    private String[] setters;
    private Class[] types;
    
    public Generator() {
      super(SOURCE);
    }
    
    public void setTarget(Class target) {
      this.target = target;
    }
    
    public void setGetters(String[] getters) {
      this.getters = getters;
    }
    
    public void setSetters(String[] setters) {
      this.setters = setters;
    }
    
    public void setTypes(Class[] types) {
      this.types = types;
    }
    
    protected ClassLoader getDefaultClassLoader() {
      return this.target.getClassLoader();
    }
    
    protected ProtectionDomain getProtectionDomain() {
      return ReflectUtils.getProtectionDomain(this.target);
    }
    
    public BulkBean create() {
      setNamePrefix(this.target.getName());
      String targetClassName = this.target.getName();
      String[] typeClassNames = ReflectUtils.getNames(this.types);
      Object key = BulkBean.KEY_FACTORY.newInstance(targetClassName, this.getters, this.setters, typeClassNames);
      return (BulkBean)create(key);
    }
    
    public void generateClass(ClassVisitor v) throws Exception {
      new BulkBeanEmitter(v, getClassName(), this.target, this.getters, this.setters, this.types);
    }
    
    protected Object firstInstance(Class type) {
      BulkBean instance = (BulkBean)ReflectUtils.newInstance(type);
      instance.target = this.target;
      
      int length = this.getters.length;
      instance.getters = new String[length];
      System.arraycopy(this.getters, 0, instance.getters, 0, length);
      
      instance.setters = new String[length];
      System.arraycopy(this.setters, 0, instance.setters, 0, length);
      
      instance.types = new Class[this.types.length];
      System.arraycopy(this.types, 0, instance.types, 0, this.types.length);
      
      return instance;
    }
    
    protected Object nextInstance(Object instance) {
      return instance;
    } }

  
  static interface BulkBeanKey {
    Object newInstance(String param1String, String[] param1ArrayOfString1, String[] param1ArrayOfString2, String[] param1ArrayOfString3);
  }
}
