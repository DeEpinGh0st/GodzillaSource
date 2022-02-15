package org.apache.log4j.spi;

import org.apache.log4j.Appender;
import org.apache.log4j.Logger;

public interface ErrorHandler extends OptionHandler {
  void setLogger(Logger paramLogger);
  
  void error(String paramString, Exception paramException, int paramInt);
  
  void error(String paramString);
  
  void error(String paramString, Exception paramException, int paramInt, LoggingEvent paramLoggingEvent);
  
  void setAppender(Appender paramAppender);
  
  void setBackupAppender(Appender paramAppender);
}
