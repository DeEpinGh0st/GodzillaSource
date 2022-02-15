package org.hamcrest;

import org.hamcrest.internal.ReflectiveTypeFinder;











public abstract class TypeSafeDiagnosingMatcher<T>
  extends BaseMatcher<T>
{
  private static final ReflectiveTypeFinder TYPE_FINDER = new ReflectiveTypeFinder("matchesSafely", 2, 0);


  
  private final Class<?> expectedType;



  
  protected abstract boolean matchesSafely(T paramT, Description paramDescription);



  
  protected TypeSafeDiagnosingMatcher(Class<?> expectedType) {
    this.expectedType = expectedType;
  }





  
  protected TypeSafeDiagnosingMatcher(ReflectiveTypeFinder typeFinder) {
    this.expectedType = typeFinder.findExpectedType(getClass());
  }



  
  protected TypeSafeDiagnosingMatcher() {
    this(TYPE_FINDER);
  }


  
  public final boolean matches(Object item) {
    return (item != null && this.expectedType.isInstance(item) && matchesSafely((T)item, new Description.NullDescription()));
  }




  
  public final void describeMismatch(Object item, Description mismatchDescription) {
    if (item == null || !this.expectedType.isInstance(item)) {
      super.describeMismatch(item, mismatchDescription);
    } else {
      matchesSafely((T)item, mismatchDescription);
    } 
  }
}
