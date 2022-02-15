package org.hamcrest.core;

import org.hamcrest.Factory;
import org.hamcrest.Matcher;




public class StringEndsWith
  extends SubstringMatcher
{
  public StringEndsWith(String substring) {
    super(substring);
  }

  
  protected boolean evalSubstringOf(String s) {
    return s.endsWith(this.substring);
  }

  
  protected String relationship() {
    return "ending with";
  }










  
  @Factory
  public static Matcher<String> endsWith(String suffix) {
    return (Matcher<String>)new StringEndsWith(suffix);
  }
}
