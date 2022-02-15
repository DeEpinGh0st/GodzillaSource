package org.springframework.core.log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;















































public final class LogDelegateFactory
{
  public static Log getCompositeLog(Log primaryLogger, Log secondaryLogger, Log... tertiaryLoggers) {
    List<Log> loggers = new ArrayList<>(2 + tertiaryLoggers.length);
    loggers.add(primaryLogger);
    loggers.add(secondaryLogger);
    Collections.addAll(loggers, tertiaryLoggers);
    return new CompositeLog(loggers);
  }








  
  public static Log getHiddenLog(Class<?> clazz) {
    return getHiddenLog(clazz.getName());
  }









  
  public static Log getHiddenLog(String category) {
    return LogFactory.getLog("_" + category);
  }
}
