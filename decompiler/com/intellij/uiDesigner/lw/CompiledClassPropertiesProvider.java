package com.intellij.uiDesigner.lw;

import com.intellij.uiDesigner.compiler.Utils;
import java.awt.Component;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.HashMap;
import javax.swing.ListModel;



















public final class CompiledClassPropertiesProvider
  implements PropertiesProvider
{
  private final ClassLoader myLoader;
  private final HashMap myCache;
  
  public CompiledClassPropertiesProvider(ClassLoader loader) {
    if (loader == null) {
      throw new IllegalArgumentException("loader cannot be null");
    }
    this.myLoader = loader;
    this.myCache = new HashMap();
  } public HashMap getLwProperties(String className) {
    Class aClass;
    BeanInfo beanInfo;
    if (this.myCache.containsKey(className)) {
      return (HashMap)this.myCache.get(className);
    }
    
    if (Utils.validateJComponentClass(this.myLoader, className, false) != null) {
      return null;
    }

    
    try {
      aClass = Class.forName(className, false, this.myLoader);
    }
    catch (ClassNotFoundException exc) {
      throw new RuntimeException(exc.toString());
    } 

    
    try {
      beanInfo = Introspector.getBeanInfo(aClass);
    }
    catch (Throwable e) {
      return null;
    } 
    
    HashMap result = new HashMap();
    PropertyDescriptor[] descriptors = beanInfo.getPropertyDescriptors();
    for (int i = 0; i < descriptors.length; i++) {
      PropertyDescriptor descriptor = descriptors[i];
      
      Method readMethod = descriptor.getReadMethod();
      Method writeMethod = descriptor.getWriteMethod();
      if (writeMethod != null && readMethod != null) {


        
        String name = descriptor.getName();
        
        LwIntrospectedProperty property = propertyFromClass(descriptor.getPropertyType(), name);
        
        if (property != null) {
          property.setDeclaringClassName(descriptor.getReadMethod().getDeclaringClass().getName());
          result.put(name, property);
        } 
      } 
    } 
    this.myCache.put(className, result);
    
    return result;
  }
  
  public static LwIntrospectedProperty propertyFromClass(Class propertyType, String name) {
    LwIntrospectedProperty property = propertyFromClassName(propertyType.getName(), name);
    if (property == null) {
      if (Component.class.isAssignableFrom(propertyType)) {
        property = new LwIntroComponentProperty(name, propertyType.getName());
      }
      else if (ListModel.class.isAssignableFrom(propertyType)) {
        property = new LwIntroListModelProperty(name, propertyType.getName());
      }
      else if (propertyType.getSuperclass() != null && "java.lang.Enum".equals(propertyType.getSuperclass().getName())) {
        property = new LwIntroEnumProperty(name, propertyType);
      } 
    }
    return property;
  }
  
  public static LwIntrospectedProperty propertyFromClassName(String propertyClassName, String name) {
    LwIntrospectedProperty property;
    if (int.class.getName().equals(propertyClassName)) {
      property = new LwIntroIntProperty(name);
    }
    else if (boolean.class.getName().equals(propertyClassName)) {
      property = new LwIntroBooleanProperty(name);
    }
    else if (double.class.getName().equals(propertyClassName)) {
      property = new LwIntroPrimitiveTypeProperty(name, Double.class);
    }
    else if (float.class.getName().equals(propertyClassName)) {
      property = new LwIntroPrimitiveTypeProperty(name, Float.class);
    }
    else if (long.class.getName().equals(propertyClassName)) {
      property = new LwIntroPrimitiveTypeProperty(name, Long.class);
    }
    else if (byte.class.getName().equals(propertyClassName)) {
      property = new LwIntroPrimitiveTypeProperty(name, Byte.class);
    }
    else if (short.class.getName().equals(propertyClassName)) {
      property = new LwIntroPrimitiveTypeProperty(name, Short.class);
    }
    else if (char.class.getName().equals(propertyClassName)) {
      property = new LwIntroCharProperty(name);
    }
    else if (String.class.getName().equals(propertyClassName)) {
      property = new LwRbIntroStringProperty(name);
    }
    else if ("java.awt.Insets".equals(propertyClassName)) {
      property = new LwIntroInsetsProperty(name);
    }
    else if ("java.awt.Dimension".equals(propertyClassName)) {
      property = new LwIntroDimensionProperty(name);
    }
    else if ("java.awt.Rectangle".equals(propertyClassName)) {
      property = new LwIntroRectangleProperty(name);
    }
    else if ("java.awt.Color".equals(propertyClassName)) {
      property = new LwIntroColorProperty(name);
    }
    else if ("java.awt.Font".equals(propertyClassName)) {
      property = new LwIntroFontProperty(name);
    }
    else if ("javax.swing.Icon".equals(propertyClassName)) {
      property = new LwIntroIconProperty(name);
    } else {
      
      property = null;
    } 
    return property;
  }
}
