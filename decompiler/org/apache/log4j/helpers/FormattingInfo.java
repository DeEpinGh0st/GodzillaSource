package org.apache.log4j.helpers;



























public class FormattingInfo
{
  int min = -1;
  int max = Integer.MAX_VALUE;
  boolean leftAlign = false;
  
  void reset() {
    this.min = -1;
    this.max = Integer.MAX_VALUE;
    this.leftAlign = false;
  }
  
  void dump() {
    LogLog.debug("min=" + this.min + ", max=" + this.max + ", leftAlign=" + this.leftAlign);
  }
}
