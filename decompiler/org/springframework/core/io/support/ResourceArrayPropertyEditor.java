package org.springframework.core.io.support;

import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.env.PropertyResolver;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;








































public class ResourceArrayPropertyEditor
  extends PropertyEditorSupport
{
  private static final Log logger = LogFactory.getLog(ResourceArrayPropertyEditor.class);


  
  private final ResourcePatternResolver resourcePatternResolver;


  
  @Nullable
  private PropertyResolver propertyResolver;

  
  private final boolean ignoreUnresolvablePlaceholders;


  
  public ResourceArrayPropertyEditor() {
    this(new PathMatchingResourcePatternResolver(), null, true);
  }








  
  public ResourceArrayPropertyEditor(ResourcePatternResolver resourcePatternResolver, @Nullable PropertyResolver propertyResolver) {
    this(resourcePatternResolver, propertyResolver, true);
  }










  
  public ResourceArrayPropertyEditor(ResourcePatternResolver resourcePatternResolver, @Nullable PropertyResolver propertyResolver, boolean ignoreUnresolvablePlaceholders) {
    Assert.notNull(resourcePatternResolver, "ResourcePatternResolver must not be null");
    this.resourcePatternResolver = resourcePatternResolver;
    this.propertyResolver = propertyResolver;
    this.ignoreUnresolvablePlaceholders = ignoreUnresolvablePlaceholders;
  }





  
  public void setAsText(String text) {
    String pattern = resolvePath(text).trim();
    try {
      setValue(this.resourcePatternResolver.getResources(pattern));
    }
    catch (IOException ex) {
      throw new IllegalArgumentException("Could not resolve resource location pattern [" + pattern + "]: " + ex
          .getMessage());
    } 
  }





  
  public void setValue(Object value) throws IllegalArgumentException {
    if (value instanceof Collection || (value instanceof Object[] && !(value instanceof Resource[]))) {
      Collection<?> input = (value instanceof Collection) ? (Collection)value : Arrays.asList((Object[])value);
      Set<Resource> merged = new LinkedHashSet<>();
      for (Object element : input) {
        if (element instanceof String) {

          
          String pattern = resolvePath((String)element).trim();
          try {
            Resource[] resources = this.resourcePatternResolver.getResources(pattern);
            Collections.addAll(merged, resources);
          }
          catch (IOException ex) {
            
            if (logger.isDebugEnabled())
              logger.debug("Could not retrieve resources for pattern '" + pattern + "'", ex); 
          } 
          continue;
        } 
        if (element instanceof Resource) {
          
          merged.add((Resource)element);
          continue;
        } 
        throw new IllegalArgumentException("Cannot convert element [" + element + "] to [" + Resource.class
            .getName() + "]: only location String and Resource object supported");
      } 
      
      super.setValue(merged.toArray(new Resource[0]));
    
    }
    else {

      
      super.setValue(value);
    } 
  }








  
  protected String resolvePath(String path) {
    if (this.propertyResolver == null) {
      this.propertyResolver = (PropertyResolver)new StandardEnvironment();
    }
    return this.ignoreUnresolvablePlaceholders ? this.propertyResolver.resolvePlaceholders(path) : this.propertyResolver
      .resolveRequiredPlaceholders(path);
  }
}
