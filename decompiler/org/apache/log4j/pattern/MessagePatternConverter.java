package org.apache.log4j.pattern;

import org.apache.log4j.spi.LoggingEvent;

























public final class MessagePatternConverter
  extends LoggingEventPatternConverter
{
  private static final MessagePatternConverter INSTANCE = new MessagePatternConverter();




  
  private MessagePatternConverter() {
    super("Message", "message");
  }






  
  public static MessagePatternConverter newInstance(String[] options) {
    return INSTANCE;
  }



  
  public void format(LoggingEvent event, StringBuffer toAppendTo) {
    toAppendTo.append(event.getRenderedMessage());
  }
}
