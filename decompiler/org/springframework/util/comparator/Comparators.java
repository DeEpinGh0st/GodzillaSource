package org.springframework.util.comparator;

import java.util.Comparator;





























public abstract class Comparators
{
  public static <T> Comparator<T> comparable() {
    return ComparableComparator.INSTANCE;
  }






  
  public static <T> Comparator<T> nullsLow() {
    return NullSafeComparator.NULLS_LOW;
  }





  
  public static <T> Comparator<T> nullsLow(Comparator<T> comparator) {
    return new NullSafeComparator<>(comparator, true);
  }






  
  public static <T> Comparator<T> nullsHigh() {
    return NullSafeComparator.NULLS_HIGH;
  }





  
  public static <T> Comparator<T> nullsHigh(Comparator<T> comparator) {
    return new NullSafeComparator<>(comparator, false);
  }
}
