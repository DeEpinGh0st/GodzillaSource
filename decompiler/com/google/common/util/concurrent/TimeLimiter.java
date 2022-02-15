package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Beta
@GwtIncompatible
public interface TimeLimiter {
  <T> T newProxy(T paramT, Class<T> paramClass, long paramLong, TimeUnit paramTimeUnit);
  
  @CanIgnoreReturnValue
  <T> T callWithTimeout(Callable<T> paramCallable, long paramLong, TimeUnit paramTimeUnit) throws TimeoutException, InterruptedException, ExecutionException;
  
  @CanIgnoreReturnValue
  <T> T callUninterruptiblyWithTimeout(Callable<T> paramCallable, long paramLong, TimeUnit paramTimeUnit) throws TimeoutException, ExecutionException;
  
  void runWithTimeout(Runnable paramRunnable, long paramLong, TimeUnit paramTimeUnit) throws TimeoutException, InterruptedException;
  
  void runUninterruptiblyWithTimeout(Runnable paramRunnable, long paramLong, TimeUnit paramTimeUnit) throws TimeoutException;
}
