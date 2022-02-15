package org.springframework.core.io;

import org.springframework.lang.Nullable;

public interface ResourceLoader {
  public static final String CLASSPATH_URL_PREFIX = "classpath:";
  
  Resource getResource(String paramString);
  
  @Nullable
  ClassLoader getClassLoader();
}
