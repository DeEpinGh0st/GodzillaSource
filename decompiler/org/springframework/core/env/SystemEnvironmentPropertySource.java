package org.springframework.core.env;

import java.util.Map;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;































































public class SystemEnvironmentPropertySource
  extends MapPropertySource
{
  public SystemEnvironmentPropertySource(String name, Map<String, Object> source) {
    super(name, source);
  }






  
  public boolean containsProperty(String name) {
    return (getProperty(name) != null);
  }





  
  @Nullable
  public Object getProperty(String name) {
    String actualName = resolvePropertyName(name);
    if (this.logger.isDebugEnabled() && !name.equals(actualName)) {
      this.logger.debug("PropertySource '" + getName() + "' does not contain property '" + name + "', but found equivalent '" + actualName + "'");
    }
    
    return super.getProperty(actualName);
  }





  
  protected final String resolvePropertyName(String name) {
    Assert.notNull(name, "Property name must not be null");
    String resolvedName = checkPropertyName(name);
    if (resolvedName != null) {
      return resolvedName;
    }
    String uppercasedName = name.toUpperCase();
    if (!name.equals(uppercasedName)) {
      resolvedName = checkPropertyName(uppercasedName);
      if (resolvedName != null) {
        return resolvedName;
      }
    } 
    return name;
  }

  
  @Nullable
  private String checkPropertyName(String name) {
    if (containsKey(name)) {
      return name;
    }
    
    String noDotName = name.replace('.', '_');
    if (!name.equals(noDotName) && containsKey(noDotName)) {
      return noDotName;
    }
    
    String noHyphenName = name.replace('-', '_');
    if (!name.equals(noHyphenName) && containsKey(noHyphenName)) {
      return noHyphenName;
    }
    
    String noDotNoHyphenName = noDotName.replace('-', '_');
    if (!noDotName.equals(noDotNoHyphenName) && containsKey(noDotNoHyphenName)) {
      return noDotNoHyphenName;
    }
    
    return null;
  }
  
  private boolean containsKey(String name) {
    return isSecurityManagerPresent() ? this.source.keySet().contains(name) : this.source.containsKey(name);
  }
  
  protected boolean isSecurityManagerPresent() {
    return (System.getSecurityManager() != null);
  }
}
