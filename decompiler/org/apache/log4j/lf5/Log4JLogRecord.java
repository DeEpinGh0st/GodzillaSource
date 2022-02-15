package org.apache.log4j.lf5;

import org.apache.log4j.spi.ThrowableInformation;



























































public class Log4JLogRecord
  extends LogRecord
{
  public boolean isSevereLevel() {
    boolean isSevere = false;
    
    if (LogLevel.ERROR.equals(getLevel()) || LogLevel.FATAL.equals(getLevel()))
    {
      isSevere = true;
    }
    
    return isSevere;
  }










  
  public void setThrownStackTrace(ThrowableInformation throwableInfo) {
    String[] stackTraceArray = throwableInfo.getThrowableStrRep();
    
    StringBuffer stackTrace = new StringBuffer();

    
    for (int i = 0; i < stackTraceArray.length; i++) {
      String nextLine = stackTraceArray[i] + "\n";
      stackTrace.append(nextLine);
    } 
    
    this._thrownStackTrace = stackTrace.toString();
  }
}
