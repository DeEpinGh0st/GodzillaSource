package org.hamcrest;










public abstract class Condition<T>
{
  public static final NotMatched<Object> NOT_MATCHED = new NotMatched();



  
  private Condition() {}


  
  public final boolean matching(Matcher<T> match)
  {
    return matching(match, ""); } public final <U> Condition<U> then(Step<? super T, U> mapping) {
    return and(mapping);
  }
  
  public static <T> Condition<T> notMatched() {
    return NOT_MATCHED;
  }
  
  public static <T> Condition<T> matched(T theValue, Description mismatch) {
    return new Matched<T>(theValue, mismatch);
  }
  public abstract boolean matching(Matcher<T> paramMatcher, String paramString);
  public abstract <U> Condition<U> and(Step<? super T, U> paramStep);
  public static interface Step<I, O> {
    Condition<O> apply(I param1I, Description param1Description); }
  private static final class Matched<T> extends Condition<T> { private final T theValue;
    private Matched(T theValue, Description mismatch) {
      this.theValue = theValue;
      this.mismatch = mismatch;
    }
    private final Description mismatch;
    
    public boolean matching(Matcher<T> matcher, String message) {
      if (matcher.matches(this.theValue)) {
        return true;
      }
      this.mismatch.appendText(message);
      matcher.describeMismatch(this.theValue, this.mismatch);
      return false;
    }

    
    public <U> Condition<U> and(Condition.Step<? super T, U> next) {
      return next.apply(this.theValue, this.mismatch);
    } }
  private static final class NotMatched<T> extends Condition<T> { private NotMatched() {}
    
    public boolean matching(Matcher<T> match, String message) {
      return false;
    }
    public <U> Condition<U> and(Condition.Step<? super T, U> mapping) {
      return notMatched();
    } }

}
