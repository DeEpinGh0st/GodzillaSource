package org.apache.log4j;

import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.helpers.PatternConverter;
import org.apache.log4j.helpers.PatternParser;
import org.apache.log4j.pattern.BridgePatternConverter;
import org.apache.log4j.pattern.BridgePatternParser;
import org.apache.log4j.spi.LoggingEvent;





































































































































































































































































































































































































































public class EnhancedPatternLayout
  extends Layout
{
  public static final String DEFAULT_CONVERSION_PATTERN = "%m%n";
  public static final String TTCC_CONVERSION_PATTERN = "%r [%t] %p %c %x - %m%n";
  protected final int BUF_SIZE = 256;




  
  protected final int MAX_CAPACITY = 1024;




  
  public static final String PATTERN_RULE_REGISTRY = "PATTERN_RULE_REGISTRY";




  
  private PatternConverter head;




  
  private String conversionPattern;



  
  private boolean handlesExceptions;




  
  public EnhancedPatternLayout() {
    this("%m%n");
  }




  
  public EnhancedPatternLayout(String pattern) {
    this.conversionPattern = pattern;
    this.head = createPatternParser((pattern == null) ? "%m%n" : pattern).parse();
    
    if (this.head instanceof BridgePatternConverter) {
      this.handlesExceptions = !((BridgePatternConverter)this.head).ignoresThrowable();
    } else {
      this.handlesExceptions = false;
    } 
  }







  
  public void setConversionPattern(String conversionPattern) {
    this.conversionPattern = OptionConverter.convertSpecialChars(conversionPattern);
    
    this.head = createPatternParser(this.conversionPattern).parse();
    if (this.head instanceof BridgePatternConverter) {
      this.handlesExceptions = !((BridgePatternConverter)this.head).ignoresThrowable();
    } else {
      this.handlesExceptions = false;
    } 
  }




  
  public String getConversionPattern() {
    return this.conversionPattern;
  }








  
  protected PatternParser createPatternParser(String pattern) {
    return (PatternParser)new BridgePatternParser(pattern);
  }






  
  public void activateOptions() {}






  
  public String format(LoggingEvent event) {
    StringBuffer buf = new StringBuffer();
    PatternConverter c = this.head;
    for (; c != null; 
      c = c.next) {
      c.format(buf, event);
    }
    return buf.toString();
  }





  
  public boolean ignoresThrowable() {
    return !this.handlesExceptions;
  }
}
