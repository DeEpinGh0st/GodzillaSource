package org.springframework.cglib.beans;

import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.asm.ClassVisitor;
import org.springframework.cglib.core.AbstractClassGenerator;
import org.springframework.cglib.core.KeyFactory;
import org.springframework.cglib.core.ReflectUtils;





























public abstract class BeanMap
  implements Map
{
  public static final int REQUIRE_GETTER = 1;
  public static final int REQUIRE_SETTER = 2;
  protected Object bean;
  
  public abstract BeanMap newInstance(Object paramObject);
  
  public abstract Class getPropertyType(String paramString);
  
  public static BeanMap create(Object bean) {
    Generator gen = new Generator();
    gen.setBean(bean);
    return gen.create();
  }
  
  public static class Generator extends AbstractClassGenerator {
    private static final AbstractClassGenerator.Source SOURCE = new AbstractClassGenerator.Source(BeanMap.class.getName());

    
    private static final BeanMapKey KEY_FACTORY = (BeanMapKey)KeyFactory.create(BeanMapKey.class, KeyFactory.CLASS_BY_NAME);

    
    private Object bean;
    
    private Class beanClass;
    
    private int require;

    
    public Generator() {
      super(SOURCE);
    }







    
    public void setBean(Object bean) {
      this.bean = bean;
      if (bean != null) {
        this.beanClass = bean.getClass();
      }
    }




    
    public void setBeanClass(Class beanClass) {
      this.beanClass = beanClass;
    }





    
    public void setRequire(int require) {
      this.require = require;
    }
    
    protected ClassLoader getDefaultClassLoader() {
      return this.beanClass.getClassLoader();
    }
    
    protected ProtectionDomain getProtectionDomain() {
      return ReflectUtils.getProtectionDomain(this.beanClass);
    }




    
    public BeanMap create() {
      if (this.beanClass == null)
        throw new IllegalArgumentException("Class of bean unknown"); 
      setNamePrefix(this.beanClass.getName());
      return (BeanMap)create(KEY_FACTORY.newInstance(this.beanClass, this.require));
    }
    
    public void generateClass(ClassVisitor v) throws Exception {
      new BeanMapEmitter(v, getClassName(), this.beanClass, this.require);
    }
    
    protected Object firstInstance(Class type) {
      return ((BeanMap)ReflectUtils.newInstance(type)).newInstance(this.bean);
    }
    
    protected Object nextInstance(Object instance) {
      return ((BeanMap)instance).newInstance(this.bean);
    }





    
    static interface BeanMapKey
    {
      Object newInstance(Class param2Class, int param2Int);
    }
  }




  
  protected BeanMap() {}




  
  protected BeanMap(Object bean) {
    setBean(bean);
  }
  
  public Object get(Object key) {
    return get(this.bean, key);
  }
  
  public Object put(Object key, Object value) {
    return put(this.bean, key, value);
  }







  
  public abstract Object get(Object paramObject1, Object paramObject2);







  
  public abstract Object put(Object paramObject1, Object paramObject2, Object paramObject3);







  
  public void setBean(Object bean) {
    this.bean = bean;
  }





  
  public Object getBean() {
    return this.bean;
  }
  
  public void clear() {
    throw new UnsupportedOperationException();
  }
  
  public boolean containsKey(Object key) {
    return keySet().contains(key);
  }
  
  public boolean containsValue(Object value) {
    for (Iterator<K> it = keySet().iterator(); it.hasNext(); ) {
      Object v = get(it.next());
      if ((value == null && v == null) || (value != null && value.equals(v)))
        return true; 
    } 
    return false;
  }
  
  public int size() {
    return keySet().size();
  }
  
  public boolean isEmpty() {
    return (size() == 0);
  }
  
  public Object remove(Object key) {
    throw new UnsupportedOperationException();
  }
  
  public void putAll(Map t) {
    for (Iterator it = t.keySet().iterator(); it.hasNext(); ) {
      Object key = it.next();
      put(key, t.get(key));
    } 
  }
  
  public boolean equals(Object o) {
    if (o == null || !(o instanceof Map)) {
      return false;
    }
    Map other = (Map)o;
    if (size() != other.size()) {
      return false;
    }
    for (Iterator<K> it = keySet().iterator(); it.hasNext(); ) {
      Object key = it.next();
      if (!other.containsKey(key)) {
        return false;
      }
      Object v1 = get(key);
      Object v2 = other.get(key);
      if ((v1 == null) ? (v2 == null) : v1.equals(v2))
        continue;  return false;
    } 
    
    return true;
  }
  
  public int hashCode() {
    int code = 0;
    for (Iterator<K> it = keySet().iterator(); it.hasNext(); ) {
      Object key = it.next();
      Object value = get(key);
      code += ((key == null) ? 0 : key.hashCode()) ^ ((value == null) ? 0 : value
        .hashCode());
    } 
    return code;
  }

  
  public Set entrySet() {
    HashMap<Object, Object> copy = new HashMap<Object, Object>();
    for (Iterator<K> it = keySet().iterator(); it.hasNext(); ) {
      Object key = it.next();
      copy.put(key, get(key));
    } 
    return Collections.<Object, Object>unmodifiableMap(copy).entrySet();
  }
  
  public Collection values() {
    Set<K> keys = keySet();
    List<Object> values = new ArrayList(keys.size());
    for (Iterator<K> it = keys.iterator(); it.hasNext();) {
      values.add(get(it.next()));
    }
    return Collections.unmodifiableCollection(values);
  }




  
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append('{');
    for (Iterator<K> it = keySet().iterator(); it.hasNext(); ) {
      Object key = it.next();
      sb.append(key);
      sb.append('=');
      sb.append(get(key));
      if (it.hasNext()) {
        sb.append(", ");
      }
    } 
    sb.append('}');
    return sb.toString();
  }
}
