package org.apache.log4j.or;

import java.util.Hashtable;
import org.apache.log4j.helpers.Loader;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.spi.RendererSupport;

























public class RendererMap
{
  static ObjectRenderer defaultRenderer = new DefaultRenderer();


  
  Hashtable map = new Hashtable();







  
  public static void addRenderer(RendererSupport repository, String renderedClassName, String renderingClassName) {
    LogLog.debug("Rendering class: [" + renderingClassName + "], Rendered class: [" + renderedClassName + "].");
    
    ObjectRenderer renderer = (ObjectRenderer)OptionConverter.instantiateByClassName(renderingClassName, ObjectRenderer.class, null);


    
    if (renderer == null) {
      LogLog.error("Could not instantiate renderer [" + renderingClassName + "].");
      return;
    } 
    try {
      Class renderedClass = Loader.loadClass(renderedClassName);
      repository.setRenderer(renderedClass, renderer);
    } catch (ClassNotFoundException e) {
      LogLog.error("Could not find class [" + renderedClassName + "].", e);
    } 
  }









  
  public String findAndRender(Object o) {
    if (o == null) {
      return null;
    }
    return get(o.getClass()).doRender(o);
  }





  
  public ObjectRenderer get(Object o) {
    if (o == null) {
      return null;
    }
    return get(o.getClass());
  }




















































  
  public ObjectRenderer get(Class clazz) {
    ObjectRenderer r = null;
    for (Class c = clazz; c != null; c = c.getSuperclass()) {
      
      r = (ObjectRenderer)this.map.get(c);
      if (r != null) {
        return r;
      }
      r = searchInterfaces(c);
      if (r != null)
        return r; 
    } 
    return defaultRenderer;
  }


  
  ObjectRenderer searchInterfaces(Class c) {
    ObjectRenderer r = (ObjectRenderer)this.map.get(c);
    if (r != null) {
      return r;
    }
    Class[] ia = c.getInterfaces();
    for (int i = 0; i < ia.length; i++) {
      r = searchInterfaces(ia[i]);
      if (r != null) {
        return r;
      }
    } 
    return null;
  }


  
  public ObjectRenderer getDefaultRenderer() {
    return defaultRenderer;
  }


  
  public void clear() {
    this.map.clear();
  }




  
  public void put(Class clazz, ObjectRenderer or) {
    this.map.put(clazz, or);
  }
}
