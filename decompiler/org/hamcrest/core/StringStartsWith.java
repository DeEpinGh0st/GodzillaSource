package org.hamcrest.core;

import org.hamcrest.Factory;
import org.hamcrest.Matcher;




public class StringStartsWith
  extends SubstringMatcher
{
  public StringStartsWith(String substring) {
    super(substring);
  }

  
  protected boolean evalSubstringOf(String s) {
    return s.startsWith(this.substring);
  }

  
  protected String relationship() {
    return "starting with";
  }










  
  @Factory
  public static Matcher<String> startsWith(String prefix) {
    return (Matcher<String>)new StringStartsWith(prefix);
  }
}
