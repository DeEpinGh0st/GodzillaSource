package org.springframework.core.annotation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.lang.Nullable;



























enum IntrospectionFailureLogger
{
  DEBUG
  {
    public boolean isEnabled() {
      return getLogger().isDebugEnabled();
    }
    
    public void log(String message) {
      getLogger().debug(message);
    }
  },
  
  INFO
  {
    public boolean isEnabled() {
      return getLogger().isInfoEnabled();
    }
    
    public void log(String message) {
      getLogger().info(message);
    }
  };

  
  @Nullable
  private static Log logger;

  
  void log(String message, @Nullable Object source, Exception ex) {
    String on = (source != null) ? (" on " + source) : "";
    log(message + on + ": " + ex);
  }





  
  private static Log getLogger() {
    Log logger = IntrospectionFailureLogger.logger;
    if (logger == null) {
      logger = LogFactory.getLog(MergedAnnotation.class);
      IntrospectionFailureLogger.logger = logger;
    } 
    return logger;
  }
  
  abstract boolean isEnabled();
  
  abstract void log(String paramString);
}
