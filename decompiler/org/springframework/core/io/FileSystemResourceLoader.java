package org.springframework.core.io;













































public class FileSystemResourceLoader
  extends DefaultResourceLoader
{
  protected Resource getResourceByPath(String path) {
    if (path.startsWith("/")) {
      path = path.substring(1);
    }
    return new FileSystemContextResource(path);
  }



  
  private static class FileSystemContextResource
    extends FileSystemResource
    implements ContextResource
  {
    public FileSystemContextResource(String path) {
      super(path);
    }

    
    public String getPathWithinContext() {
      return getPath();
    }
  }
}
