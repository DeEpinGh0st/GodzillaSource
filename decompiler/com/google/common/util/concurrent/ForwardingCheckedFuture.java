package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;



































@Deprecated
@Beta
@GwtIncompatible
public abstract class ForwardingCheckedFuture<V, X extends Exception>
  extends ForwardingListenableFuture<V>
  implements CheckedFuture<V, X>
{
  @CanIgnoreReturnValue
  public V checkedGet() throws X {
    return delegate().checkedGet();
  }

  
  @CanIgnoreReturnValue
  public V checkedGet(long timeout, TimeUnit unit) throws TimeoutException, X {
    return delegate().checkedGet(timeout, unit);
  }





  
  protected abstract CheckedFuture<V, X> delegate();





  
  @Deprecated
  @Beta
  public static abstract class SimpleForwardingCheckedFuture<V, X extends Exception>
    extends ForwardingCheckedFuture<V, X>
  {
    private final CheckedFuture<V, X> delegate;





    
    protected SimpleForwardingCheckedFuture(CheckedFuture<V, X> delegate) {
      this.delegate = (CheckedFuture<V, X>)Preconditions.checkNotNull(delegate);
    }

    
    protected final CheckedFuture<V, X> delegate() {
      return this.delegate;
    }
  }
}
