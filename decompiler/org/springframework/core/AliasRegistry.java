package org.springframework.core;

public interface AliasRegistry {
  void registerAlias(String paramString1, String paramString2);
  
  void removeAlias(String paramString);
  
  boolean isAlias(String paramString);
  
  String[] getAliases(String paramString);
}
