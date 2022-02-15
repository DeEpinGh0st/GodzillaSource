package org.apache.log4j.helpers;

import java.io.File;




































public abstract class FileWatchdog
  extends Thread
{
  public static final long DEFAULT_DELAY = 60000L;
  protected String filename;
  protected long delay = 60000L;
  
  File file;
  long lastModif = 0L;
  
  boolean warnedAlready = false;
  boolean interrupted = false;
  
  protected FileWatchdog(String filename) {
    super("FileWatchdog");
    this.filename = filename;
    this.file = new File(filename);
    setDaemon(true);
    checkAndConfigure();
  }




  
  public void setDelay(long delay) {
    this.delay = delay;
  }


  
  protected abstract void doOnChange();

  
  protected void checkAndConfigure() {
    boolean fileExists;
    try {
      fileExists = this.file.exists();
    } catch (SecurityException e) {
      LogLog.warn("Was not allowed to read check file existance, file:[" + this.filename + "].");
      
      this.interrupted = true;
      
      return;
    } 
    if (fileExists) {
      long l = this.file.lastModified();
      if (l > this.lastModif) {
        this.lastModif = l;
        doOnChange();
        this.warnedAlready = false;
      }
    
    } else if (!this.warnedAlready) {
      LogLog.debug("[" + this.filename + "] does not exist.");
      this.warnedAlready = true;
    } 
  }


  
  public void run() {
    while (!this.interrupted) {
      try {
        Thread.sleep(this.delay);
      } catch (InterruptedException e) {}

      
      checkAndConfigure();
    } 
  }
}
