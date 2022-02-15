package org.springframework.core.io;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

































public class DefaultResourceLoader
  implements ResourceLoader
{
  @Nullable
  private ClassLoader classLoader;
  private final Set<ProtocolResolver> protocolResolvers = new LinkedHashSet<>(4);
  
  private final Map<Class<?>, Map<Resource, ?>> resourceCaches = new ConcurrentHashMap<>(4);
















  
  public DefaultResourceLoader(@Nullable ClassLoader classLoader) {
    this.classLoader = classLoader;
  }







  
  public void setClassLoader(@Nullable ClassLoader classLoader) {
    this.classLoader = classLoader;
  }







  
  @Nullable
  public ClassLoader getClassLoader() {
    return (this.classLoader != null) ? this.classLoader : ClassUtils.getDefaultClassLoader();
  }








  
  public void addProtocolResolver(ProtocolResolver resolver) {
    Assert.notNull(resolver, "ProtocolResolver must not be null");
    this.protocolResolvers.add(resolver);
  }





  
  public Collection<ProtocolResolver> getProtocolResolvers() {
    return this.protocolResolvers;
  }







  
  public <T> Map<Resource, T> getResourceCache(Class<T> valueType) {
    return (Map<Resource, T>)this.resourceCaches.computeIfAbsent(valueType, key -> new ConcurrentHashMap<>());
  }





  
  public void clearResourceCaches() {
    this.resourceCaches.clear();
  }


  
  public Resource getResource(String location) {
    Assert.notNull(location, "Location must not be null");
    
    for (ProtocolResolver protocolResolver : getProtocolResolvers()) {
      Resource resource = protocolResolver.resolve(location, this);
      if (resource != null) {
        return resource;
      }
    } 
    
    if (location.startsWith("/")) {
      return getResourceByPath(location);
    }
    if (location.startsWith("classpath:")) {
      return new ClassPathResource(location.substring("classpath:".length()), getClassLoader());
    }

    
    try {
      URL url = new URL(location);
      return ResourceUtils.isFileURL(url) ? new FileUrlResource(url) : new UrlResource(url);
    }
    catch (MalformedURLException ex) {
      
      return getResourceByPath(location);
    } 
  }












  
  protected Resource getResourceByPath(String path) {
    return new ClassPathContextResource(path, getClassLoader());
  }

  
  public DefaultResourceLoader() {}
  
  protected static class ClassPathContextResource
    extends ClassPathResource
    implements ContextResource
  {
    public ClassPathContextResource(String path, @Nullable ClassLoader classLoader) {
      super(path, classLoader);
    }

    
    public String getPathWithinContext() {
      return getPath();
    }

    
    public Resource createRelative(String relativePath) {
      String pathToUse = StringUtils.applyRelativePath(getPath(), relativePath);
      return new ClassPathContextResource(pathToUse, getClassLoader());
    }
  }
}
