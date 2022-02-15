package org.springframework.core.convert.converter;

import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
















































@FunctionalInterface
public interface Converter<S, T>
{
  @Nullable
  T convert(S paramS);
  
  default <U> Converter<S, U> andThen(Converter<? super T, ? extends U> after) {
    Assert.notNull(after, "After Converter must not be null");
    return s -> {
        T initialResult = convert((S)s);
        return (initialResult != null) ? after.convert(initialResult) : null;
      };
  }
}
