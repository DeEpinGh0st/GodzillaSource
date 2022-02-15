package org.apache.log4j.pattern;

import org.apache.log4j.Layout;
import org.apache.log4j.spi.LoggingEvent;


























public final class LineSeparatorPatternConverter
  extends LoggingEventPatternConverter
{
  private static final LineSeparatorPatternConverter INSTANCE = new LineSeparatorPatternConverter();



  
  private final String lineSep;




  
  private LineSeparatorPatternConverter() {
    super("Line Sep", "lineSep");
    this.lineSep = Layout.LINE_SEP;
  }






  
  public static LineSeparatorPatternConverter newInstance(String[] options) {
    return INSTANCE;
  }



  
  public void format(LoggingEvent event, StringBuffer toAppendTo) {
    toAppendTo.append(this.lineSep);
  }



  
  public void format(Object obj, StringBuffer toAppendTo) {
    toAppendTo.append(this.lineSep);
  }
}
