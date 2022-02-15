package org.hamcrest;
import org.hamcrest.core.AllOf;
import org.hamcrest.core.AnyOf;
import org.hamcrest.core.CombinableMatcher;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsCollectionContaining;
import org.hamcrest.core.IsInstanceOf;
import org.hamcrest.core.IsNot;
import org.hamcrest.core.IsNull;

public class CoreMatchers {
  public static <T> Matcher<T> allOf(Iterable<Matcher<? super T>> matchers) {
    return AllOf.allOf(matchers);
  }






  
  public static <T> Matcher<T> allOf(Matcher<? super T>... matchers) {
    return AllOf.allOf((Matcher[])matchers);
  }






  
  public static <T> Matcher<T> allOf(Matcher<? super T> first, Matcher<? super T> second) {
    return AllOf.allOf(first, second);
  }






  
  public static <T> Matcher<T> allOf(Matcher<? super T> first, Matcher<? super T> second, Matcher<? super T> third) {
    return AllOf.allOf(first, second, third);
  }






  
  public static <T> Matcher<T> allOf(Matcher<? super T> first, Matcher<? super T> second, Matcher<? super T> third, Matcher<? super T> fourth) {
    return AllOf.allOf(first, second, third, fourth);
  }






  
  public static <T> Matcher<T> allOf(Matcher<? super T> first, Matcher<? super T> second, Matcher<? super T> third, Matcher<? super T> fourth, Matcher<? super T> fifth) {
    return AllOf.allOf(first, second, third, fourth, fifth);
  }






  
  public static <T> Matcher<T> allOf(Matcher<? super T> first, Matcher<? super T> second, Matcher<? super T> third, Matcher<? super T> fourth, Matcher<? super T> fifth, Matcher<? super T> sixth) {
    return AllOf.allOf(first, second, third, fourth, fifth, sixth);
  }






  
  public static <T> AnyOf<T> anyOf(Iterable<Matcher<? super T>> matchers) {
    return AnyOf.anyOf(matchers);
  }






  
  public static <T> AnyOf<T> anyOf(Matcher<T> first, Matcher<? super T> second, Matcher<? super T> third) {
    return AnyOf.anyOf(first, second, third);
  }






  
  public static <T> AnyOf<T> anyOf(Matcher<T> first, Matcher<? super T> second, Matcher<? super T> third, Matcher<? super T> fourth) {
    return AnyOf.anyOf(first, second, third, fourth);
  }






  
  public static <T> AnyOf<T> anyOf(Matcher<T> first, Matcher<? super T> second, Matcher<? super T> third, Matcher<? super T> fourth, Matcher<? super T> fifth) {
    return AnyOf.anyOf(first, second, third, fourth, fifth);
  }






  
  public static <T> AnyOf<T> anyOf(Matcher<T> first, Matcher<? super T> second, Matcher<? super T> third, Matcher<? super T> fourth, Matcher<? super T> fifth, Matcher<? super T> sixth) {
    return AnyOf.anyOf(first, second, third, fourth, fifth, sixth);
  }






  
  public static <T> AnyOf<T> anyOf(Matcher<T> first, Matcher<? super T> second) {
    return AnyOf.anyOf(first, second);
  }






  
  public static <T> AnyOf<T> anyOf(Matcher<? super T>... matchers) {
    return AnyOf.anyOf((Matcher[])matchers);
  }






  
  public static <LHS> CombinableMatcher.CombinableBothMatcher<LHS> both(Matcher<? super LHS> matcher) {
    return CombinableMatcher.both(matcher);
  }






  
  public static <LHS> CombinableMatcher.CombinableEitherMatcher<LHS> either(Matcher<? super LHS> matcher) {
    return CombinableMatcher.either(matcher);
  }














  
  public static <T> Matcher<T> describedAs(String description, Matcher<T> matcher, Object... values) {
    return DescribedAs.describedAs(description, matcher, values);
  }











  
  public static <U> Matcher<Iterable<U>> everyItem(Matcher<U> itemMatcher) {
    return Every.everyItem(itemMatcher);
  }








  
  public static <T> Matcher<T> is(T value) {
    return Is.is(value);
  }









  
  public static <T> Matcher<T> is(Matcher<T> matcher) {
    return Is.is(matcher);
  }










  
  public static <T> Matcher<T> is(Class<T> type) {
    return Is.is(type);
  }








  
  public static <T> Matcher<T> isA(Class<T> type) {
    return Is.isA(type);
  }



  
  public static Matcher<Object> anything() {
    return IsAnything.anything();
  }







  
  public static Matcher<Object> anything(String description) {
    return IsAnything.anything(description);
  }












  
  public static <T> Matcher<Iterable<? super T>> hasItem(T item) {
    return IsCollectionContaining.hasItem(item);
  }












  
  public static <T> Matcher<Iterable<? super T>> hasItem(Matcher<? super T> itemMatcher) {
    return IsCollectionContaining.hasItem(itemMatcher);
  }












  
  public static <T> Matcher<Iterable<T>> hasItems(T... items) {
    return IsCollectionContaining.hasItems((Object[])items);
  }












  
  public static <T> Matcher<Iterable<T>> hasItems(Matcher<? super T>... itemMatchers) {
    return IsCollectionContaining.hasItems((Matcher[])itemMatchers);
  }






















  
  public static <T> Matcher<T> equalTo(T operand) {
    return IsEqual.equalTo(operand);
  }












  
  public static <T> Matcher<T> any(Class<T> type) {
    return IsInstanceOf.any(type);
  }










  
  public static <T> Matcher<T> instanceOf(Class<?> type) {
    return IsInstanceOf.instanceOf(type);
  }










  
  public static <T> Matcher<T> not(Matcher<T> matcher) {
    return IsNot.not(matcher);
  }











  
  public static <T> Matcher<T> not(T value) {
    return IsNot.not(value);
  }






  
  public static Matcher<Object> nullValue() {
    return IsNull.nullValue();
  }










  
  public static <T> Matcher<T> nullValue(Class<T> type) {
    return IsNull.nullValue(type);
  }








  
  public static Matcher<Object> notNullValue() {
    return IsNull.notNullValue();
  }












  
  public static <T> Matcher<T> notNullValue(Class<T> type) {
    return IsNull.notNullValue(type);
  }







  
  public static <T> Matcher<T> sameInstance(T target) {
    return IsSame.sameInstance(target);
  }







  
  public static <T> Matcher<T> theInstance(T target) {
    return IsSame.theInstance(target);
  }










  
  public static Matcher<String> containsString(String substring) {
    return StringContains.containsString(substring);
  }










  
  public static Matcher<String> startsWith(String prefix) {
    return StringStartsWith.startsWith(prefix);
  }










  
  public static Matcher<String> endsWith(String suffix) {
    return StringEndsWith.endsWith(suffix);
  }
}
