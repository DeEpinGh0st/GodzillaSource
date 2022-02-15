package com.google.common.base;

import com.google.common.annotations.GwtCompatible;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.function.Supplier;

@FunctionalInterface
@GwtCompatible
public interface Supplier<T> extends Supplier<T> {
  @CanIgnoreReturnValue
  T get();
}
