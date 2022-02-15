package org.springframework.core.env;

import org.springframework.lang.Nullable;
































public class PropertySourcesPropertyResolver
  extends AbstractPropertyResolver
{
  @Nullable
  private final PropertySources propertySources;
  
  public PropertySourcesPropertyResolver(@Nullable PropertySources propertySources) {
    this.propertySources = propertySources;
  }


  
  public boolean containsProperty(String key) {
    if (this.propertySources != null) {
      for (PropertySource<?> propertySource : (Iterable<PropertySource<?>>)this.propertySources) {
        if (propertySource.containsProperty(key)) {
          return true;
        }
      } 
    }
    return false;
  }

  
  @Nullable
  public String getProperty(String key) {
    return getProperty(key, String.class, true);
  }

  
  @Nullable
  public <T> T getProperty(String key, Class<T> targetValueType) {
    return getProperty(key, targetValueType, true);
  }

  
  @Nullable
  protected String getPropertyAsRawString(String key) {
    return getProperty(key, String.class, false);
  }
  
  @Nullable
  protected <T> T getProperty(String key, Class<T> targetValueType, boolean resolveNestedPlaceholders) {
    if (this.propertySources != null) {
      for (PropertySource<?> propertySource : (Iterable<PropertySource<?>>)this.propertySources) {
        if (this.logger.isTraceEnabled()) {
          this.logger.trace("Searching for key '" + key + "' in PropertySource '" + propertySource
              .getName() + "'");
        }
        Object value = propertySource.getProperty(key);
        if (value != null) {
          if (resolveNestedPlaceholders && value instanceof String) {
            value = resolveNestedPlaceholders((String)value);
          }
          logKeyFound(key, propertySource, value);
          return convertValueIfNecessary(value, targetValueType);
        } 
      } 
    }
    if (this.logger.isTraceEnabled()) {
      this.logger.trace("Could not find key '" + key + "' in any property source");
    }
    return null;
  }












  
  protected void logKeyFound(String key, PropertySource<?> propertySource, Object value) {
    if (this.logger.isDebugEnabled())
      this.logger.debug("Found key '" + key + "' in PropertySource '" + propertySource.getName() + "' with value of type " + value
          .getClass().getSimpleName()); 
  }
}
