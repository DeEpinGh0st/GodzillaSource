package org.springframework.core.env;

import java.util.Map;
import java.util.Properties;































public class PropertiesPropertySource
  extends MapPropertySource
{
  public PropertiesPropertySource(String name, Properties source) {
    super(name, source);
  }
  
  protected PropertiesPropertySource(String name, Map<String, Object> source) {
    super(name, source);
  }


  
  public String[] getPropertyNames() {
    synchronized (this.source) {
      return super.getPropertyNames();
    } 
  }
}
