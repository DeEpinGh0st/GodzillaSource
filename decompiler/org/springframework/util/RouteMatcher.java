package org.springframework.util;

import java.util.Comparator;
import java.util.Map;
import org.springframework.lang.Nullable;

public interface RouteMatcher {
  Route parseRoute(String paramString);
  
  boolean isPattern(String paramString);
  
  String combine(String paramString1, String paramString2);
  
  boolean match(String paramString, Route paramRoute);
  
  @Nullable
  Map<String, String> matchAndExtract(String paramString, Route paramRoute);
  
  Comparator<String> getPatternComparator(Route paramRoute);
  
  public static interface Route {
    String value();
  }
}
