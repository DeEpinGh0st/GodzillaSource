package com.google.common.util.concurrent;

import com.google.common.annotations.GwtCompatible;

@FunctionalInterface
@GwtCompatible
public interface AsyncFunction<I, O> {
  ListenableFuture<O> apply(I paramI) throws Exception;
}
