package org.hamcrest;






public abstract class DiagnosingMatcher<T>
  extends BaseMatcher<T>
{
  public final boolean matches(Object item) {
    return matches(item, Description.NONE);
  }

  
  public final void describeMismatch(Object item, Description mismatchDescription) {
    matches(item, mismatchDescription);
  }
  
  protected abstract boolean matches(Object paramObject, Description paramDescription);
}
