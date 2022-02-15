package org.springframework.core.io;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;






























public class ClassRelativeResourceLoader
  extends DefaultResourceLoader
{
  private final Class<?> clazz;
  
  public ClassRelativeResourceLoader(Class<?> clazz) {
    Assert.notNull(clazz, "Class must not be null");
    this.clazz = clazz;
    setClassLoader(clazz.getClassLoader());
  }

  
  protected Resource getResourceByPath(String path) {
    return new ClassRelativeContextResource(path, this.clazz);
  }


  
  private static class ClassRelativeContextResource
    extends ClassPathResource
    implements ContextResource
  {
    private final Class<?> clazz;

    
    public ClassRelativeContextResource(String path, Class<?> clazz) {
      super(path, clazz);
      this.clazz = clazz;
    }

    
    public String getPathWithinContext() {
      return getPath();
    }

    
    public Resource createRelative(String relativePath) {
      String pathToUse = StringUtils.applyRelativePath(getPath(), relativePath);
      return new ClassRelativeContextResource(pathToUse, this.clazz);
    }
  }
}
