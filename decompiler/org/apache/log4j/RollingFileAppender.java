package org.apache.log4j;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import org.apache.log4j.helpers.CountingQuietWriter;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.helpers.QuietWriter;
import org.apache.log4j.spi.LoggingEvent;




































public class RollingFileAppender
  extends FileAppender
{
  protected long maxFileSize = 10485760L;



  
  protected int maxBackupIndex = 1;
  
  private long nextRollover = 0L;









  
  public RollingFileAppender() {}








  
  public RollingFileAppender(Layout layout, String filename, boolean append) throws IOException {
    super(layout, filename, append);
  }







  
  public RollingFileAppender(Layout layout, String filename) throws IOException {
    super(layout, filename);
  }




  
  public int getMaxBackupIndex() {
    return this.maxBackupIndex;
  }







  
  public long getMaximumFileSize() {
    return this.maxFileSize;
  }


















  
  public void rollOver() {
    if (this.qw != null) {
      long size = ((CountingQuietWriter)this.qw).getCount();
      LogLog.debug("rolling over count=" + size);

      
      this.nextRollover = size + this.maxFileSize;
    } 
    LogLog.debug("maxBackupIndex=" + this.maxBackupIndex);
    
    boolean renameSucceeded = true;
    
    if (this.maxBackupIndex > 0) {
      
      File file = new File(this.fileName + '.' + this.maxBackupIndex);
      if (file.exists()) {
        renameSucceeded = file.delete();
      }
      
      for (int i = this.maxBackupIndex - 1; i >= 1 && renameSucceeded; i--) {
        file = new File(this.fileName + "." + i);
        if (file.exists()) {
          File target = new File(this.fileName + '.' + (i + 1));
          LogLog.debug("Renaming file " + file + " to " + target);
          renameSucceeded = file.renameTo(target);
        } 
      } 
      
      if (renameSucceeded) {
        
        File target = new File(this.fileName + "." + '\001');
        
        closeFile();
        
        file = new File(this.fileName);
        LogLog.debug("Renaming file " + file + " to " + target);
        renameSucceeded = file.renameTo(target);


        
        if (!renameSucceeded) {
          try {
            setFile(this.fileName, true, this.bufferedIO, this.bufferSize);
          }
          catch (IOException e) {
            if (e instanceof java.io.InterruptedIOException) {
              Thread.currentThread().interrupt();
            }
            LogLog.error("setFile(" + this.fileName + ", true) call failed.", e);
          } 
        }
      } 
    } 



    
    if (renameSucceeded) {
      
      try {
        
        setFile(this.fileName, false, this.bufferedIO, this.bufferSize);
        this.nextRollover = 0L;
      }
      catch (IOException e) {
        if (e instanceof java.io.InterruptedIOException) {
          Thread.currentThread().interrupt();
        }
        LogLog.error("setFile(" + this.fileName + ", false) call failed.", e);
      } 
    }
  }



  
  public synchronized void setFile(String fileName, boolean append, boolean bufferedIO, int bufferSize) throws IOException {
    super.setFile(fileName, append, this.bufferedIO, this.bufferSize);
    if (append) {
      File f = new File(fileName);
      ((CountingQuietWriter)this.qw).setCount(f.length());
    } 
  }











  
  public void setMaxBackupIndex(int maxBackups) {
    this.maxBackupIndex = maxBackups;
  }













  
  public void setMaximumFileSize(long maxFileSize) {
    this.maxFileSize = maxFileSize;
  }













  
  public void setMaxFileSize(String value) {
    this.maxFileSize = OptionConverter.toFileSize(value, this.maxFileSize + 1L);
  }

  
  protected void setQWForFiles(Writer writer) {
    this.qw = (QuietWriter)new CountingQuietWriter(writer, this.errorHandler);
  }







  
  protected void subAppend(LoggingEvent event) {
    super.subAppend(event);
    if (this.fileName != null && this.qw != null) {
      long size = ((CountingQuietWriter)this.qw).getCount();
      if (size >= this.maxFileSize && size >= this.nextRollover)
        rollOver(); 
    } 
  }
}
