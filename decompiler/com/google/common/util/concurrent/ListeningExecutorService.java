package com.google.common.util.concurrent;

import com.google.common.annotations.GwtIncompatible;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@GwtIncompatible
public interface ListeningExecutorService extends ExecutorService {
  <T> ListenableFuture<T> submit(Callable<T> paramCallable);
  
  ListenableFuture<?> submit(Runnable paramRunnable);
  
  <T> ListenableFuture<T> submit(Runnable paramRunnable, T paramT);
  
  <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> paramCollection) throws InterruptedException;
  
  <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> paramCollection, long paramLong, TimeUnit paramTimeUnit) throws InterruptedException;
}
