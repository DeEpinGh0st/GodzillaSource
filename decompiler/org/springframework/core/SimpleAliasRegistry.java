package org.springframework.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.util.StringValueResolver;





























public class SimpleAliasRegistry
  implements AliasRegistry
{
  protected final Log logger = LogFactory.getLog(getClass());

  
  private final Map<String, String> aliasMap = new ConcurrentHashMap<>(16);


  
  public void registerAlias(String name, String alias) {
    Assert.hasText(name, "'name' must not be empty");
    Assert.hasText(alias, "'alias' must not be empty");
    synchronized (this.aliasMap) {
      if (alias.equals(name)) {
        this.aliasMap.remove(alias);
        if (this.logger.isDebugEnabled()) {
          this.logger.debug("Alias definition '" + alias + "' ignored since it points to same name");
        }
      } else {
        
        String registeredName = this.aliasMap.get(alias);
        if (registeredName != null) {
          if (registeredName.equals(name)) {
            return;
          }
          
          if (!allowAliasOverriding()) {
            throw new IllegalStateException("Cannot define alias '" + alias + "' for name '" + name + "': It is already registered for name '" + registeredName + "'.");
          }
          
          if (this.logger.isDebugEnabled()) {
            this.logger.debug("Overriding alias '" + alias + "' definition for registered name '" + registeredName + "' with new target name '" + name + "'");
          }
        } 
        
        checkForAliasCircle(name, alias);
        this.aliasMap.put(alias, name);
        if (this.logger.isTraceEnabled()) {
          this.logger.trace("Alias definition '" + alias + "' registered for name '" + name + "'");
        }
      } 
    } 
  }




  
  protected boolean allowAliasOverriding() {
    return true;
  }






  
  public boolean hasAlias(String name, String alias) {
    String registeredName = this.aliasMap.get(alias);
    return (ObjectUtils.nullSafeEquals(registeredName, name) || (registeredName != null && 
      hasAlias(name, registeredName)));
  }

  
  public void removeAlias(String alias) {
    synchronized (this.aliasMap) {
      String name = this.aliasMap.remove(alias);
      if (name == null) {
        throw new IllegalStateException("No alias '" + alias + "' registered");
      }
    } 
  }

  
  public boolean isAlias(String name) {
    return this.aliasMap.containsKey(name);
  }

  
  public String[] getAliases(String name) {
    List<String> result = new ArrayList<>();
    synchronized (this.aliasMap) {
      retrieveAliases(name, result);
    } 
    return StringUtils.toStringArray(result);
  }





  
  private void retrieveAliases(String name, List<String> result) {
    this.aliasMap.forEach((alias, registeredName) -> {
          if (registeredName.equals(name)) {
            result.add(alias);
            retrieveAliases(alias, result);
          } 
        });
  }







  
  public void resolveAliases(StringValueResolver valueResolver) {
    Assert.notNull(valueResolver, "StringValueResolver must not be null");
    synchronized (this.aliasMap) {
      Map<String, String> aliasCopy = new HashMap<>(this.aliasMap);
      aliasCopy.forEach((alias, registeredName) -> {
            String resolvedAlias = valueResolver.resolveStringValue(alias);
            String resolvedName = valueResolver.resolveStringValue(registeredName);
            if (resolvedAlias == null || resolvedName == null || resolvedAlias.equals(resolvedName)) {
              this.aliasMap.remove(alias);
            } else if (!resolvedAlias.equals(alias)) {
              String existingName = this.aliasMap.get(resolvedAlias);
              if (existingName != null) {
                if (existingName.equals(resolvedName)) {
                  this.aliasMap.remove(alias);
                  return;
                } 
                throw new IllegalStateException("Cannot register resolved alias '" + resolvedAlias + "' (original: '" + alias + "') for name '" + resolvedName + "': It is already registered for name '" + registeredName + "'.");
              } 
              checkForAliasCircle(resolvedName, resolvedAlias);
              this.aliasMap.remove(alias);
              this.aliasMap.put(resolvedAlias, resolvedName);
            } else if (!registeredName.equals(resolvedName)) {
              this.aliasMap.put(alias, resolvedName);
            } 
          });
    } 
  }















  
  protected void checkForAliasCircle(String name, String alias) {
    if (hasAlias(alias, name)) {
      throw new IllegalStateException("Cannot register alias '" + alias + "' for name '" + name + "': Circular reference - '" + name + "' is a direct or indirect alias for '" + alias + "' already");
    }
  }







  
  public String canonicalName(String name) {
    String canonicalName = name;

    
    while (true) {
      String resolvedName = this.aliasMap.get(canonicalName);
      if (resolvedName != null) {
        canonicalName = resolvedName;
      }
      
      if (resolvedName == null)
        return canonicalName; 
    } 
  }
}
