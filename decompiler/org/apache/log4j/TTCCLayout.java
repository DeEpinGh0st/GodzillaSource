package org.apache.log4j;

import org.apache.log4j.helpers.DateLayout;
import org.apache.log4j.spi.LoggingEvent;







































































public class TTCCLayout
  extends DateLayout
{
  private boolean threadPrinting = true;
  private boolean categoryPrefixing = true;
  private boolean contextPrinting = true;
  protected final StringBuffer buf = new StringBuffer(256);







  
  public TTCCLayout() {
    setDateFormat("RELATIVE", null);
  }









  
  public TTCCLayout(String dateFormatType) {
    setDateFormat(dateFormatType);
  }






  
  public void setThreadPrinting(boolean threadPrinting) {
    this.threadPrinting = threadPrinting;
  }




  
  public boolean getThreadPrinting() {
    return this.threadPrinting;
  }





  
  public void setCategoryPrefixing(boolean categoryPrefixing) {
    this.categoryPrefixing = categoryPrefixing;
  }




  
  public boolean getCategoryPrefixing() {
    return this.categoryPrefixing;
  }






  
  public void setContextPrinting(boolean contextPrinting) {
    this.contextPrinting = contextPrinting;
  }




  
  public boolean getContextPrinting() {
    return this.contextPrinting;
  }














  
  public String format(LoggingEvent event) {
    this.buf.setLength(0);
    
    dateFormat(this.buf, event);
    
    if (this.threadPrinting) {
      this.buf.append('[');
      this.buf.append(event.getThreadName());
      this.buf.append("] ");
    } 
    this.buf.append(event.getLevel().toString());
    this.buf.append(' ');
    
    if (this.categoryPrefixing) {
      this.buf.append(event.getLoggerName());
      this.buf.append(' ');
    } 
    
    if (this.contextPrinting) {
      String ndc = event.getNDC();
      
      if (ndc != null) {
        this.buf.append(ndc);
        this.buf.append(' ');
      } 
    } 
    this.buf.append("- ");
    this.buf.append(event.getRenderedMessage());
    this.buf.append(LINE_SEP);
    return this.buf.toString();
  }







  
  public boolean ignoresThrowable() {
    return true;
  }
}
