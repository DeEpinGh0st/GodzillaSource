package com.google.common.util.concurrent;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableCollection;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
























@GwtCompatible
final class CombinedFuture<V>
  extends AggregateFuture<Object, V>
{
  CombinedFuture(ImmutableCollection<? extends ListenableFuture<?>> futures, boolean allMustSucceed, Executor listenerExecutor, AsyncCallable<V> callable) {
    init(new CombinedFutureRunningState(futures, allMustSucceed, new AsyncCallableInterruptibleTask(callable, listenerExecutor)));
  }








  
  CombinedFuture(ImmutableCollection<? extends ListenableFuture<?>> futures, boolean allMustSucceed, Executor listenerExecutor, Callable<V> callable) {
    init(new CombinedFutureRunningState(futures, allMustSucceed, new CallableInterruptibleTask(callable, listenerExecutor)));
  }


  
  private final class CombinedFutureRunningState
    extends AggregateFuture<Object, V>.RunningState
  {
    private CombinedFuture.CombinedFutureInterruptibleTask task;

    
    CombinedFutureRunningState(ImmutableCollection<? extends ListenableFuture<?>> futures, boolean allMustSucceed, CombinedFuture.CombinedFutureInterruptibleTask task) {
      super(futures, allMustSucceed, false);
      this.task = task;
    }

    
    void collectOneValue(boolean allMustSucceed, int index, Object returnValue) {}

    
    void handleAllCompleted() {
      CombinedFuture.CombinedFutureInterruptibleTask localTask = this.task;
      if (localTask != null) {
        localTask.execute();
      } else {
        Preconditions.checkState(CombinedFuture.this.isDone());
      } 
    }

    
    void releaseResourcesAfterFailure() {
      super.releaseResourcesAfterFailure();
      this.task = null;
    }

    
    void interruptTask() {
      CombinedFuture.CombinedFutureInterruptibleTask localTask = this.task;
      if (localTask != null)
        localTask.interruptTask(); 
    }
  }
  
  private abstract class CombinedFutureInterruptibleTask<T>
    extends InterruptibleTask<T>
  {
    private final Executor listenerExecutor;
    boolean thrownByExecute = true;
    
    public CombinedFutureInterruptibleTask(Executor listenerExecutor) {
      this.listenerExecutor = (Executor)Preconditions.checkNotNull(listenerExecutor);
    }

    
    final boolean isDone() {
      return CombinedFuture.this.isDone();
    }
    
    final void execute() {
      try {
        this.listenerExecutor.execute(this);
      } catch (RejectedExecutionException e) {
        if (this.thrownByExecute) {
          CombinedFuture.this.setException(e);
        }
      } 
    }

    
    final void afterRanInterruptibly(T result, Throwable error) {
      if (error != null) {
        if (error instanceof java.util.concurrent.ExecutionException) {
          CombinedFuture.this.setException(error.getCause());
        } else if (error instanceof java.util.concurrent.CancellationException) {
          CombinedFuture.this.cancel(false);
        } else {
          CombinedFuture.this.setException(error);
        } 
      } else {
        setValue(result);
      } 
    }
    
    abstract void setValue(T param1T);
  }
  
  private final class AsyncCallableInterruptibleTask
    extends CombinedFutureInterruptibleTask<ListenableFuture<V>>
  {
    private final AsyncCallable<V> callable;
    
    public AsyncCallableInterruptibleTask(AsyncCallable<V> callable, Executor listenerExecutor) {
      super(listenerExecutor);
      this.callable = (AsyncCallable<V>)Preconditions.checkNotNull(callable);
    }

    
    ListenableFuture<V> runInterruptibly() throws Exception {
      this.thrownByExecute = false;
      ListenableFuture<V> result = this.callable.call();
      return (ListenableFuture<V>)Preconditions.checkNotNull(result, "AsyncCallable.call returned null instead of a Future. Did you mean to return immediateFuture(null)? %s", this.callable);
    }





    
    void setValue(ListenableFuture<V> value) {
      CombinedFuture.this.setFuture(value);
    }

    
    String toPendingString() {
      return this.callable.toString();
    }
  }
  
  private final class CallableInterruptibleTask
    extends CombinedFutureInterruptibleTask<V> {
    private final Callable<V> callable;
    
    public CallableInterruptibleTask(Callable<V> callable, Executor listenerExecutor) {
      super(listenerExecutor);
      this.callable = (Callable<V>)Preconditions.checkNotNull(callable);
    }

    
    V runInterruptibly() throws Exception {
      this.thrownByExecute = false;
      return this.callable.call();
    }

    
    void setValue(V value) {
      CombinedFuture.this.set(value);
    }

    
    String toPendingString() {
      return this.callable.toString();
    }
  }
}
