package org.apache.log4j.pattern;

import org.apache.log4j.spi.LoggingEvent;


























public class SequenceNumberPatternConverter
  extends LoggingEventPatternConverter
{
  private static final SequenceNumberPatternConverter INSTANCE = new SequenceNumberPatternConverter();




  
  private SequenceNumberPatternConverter() {
    super("Sequence Number", "sn");
  }






  
  public static SequenceNumberPatternConverter newInstance(String[] options) {
    return INSTANCE;
  }



  
  public void format(LoggingEvent event, StringBuffer toAppendTo) {
    toAppendTo.append("0");
  }
}
