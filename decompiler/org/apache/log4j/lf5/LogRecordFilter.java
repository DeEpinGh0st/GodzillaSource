package org.apache.log4j.lf5;

public interface LogRecordFilter {
  boolean passes(LogRecord paramLogRecord);
}
