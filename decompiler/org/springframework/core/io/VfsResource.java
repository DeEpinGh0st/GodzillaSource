package org.springframework.core.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import org.springframework.core.NestedIOException;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;





































public class VfsResource
  extends AbstractResource
{
  private final Object resource;
  
  public VfsResource(Object resource) {
    Assert.notNull(resource, "VirtualFile must not be null");
    this.resource = resource;
  }


  
  public InputStream getInputStream() throws IOException {
    return VfsUtils.getInputStream(this.resource);
  }

  
  public boolean exists() {
    return VfsUtils.exists(this.resource);
  }

  
  public boolean isReadable() {
    return VfsUtils.isReadable(this.resource);
  }

  
  public URL getURL() throws IOException {
    try {
      return VfsUtils.getURL(this.resource);
    }
    catch (Exception ex) {
      throw new NestedIOException("Failed to obtain URL for file " + this.resource, ex);
    } 
  }

  
  public URI getURI() throws IOException {
    try {
      return VfsUtils.getURI(this.resource);
    }
    catch (Exception ex) {
      throw new NestedIOException("Failed to obtain URI for " + this.resource, ex);
    } 
  }

  
  public File getFile() throws IOException {
    return VfsUtils.getFile(this.resource);
  }

  
  public long contentLength() throws IOException {
    return VfsUtils.getSize(this.resource);
  }

  
  public long lastModified() throws IOException {
    return VfsUtils.getLastModified(this.resource);
  }

  
  public Resource createRelative(String relativePath) throws IOException {
    if (!relativePath.startsWith(".") && relativePath.contains("/")) {
      try {
        return new VfsResource(VfsUtils.getChild(this.resource, relativePath));
      }
      catch (IOException iOException) {}
    }


    
    return new VfsResource(VfsUtils.getRelative(new URL(getURL(), relativePath)));
  }

  
  public String getFilename() {
    return VfsUtils.getName(this.resource);
  }

  
  public String getDescription() {
    return "VFS resource [" + this.resource + "]";
  }

  
  public boolean equals(@Nullable Object other) {
    return (this == other || (other instanceof VfsResource && this.resource
      .equals(((VfsResource)other).resource)));
  }

  
  public int hashCode() {
    return this.resource.hashCode();
  }
}
