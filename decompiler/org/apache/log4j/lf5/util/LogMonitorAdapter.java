package org.apache.log4j.lf5.util;

import java.awt.Toolkit;
import java.util.Arrays;
import java.util.List;
import org.apache.log4j.lf5.LogLevel;
import org.apache.log4j.lf5.LogRecord;
import org.apache.log4j.lf5.viewer.LogBrokerMonitor;



































public class LogMonitorAdapter
{
  public static final int LOG4J_LOG_LEVELS = 0;
  public static final int JDK14_LOG_LEVELS = 1;
  private LogBrokerMonitor _logMonitor;
  private LogLevel _defaultLevel = null;





  
  private LogMonitorAdapter(List userDefinedLevels) {
    this._defaultLevel = userDefinedLevels.get(0);
    this._logMonitor = new LogBrokerMonitor(userDefinedLevels);
    
    this._logMonitor.setFrameSize(getDefaultMonitorWidth(), getDefaultMonitorHeight());
    
    this._logMonitor.setFontSize(12);
    this._logMonitor.show();
  }










  
  public static LogMonitorAdapter newInstance(int loglevels) {
    LogMonitorAdapter adapter;
    if (loglevels == 1) {
      adapter = newInstance(LogLevel.getJdk14Levels());
      adapter.setDefaultLevel(LogLevel.FINEST);
      adapter.setSevereLevel(LogLevel.SEVERE);
    } else {
      adapter = newInstance(LogLevel.getLog4JLevels());
      adapter.setDefaultLevel(LogLevel.DEBUG);
      adapter.setSevereLevel(LogLevel.FATAL);
    } 
    return adapter;
  }








  
  public static LogMonitorAdapter newInstance(LogLevel[] userDefined) {
    if (userDefined == null) {
      return null;
    }
    return newInstance(Arrays.asList(userDefined));
  }








  
  public static LogMonitorAdapter newInstance(List userDefinedLevels) {
    return new LogMonitorAdapter(userDefinedLevels);
  }





  
  public void addMessage(LogRecord record) {
    this._logMonitor.addMessage(record);
  }





  
  public void setMaxNumberOfRecords(int maxNumberOfRecords) {
    this._logMonitor.setMaxNumberOfLogRecords(maxNumberOfRecords);
  }






  
  public void setDefaultLevel(LogLevel level) {
    this._defaultLevel = level;
  }





  
  public LogLevel getDefaultLevel() {
    return this._defaultLevel;
  }





  
  public void setSevereLevel(LogLevel level) {
    AdapterLogRecord.setSevereLevel(level);
  }





  
  public LogLevel getSevereLevel() {
    return AdapterLogRecord.getSevereLevel();
  }











  
  public void log(String category, LogLevel level, String message, Throwable t, String NDC) {
    AdapterLogRecord record = new AdapterLogRecord();
    record.setCategory(category);
    record.setMessage(message);
    record.setNDC(NDC);
    record.setThrown(t);
    
    if (level == null) {
      record.setLevel(getDefaultLevel());
    } else {
      record.setLevel(level);
    } 
    
    addMessage(record);
  }






  
  public void log(String category, String message) {
    log(category, null, message);
  }








  
  public void log(String category, LogLevel level, String message, String NDC) {
    log(category, level, message, null, NDC);
  }









  
  public void log(String category, LogLevel level, String message, Throwable t) {
    log(category, level, message, t, null);
  }







  
  public void log(String category, LogLevel level, String message) {
    log(category, level, message, null, null);
  }








  
  protected static int getScreenWidth() {
    try {
      return (Toolkit.getDefaultToolkit().getScreenSize()).width;
    } catch (Throwable t) {
      return 800;
    } 
  }





  
  protected static int getScreenHeight() {
    try {
      return (Toolkit.getDefaultToolkit().getScreenSize()).height;
    } catch (Throwable t) {
      return 600;
    } 
  }
  
  protected static int getDefaultMonitorWidth() {
    return 3 * getScreenWidth() / 4;
  }
  
  protected static int getDefaultMonitorHeight() {
    return 3 * getScreenHeight() / 4;
  }
}
