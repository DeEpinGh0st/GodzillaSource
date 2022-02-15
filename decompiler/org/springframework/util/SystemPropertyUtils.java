package org.springframework.util;

import org.springframework.lang.Nullable;







































public abstract class SystemPropertyUtils
{
  public static final String PLACEHOLDER_PREFIX = "${";
  public static final String PLACEHOLDER_SUFFIX = "}";
  public static final String VALUE_SEPARATOR = ":";
  private static final PropertyPlaceholderHelper strictHelper = new PropertyPlaceholderHelper("${", "}", ":", false);

  
  private static final PropertyPlaceholderHelper nonStrictHelper = new PropertyPlaceholderHelper("${", "}", ":", true);











  
  public static String resolvePlaceholders(String text) {
    return resolvePlaceholders(text, false);
  }












  
  public static String resolvePlaceholders(String text, boolean ignoreUnresolvablePlaceholders) {
    if (text.isEmpty()) {
      return text;
    }
    PropertyPlaceholderHelper helper = ignoreUnresolvablePlaceholders ? nonStrictHelper : strictHelper;
    return helper.replacePlaceholders(text, new SystemPropertyPlaceholderResolver(text));
  }


  
  private static class SystemPropertyPlaceholderResolver
    implements PropertyPlaceholderHelper.PlaceholderResolver
  {
    private final String text;


    
    public SystemPropertyPlaceholderResolver(String text) {
      this.text = text;
    }

    
    @Nullable
    public String resolvePlaceholder(String placeholderName) {
      try {
        String propVal = System.getProperty(placeholderName);
        if (propVal == null)
        {
          propVal = System.getenv(placeholderName);
        }
        return propVal;
      }
      catch (Throwable ex) {
        System.err.println("Could not resolve placeholder '" + placeholderName + "' in [" + this.text + "] as system property: " + ex);
        
        return null;
      } 
    }
  }
}
