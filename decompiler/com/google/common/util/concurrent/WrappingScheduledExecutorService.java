package com.google.common.util.concurrent;

import com.google.common.annotations.GwtIncompatible;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;





















@CanIgnoreReturnValue
@GwtIncompatible
abstract class WrappingScheduledExecutorService
  extends WrappingExecutorService
  implements ScheduledExecutorService
{
  final ScheduledExecutorService delegate;
  
  protected WrappingScheduledExecutorService(ScheduledExecutorService delegate) {
    super(delegate);
    this.delegate = delegate;
  }

  
  public final ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
    return this.delegate.schedule(wrapTask(command), delay, unit);
  }

  
  public final <V> ScheduledFuture<V> schedule(Callable<V> task, long delay, TimeUnit unit) {
    return this.delegate.schedule(wrapTask(task), delay, unit);
  }


  
  public final ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
    return this.delegate.scheduleAtFixedRate(wrapTask(command), initialDelay, period, unit);
  }


  
  public final ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
    return this.delegate.scheduleWithFixedDelay(wrapTask(command), initialDelay, delay, unit);
  }
}
