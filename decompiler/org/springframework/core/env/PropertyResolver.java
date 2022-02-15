package org.springframework.core.env;

import org.springframework.lang.Nullable;

public interface PropertyResolver {
  boolean containsProperty(String paramString);
  
  @Nullable
  String getProperty(String paramString);
  
  String getProperty(String paramString1, String paramString2);
  
  @Nullable
  <T> T getProperty(String paramString, Class<T> paramClass);
  
  <T> T getProperty(String paramString, Class<T> paramClass, T paramT);
  
  String getRequiredProperty(String paramString) throws IllegalStateException;
  
  <T> T getRequiredProperty(String paramString, Class<T> paramClass) throws IllegalStateException;
  
  String resolvePlaceholders(String paramString);
  
  String resolveRequiredPlaceholders(String paramString) throws IllegalArgumentException;
}
