package org.apache.log4j.lf5;

import java.awt.Color;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;



































public class LogLevel
  implements Serializable
{
  public static final LogLevel FATAL = new LogLevel("FATAL", 0);
  public static final LogLevel ERROR = new LogLevel("ERROR", 1);
  public static final LogLevel WARN = new LogLevel("WARN", 2);
  public static final LogLevel INFO = new LogLevel("INFO", 3);
  public static final LogLevel DEBUG = new LogLevel("DEBUG", 4);

  
  public static final LogLevel SEVERE = new LogLevel("SEVERE", 1);
  public static final LogLevel WARNING = new LogLevel("WARNING", 2);
  public static final LogLevel CONFIG = new LogLevel("CONFIG", 4);
  public static final LogLevel FINE = new LogLevel("FINE", 5);
  public static final LogLevel FINER = new LogLevel("FINER", 6);
  public static final LogLevel FINEST = new LogLevel("FINEST", 7);
  
  protected String _label;
  
  protected int _precedence;
  
  private static LogLevel[] _log4JLevels;
  
  private static LogLevel[] _jdk14Levels;
  
  private static LogLevel[] _allDefaultLevels;
  
  private static Map _logLevelMap;
  
  private static Map _logLevelColorMap;
  private static Map _registeredLogLevelMap = new HashMap();



  
  static {
    _log4JLevels = new LogLevel[] { FATAL, ERROR, WARN, INFO, DEBUG };
    _jdk14Levels = new LogLevel[] { SEVERE, WARNING, INFO, CONFIG, FINE, FINER, FINEST };
    
    _allDefaultLevels = new LogLevel[] { FATAL, ERROR, WARN, INFO, DEBUG, SEVERE, WARNING, CONFIG, FINE, FINER, FINEST };

    
    _logLevelMap = new HashMap(); int i;
    for (i = 0; i < _allDefaultLevels.length; i++) {
      _logLevelMap.put(_allDefaultLevels[i].getLabel(), _allDefaultLevels[i]);
    }

    
    _logLevelColorMap = new HashMap();
    for (i = 0; i < _allDefaultLevels.length; i++) {
      _logLevelColorMap.put(_allDefaultLevels[i], Color.black);
    }
  }
  
  public LogLevel(String label, int precedence) {
    this._label = label;
    this._precedence = precedence;
  }







  
  public String getLabel() {
    return this._label;
  }






  
  public boolean encompasses(LogLevel level) {
    if (level.getPrecedence() <= getPrecedence()) {
      return true;
    }
    
    return false;
  }









  
  public static LogLevel valueOf(String level) throws LogLevelFormatException {
    LogLevel logLevel = null;
    if (level != null) {
      level = level.trim().toUpperCase();
      logLevel = (LogLevel)_logLevelMap.get(level);
    } 

    
    if (logLevel == null && _registeredLogLevelMap.size() > 0) {
      logLevel = (LogLevel)_registeredLogLevelMap.get(level);
    }
    
    if (logLevel == null) {
      StringBuffer buf = new StringBuffer();
      buf.append("Error while trying to parse (" + level + ") into");
      buf.append(" a LogLevel.");
      throw new LogLevelFormatException(buf.toString());
    } 
    return logLevel;
  }






  
  public static LogLevel register(LogLevel logLevel) {
    if (logLevel == null) return null;

    
    if (_logLevelMap.get(logLevel.getLabel()) == null) {
      return _registeredLogLevelMap.put(logLevel.getLabel(), logLevel);
    }
    
    return null;
  }
  
  public static void register(LogLevel[] logLevels) {
    if (logLevels != null) {
      for (int i = 0; i < logLevels.length; i++) {
        register(logLevels[i]);
      }
    }
  }
  
  public static void register(List logLevels) {
    if (logLevels != null) {
      Iterator it = logLevels.iterator();
      while (it.hasNext()) {
        register(it.next());
      }
    } 
  }
  
  public boolean equals(Object o) {
    boolean equals = false;
    
    if (o instanceof LogLevel && 
      getPrecedence() == ((LogLevel)o).getPrecedence())
    {
      equals = true;
    }


    
    return equals;
  }
  
  public int hashCode() {
    return this._label.hashCode();
  }
  
  public String toString() {
    return this._label;
  }


  
  public void setLogLevelColorMap(LogLevel level, Color color) {
    _logLevelColorMap.remove(level);
    
    if (color == null) {
      color = Color.black;
    }
    _logLevelColorMap.put(level, color);
  }

  
  public static void resetLogLevelColorMap() {
    _logLevelColorMap.clear();

    
    for (int i = 0; i < _allDefaultLevels.length; i++) {
      _logLevelColorMap.put(_allDefaultLevels[i], Color.black);
    }
  }




  
  public static List getLog4JLevels() {
    return Arrays.asList(_log4JLevels);
  }
  
  public static List getJdk14Levels() {
    return Arrays.asList(_jdk14Levels);
  }
  
  public static List getAllDefaultLevels() {
    return Arrays.asList(_allDefaultLevels);
  }
  
  public static Map getLogLevelColorMap() {
    return _logLevelColorMap;
  }




  
  protected int getPrecedence() {
    return this._precedence;
  }
}
