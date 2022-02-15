package org.apache.log4j;

import java.util.ResourceBundle;
import org.apache.log4j.spi.LoggingEvent;












































public final class LogSF
  extends LogXF
{
  private static String format(String pattern, Object[] arguments) {
    if (pattern != null) {
      String retval = "";
      int count = 0;
      int prev = 0;
      int pos = pattern.indexOf("{");
      while (pos >= 0) {
        if (pos == 0 || pattern.charAt(pos - 1) != '\\') {
          retval = retval + pattern.substring(prev, pos);
          if (pos + 1 < pattern.length() && pattern.charAt(pos + 1) == '}') {
            if (arguments != null && count < arguments.length) {
              retval = retval + arguments[count++];
            } else {
              retval = retval + "{}";
            } 
            prev = pos + 2;
          } else {
            retval = retval + "{";
            prev = pos + 1;
          } 
        } else {
          retval = retval + pattern.substring(prev, pos - 1) + "{";
          prev = pos + 1;
        } 
        pos = pattern.indexOf("{", prev);
      } 
      return retval + pattern.substring(prev);
    } 
    return null;
  }






  
  private static String format(String pattern, Object arg0) {
    if (pattern != null) {

      
      if (pattern.indexOf("\\{") >= 0) {
        return format(pattern, new Object[] { arg0 });
      }
      int pos = pattern.indexOf("{}");
      if (pos >= 0) {
        return pattern.substring(0, pos) + arg0 + pattern.substring(pos + 2);
      }
    } 
    return pattern;
  }











  
  private static String format(String resourceBundleName, String key, Object[] arguments) {
    String pattern;
    if (resourceBundleName != null) {
      try {
        ResourceBundle bundle = ResourceBundle.getBundle(resourceBundleName);
        
        pattern = bundle.getString(key);
      } catch (Exception ex) {
        pattern = key;
      } 
    } else {
      pattern = key;
    } 
    return format(pattern, arguments);
  }




  
  private static final String FQCN = LogSF.class.getName();









  
  private static void forcedLog(Logger logger, Level level, String msg) {
    logger.callAppenders(new LoggingEvent(FQCN, logger, level, msg, null));
  }











  
  private static void forcedLog(Logger logger, Level level, String msg, Throwable t) {
    logger.callAppenders(new LoggingEvent(FQCN, logger, level, msg, t));
  }







  
  public static void trace(Logger logger, String pattern, Object[] arguments) {
    if (logger.isEnabledFor(TRACE)) {
      forcedLog(logger, TRACE, format(pattern, arguments));
    }
  }







  
  public static void debug(Logger logger, String pattern, Object[] arguments) {
    if (logger.isDebugEnabled()) {
      forcedLog(logger, Level.DEBUG, format(pattern, arguments));
    }
  }







  
  public static void info(Logger logger, String pattern, Object[] arguments) {
    if (logger.isInfoEnabled()) {
      forcedLog(logger, Level.INFO, format(pattern, arguments));
    }
  }







  
  public static void warn(Logger logger, String pattern, Object[] arguments) {
    if (logger.isEnabledFor(Level.WARN)) {
      forcedLog(logger, Level.WARN, format(pattern, arguments));
    }
  }







  
  public static void error(Logger logger, String pattern, Object[] arguments) {
    if (logger.isEnabledFor(Level.ERROR)) {
      forcedLog(logger, Level.ERROR, format(pattern, arguments));
    }
  }







  
  public static void fatal(Logger logger, String pattern, Object[] arguments) {
    if (logger.isEnabledFor(Level.FATAL)) {
      forcedLog(logger, Level.FATAL, format(pattern, arguments));
    }
  }











  
  public static void trace(Logger logger, Throwable t, String pattern, Object[] arguments) {
    if (logger.isEnabledFor(TRACE)) {
      forcedLog(logger, TRACE, format(pattern, arguments), t);
    }
  }










  
  public static void debug(Logger logger, Throwable t, String pattern, Object[] arguments) {
    if (logger.isDebugEnabled()) {
      forcedLog(logger, Level.DEBUG, format(pattern, arguments), t);
    }
  }










  
  public static void info(Logger logger, Throwable t, String pattern, Object[] arguments) {
    if (logger.isInfoEnabled()) {
      forcedLog(logger, Level.INFO, format(pattern, arguments), t);
    }
  }










  
  public static void warn(Logger logger, Throwable t, String pattern, Object[] arguments) {
    if (logger.isEnabledFor(Level.WARN)) {
      forcedLog(logger, Level.WARN, format(pattern, arguments), t);
    }
  }










  
  public static void error(Logger logger, Throwable t, String pattern, Object[] arguments) {
    if (logger.isEnabledFor(Level.ERROR)) {
      forcedLog(logger, Level.ERROR, format(pattern, arguments), t);
    }
  }










  
  public static void fatal(Logger logger, Throwable t, String pattern, Object[] arguments) {
    if (logger.isEnabledFor(Level.FATAL)) {
      forcedLog(logger, Level.FATAL, format(pattern, arguments), t);
    }
  }









  
  public static void trace(Logger logger, String pattern, boolean argument) {
    if (logger.isEnabledFor(TRACE)) {
      forcedLog(logger, TRACE, format(pattern, valueOf(argument)));
    }
  }







  
  public static void trace(Logger logger, String pattern, char argument) {
    if (logger.isEnabledFor(TRACE)) {
      forcedLog(logger, TRACE, format(pattern, valueOf(argument)));
    }
  }







  
  public static void trace(Logger logger, String pattern, byte argument) {
    if (logger.isEnabledFor(TRACE)) {
      forcedLog(logger, TRACE, format(pattern, valueOf(argument)));
    }
  }







  
  public static void trace(Logger logger, String pattern, short argument) {
    if (logger.isEnabledFor(TRACE)) {
      forcedLog(logger, TRACE, format(pattern, valueOf(argument)));
    }
  }







  
  public static void trace(Logger logger, String pattern, int argument) {
    if (logger.isEnabledFor(TRACE)) {
      forcedLog(logger, TRACE, format(pattern, valueOf(argument)));
    }
  }







  
  public static void trace(Logger logger, String pattern, long argument) {
    if (logger.isEnabledFor(TRACE)) {
      forcedLog(logger, TRACE, format(pattern, valueOf(argument)));
    }
  }







  
  public static void trace(Logger logger, String pattern, float argument) {
    if (logger.isEnabledFor(TRACE)) {
      forcedLog(logger, TRACE, format(pattern, valueOf(argument)));
    }
  }







  
  public static void trace(Logger logger, String pattern, double argument) {
    if (logger.isEnabledFor(TRACE)) {
      forcedLog(logger, TRACE, format(pattern, valueOf(argument)));
    }
  }







  
  public static void trace(Logger logger, String pattern, Object argument) {
    if (logger.isEnabledFor(TRACE)) {
      forcedLog(logger, TRACE, format(pattern, argument));
    }
  }








  
  public static void trace(Logger logger, String pattern, Object arg0, Object arg1) {
    if (logger.isEnabledFor(TRACE)) {
      forcedLog(logger, TRACE, format(pattern, toArray(arg0, arg1)));
    }
  }










  
  public static void trace(Logger logger, String pattern, Object arg0, Object arg1, Object arg2) {
    if (logger.isEnabledFor(TRACE)) {
      forcedLog(logger, TRACE, format(pattern, toArray(arg0, arg1, arg2)));
    }
  }












  
  public static void trace(Logger logger, String pattern, Object arg0, Object arg1, Object arg2, Object arg3) {
    if (logger.isEnabledFor(TRACE)) {
      forcedLog(logger, TRACE, format(pattern, toArray(arg0, arg1, arg2, arg3)));
    }
  }








  
  public static void debug(Logger logger, String pattern, boolean argument) {
    if (logger.isDebugEnabled()) {
      forcedLog(logger, Level.DEBUG, format(pattern, valueOf(argument)));
    }
  }







  
  public static void debug(Logger logger, String pattern, char argument) {
    if (logger.isDebugEnabled()) {
      forcedLog(logger, Level.DEBUG, format(pattern, valueOf(argument)));
    }
  }







  
  public static void debug(Logger logger, String pattern, byte argument) {
    if (logger.isDebugEnabled()) {
      forcedLog(logger, Level.DEBUG, format(pattern, valueOf(argument)));
    }
  }







  
  public static void debug(Logger logger, String pattern, short argument) {
    if (logger.isDebugEnabled()) {
      forcedLog(logger, Level.DEBUG, format(pattern, valueOf(argument)));
    }
  }







  
  public static void debug(Logger logger, String pattern, int argument) {
    if (logger.isDebugEnabled()) {
      forcedLog(logger, Level.DEBUG, format(pattern, valueOf(argument)));
    }
  }







  
  public static void debug(Logger logger, String pattern, long argument) {
    if (logger.isDebugEnabled()) {
      forcedLog(logger, Level.DEBUG, format(pattern, valueOf(argument)));
    }
  }







  
  public static void debug(Logger logger, String pattern, float argument) {
    if (logger.isDebugEnabled()) {
      forcedLog(logger, Level.DEBUG, format(pattern, valueOf(argument)));
    }
  }







  
  public static void debug(Logger logger, String pattern, double argument) {
    if (logger.isDebugEnabled()) {
      forcedLog(logger, Level.DEBUG, format(pattern, valueOf(argument)));
    }
  }







  
  public static void debug(Logger logger, String pattern, Object argument) {
    if (logger.isDebugEnabled()) {
      forcedLog(logger, Level.DEBUG, format(pattern, argument));
    }
  }








  
  public static void debug(Logger logger, String pattern, Object arg0, Object arg1) {
    if (logger.isDebugEnabled()) {
      forcedLog(logger, Level.DEBUG, format(pattern, toArray(arg0, arg1)));
    }
  }










  
  public static void debug(Logger logger, String pattern, Object arg0, Object arg1, Object arg2) {
    if (logger.isDebugEnabled()) {
      forcedLog(logger, Level.DEBUG, format(pattern, toArray(arg0, arg1, arg2)));
    }
  }












  
  public static void debug(Logger logger, String pattern, Object arg0, Object arg1, Object arg2, Object arg3) {
    if (logger.isDebugEnabled()) {
      forcedLog(logger, Level.DEBUG, format(pattern, toArray(arg0, arg1, arg2, arg3)));
    }
  }








  
  public static void info(Logger logger, String pattern, boolean argument) {
    if (logger.isInfoEnabled()) {
      forcedLog(logger, Level.INFO, format(pattern, valueOf(argument)));
    }
  }







  
  public static void info(Logger logger, String pattern, char argument) {
    if (logger.isInfoEnabled()) {
      forcedLog(logger, Level.INFO, format(pattern, valueOf(argument)));
    }
  }







  
  public static void info(Logger logger, String pattern, byte argument) {
    if (logger.isInfoEnabled()) {
      forcedLog(logger, Level.INFO, format(pattern, valueOf(argument)));
    }
  }







  
  public static void info(Logger logger, String pattern, short argument) {
    if (logger.isInfoEnabled()) {
      forcedLog(logger, Level.INFO, format(pattern, valueOf(argument)));
    }
  }







  
  public static void info(Logger logger, String pattern, int argument) {
    if (logger.isInfoEnabled()) {
      forcedLog(logger, Level.INFO, format(pattern, valueOf(argument)));
    }
  }







  
  public static void info(Logger logger, String pattern, long argument) {
    if (logger.isInfoEnabled()) {
      forcedLog(logger, Level.INFO, format(pattern, valueOf(argument)));
    }
  }







  
  public static void info(Logger logger, String pattern, float argument) {
    if (logger.isInfoEnabled()) {
      forcedLog(logger, Level.INFO, format(pattern, valueOf(argument)));
    }
  }







  
  public static void info(Logger logger, String pattern, double argument) {
    if (logger.isInfoEnabled()) {
      forcedLog(logger, Level.INFO, format(pattern, valueOf(argument)));
    }
  }







  
  public static void info(Logger logger, String pattern, Object argument) {
    if (logger.isInfoEnabled()) {
      forcedLog(logger, Level.INFO, format(pattern, argument));
    }
  }








  
  public static void info(Logger logger, String pattern, Object arg0, Object arg1) {
    if (logger.isInfoEnabled()) {
      forcedLog(logger, Level.INFO, format(pattern, toArray(arg0, arg1)));
    }
  }









  
  public static void info(Logger logger, String pattern, Object arg0, Object arg1, Object arg2) {
    if (logger.isInfoEnabled()) {
      forcedLog(logger, Level.INFO, format(pattern, toArray(arg0, arg1, arg2)));
    }
  }












  
  public static void info(Logger logger, String pattern, Object arg0, Object arg1, Object arg2, Object arg3) {
    if (logger.isInfoEnabled()) {
      forcedLog(logger, Level.INFO, format(pattern, toArray(arg0, arg1, arg2, arg3)));
    }
  }








  
  public static void warn(Logger logger, String pattern, boolean argument) {
    if (logger.isEnabledFor(Level.WARN)) {
      forcedLog(logger, Level.WARN, format(pattern, valueOf(argument)));
    }
  }







  
  public static void warn(Logger logger, String pattern, char argument) {
    if (logger.isEnabledFor(Level.WARN)) {
      forcedLog(logger, Level.WARN, format(pattern, valueOf(argument)));
    }
  }







  
  public static void warn(Logger logger, String pattern, byte argument) {
    if (logger.isEnabledFor(Level.WARN)) {
      forcedLog(logger, Level.WARN, format(pattern, valueOf(argument)));
    }
  }







  
  public static void warn(Logger logger, String pattern, short argument) {
    if (logger.isEnabledFor(Level.WARN)) {
      forcedLog(logger, Level.WARN, format(pattern, valueOf(argument)));
    }
  }







  
  public static void warn(Logger logger, String pattern, int argument) {
    if (logger.isEnabledFor(Level.WARN)) {
      forcedLog(logger, Level.WARN, format(pattern, valueOf(argument)));
    }
  }







  
  public static void warn(Logger logger, String pattern, long argument) {
    if (logger.isEnabledFor(Level.WARN)) {
      forcedLog(logger, Level.WARN, format(pattern, valueOf(argument)));
    }
  }







  
  public static void warn(Logger logger, String pattern, float argument) {
    if (logger.isEnabledFor(Level.WARN)) {
      forcedLog(logger, Level.WARN, format(pattern, valueOf(argument)));
    }
  }







  
  public static void warn(Logger logger, String pattern, double argument) {
    if (logger.isEnabledFor(Level.WARN)) {
      forcedLog(logger, Level.WARN, format(pattern, valueOf(argument)));
    }
  }







  
  public static void warn(Logger logger, String pattern, Object argument) {
    if (logger.isEnabledFor(Level.WARN)) {
      forcedLog(logger, Level.WARN, format(pattern, argument));
    }
  }








  
  public static void warn(Logger logger, String pattern, Object arg0, Object arg1) {
    if (logger.isEnabledFor(Level.WARN)) {
      forcedLog(logger, Level.WARN, format(pattern, toArray(arg0, arg1)));
    }
  }










  
  public static void warn(Logger logger, String pattern, Object arg0, Object arg1, Object arg2) {
    if (logger.isEnabledFor(Level.WARN)) {
      forcedLog(logger, Level.WARN, format(pattern, toArray(arg0, arg1, arg2)));
    }
  }












  
  public static void warn(Logger logger, String pattern, Object arg0, Object arg1, Object arg2, Object arg3) {
    if (logger.isEnabledFor(Level.WARN)) {
      forcedLog(logger, Level.WARN, format(pattern, toArray(arg0, arg1, arg2, arg3)));
    }
  }











  
  public static void log(Logger logger, Level level, String pattern, Object[] parameters) {
    if (logger.isEnabledFor(level)) {
      forcedLog(logger, level, format(pattern, parameters));
    }
  }













  
  public static void log(Logger logger, Level level, Throwable t, String pattern, Object[] parameters) {
    if (logger.isEnabledFor(level)) {
      forcedLog(logger, level, format(pattern, parameters), t);
    }
  }











  
  public static void log(Logger logger, Level level, String pattern, Object param1) {
    if (logger.isEnabledFor(level)) {
      forcedLog(logger, level, format(pattern, toArray(param1)));
    }
  }











  
  public static void log(Logger logger, Level level, String pattern, boolean param1) {
    if (logger.isEnabledFor(level)) {
      forcedLog(logger, level, format(pattern, toArray(valueOf(param1))));
    }
  }












  
  public static void log(Logger logger, Level level, String pattern, byte param1) {
    if (logger.isEnabledFor(level)) {
      forcedLog(logger, level, format(pattern, toArray(valueOf(param1))));
    }
  }












  
  public static void log(Logger logger, Level level, String pattern, char param1) {
    if (logger.isEnabledFor(level)) {
      forcedLog(logger, level, format(pattern, toArray(valueOf(param1))));
    }
  }











  
  public static void log(Logger logger, Level level, String pattern, short param1) {
    if (logger.isEnabledFor(level)) {
      forcedLog(logger, level, format(pattern, toArray(valueOf(param1))));
    }
  }











  
  public static void log(Logger logger, Level level, String pattern, int param1) {
    if (logger.isEnabledFor(level)) {
      forcedLog(logger, level, format(pattern, toArray(valueOf(param1))));
    }
  }












  
  public static void log(Logger logger, Level level, String pattern, long param1) {
    if (logger.isEnabledFor(level)) {
      forcedLog(logger, level, format(pattern, toArray(valueOf(param1))));
    }
  }












  
  public static void log(Logger logger, Level level, String pattern, float param1) {
    if (logger.isEnabledFor(level)) {
      forcedLog(logger, level, format(pattern, toArray(valueOf(param1))));
    }
  }












  
  public static void log(Logger logger, Level level, String pattern, double param1) {
    if (logger.isEnabledFor(level)) {
      forcedLog(logger, level, format(pattern, toArray(valueOf(param1))));
    }
  }













  
  public static void log(Logger logger, Level level, String pattern, Object arg0, Object arg1) {
    if (logger.isEnabledFor(level)) {
      forcedLog(logger, level, format(pattern, toArray(arg0, arg1)));
    }
  }













  
  public static void log(Logger logger, Level level, String pattern, Object arg0, Object arg1, Object arg2) {
    if (logger.isEnabledFor(level)) {
      forcedLog(logger, level, format(pattern, toArray(arg0, arg1, arg2)));
    }
  }















  
  public static void log(Logger logger, Level level, String pattern, Object arg0, Object arg1, Object arg2, Object arg3) {
    if (logger.isEnabledFor(level)) {
      forcedLog(logger, level, format(pattern, toArray(arg0, arg1, arg2, arg3)));
    }
  }














  
  public static void logrb(Logger logger, Level level, String bundleName, String key, Object[] parameters) {
    if (logger.isEnabledFor(level)) {
      forcedLog(logger, level, format(bundleName, key, parameters));
    }
  }















  
  public static void logrb(Logger logger, Level level, Throwable t, String bundleName, String key, Object[] parameters) {
    if (logger.isEnabledFor(level)) {
      forcedLog(logger, level, format(bundleName, key, parameters), t);
    }
  }













  
  public static void logrb(Logger logger, Level level, String bundleName, String key, Object param1) {
    if (logger.isEnabledFor(level)) {
      forcedLog(logger, level, format(bundleName, key, toArray(param1)));
    }
  }













  
  public static void logrb(Logger logger, Level level, String bundleName, String key, boolean param1) {
    if (logger.isEnabledFor(level)) {
      forcedLog(logger, level, format(bundleName, key, toArray(valueOf(param1))));
    }
  }













  
  public static void logrb(Logger logger, Level level, String bundleName, String key, char param1) {
    if (logger.isEnabledFor(level)) {
      forcedLog(logger, level, format(bundleName, key, toArray(valueOf(param1))));
    }
  }













  
  public static void logrb(Logger logger, Level level, String bundleName, String key, byte param1) {
    if (logger.isEnabledFor(level)) {
      forcedLog(logger, level, format(bundleName, key, toArray(valueOf(param1))));
    }
  }













  
  public static void logrb(Logger logger, Level level, String bundleName, String key, short param1) {
    if (logger.isEnabledFor(level)) {
      forcedLog(logger, level, format(bundleName, key, toArray(valueOf(param1))));
    }
  }













  
  public static void logrb(Logger logger, Level level, String bundleName, String key, int param1) {
    if (logger.isEnabledFor(level)) {
      forcedLog(logger, level, format(bundleName, key, toArray(valueOf(param1))));
    }
  }













  
  public static void logrb(Logger logger, Level level, String bundleName, String key, long param1) {
    if (logger.isEnabledFor(level)) {
      forcedLog(logger, level, format(bundleName, key, toArray(valueOf(param1))));
    }
  }












  
  public static void logrb(Logger logger, Level level, String bundleName, String key, float param1) {
    if (logger.isEnabledFor(level)) {
      forcedLog(logger, level, format(bundleName, key, toArray(valueOf(param1))));
    }
  }














  
  public static void logrb(Logger logger, Level level, String bundleName, String key, double param1) {
    if (logger.isEnabledFor(level)) {
      forcedLog(logger, level, format(bundleName, key, toArray(valueOf(param1))));
    }
  }















  
  public static void logrb(Logger logger, Level level, String bundleName, String key, Object param0, Object param1) {
    if (logger.isEnabledFor(level)) {
      forcedLog(logger, level, format(bundleName, key, toArray(param0, param1)));
    }
  }


















  
  public static void logrb(Logger logger, Level level, String bundleName, String key, Object param0, Object param1, Object param2) {
    if (logger.isEnabledFor(level)) {
      forcedLog(logger, level, format(bundleName, key, toArray(param0, param1, param2)));
    }
  }




















  
  public static void logrb(Logger logger, Level level, String bundleName, String key, Object param0, Object param1, Object param2, Object param3) {
    if (logger.isEnabledFor(level))
      forcedLog(logger, level, format(bundleName, key, toArray(param0, param1, param2, param3))); 
  }
}
