package org.apache.log4j;

import org.apache.log4j.spi.ErrorHandler;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;

public interface Appender {
  void addFilter(Filter paramFilter);
  
  Filter getFilter();
  
  void clearFilters();
  
  void close();
  
  void doAppend(LoggingEvent paramLoggingEvent);
  
  String getName();
  
  void setErrorHandler(ErrorHandler paramErrorHandler);
  
  ErrorHandler getErrorHandler();
  
  void setLayout(Layout paramLayout);
  
  Layout getLayout();
  
  void setName(String paramString);
  
  boolean requiresLayout();
}
