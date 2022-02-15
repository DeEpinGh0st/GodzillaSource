package org.springframework.core.metrics;






























public interface ApplicationStartup
{
  public static final ApplicationStartup DEFAULT = new DefaultApplicationStartup();
  
  StartupStep start(String paramString);
}
