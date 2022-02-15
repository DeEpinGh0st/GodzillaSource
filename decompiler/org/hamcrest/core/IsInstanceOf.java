package org.hamcrest.core;

import org.hamcrest.Description;
import org.hamcrest.DiagnosingMatcher;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;












public class IsInstanceOf
  extends DiagnosingMatcher<Object>
{
  private final Class<?> expectedClass;
  private final Class<?> matchableClass;
  
  public IsInstanceOf(Class<?> expectedClass) {
    this.expectedClass = expectedClass;
    this.matchableClass = matchableClass(expectedClass);
  }
  
  private static Class<?> matchableClass(Class<?> expectedClass) {
    if (boolean.class.equals(expectedClass)) return Boolean.class; 
    if (byte.class.equals(expectedClass)) return Byte.class; 
    if (char.class.equals(expectedClass)) return Character.class; 
    if (double.class.equals(expectedClass)) return Double.class; 
    if (float.class.equals(expectedClass)) return Float.class; 
    if (int.class.equals(expectedClass)) return Integer.class; 
    if (long.class.equals(expectedClass)) return Long.class; 
    if (short.class.equals(expectedClass)) return Short.class; 
    return expectedClass;
  }

  
  protected boolean matches(Object item, Description mismatch) {
    if (null == item) {
      mismatch.appendText("null");
      return false;
    } 
    
    if (!this.matchableClass.isInstance(item)) {
      mismatch.appendValue(item).appendText(" is a " + item.getClass().getName());
      return false;
    } 
    
    return true;
  }

  
  public void describeTo(Description description) {
    description.appendText("an instance of ").appendText(this.expectedClass.getName());
  }












  
  @Factory
  public static <T> Matcher<T> instanceOf(Class<?> type) {
    return (Matcher<T>)new IsInstanceOf(type);
  }














  
  @Factory
  public static <T> Matcher<T> any(Class<T> type) {
    return (Matcher<T>)new IsInstanceOf(type);
  }
}
