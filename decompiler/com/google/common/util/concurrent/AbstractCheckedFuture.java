package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.concurrent.CancellationException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
































@Deprecated
@Beta
@GwtIncompatible
public abstract class AbstractCheckedFuture<V, X extends Exception>
  extends ForwardingListenableFuture.SimpleForwardingListenableFuture<V>
  implements CheckedFuture<V, X>
{
  protected AbstractCheckedFuture(ListenableFuture<V> delegate) {
    super(delegate);
  }














  
  protected abstract X mapException(Exception paramException);













  
  @CanIgnoreReturnValue
  public V checkedGet() throws X {
    try {
      return get();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw mapException(e);
    } catch (CancellationException|java.util.concurrent.ExecutionException e) {
      throw mapException(e);
    } 
  }














  
  @CanIgnoreReturnValue
  public V checkedGet(long timeout, TimeUnit unit) throws TimeoutException, X {
    try {
      return get(timeout, unit);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw mapException(e);
    } catch (CancellationException|java.util.concurrent.ExecutionException e) {
      throw mapException(e);
    } 
  }
}
