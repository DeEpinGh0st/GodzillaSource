package org.springframework.core.type.classreading;

import java.io.FileNotFoundException;
import java.io.IOException;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;




























public class SimpleMetadataReaderFactory
  implements MetadataReaderFactory
{
  private final ResourceLoader resourceLoader;
  
  public SimpleMetadataReaderFactory() {
    this.resourceLoader = (ResourceLoader)new DefaultResourceLoader();
  }





  
  public SimpleMetadataReaderFactory(@Nullable ResourceLoader resourceLoader) {
    this.resourceLoader = (resourceLoader != null) ? resourceLoader : (ResourceLoader)new DefaultResourceLoader();
  }




  
  public SimpleMetadataReaderFactory(@Nullable ClassLoader classLoader) {
    this.resourceLoader = (classLoader != null) ? (ResourceLoader)new DefaultResourceLoader(classLoader) : (ResourceLoader)new DefaultResourceLoader();
  }






  
  public final ResourceLoader getResourceLoader() {
    return this.resourceLoader;
  }



  
  public MetadataReader getMetadataReader(String className) throws IOException {
    try {
      String resourcePath = "classpath:" + ClassUtils.convertClassNameToResourcePath(className) + ".class";
      Resource resource = this.resourceLoader.getResource(resourcePath);
      return getMetadataReader(resource);
    }
    catch (FileNotFoundException ex) {

      
      int lastDotIndex = className.lastIndexOf('.');
      if (lastDotIndex != -1) {
        
        String innerClassName = className.substring(0, lastDotIndex) + '$' + className.substring(lastDotIndex + 1);
        
        String innerClassResourcePath = "classpath:" + ClassUtils.convertClassNameToResourcePath(innerClassName) + ".class";
        Resource innerClassResource = this.resourceLoader.getResource(innerClassResourcePath);
        if (innerClassResource.exists()) {
          return getMetadataReader(innerClassResource);
        }
      } 
      throw ex;
    } 
  }

  
  public MetadataReader getMetadataReader(Resource resource) throws IOException {
    return new SimpleMetadataReader(resource, this.resourceLoader.getClassLoader());
  }
}
