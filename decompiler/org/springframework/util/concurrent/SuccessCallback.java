package org.springframework.util.concurrent;

import org.springframework.lang.Nullable;

@FunctionalInterface
public interface SuccessCallback<T> {
  void onSuccess(@Nullable T paramT);
}
