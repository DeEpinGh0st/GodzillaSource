package org.springframework.core.env;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.ConfigurableConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.PropertyPlaceholderHelper;


























public abstract class AbstractPropertyResolver
  implements ConfigurablePropertyResolver
{
  protected final Log logger = LogFactory.getLog(getClass());
  
  @Nullable
  private volatile ConfigurableConversionService conversionService;
  
  @Nullable
  private PropertyPlaceholderHelper nonStrictHelper;
  
  @Nullable
  private PropertyPlaceholderHelper strictHelper;
  
  private boolean ignoreUnresolvableNestedPlaceholders = false;
  
  private String placeholderPrefix = "${";
  
  private String placeholderSuffix = "}";
  @Nullable
  private String valueSeparator = ":";

  
  private final Set<String> requiredProperties = new LinkedHashSet<>();



  
  public ConfigurableConversionService getConversionService() {
    DefaultConversionService defaultConversionService;
    ConfigurableConversionService cs = this.conversionService;
    if (cs == null) {
      synchronized (this) {
        cs = this.conversionService;
        if (cs == null) {
          defaultConversionService = new DefaultConversionService();
          this.conversionService = (ConfigurableConversionService)defaultConversionService;
        } 
      } 
    }
    return (ConfigurableConversionService)defaultConversionService;
  }

  
  public void setConversionService(ConfigurableConversionService conversionService) {
    Assert.notNull(conversionService, "ConversionService must not be null");
    this.conversionService = conversionService;
  }






  
  public void setPlaceholderPrefix(String placeholderPrefix) {
    Assert.notNull(placeholderPrefix, "'placeholderPrefix' must not be null");
    this.placeholderPrefix = placeholderPrefix;
  }






  
  public void setPlaceholderSuffix(String placeholderSuffix) {
    Assert.notNull(placeholderSuffix, "'placeholderSuffix' must not be null");
    this.placeholderSuffix = placeholderSuffix;
  }








  
  public void setValueSeparator(@Nullable String valueSeparator) {
    this.valueSeparator = valueSeparator;
  }










  
  public void setIgnoreUnresolvableNestedPlaceholders(boolean ignoreUnresolvableNestedPlaceholders) {
    this.ignoreUnresolvableNestedPlaceholders = ignoreUnresolvableNestedPlaceholders;
  }

  
  public void setRequiredProperties(String... requiredProperties) {
    Collections.addAll(this.requiredProperties, requiredProperties);
  }

  
  public void validateRequiredProperties() {
    MissingRequiredPropertiesException ex = new MissingRequiredPropertiesException();
    for (String key : this.requiredProperties) {
      if (getProperty(key) == null) {
        ex.addMissingRequiredProperty(key);
      }
    } 
    if (!ex.getMissingRequiredProperties().isEmpty()) {
      throw ex;
    }
  }

  
  public boolean containsProperty(String key) {
    return (getProperty(key) != null);
  }

  
  @Nullable
  public String getProperty(String key) {
    return getProperty(key, String.class);
  }

  
  public String getProperty(String key, String defaultValue) {
    String value = getProperty(key);
    return (value != null) ? value : defaultValue;
  }

  
  public <T> T getProperty(String key, Class<T> targetType, T defaultValue) {
    T value = getProperty(key, targetType);
    return (value != null) ? value : defaultValue;
  }

  
  public String getRequiredProperty(String key) throws IllegalStateException {
    String value = getProperty(key);
    if (value == null) {
      throw new IllegalStateException("Required key '" + key + "' not found");
    }
    return value;
  }

  
  public <T> T getRequiredProperty(String key, Class<T> valueType) throws IllegalStateException {
    T value = getProperty(key, valueType);
    if (value == null) {
      throw new IllegalStateException("Required key '" + key + "' not found");
    }
    return value;
  }

  
  public String resolvePlaceholders(String text) {
    if (this.nonStrictHelper == null) {
      this.nonStrictHelper = createPlaceholderHelper(true);
    }
    return doResolvePlaceholders(text, this.nonStrictHelper);
  }

  
  public String resolveRequiredPlaceholders(String text) throws IllegalArgumentException {
    if (this.strictHelper == null) {
      this.strictHelper = createPlaceholderHelper(false);
    }
    return doResolvePlaceholders(text, this.strictHelper);
  }












  
  protected String resolveNestedPlaceholders(String value) {
    if (value.isEmpty()) {
      return value;
    }
    return this.ignoreUnresolvableNestedPlaceholders ? 
      resolvePlaceholders(value) : resolveRequiredPlaceholders(value);
  }
  
  private PropertyPlaceholderHelper createPlaceholderHelper(boolean ignoreUnresolvablePlaceholders) {
    return new PropertyPlaceholderHelper(this.placeholderPrefix, this.placeholderSuffix, this.valueSeparator, ignoreUnresolvablePlaceholders);
  }

  
  private String doResolvePlaceholders(String text, PropertyPlaceholderHelper helper) {
    return helper.replacePlaceholders(text, this::getPropertyAsRawString);
  }








  
  @Nullable
  protected <T> T convertValueIfNecessary(Object value, @Nullable Class<T> targetType) {
    ConversionService conversionService;
    if (targetType == null) {
      return (T)value;
    }
    ConfigurableConversionService configurableConversionService = this.conversionService;
    if (configurableConversionService == null) {

      
      if (ClassUtils.isAssignableValue(targetType, value)) {
        return (T)value;
      }
      conversionService = DefaultConversionService.getSharedInstance();
    } 
    return (T)conversionService.convert(value, targetType);
  }
  
  @Nullable
  protected abstract String getPropertyAsRawString(String paramString);
}
