package org.apache.log4j.helpers;

import org.apache.log4j.spi.LoggingEvent;































public abstract class PatternConverter
{
  public PatternConverter next;
  int min = -1;
  int max = Integer.MAX_VALUE;
  
  boolean leftAlign = false;

  
  protected PatternConverter() {}
  
  protected PatternConverter(FormattingInfo fi) {
    this.min = fi.min;
    this.max = fi.max;
    this.leftAlign = fi.leftAlign;
  }





  
  protected abstract String convert(LoggingEvent paramLoggingEvent);





  
  public void format(StringBuffer sbuf, LoggingEvent e) {
    String s = convert(e);
    
    if (s == null) {
      if (0 < this.min) {
        spacePad(sbuf, this.min);
      }
      return;
    } 
    int len = s.length();
    
    if (len > this.max) {
      sbuf.append(s.substring(len - this.max));
    } else if (len < this.min) {
      if (this.leftAlign) {
        sbuf.append(s);
        spacePad(sbuf, this.min - len);
      } else {
        
        spacePad(sbuf, this.min - len);
        sbuf.append(s);
      } 
    } else {
      
      sbuf.append(s);
    } 
  }
  static String[] SPACES = new String[] { " ", "  ", "    ", "        ", "                ", "                                " };






  
  public void spacePad(StringBuffer sbuf, int length) {
    while (length >= 32) {
      sbuf.append(SPACES[5]);
      length -= 32;
    } 
    
    for (int i = 4; i >= 0; i--) {
      if ((length & 1 << i) != 0)
        sbuf.append(SPACES[i]); 
    } 
  }
}
