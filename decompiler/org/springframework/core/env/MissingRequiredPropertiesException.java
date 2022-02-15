package org.springframework.core.env;

import java.util.LinkedHashSet;
import java.util.Set;


























public class MissingRequiredPropertiesException
  extends IllegalStateException
{
  private final Set<String> missingRequiredProperties = new LinkedHashSet<>();

  
  void addMissingRequiredProperty(String key) {
    this.missingRequiredProperties.add(key);
  }

  
  public String getMessage() {
    return "The following properties were declared as required but could not be resolved: " + 
      getMissingRequiredProperties();
  }






  
  public Set<String> getMissingRequiredProperties() {
    return this.missingRequiredProperties;
  }
}
