package org.hamcrest;

public interface Matcher<T> extends SelfDescribing {
  boolean matches(Object paramObject);
  
  void describeMismatch(Object paramObject, Description paramDescription);
  
  @Deprecated
  void _dont_implement_Matcher___instead_extend_BaseMatcher_();
}
