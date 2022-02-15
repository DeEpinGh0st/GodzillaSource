package org.apache.log4j.config;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.Properties;
import org.apache.log4j.Level;
import org.apache.log4j.Priority;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.spi.ErrorHandler;
import org.apache.log4j.spi.OptionHandler;


















































public class PropertySetter
{
  protected Object obj;
  protected PropertyDescriptor[] props;
  
  public PropertySetter(Object obj) {
    this.obj = obj;
  }





  
  protected void introspect() {
    try {
      BeanInfo bi = Introspector.getBeanInfo(this.obj.getClass());
      this.props = bi.getPropertyDescriptors();
    } catch (IntrospectionException ex) {
      LogLog.error("Failed to introspect " + this.obj + ": " + ex.getMessage());
      this.props = new PropertyDescriptor[0];
    } 
  }












  
  public static void setProperties(Object obj, Properties properties, String prefix) {
    (new PropertySetter(obj)).setProperties(properties, prefix);
  }








  
  public void setProperties(Properties properties, String prefix) {
    int len = prefix.length();
    
    for (Enumeration e = properties.propertyNames(); e.hasMoreElements(); ) {
      String key = (String)e.nextElement();

      
      if (key.startsWith(prefix)) {


        
        if (key.indexOf('.', len + 1) > 0) {
          continue;
        }


        
        String value = OptionConverter.findAndSubst(key, properties);
        key = key.substring(len);
        if (("layout".equals(key) || "errorhandler".equals(key)) && this.obj instanceof org.apache.log4j.Appender) {
          continue;
        }


        
        PropertyDescriptor prop = getPropertyDescriptor(Introspector.decapitalize(key));
        if (prop != null && OptionHandler.class.isAssignableFrom(prop.getPropertyType()) && prop.getWriteMethod() != null) {

          
          OptionHandler opt = (OptionHandler)OptionConverter.instantiateByKey(properties, prefix + key, prop.getPropertyType(), null);


          
          PropertySetter setter = new PropertySetter(opt);
          setter.setProperties(properties, prefix + key + ".");
          try {
            prop.getWriteMethod().invoke(this.obj, new Object[] { opt });
          } catch (IllegalAccessException ex) {
            LogLog.warn("Failed to set property [" + key + "] to value \"" + value + "\". ", ex);
          }
          catch (InvocationTargetException ex) {
            if (ex.getTargetException() instanceof InterruptedException || ex.getTargetException() instanceof java.io.InterruptedIOException)
            {
              Thread.currentThread().interrupt();
            }
            LogLog.warn("Failed to set property [" + key + "] to value \"" + value + "\". ", ex);
          }
          catch (RuntimeException ex) {
            LogLog.warn("Failed to set property [" + key + "] to value \"" + value + "\". ", ex);
          } 
          
          continue;
        } 
        
        setProperty(key, value);
      } 
    } 
    activate();
  }
















  
  public void setProperty(String name, String value) {
    if (value == null)
      return; 
    name = Introspector.decapitalize(name);
    PropertyDescriptor prop = getPropertyDescriptor(name);


    
    if (prop == null) {
      LogLog.warn("No such property [" + name + "] in " + this.obj.getClass().getName() + ".");
    } else {
      
      try {
        setProperty(prop, name, value);
      } catch (PropertySetterException ex) {
        LogLog.warn("Failed to set property [" + name + "] to value \"" + value + "\". ", ex.rootCause);
      } 
    } 
  }










  
  public void setProperty(PropertyDescriptor prop, String name, String value) throws PropertySetterException {
    Object arg;
    Method setter = prop.getWriteMethod();
    if (setter == null) {
      throw new PropertySetterException("No setter for property [" + name + "].");
    }
    Class[] paramTypes = setter.getParameterTypes();
    if (paramTypes.length != 1) {
      throw new PropertySetterException("#params for setter != 1");
    }

    
    try {
      arg = convertArg(value, paramTypes[0]);
    } catch (Throwable t) {
      throw new PropertySetterException("Conversion to type [" + paramTypes[0] + "] failed. Reason: " + t);
    } 
    
    if (arg == null) {
      throw new PropertySetterException("Conversion to type [" + paramTypes[0] + "] failed.");
    }
    
    LogLog.debug("Setting property [" + name + "] to [" + arg + "].");
    try {
      setter.invoke(this.obj, new Object[] { arg });
    } catch (IllegalAccessException ex) {
      throw new PropertySetterException(ex);
    } catch (InvocationTargetException ex) {
      if (ex.getTargetException() instanceof InterruptedException || ex.getTargetException() instanceof java.io.InterruptedIOException)
      {
        Thread.currentThread().interrupt();
      }
      throw new PropertySetterException(ex);
    } catch (RuntimeException ex) {
      throw new PropertySetterException(ex);
    } 
  }






  
  protected Object convertArg(String val, Class type) {
    if (val == null) {
      return null;
    }
    String v = val.trim();
    if (String.class.isAssignableFrom(type))
      return val; 
    if (int.class.isAssignableFrom(type))
      return new Integer(v); 
    if (long.class.isAssignableFrom(type))
      return new Long(v); 
    if (boolean.class.isAssignableFrom(type)) {
      if ("true".equalsIgnoreCase(v))
        return Boolean.TRUE; 
      if ("false".equalsIgnoreCase(v))
        return Boolean.FALSE; 
    } else {
      if (Priority.class.isAssignableFrom(type))
        return OptionConverter.toLevel(v, Level.DEBUG); 
      if (ErrorHandler.class.isAssignableFrom(type)) {
        return OptionConverter.instantiateByClassName(v, ErrorHandler.class, null);
      }
    } 
    return null;
  }


  
  protected PropertyDescriptor getPropertyDescriptor(String name) {
    if (this.props == null) introspect();
    
    for (int i = 0; i < this.props.length; i++) {
      if (name.equals(this.props[i].getName())) {
        return this.props[i];
      }
    } 
    return null;
  }

  
  public void activate() {
    if (this.obj instanceof OptionHandler)
      ((OptionHandler)this.obj).activateOptions(); 
  }
}
