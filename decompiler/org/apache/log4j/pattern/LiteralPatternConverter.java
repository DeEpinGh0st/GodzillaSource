package org.apache.log4j.pattern;

import org.apache.log4j.spi.LoggingEvent;






























public final class LiteralPatternConverter
  extends LoggingEventPatternConverter
{
  private final String literal;
  
  public LiteralPatternConverter(String literal) {
    super("Literal", "literal");
    this.literal = literal;
  }



  
  public void format(LoggingEvent event, StringBuffer toAppendTo) {
    toAppendTo.append(this.literal);
  }



  
  public void format(Object obj, StringBuffer toAppendTo) {
    toAppendTo.append(this.literal);
  }
}
