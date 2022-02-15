package org.springframework.core.env;

import java.util.Map;

public interface ConfigurableEnvironment extends Environment, ConfigurablePropertyResolver {
  void setActiveProfiles(String... paramVarArgs);
  
  void addActiveProfile(String paramString);
  
  void setDefaultProfiles(String... paramVarArgs);
  
  MutablePropertySources getPropertySources();
  
  Map<String, Object> getSystemProperties();
  
  Map<String, Object> getSystemEnvironment();
  
  void merge(ConfigurableEnvironment paramConfigurableEnvironment);
}
