package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;


























@Beta
@CanIgnoreReturnValue
@GwtIncompatible
public final class FakeTimeLimiter
  implements TimeLimiter
{
  public <T> T newProxy(T target, Class<T> interfaceType, long timeoutDuration, TimeUnit timeoutUnit) {
    Preconditions.checkNotNull(target);
    Preconditions.checkNotNull(interfaceType);
    Preconditions.checkNotNull(timeoutUnit);
    return target;
  }


  
  public <T> T callWithTimeout(Callable<T> callable, long timeoutDuration, TimeUnit timeoutUnit) throws ExecutionException {
    Preconditions.checkNotNull(callable);
    Preconditions.checkNotNull(timeoutUnit);
    try {
      return callable.call();
    } catch (RuntimeException e) {
      throw new UncheckedExecutionException(e);
    } catch (Exception e) {
      throw new ExecutionException(e);
    } catch (Error e) {
      throw new ExecutionError(e);
    } catch (Throwable e) {

      
      throw new ExecutionException(e);
    } 
  }


  
  public <T> T callUninterruptiblyWithTimeout(Callable<T> callable, long timeoutDuration, TimeUnit timeoutUnit) throws ExecutionException {
    return callWithTimeout(callable, timeoutDuration, timeoutUnit);
  }

  
  public void runWithTimeout(Runnable runnable, long timeoutDuration, TimeUnit timeoutUnit) {
    Preconditions.checkNotNull(runnable);
    Preconditions.checkNotNull(timeoutUnit);
    try {
      runnable.run();
    } catch (RuntimeException e) {
      throw new UncheckedExecutionException(e);
    } catch (Error e) {
      throw new ExecutionError(e);
    } catch (Throwable e) {

      
      throw new UncheckedExecutionException(e);
    } 
  }


  
  public void runUninterruptiblyWithTimeout(Runnable runnable, long timeoutDuration, TimeUnit timeoutUnit) {
    runWithTimeout(runnable, timeoutDuration, timeoutUnit);
  }
}
