package org.apache.log4j.helpers;



























































public class LogLog
{
  public static final String DEBUG_KEY = "log4j.debug";
  public static final String CONFIG_DEBUG_KEY = "log4j.configDebug";
  protected static boolean debugEnabled = false;
  private static boolean quietMode = false;
  private static final String PREFIX = "log4j: ";
  private static final String ERR_PREFIX = "log4j:ERROR ";
  private static final String WARN_PREFIX = "log4j:WARN ";
  
  static {
    String key = OptionConverter.getSystemProperty("log4j.debug", null);
    
    if (key == null) {
      key = OptionConverter.getSystemProperty("log4j.configDebug", null);
    }
    
    if (key != null) {
      debugEnabled = OptionConverter.toBoolean(key, true);
    }
  }





  
  public static void setInternalDebugging(boolean enabled) {
    debugEnabled = enabled;
  }






  
  public static void debug(String msg) {
    if (debugEnabled && !quietMode) {
      System.out.println("log4j: " + msg);
    }
  }






  
  public static void debug(String msg, Throwable t) {
    if (debugEnabled && !quietMode) {
      System.out.println("log4j: " + msg);
      if (t != null) {
        t.printStackTrace(System.out);
      }
    } 
  }







  
  public static void error(String msg) {
    if (quietMode)
      return; 
    System.err.println("log4j:ERROR " + msg);
  }







  
  public static void error(String msg, Throwable t) {
    if (quietMode) {
      return;
    }
    System.err.println("log4j:ERROR " + msg);
    if (t != null) {
      t.printStackTrace();
    }
  }








  
  public static void setQuietMode(boolean quietMode) {
    LogLog.quietMode = quietMode;
  }






  
  public static void warn(String msg) {
    if (quietMode) {
      return;
    }
    System.err.println("log4j:WARN " + msg);
  }






  
  public static void warn(String msg, Throwable t) {
    if (quietMode) {
      return;
    }
    System.err.println("log4j:WARN " + msg);
    if (t != null)
      t.printStackTrace(); 
  }
}
