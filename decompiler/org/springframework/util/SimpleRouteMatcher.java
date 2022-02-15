package org.springframework.util;

import java.util.Comparator;
import java.util.Map;
import org.springframework.lang.Nullable;

































public class SimpleRouteMatcher
  implements RouteMatcher
{
  private final PathMatcher pathMatcher;
  
  public SimpleRouteMatcher(PathMatcher pathMatcher) {
    Assert.notNull(pathMatcher, "PathMatcher is required");
    this.pathMatcher = pathMatcher;
  }



  
  public PathMatcher getPathMatcher() {
    return this.pathMatcher;
  }


  
  public RouteMatcher.Route parseRoute(String route) {
    return new DefaultRoute(route);
  }

  
  public boolean isPattern(String route) {
    return this.pathMatcher.isPattern(route);
  }

  
  public String combine(String pattern1, String pattern2) {
    return this.pathMatcher.combine(pattern1, pattern2);
  }

  
  public boolean match(String pattern, RouteMatcher.Route route) {
    return this.pathMatcher.match(pattern, route.value());
  }

  
  @Nullable
  public Map<String, String> matchAndExtract(String pattern, RouteMatcher.Route route) {
    if (!match(pattern, route)) {
      return null;
    }
    return this.pathMatcher.extractUriTemplateVariables(pattern, route.value());
  }

  
  public Comparator<String> getPatternComparator(RouteMatcher.Route route) {
    return this.pathMatcher.getPatternComparator(route.value());
  }
  
  private static class DefaultRoute
    implements RouteMatcher.Route
  {
    private final String path;
    
    DefaultRoute(String path) {
      this.path = path;
    }

    
    public String value() {
      return this.path;
    }

    
    public String toString() {
      return value();
    }
  }
}
