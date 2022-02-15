package org.springframework.core.env;

import java.util.function.Predicate;





























































@FunctionalInterface
public interface Profiles
{
  boolean matches(Predicate<String> paramPredicate);
  
  static Profiles of(String... profiles) {
    return ProfilesParser.parse(profiles);
  }
}
