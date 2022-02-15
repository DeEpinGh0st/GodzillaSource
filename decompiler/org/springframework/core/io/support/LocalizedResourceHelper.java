package org.springframework.core.io.support;

import java.util.Locale;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;





























public class LocalizedResourceHelper
{
  public static final String DEFAULT_SEPARATOR = "_";
  private final ResourceLoader resourceLoader;
  private String separator = "_";





  
  public LocalizedResourceHelper() {
    this.resourceLoader = (ResourceLoader)new DefaultResourceLoader();
  }




  
  public LocalizedResourceHelper(ResourceLoader resourceLoader) {
    Assert.notNull(resourceLoader, "ResourceLoader must not be null");
    this.resourceLoader = resourceLoader;
  }




  
  public void setSeparator(@Nullable String separator) {
    this.separator = (separator != null) ? separator : "_";
  }




















  
  public Resource findLocalizedResource(String name, String extension, @Nullable Locale locale) {
    Assert.notNull(name, "Name must not be null");
    Assert.notNull(extension, "Extension must not be null");
    
    Resource resource = null;
    
    if (locale != null) {
      String lang = locale.getLanguage();
      String country = locale.getCountry();
      String variant = locale.getVariant();

      
      if (variant.length() > 0) {
        String location = name + this.separator + lang + this.separator + country + this.separator + variant + extension;
        
        resource = this.resourceLoader.getResource(location);
      } 

      
      if ((resource == null || !resource.exists()) && country.length() > 0) {
        String location = name + this.separator + lang + this.separator + country + extension;
        resource = this.resourceLoader.getResource(location);
      } 

      
      if ((resource == null || !resource.exists()) && lang.length() > 0) {
        String location = name + this.separator + lang + extension;
        resource = this.resourceLoader.getResource(location);
      } 
    } 

    
    if (resource == null || !resource.exists()) {
      String location = name + extension;
      resource = this.resourceLoader.getResource(location);
    } 
    
    return resource;
  }
}
