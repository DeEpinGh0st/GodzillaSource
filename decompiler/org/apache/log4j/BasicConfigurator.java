package org.apache.log4j;









































public class BasicConfigurator
{
  public static void configure() {
    Logger root = Logger.getRootLogger();
    root.addAppender(new ConsoleAppender(new PatternLayout("%r [%t] %p %c %x - %m%n")));
  }







  
  public static void configure(Appender appender) {
    Logger root = Logger.getRootLogger();
    root.addAppender(appender);
  }








  
  public static void resetConfiguration() {
    LogManager.resetConfiguration();
  }
}
