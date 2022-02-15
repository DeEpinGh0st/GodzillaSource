package com.google.common.base;

import com.google.common.annotations.GwtCompatible;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.function.Predicate;






























































@FunctionalInterface
@GwtCompatible
public interface Predicate<T>
  extends Predicate<T>
{
  @CanIgnoreReturnValue
  boolean apply(T paramT);
  
  boolean equals(Object paramObject);
  
  default boolean test(T input) {
    return apply(input);
  }
}
