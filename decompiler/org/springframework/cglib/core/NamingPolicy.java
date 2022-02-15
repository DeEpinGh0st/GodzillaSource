package org.springframework.cglib.core;

public interface NamingPolicy {
  String getClassName(String paramString1, String paramString2, Object paramObject, Predicate paramPredicate);
  
  boolean equals(Object paramObject);
}
