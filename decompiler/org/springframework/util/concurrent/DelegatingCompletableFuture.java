package org.springframework.util.concurrent;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import org.springframework.util.Assert;


























class DelegatingCompletableFuture<T>
  extends CompletableFuture<T>
{
  private final Future<T> delegate;
  
  public DelegatingCompletableFuture(Future<T> delegate) {
    Assert.notNull(delegate, "Delegate must not be null");
    this.delegate = delegate;
  }


  
  public boolean cancel(boolean mayInterruptIfRunning) {
    boolean result = this.delegate.cancel(mayInterruptIfRunning);
    super.cancel(mayInterruptIfRunning);
    return result;
  }
}
