package org.springframework.core.env;

import java.security.AccessControlException;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.SpringProperties;
import org.springframework.core.convert.support.ConfigurableConversionService;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

















































































public abstract class AbstractEnvironment
  implements ConfigurableEnvironment
{
  public static final String IGNORE_GETENV_PROPERTY_NAME = "spring.getenv.ignore";
  public static final String ACTIVE_PROFILES_PROPERTY_NAME = "spring.profiles.active";
  public static final String DEFAULT_PROFILES_PROPERTY_NAME = "spring.profiles.default";
  protected static final String RESERVED_DEFAULT_PROFILE_NAME = "default";
  protected final Log logger = LogFactory.getLog(getClass());
  
  private final Set<String> activeProfiles = new LinkedHashSet<>();
  
  private final Set<String> defaultProfiles = new LinkedHashSet<>(getReservedDefaultProfiles());



  
  private final MutablePropertySources propertySources;


  
  private final ConfigurablePropertyResolver propertyResolver;



  
  public AbstractEnvironment() {
    this(new MutablePropertySources());
  }










  
  protected AbstractEnvironment(MutablePropertySources propertySources) {
    this.propertySources = propertySources;
    this.propertyResolver = createPropertyResolver(propertySources);
    customizePropertySources(propertySources);
  }







  
  protected ConfigurablePropertyResolver createPropertyResolver(MutablePropertySources propertySources) {
    return new PropertySourcesPropertyResolver(propertySources);
  }






  
  protected final ConfigurablePropertyResolver getPropertyResolver() {
    return this.propertyResolver;
  }












































  
  protected void customizePropertySources(MutablePropertySources propertySources) {}











































  
  protected Set<String> getReservedDefaultProfiles() {
    return Collections.singleton("default");
  }






  
  public String[] getActiveProfiles() {
    return StringUtils.toStringArray(doGetActiveProfiles());
  }








  
  protected Set<String> doGetActiveProfiles() {
    synchronized (this.activeProfiles) {
      if (this.activeProfiles.isEmpty()) {
        String profiles = doGetActiveProfilesProperty();
        if (StringUtils.hasText(profiles)) {
          setActiveProfiles(StringUtils.commaDelimitedListToStringArray(
                StringUtils.trimAllWhitespace(profiles)));
        }
      } 
      return this.activeProfiles;
    } 
  }





  
  @Nullable
  protected String doGetActiveProfilesProperty() {
    return getProperty("spring.profiles.active");
  }

  
  public void setActiveProfiles(String... profiles) {
    Assert.notNull(profiles, "Profile array must not be null");
    if (this.logger.isDebugEnabled()) {
      this.logger.debug("Activating profiles " + Arrays.<String>asList(profiles));
    }
    synchronized (this.activeProfiles) {
      this.activeProfiles.clear();
      for (String profile : profiles) {
        validateProfile(profile);
        this.activeProfiles.add(profile);
      } 
    } 
  }

  
  public void addActiveProfile(String profile) {
    if (this.logger.isDebugEnabled()) {
      this.logger.debug("Activating profile '" + profile + "'");
    }
    validateProfile(profile);
    doGetActiveProfiles();
    synchronized (this.activeProfiles) {
      this.activeProfiles.add(profile);
    } 
  }


  
  public String[] getDefaultProfiles() {
    return StringUtils.toStringArray(doGetDefaultProfiles());
  }











  
  protected Set<String> doGetDefaultProfiles() {
    synchronized (this.defaultProfiles) {
      if (this.defaultProfiles.equals(getReservedDefaultProfiles())) {
        String profiles = doGetDefaultProfilesProperty();
        if (StringUtils.hasText(profiles)) {
          setDefaultProfiles(StringUtils.commaDelimitedListToStringArray(
                StringUtils.trimAllWhitespace(profiles)));
        }
      } 
      return this.defaultProfiles;
    } 
  }





  
  @Nullable
  protected String doGetDefaultProfilesProperty() {
    return getProperty("spring.profiles.default");
  }









  
  public void setDefaultProfiles(String... profiles) {
    Assert.notNull(profiles, "Profile array must not be null");
    synchronized (this.defaultProfiles) {
      this.defaultProfiles.clear();
      for (String profile : profiles) {
        validateProfile(profile);
        this.defaultProfiles.add(profile);
      } 
    } 
  }

  
  @Deprecated
  public boolean acceptsProfiles(String... profiles) {
    Assert.notEmpty((Object[])profiles, "Must specify at least one profile");
    for (String profile : profiles) {
      if (StringUtils.hasLength(profile) && profile.charAt(0) == '!') {
        if (!isProfileActive(profile.substring(1))) {
          return true;
        }
      }
      else if (isProfileActive(profile)) {
        return true;
      } 
    } 
    return false;
  }

  
  public boolean acceptsProfiles(Profiles profiles) {
    Assert.notNull(profiles, "Profiles must not be null");
    return profiles.matches(this::isProfileActive);
  }





  
  protected boolean isProfileActive(String profile) {
    validateProfile(profile);
    Set<String> currentActiveProfiles = doGetActiveProfiles();
    return (currentActiveProfiles.contains(profile) || (currentActiveProfiles
      .isEmpty() && doGetDefaultProfiles().contains(profile)));
  }










  
  protected void validateProfile(String profile) {
    if (!StringUtils.hasText(profile)) {
      throw new IllegalArgumentException("Invalid profile [" + profile + "]: must contain text");
    }
    if (profile.charAt(0) == '!') {
      throw new IllegalArgumentException("Invalid profile [" + profile + "]: must not begin with ! operator");
    }
  }

  
  public MutablePropertySources getPropertySources() {
    return this.propertySources;
  }


  
  public Map<String, Object> getSystemProperties() {
    try {
      return System.getProperties();
    }
    catch (AccessControlException ex) {
      return new ReadOnlySystemAttributesMap()
        {
          @Nullable
          protected String getSystemAttribute(String attributeName) {
            try {
              return System.getProperty(attributeName);
            }
            catch (AccessControlException ex) {
              if (AbstractEnvironment.this.logger.isInfoEnabled()) {
                AbstractEnvironment.this.logger.info("Caught AccessControlException when accessing system property '" + attributeName + "'; its value will be returned [null]. Reason: " + ex
                    .getMessage());
              }
              return null;
            } 
          }
        };
    } 
  }


  
  public Map<String, Object> getSystemEnvironment() {
    if (suppressGetenvAccess()) {
      return Collections.emptyMap();
    }
    try {
      return (Map)System.getenv();
    }
    catch (AccessControlException ex) {
      return new ReadOnlySystemAttributesMap()
        {
          @Nullable
          protected String getSystemAttribute(String attributeName) {
            try {
              return System.getenv(attributeName);
            }
            catch (AccessControlException ex) {
              if (AbstractEnvironment.this.logger.isInfoEnabled()) {
                AbstractEnvironment.this.logger.info("Caught AccessControlException when accessing system environment variable '" + attributeName + "'; its value will be returned [null]. Reason: " + ex
                    .getMessage());
              }
              return null;
            } 
          }
        };
    } 
  }











  
  protected boolean suppressGetenvAccess() {
    return SpringProperties.getFlag("spring.getenv.ignore");
  }

  
  public void merge(ConfigurableEnvironment parent) {
    for (PropertySource<?> ps : (Iterable<PropertySource<?>>)parent.getPropertySources()) {
      if (!this.propertySources.contains(ps.getName())) {
        this.propertySources.addLast(ps);
      }
    } 
    String[] parentActiveProfiles = parent.getActiveProfiles();
    if (!ObjectUtils.isEmpty((Object[])parentActiveProfiles)) {
      synchronized (this.activeProfiles) {
        Collections.addAll(this.activeProfiles, parentActiveProfiles);
      } 
    }
    String[] parentDefaultProfiles = parent.getDefaultProfiles();
    if (!ObjectUtils.isEmpty((Object[])parentDefaultProfiles)) {
      synchronized (this.defaultProfiles) {
        this.defaultProfiles.remove("default");
        Collections.addAll(this.defaultProfiles, parentDefaultProfiles);
      } 
    }
  }






  
  public ConfigurableConversionService getConversionService() {
    return this.propertyResolver.getConversionService();
  }

  
  public void setConversionService(ConfigurableConversionService conversionService) {
    this.propertyResolver.setConversionService(conversionService);
  }

  
  public void setPlaceholderPrefix(String placeholderPrefix) {
    this.propertyResolver.setPlaceholderPrefix(placeholderPrefix);
  }

  
  public void setPlaceholderSuffix(String placeholderSuffix) {
    this.propertyResolver.setPlaceholderSuffix(placeholderSuffix);
  }

  
  public void setValueSeparator(@Nullable String valueSeparator) {
    this.propertyResolver.setValueSeparator(valueSeparator);
  }

  
  public void setIgnoreUnresolvableNestedPlaceholders(boolean ignoreUnresolvableNestedPlaceholders) {
    this.propertyResolver.setIgnoreUnresolvableNestedPlaceholders(ignoreUnresolvableNestedPlaceholders);
  }

  
  public void setRequiredProperties(String... requiredProperties) {
    this.propertyResolver.setRequiredProperties(requiredProperties);
  }

  
  public void validateRequiredProperties() throws MissingRequiredPropertiesException {
    this.propertyResolver.validateRequiredProperties();
  }






  
  public boolean containsProperty(String key) {
    return this.propertyResolver.containsProperty(key);
  }

  
  @Nullable
  public String getProperty(String key) {
    return this.propertyResolver.getProperty(key);
  }

  
  public String getProperty(String key, String defaultValue) {
    return this.propertyResolver.getProperty(key, defaultValue);
  }

  
  @Nullable
  public <T> T getProperty(String key, Class<T> targetType) {
    return this.propertyResolver.getProperty(key, targetType);
  }

  
  public <T> T getProperty(String key, Class<T> targetType, T defaultValue) {
    return this.propertyResolver.getProperty(key, targetType, defaultValue);
  }

  
  public String getRequiredProperty(String key) throws IllegalStateException {
    return this.propertyResolver.getRequiredProperty(key);
  }

  
  public <T> T getRequiredProperty(String key, Class<T> targetType) throws IllegalStateException {
    return this.propertyResolver.getRequiredProperty(key, targetType);
  }

  
  public String resolvePlaceholders(String text) {
    return this.propertyResolver.resolvePlaceholders(text);
  }

  
  public String resolveRequiredPlaceholders(String text) throws IllegalArgumentException {
    return this.propertyResolver.resolveRequiredPlaceholders(text);
  }


  
  public String toString() {
    return getClass().getSimpleName() + " {activeProfiles=" + this.activeProfiles + ", defaultProfiles=" + this.defaultProfiles + ", propertySources=" + this.propertySources + "}";
  }
}
