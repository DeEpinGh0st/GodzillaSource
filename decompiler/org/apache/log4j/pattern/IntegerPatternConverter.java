package org.apache.log4j.pattern;

import java.util.Date;

























public final class IntegerPatternConverter
  extends PatternConverter
{
  private static final IntegerPatternConverter INSTANCE = new IntegerPatternConverter();




  
  private IntegerPatternConverter() {
    super("Integer", "integer");
  }






  
  public static IntegerPatternConverter newInstance(String[] options) {
    return INSTANCE;
  }



  
  public void format(Object obj, StringBuffer toAppendTo) {
    if (obj instanceof Integer) {
      toAppendTo.append(obj.toString());
    }
    
    if (obj instanceof Date)
      toAppendTo.append(Long.toString(((Date)obj).getTime())); 
  }
}
