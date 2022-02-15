package org.springframework.cglib.beans;

import java.beans.PropertyDescriptor;
import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.springframework.asm.ClassVisitor;
import org.springframework.asm.Type;
import org.springframework.cglib.core.AbstractClassGenerator;
import org.springframework.cglib.core.ClassEmitter;
import org.springframework.cglib.core.Constants;
import org.springframework.cglib.core.EmitUtils;
import org.springframework.cglib.core.KeyFactory;
import org.springframework.cglib.core.ReflectUtils;











public class BeanGenerator
  extends AbstractClassGenerator
{
  private static final AbstractClassGenerator.Source SOURCE = new AbstractClassGenerator.Source(BeanGenerator.class.getName());
  
  private static final BeanGeneratorKey KEY_FACTORY = (BeanGeneratorKey)KeyFactory.create(BeanGeneratorKey.class);


  
  private Class superclass;

  
  private Map props = new HashMap<Object, Object>();
  private boolean classOnly;
  
  public BeanGenerator() {
    super(SOURCE);
  }






  
  public void setSuperclass(Class superclass) {
    if (superclass != null && superclass.equals(Object.class)) {
      superclass = null;
    }
    this.superclass = superclass;
  }
  
  public void addProperty(String name, Class type) {
    if (this.props.containsKey(name)) {
      throw new IllegalArgumentException("Duplicate property name \"" + name + "\"");
    }
    this.props.put(name, Type.getType(type));
  }
  
  protected ClassLoader getDefaultClassLoader() {
    if (this.superclass != null) {
      return this.superclass.getClassLoader();
    }
    return null;
  }

  
  protected ProtectionDomain getProtectionDomain() {
    return ReflectUtils.getProtectionDomain(this.superclass);
  }
  
  public Object create() {
    this.classOnly = false;
    return createHelper();
  }
  
  public Object createClass() {
    this.classOnly = true;
    return createHelper();
  }
  
  private Object createHelper() {
    if (this.superclass != null) {
      setNamePrefix(this.superclass.getName());
    }
    String superName = (this.superclass != null) ? this.superclass.getName() : "java.lang.Object";
    Object key = KEY_FACTORY.newInstance(superName, this.props);
    return create(key);
  }
  
  public void generateClass(ClassVisitor v) throws Exception {
    int size = this.props.size();
    String[] names = (String[])this.props.keySet().toArray((Object[])new String[size]);
    Type[] types = new Type[size];
    for (int i = 0; i < size; i++) {
      types[i] = (Type)this.props.get(names[i]);
    }
    ClassEmitter ce = new ClassEmitter(v);
    ce.begin_class(52, 1, 
        
        getClassName(), (this.superclass != null) ? 
        Type.getType(this.superclass) : Constants.TYPE_OBJECT, null, null);

    
    EmitUtils.null_constructor(ce);
    EmitUtils.add_properties(ce, names, types);
    ce.end_class();
  }
  
  protected Object firstInstance(Class type) {
    if (this.classOnly) {
      return type;
    }
    return ReflectUtils.newInstance(type);
  }

  
  protected Object nextInstance(Object instance) {
    Class protoclass = (instance instanceof Class) ? (Class)instance : instance.getClass();
    if (this.classOnly) {
      return protoclass;
    }
    return ReflectUtils.newInstance(protoclass);
  }

  
  public static void addProperties(BeanGenerator gen, Map props) {
    for (Iterator<String> it = props.keySet().iterator(); it.hasNext(); ) {
      String name = it.next();
      gen.addProperty(name, (Class)props.get(name));
    } 
  }
  
  public static void addProperties(BeanGenerator gen, Class type) {
    addProperties(gen, ReflectUtils.getBeanProperties(type));
  }
  
  public static void addProperties(BeanGenerator gen, PropertyDescriptor[] descriptors) {
    for (int i = 0; i < descriptors.length; i++)
      gen.addProperty(descriptors[i].getName(), descriptors[i].getPropertyType()); 
  }
  
  static interface BeanGeneratorKey {
    Object newInstance(String param1String, Map param1Map);
  }
}
