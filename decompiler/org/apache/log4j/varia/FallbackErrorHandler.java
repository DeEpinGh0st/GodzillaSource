package org.apache.log4j.varia;

import java.util.Vector;
import org.apache.log4j.Appender;
import org.apache.log4j.Logger;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.ErrorHandler;
import org.apache.log4j.spi.LoggingEvent;








































public class FallbackErrorHandler
  implements ErrorHandler
{
  Appender backup;
  Appender primary;
  Vector loggers;
  
  public void setLogger(Logger logger) {
    LogLog.debug("FB: Adding logger [" + logger.getName() + "].");
    if (this.loggers == null) {
      this.loggers = new Vector();
    }
    this.loggers.addElement(logger);
  }






  
  public void activateOptions() {}





  
  public void error(String message, Exception e, int errorCode) {
    error(message, e, errorCode, null);
  }





  
  public void error(String message, Exception e, int errorCode, LoggingEvent event) {
    if (e instanceof java.io.InterruptedIOException) {
      Thread.currentThread().interrupt();
    }
    LogLog.debug("FB: The following error reported: " + message, e);
    LogLog.debug("FB: INITIATING FALLBACK PROCEDURE.");
    if (this.loggers != null) {
      for (int i = 0; i < this.loggers.size(); i++) {
        Logger l = this.loggers.elementAt(i);
        LogLog.debug("FB: Searching for [" + this.primary.getName() + "] in logger [" + l.getName() + "].");
        
        LogLog.debug("FB: Replacing [" + this.primary.getName() + "] by [" + this.backup.getName() + "] in logger [" + l.getName() + "].");
        
        l.removeAppender(this.primary);
        LogLog.debug("FB: Adding appender [" + this.backup.getName() + "] to logger " + l.getName());
        
        l.addAppender(this.backup);
      } 
    }
  }








  
  public void error(String message) {}







  
  public void setAppender(Appender primary) {
    LogLog.debug("FB: Setting primary appender to [" + primary.getName() + "].");
    this.primary = primary;
  }




  
  public void setBackupAppender(Appender backup) {
    LogLog.debug("FB: Setting backup appender to [" + backup.getName() + "].");
    this.backup = backup;
  }
}
