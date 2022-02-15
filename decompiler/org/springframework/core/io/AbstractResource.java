package org.springframework.core.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.NestedIOException;
import org.springframework.lang.Nullable;
import org.springframework.util.ResourceUtils;





































public abstract class AbstractResource
  implements Resource
{
  public boolean exists() {
    if (isFile()) {
      try {
        return getFile().exists();
      }
      catch (IOException ex) {
        Log logger = LogFactory.getLog(getClass());
        if (logger.isDebugEnabled()) {
          logger.debug("Could not retrieve File for existence check of " + getDescription(), ex);
        }
      } 
    }
    
    try {
      getInputStream().close();
      return true;
    }
    catch (Throwable ex) {
      Log logger = LogFactory.getLog(getClass());
      if (logger.isDebugEnabled()) {
        logger.debug("Could not retrieve InputStream for existence check of " + getDescription(), ex);
      }
      return false;
    } 
  }





  
  public boolean isReadable() {
    return exists();
  }




  
  public boolean isOpen() {
    return false;
  }




  
  public boolean isFile() {
    return false;
  }





  
  public URL getURL() throws IOException {
    throw new FileNotFoundException(getDescription() + " cannot be resolved to URL");
  }





  
  public URI getURI() throws IOException {
    URL url = getURL();
    try {
      return ResourceUtils.toURI(url);
    }
    catch (URISyntaxException ex) {
      throw new NestedIOException("Invalid URI [" + url + "]", ex);
    } 
  }





  
  public File getFile() throws IOException {
    throw new FileNotFoundException(getDescription() + " cannot be resolved to absolute file path");
  }







  
  public ReadableByteChannel readableChannel() throws IOException {
    return Channels.newChannel(getInputStream());
  }









  
  public long contentLength() throws IOException {
    InputStream is = getInputStream();
    try {
      long size = 0L;
      byte[] buf = new byte[256];
      int read;
      while ((read = is.read(buf)) != -1) {
        size += read;
      }
      return size;
    } finally {
      
      try {
        is.close();
      }
      catch (IOException ex) {
        Log logger = LogFactory.getLog(getClass());
        if (logger.isDebugEnabled()) {
          logger.debug("Could not close content-length InputStream for " + getDescription(), ex);
        }
      } 
    } 
  }






  
  public long lastModified() throws IOException {
    File fileToCheck = getFileForLastModifiedCheck();
    long lastModified = fileToCheck.lastModified();
    if (lastModified == 0L && !fileToCheck.exists()) {
      throw new FileNotFoundException(getDescription() + " cannot be resolved in the file system for checking its last-modified timestamp");
    }
    
    return lastModified;
  }








  
  protected File getFileForLastModifiedCheck() throws IOException {
    return getFile();
  }





  
  public Resource createRelative(String relativePath) throws IOException {
    throw new FileNotFoundException("Cannot create a relative resource for " + getDescription());
  }





  
  @Nullable
  public String getFilename() {
    return null;
  }






  
  public boolean equals(@Nullable Object other) {
    return (this == other || (other instanceof Resource && ((Resource)other)
      .getDescription().equals(getDescription())));
  }





  
  public int hashCode() {
    return getDescription().hashCode();
  }





  
  public String toString() {
    return getDescription();
  }
}
