package com.google.common.util.concurrent;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.RunnableFuture;





















@GwtCompatible
class TrustedListenableFutureTask<V>
  extends FluentFuture.TrustedFuture<V>
  implements RunnableFuture<V>
{
  private volatile InterruptibleTask<?> task;
  
  static <V> TrustedListenableFutureTask<V> create(AsyncCallable<V> callable) {
    return new TrustedListenableFutureTask<>(callable);
  }
  
  static <V> TrustedListenableFutureTask<V> create(Callable<V> callable) {
    return new TrustedListenableFutureTask<>(callable);
  }









  
  static <V> TrustedListenableFutureTask<V> create(Runnable runnable, V result) {
    return new TrustedListenableFutureTask<>(Executors.callable(runnable, result));
  }









  
  TrustedListenableFutureTask(Callable<V> callable) {
    this.task = new TrustedFutureInterruptibleTask(callable);
  }
  
  TrustedListenableFutureTask(AsyncCallable<V> callable) {
    this.task = new TrustedFutureInterruptibleAsyncTask(callable);
  }

  
  public void run() {
    InterruptibleTask<?> localTask = this.task;
    if (localTask != null) {
      localTask.run();
    }



    
    this.task = null;
  }

  
  protected void afterDone() {
    super.afterDone();
    
    if (wasInterrupted()) {
      InterruptibleTask<?> localTask = this.task;
      if (localTask != null) {
        localTask.interruptTask();
      }
    } 
    
    this.task = null;
  }

  
  protected String pendingToString() {
    InterruptibleTask<?> localTask = this.task;
    if (localTask != null) {
      return "task=[" + localTask + "]";
    }
    return super.pendingToString();
  }
  
  private final class TrustedFutureInterruptibleTask
    extends InterruptibleTask<V> {
    private final Callable<V> callable;
    
    TrustedFutureInterruptibleTask(Callable<V> callable) {
      this.callable = (Callable<V>)Preconditions.checkNotNull(callable);
    }

    
    final boolean isDone() {
      return TrustedListenableFutureTask.this.isDone();
    }

    
    V runInterruptibly() throws Exception {
      return this.callable.call();
    }

    
    void afterRanInterruptibly(V result, Throwable error) {
      if (error == null) {
        TrustedListenableFutureTask.this.set(result);
      } else {
        TrustedListenableFutureTask.this.setException(error);
      } 
    }

    
    String toPendingString() {
      return this.callable.toString();
    }
  }
  
  private final class TrustedFutureInterruptibleAsyncTask
    extends InterruptibleTask<ListenableFuture<V>>
  {
    private final AsyncCallable<V> callable;
    
    TrustedFutureInterruptibleAsyncTask(AsyncCallable<V> callable) {
      this.callable = (AsyncCallable<V>)Preconditions.checkNotNull(callable);
    }

    
    final boolean isDone() {
      return TrustedListenableFutureTask.this.isDone();
    }

    
    ListenableFuture<V> runInterruptibly() throws Exception {
      return (ListenableFuture<V>)Preconditions.checkNotNull(this.callable
          .call(), "AsyncCallable.call returned null instead of a Future. Did you mean to return immediateFuture(null)? %s", this.callable);
    }




    
    void afterRanInterruptibly(ListenableFuture<V> result, Throwable error) {
      if (error == null) {
        TrustedListenableFutureTask.this.setFuture(result);
      } else {
        TrustedListenableFutureTask.this.setException(error);
      } 
    }

    
    String toPendingString() {
      return this.callable.toString();
    }
  }
}
