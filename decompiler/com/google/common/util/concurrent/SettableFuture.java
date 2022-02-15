package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.errorprone.annotations.CanIgnoreReturnValue;































@GwtCompatible
public final class SettableFuture<V>
  extends AbstractFuture.TrustedFuture<V>
{
  public static <V> SettableFuture<V> create() {
    return new SettableFuture<>();
  }

  
  @CanIgnoreReturnValue
  public boolean set(V value) {
    return super.set(value);
  }

  
  @CanIgnoreReturnValue
  public boolean setException(Throwable throwable) {
    return super.setException(throwable);
  }

  
  @Beta
  @CanIgnoreReturnValue
  public boolean setFuture(ListenableFuture<? extends V> future) {
    return super.setFuture(future);
  }
}
