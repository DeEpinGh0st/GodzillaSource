package org.hamcrest.core;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;






public class IsAnything<T>
  extends BaseMatcher<T>
{
  private final String message;
  
  public IsAnything() {
    this("ANYTHING");
  }
  
  public IsAnything(String message) {
    this.message = message;
  }

  
  public boolean matches(Object o) {
    return true;
  }

  
  public void describeTo(Description description) {
    description.appendText(this.message);
  }



  
  @Factory
  public static Matcher<Object> anything() {
    return (Matcher<Object>)new IsAnything();
  }







  
  @Factory
  public static Matcher<Object> anything(String description) {
    return (Matcher<Object>)new IsAnything(description);
  }
}
