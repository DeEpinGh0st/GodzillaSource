package org.springframework.core.io;

import java.beans.PropertyEditorSupport;
import java.io.IOException;
import org.springframework.core.env.PropertyResolver;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;














































public class ResourceEditor
  extends PropertyEditorSupport
{
  private final ResourceLoader resourceLoader;
  @Nullable
  private PropertyResolver propertyResolver;
  private final boolean ignoreUnresolvablePlaceholders;
  
  public ResourceEditor() {
    this(new DefaultResourceLoader(), (PropertyResolver)null);
  }






  
  public ResourceEditor(ResourceLoader resourceLoader, @Nullable PropertyResolver propertyResolver) {
    this(resourceLoader, propertyResolver, true);
  }










  
  public ResourceEditor(ResourceLoader resourceLoader, @Nullable PropertyResolver propertyResolver, boolean ignoreUnresolvablePlaceholders) {
    Assert.notNull(resourceLoader, "ResourceLoader must not be null");
    this.resourceLoader = resourceLoader;
    this.propertyResolver = propertyResolver;
    this.ignoreUnresolvablePlaceholders = ignoreUnresolvablePlaceholders;
  }


  
  public void setAsText(String text) {
    if (StringUtils.hasText(text)) {
      String locationToUse = resolvePath(text).trim();
      setValue(this.resourceLoader.getResource(locationToUse));
    } else {
      
      setValue(null);
    } 
  }








  
  protected String resolvePath(String path) {
    if (this.propertyResolver == null) {
      this.propertyResolver = (PropertyResolver)new StandardEnvironment();
    }
    return this.ignoreUnresolvablePlaceholders ? this.propertyResolver.resolvePlaceholders(path) : this.propertyResolver
      .resolveRequiredPlaceholders(path);
  }


  
  @Nullable
  public String getAsText() {
    Resource value = (Resource)getValue();
    
    try {
      return (value != null) ? value.getURL().toExternalForm() : "";
    }
    catch (IOException ex) {

      
      return null;
    } 
  }
}
