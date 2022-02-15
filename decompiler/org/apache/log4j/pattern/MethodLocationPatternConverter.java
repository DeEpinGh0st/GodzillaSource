package org.apache.log4j.pattern;

import org.apache.log4j.spi.LocationInfo;
import org.apache.log4j.spi.LoggingEvent;


























public final class MethodLocationPatternConverter
  extends LoggingEventPatternConverter
{
  private static final MethodLocationPatternConverter INSTANCE = new MethodLocationPatternConverter();




  
  private MethodLocationPatternConverter() {
    super("Method", "method");
  }






  
  public static MethodLocationPatternConverter newInstance(String[] options) {
    return INSTANCE;
  }



  
  public void format(LoggingEvent event, StringBuffer toAppendTo) {
    LocationInfo locationInfo = event.getLocationInformation();
    
    if (locationInfo != null)
      toAppendTo.append(locationInfo.getMethodName()); 
  }
}
