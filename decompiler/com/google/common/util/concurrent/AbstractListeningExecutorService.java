package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.RunnableFuture;



























@Beta
@CanIgnoreReturnValue
@GwtIncompatible
public abstract class AbstractListeningExecutorService
  extends AbstractExecutorService
  implements ListeningExecutorService
{
  protected final <T> RunnableFuture<T> newTaskFor(Runnable runnable, T value) {
    return TrustedListenableFutureTask.create(runnable, value);
  }


  
  protected final <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
    return TrustedListenableFutureTask.create(callable);
  }

  
  public ListenableFuture<?> submit(Runnable task) {
    return (ListenableFuture)super.submit(task);
  }

  
  public <T> ListenableFuture<T> submit(Runnable task, T result) {
    return (ListenableFuture<T>)super.<T>submit(task, result);
  }

  
  public <T> ListenableFuture<T> submit(Callable<T> task) {
    return (ListenableFuture<T>)super.<T>submit(task);
  }
}
