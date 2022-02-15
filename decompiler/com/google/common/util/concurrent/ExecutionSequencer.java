package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReference;




































@Beta
public final class ExecutionSequencer
{
  public static ExecutionSequencer create() {
    return new ExecutionSequencer();
  }
  
  enum RunningState {
    NOT_RUN,
    CANCELLED,
    STARTED;
  }

  
  private final AtomicReference<ListenableFuture<Object>> ref = new AtomicReference<>(
      Futures.immediateFuture(null));







  
  public <T> ListenableFuture<T> submit(final Callable<T> callable, Executor executor) {
    Preconditions.checkNotNull(callable);
    return submitAsync(new AsyncCallable<T>()
        {
          public ListenableFuture<T> call() throws Exception
          {
            return Futures.immediateFuture(callable.call());
          }

          
          public String toString() {
            return callable.toString();
          }
        },  executor);
  }









  
  public <T> ListenableFuture<T> submitAsync(final AsyncCallable<T> callable, final Executor executor) {
    Preconditions.checkNotNull(callable);
    final AtomicReference<RunningState> runningState = new AtomicReference<>(RunningState.NOT_RUN);
    AsyncCallable<T> task = new AsyncCallable<T>()
      {
        public ListenableFuture<T> call() throws Exception
        {
          if (!runningState.compareAndSet(ExecutionSequencer.RunningState.NOT_RUN, ExecutionSequencer.RunningState.STARTED)) {
            return Futures.immediateCancelledFuture();
          }
          return callable.call();
        }

        
        public String toString() {
          return callable.toString();
        }
      };










    
    final SettableFuture<Object> newFuture = SettableFuture.create();
    
    final ListenableFuture<?> oldFuture = this.ref.getAndSet(newFuture);


    
    final ListenableFuture<T> taskFuture = Futures.submitAsync(task, new Executor()
        {
          
          public void execute(Runnable runnable)
          {
            oldFuture.addListener(runnable, executor);
          }
        });
    
    final ListenableFuture<T> outputFuture = Futures.nonCancellationPropagating(taskFuture);




    
    Runnable listener = new Runnable()
      {
        public void run()
        {
          if (taskFuture.isDone() || (outputFuture


            
            .isCancelled() && runningState.compareAndSet(ExecutionSequencer.RunningState.NOT_RUN, ExecutionSequencer.RunningState.CANCELLED)))
          {

            
            newFuture.setFuture(oldFuture);
          }
        }
      };


    
    outputFuture.addListener(listener, MoreExecutors.directExecutor());
    taskFuture.addListener(listener, MoreExecutors.directExecutor());
    
    return outputFuture;
  }
}
