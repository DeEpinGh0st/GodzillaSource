package org.apache.log4j.lf5;















































public class PassingLogRecordFilter
  implements LogRecordFilter
{
  public boolean passes(LogRecord record) {
    return true;
  }
  
  public void reset() {}
}
