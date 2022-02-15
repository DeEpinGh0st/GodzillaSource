package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;

@FunctionalInterface
@Beta
@GwtCompatible
public interface AsyncCallable<V> {
  ListenableFuture<V> call() throws Exception;
}
