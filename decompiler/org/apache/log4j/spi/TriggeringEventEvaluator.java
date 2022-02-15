package org.apache.log4j.spi;

public interface TriggeringEventEvaluator {
  boolean isTriggeringEvent(LoggingEvent paramLoggingEvent);
}
