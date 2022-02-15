package org.fife.ui.rsyntaxtextarea;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

























class FileFileLocation
  extends FileLocation
{
  private File file;
  
  FileFileLocation(File file) {
    try {
      this.file = file.getCanonicalFile();
    } catch (IOException ioe) {
      this.file = file;
    } 
  }


  
  protected long getActualLastModified() {
    return this.file.lastModified();
  }








  
  public String getFileFullPath() {
    return this.file.getAbsolutePath();
  }


  
  public String getFileName() {
    return this.file.getName();
  }


  
  protected InputStream getInputStream() throws IOException {
    return new FileInputStream(this.file);
  }


  
  protected OutputStream getOutputStream() throws IOException {
    return new FileOutputStream(this.file);
  }








  
  public boolean isLocal() {
    return true;
  }









  
  public boolean isLocalAndExists() {
    return this.file.exists();
  }
}
