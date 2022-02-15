package org.hamcrest;

import org.hamcrest.internal.ReflectiveTypeFinder;






public abstract class FeatureMatcher<T, U>
  extends TypeSafeDiagnosingMatcher<T>
{
  private static final ReflectiveTypeFinder TYPE_FINDER = new ReflectiveTypeFinder("featureValueOf", 1, 0);

  
  private final Matcher<? super U> subMatcher;

  
  private final String featureDescription;
  
  private final String featureName;

  
  public FeatureMatcher(Matcher<? super U> subMatcher, String featureDescription, String featureName) {
    super(TYPE_FINDER);
    this.subMatcher = subMatcher;
    this.featureDescription = featureDescription;
    this.featureName = featureName;
  }



  
  protected abstract U featureValueOf(T paramT);



  
  protected boolean matchesSafely(T actual, Description mismatch) {
    U featureValue = featureValueOf(actual);
    if (!this.subMatcher.matches(featureValue)) {
      mismatch.appendText(this.featureName).appendText(" ");
      this.subMatcher.describeMismatch(featureValue, mismatch);
      return false;
    } 
    return true;
  }

  
  public final void describeTo(Description description) {
    description.appendText(this.featureDescription).appendText(" ").appendDescriptionOf(this.subMatcher);
  }
}
