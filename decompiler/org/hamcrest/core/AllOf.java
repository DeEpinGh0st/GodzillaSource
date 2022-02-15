package org.hamcrest.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.hamcrest.Description;
import org.hamcrest.DiagnosingMatcher;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.SelfDescribing;




public class AllOf<T>
  extends DiagnosingMatcher<T>
{
  private final Iterable<Matcher<? super T>> matchers;
  
  public AllOf(Iterable<Matcher<? super T>> matchers) {
    this.matchers = matchers;
  }

  
  public boolean matches(Object o, Description mismatch) {
    for (Matcher<? super T> matcher : this.matchers) {
      if (!matcher.matches(o)) {
        mismatch.appendDescriptionOf((SelfDescribing)matcher).appendText(" ");
        matcher.describeMismatch(o, mismatch);
        return false;
      } 
    } 
    return true;
  }

  
  public void describeTo(Description description) {
    description.appendList("(", " and ", ")", this.matchers);
  }






  
  @Factory
  public static <T> Matcher<T> allOf(Iterable<Matcher<? super T>> matchers) {
    return (Matcher<T>)new AllOf<T>(matchers);
  }






  
  @Factory
  public static <T> Matcher<T> allOf(Matcher<? super T>... matchers) {
    return allOf(Arrays.asList(matchers));
  }






  
  @Factory
  public static <T> Matcher<T> allOf(Matcher<? super T> first, Matcher<? super T> second) {
    List<Matcher<? super T>> matchers = new ArrayList<Matcher<? super T>>(2);
    matchers.add(first);
    matchers.add(second);
    return allOf(matchers);
  }






  
  @Factory
  public static <T> Matcher<T> allOf(Matcher<? super T> first, Matcher<? super T> second, Matcher<? super T> third) {
    List<Matcher<? super T>> matchers = new ArrayList<Matcher<? super T>>(3);
    matchers.add(first);
    matchers.add(second);
    matchers.add(third);
    return allOf(matchers);
  }






  
  @Factory
  public static <T> Matcher<T> allOf(Matcher<? super T> first, Matcher<? super T> second, Matcher<? super T> third, Matcher<? super T> fourth) {
    List<Matcher<? super T>> matchers = new ArrayList<Matcher<? super T>>(4);
    matchers.add(first);
    matchers.add(second);
    matchers.add(third);
    matchers.add(fourth);
    return allOf(matchers);
  }






  
  @Factory
  public static <T> Matcher<T> allOf(Matcher<? super T> first, Matcher<? super T> second, Matcher<? super T> third, Matcher<? super T> fourth, Matcher<? super T> fifth) {
    List<Matcher<? super T>> matchers = new ArrayList<Matcher<? super T>>(5);
    matchers.add(first);
    matchers.add(second);
    matchers.add(third);
    matchers.add(fourth);
    matchers.add(fifth);
    return allOf(matchers);
  }






  
  @Factory
  public static <T> Matcher<T> allOf(Matcher<? super T> first, Matcher<? super T> second, Matcher<? super T> third, Matcher<? super T> fourth, Matcher<? super T> fifth, Matcher<? super T> sixth) {
    List<Matcher<? super T>> matchers = new ArrayList<Matcher<? super T>>(6);
    matchers.add(first);
    matchers.add(second);
    matchers.add(third);
    matchers.add(fourth);
    matchers.add(fifth);
    matchers.add(sixth);
    return allOf(matchers);
  }
}
