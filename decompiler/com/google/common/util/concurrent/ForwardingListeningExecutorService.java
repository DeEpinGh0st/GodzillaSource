package com.google.common.util.concurrent;

import com.google.common.annotations.GwtIncompatible;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;



























@CanIgnoreReturnValue
@GwtIncompatible
public abstract class ForwardingListeningExecutorService
  extends ForwardingExecutorService
  implements ListeningExecutorService
{
  public <T> ListenableFuture<T> submit(Callable<T> task) {
    return delegate().submit(task);
  }

  
  public ListenableFuture<?> submit(Runnable task) {
    return delegate().submit(task);
  }

  
  public <T> ListenableFuture<T> submit(Runnable task, T result) {
    return delegate().submit(task, result);
  }
  
  protected abstract ListeningExecutorService delegate();
}
