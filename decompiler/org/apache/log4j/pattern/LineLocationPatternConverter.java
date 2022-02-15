package org.apache.log4j.pattern;

import org.apache.log4j.spi.LocationInfo;
import org.apache.log4j.spi.LoggingEvent;


























public final class LineLocationPatternConverter
  extends LoggingEventPatternConverter
{
  private static final LineLocationPatternConverter INSTANCE = new LineLocationPatternConverter();




  
  private LineLocationPatternConverter() {
    super("Line", "line");
  }






  
  public static LineLocationPatternConverter newInstance(String[] options) {
    return INSTANCE;
  }



  
  public void format(LoggingEvent event, StringBuffer output) {
    LocationInfo locationInfo = event.getLocationInformation();
    
    if (locationInfo != null)
      output.append(locationInfo.getLineNumber()); 
  }
}
