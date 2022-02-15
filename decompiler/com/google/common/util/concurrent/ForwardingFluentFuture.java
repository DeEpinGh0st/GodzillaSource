package com.google.common.util.concurrent;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
























@GwtCompatible
final class ForwardingFluentFuture<V>
  extends FluentFuture<V>
{
  private final ListenableFuture<V> delegate;
  
  ForwardingFluentFuture(ListenableFuture<V> delegate) {
    this.delegate = (ListenableFuture<V>)Preconditions.checkNotNull(delegate);
  }

  
  public void addListener(Runnable listener, Executor executor) {
    this.delegate.addListener(listener, executor);
  }

  
  public boolean cancel(boolean mayInterruptIfRunning) {
    return this.delegate.cancel(mayInterruptIfRunning);
  }

  
  public boolean isCancelled() {
    return this.delegate.isCancelled();
  }

  
  public boolean isDone() {
    return this.delegate.isDone();
  }

  
  public V get() throws InterruptedException, ExecutionException {
    return this.delegate.get();
  }


  
  public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
    return this.delegate.get(timeout, unit);
  }
}
