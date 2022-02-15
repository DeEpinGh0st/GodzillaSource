package org.springframework.core.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.OpenOption;
import java.nio.file.StandardOpenOption;
import org.springframework.util.ResourceUtils;




























public abstract class AbstractFileResolvingResource
  extends AbstractResource
{
  public boolean exists() {
    try {
      URL url = getURL();
      if (ResourceUtils.isFileURL(url))
      {
        return getFile().exists();
      }

      
      URLConnection con = url.openConnection();
      customizeConnection(con);
      HttpURLConnection httpCon = (con instanceof HttpURLConnection) ? (HttpURLConnection)con : null;
      
      if (httpCon != null) {
        int code = httpCon.getResponseCode();
        if (code == 200) {
          return true;
        }
        if (code == 404) {
          return false;
        }
      } 
      if (con.getContentLengthLong() > 0L) {
        return true;
      }
      if (httpCon != null) {
        
        httpCon.disconnect();
        return false;
      } 

      
      getInputStream().close();
      return true;

    
    }
    catch (IOException ex) {
      return false;
    } 
  }

  
  public boolean isReadable() {
    try {
      return checkReadable(getURL());
    }
    catch (IOException ex) {
      return false;
    } 
  }
  
  boolean checkReadable(URL url) {
    try {
      if (ResourceUtils.isFileURL(url)) {
        
        File file = getFile();
        return (file.canRead() && !file.isDirectory());
      } 

      
      URLConnection con = url.openConnection();
      customizeConnection(con);
      if (con instanceof HttpURLConnection) {
        HttpURLConnection httpCon = (HttpURLConnection)con;
        int code = httpCon.getResponseCode();
        if (code != 200) {
          httpCon.disconnect();
          return false;
        } 
      } 
      long contentLength = con.getContentLengthLong();
      if (contentLength > 0L) {
        return true;
      }
      if (contentLength == 0L)
      {
        return false;
      }

      
      getInputStream().close();
      return true;

    
    }
    catch (IOException ex) {
      return false;
    } 
  }

  
  public boolean isFile() {
    try {
      URL url = getURL();
      if (url.getProtocol().startsWith("vfs")) {
        return VfsResourceDelegate.getResource(url).isFile();
      }
      return "file".equals(url.getProtocol());
    }
    catch (IOException ex) {
      return false;
    } 
  }






  
  public File getFile() throws IOException {
    URL url = getURL();
    if (url.getProtocol().startsWith("vfs")) {
      return VfsResourceDelegate.getResource(url).getFile();
    }
    return ResourceUtils.getFile(url, getDescription());
  }





  
  protected File getFileForLastModifiedCheck() throws IOException {
    URL url = getURL();
    if (ResourceUtils.isJarURL(url)) {
      URL actualUrl = ResourceUtils.extractArchiveURL(url);
      if (actualUrl.getProtocol().startsWith("vfs")) {
        return VfsResourceDelegate.getResource(actualUrl).getFile();
      }
      return ResourceUtils.getFile(actualUrl, "Jar URL");
    } 
    
    return getFile();
  }







  
  protected boolean isFile(URI uri) {
    try {
      if (uri.getScheme().startsWith("vfs")) {
        return VfsResourceDelegate.getResource(uri).isFile();
      }
      return "file".equals(uri.getScheme());
    }
    catch (IOException ex) {
      return false;
    } 
  }





  
  protected File getFile(URI uri) throws IOException {
    if (uri.getScheme().startsWith("vfs")) {
      return VfsResourceDelegate.getResource(uri).getFile();
    }
    return ResourceUtils.getFile(uri, getDescription());
  }








  
  public ReadableByteChannel readableChannel() throws IOException {
    try {
      return FileChannel.open(getFile().toPath(), new OpenOption[] { StandardOpenOption.READ });
    }
    catch (FileNotFoundException|java.nio.file.NoSuchFileException ex) {
      
      return super.readableChannel();
    } 
  }

  
  public long contentLength() throws IOException {
    URL url = getURL();
    if (ResourceUtils.isFileURL(url)) {
      
      File file = getFile();
      long length = file.length();
      if (length == 0L && !file.exists()) {
        throw new FileNotFoundException(getDescription() + " cannot be resolved in the file system for checking its content length");
      }
      
      return length;
    } 

    
    URLConnection con = url.openConnection();
    customizeConnection(con);
    return con.getContentLengthLong();
  }


  
  public long lastModified() throws IOException {
    URL url = getURL();
    boolean fileCheck = false;
    if (ResourceUtils.isFileURL(url) || ResourceUtils.isJarURL(url)) {
      
      fileCheck = true;
      try {
        File fileToCheck = getFileForLastModifiedCheck();
        long l = fileToCheck.lastModified();
        if (l > 0L || fileToCheck.exists()) {
          return l;
        }
      }
      catch (FileNotFoundException fileNotFoundException) {}
    } 


    
    URLConnection con = url.openConnection();
    customizeConnection(con);
    long lastModified = con.getLastModified();
    if (fileCheck && lastModified == 0L && con.getContentLengthLong() <= 0L) {
      throw new FileNotFoundException(getDescription() + " cannot be resolved in the file system for checking its last-modified timestamp");
    }
    
    return lastModified;
  }









  
  protected void customizeConnection(URLConnection con) throws IOException {
    ResourceUtils.useCachesIfNecessary(con);
    if (con instanceof HttpURLConnection) {
      customizeConnection((HttpURLConnection)con);
    }
  }







  
  protected void customizeConnection(HttpURLConnection con) throws IOException {
    con.setRequestMethod("HEAD");
  }




  
  private static class VfsResourceDelegate
  {
    public static Resource getResource(URL url) throws IOException {
      return new VfsResource(VfsUtils.getRoot(url));
    }
    
    public static Resource getResource(URI uri) throws IOException {
      return new VfsResource(VfsUtils.getRoot(uri));
    }
  }
}
