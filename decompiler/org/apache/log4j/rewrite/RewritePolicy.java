package org.apache.log4j.rewrite;

import org.apache.log4j.spi.LoggingEvent;

public interface RewritePolicy {
  LoggingEvent rewrite(LoggingEvent paramLoggingEvent);
}
