package org.hamcrest.core;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

abstract class ShortcutCombination<T>
  extends BaseMatcher<T> {
  private final Iterable<Matcher<? super T>> matchers;
  
  public ShortcutCombination(Iterable<Matcher<? super T>> matchers) {
    this.matchers = matchers;
  }

  
  public abstract boolean matches(Object paramObject);

  
  public abstract void describeTo(Description paramDescription);
  
  protected boolean matches(Object o, boolean shortcut) {
    for (Matcher<? super T> matcher : this.matchers) {
      if (matcher.matches(o) == shortcut) {
        return shortcut;
      }
    } 
    return !shortcut;
  }
  
  public void describeTo(Description description, String operator) {
    description.appendList("(", " " + operator + " ", ")", this.matchers);
  }
}
