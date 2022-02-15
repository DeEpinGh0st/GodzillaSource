package org.springframework.core.env;



































































public class StandardEnvironment
  extends AbstractEnvironment
{
  public static final String SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME = "systemEnvironment";
  public static final String SYSTEM_PROPERTIES_PROPERTY_SOURCE_NAME = "systemProperties";
  
  public StandardEnvironment() {}
  
  protected StandardEnvironment(MutablePropertySources propertySources) {
    super(propertySources);
  }















  
  protected void customizePropertySources(MutablePropertySources propertySources) {
    propertySources.addLast(new PropertiesPropertySource("systemProperties", 
          getSystemProperties()));
    propertySources.addLast(new SystemEnvironmentPropertySource("systemEnvironment", 
          getSystemEnvironment()));
  }
}
