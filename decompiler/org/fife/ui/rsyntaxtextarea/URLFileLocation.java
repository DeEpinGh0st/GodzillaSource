package org.fife.ui.rsyntaxtextarea;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

































class URLFileLocation
  extends FileLocation
{
  private URL url;
  private String fileFullPath;
  private String fileName;
  
  URLFileLocation(URL url) {
    this.url = url;
    this.fileFullPath = createFileFullPath();
    this.fileName = createFileName();
  }







  
  private String createFileFullPath() {
    String fullPath = this.url.toString();
    fullPath = fullPath.replaceFirst("://([^:]+)(?:.+)@", "://$1@");
    return fullPath;
  }






  
  private String createFileName() {
    String fileName = this.url.getPath();
    if (fileName.startsWith("/%2F/")) {
      fileName = fileName.substring(4);
    }
    else if (fileName.startsWith("/")) {
      fileName = fileName.substring(1);
    } 
    return fileName;
  }










  
  protected long getActualLastModified() {
    return 0L;
  }


  
  public String getFileFullPath() {
    return this.fileFullPath;
  }


  
  public String getFileName() {
    return this.fileName;
  }


  
  protected InputStream getInputStream() throws IOException {
    return this.url.openStream();
  }


  
  protected OutputStream getOutputStream() throws IOException {
    return this.url.openConnection().getOutputStream();
  }








  
  public boolean isLocal() {
    return "file".equalsIgnoreCase(this.url.getProtocol());
  }










  
  public boolean isLocalAndExists() {
    return false;
  }
}
