package org.springframework.util.concurrent;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


























public class CompletableToListenableFutureAdapter<T>
  implements ListenableFuture<T>
{
  private final CompletableFuture<T> completableFuture;
  private final ListenableFutureCallbackRegistry<T> callbacks = new ListenableFutureCallbackRegistry<>();





  
  public CompletableToListenableFutureAdapter(CompletionStage<T> completionStage) {
    this(completionStage.toCompletableFuture());
  }



  
  public CompletableToListenableFutureAdapter(CompletableFuture<T> completableFuture) {
    this.completableFuture = completableFuture;
    this.completableFuture.whenComplete((result, ex) -> {
          if (ex != null) {
            this.callbacks.failure(ex);
          } else {
            this.callbacks.success((T)result);
          } 
        });
  }



  
  public void addCallback(ListenableFutureCallback<? super T> callback) {
    this.callbacks.addCallback(callback);
  }

  
  public void addCallback(SuccessCallback<? super T> successCallback, FailureCallback failureCallback) {
    this.callbacks.addSuccessCallback(successCallback);
    this.callbacks.addFailureCallback(failureCallback);
  }

  
  public CompletableFuture<T> completable() {
    return this.completableFuture;
  }


  
  public boolean cancel(boolean mayInterruptIfRunning) {
    return this.completableFuture.cancel(mayInterruptIfRunning);
  }

  
  public boolean isCancelled() {
    return this.completableFuture.isCancelled();
  }

  
  public boolean isDone() {
    return this.completableFuture.isDone();
  }

  
  public T get() throws InterruptedException, ExecutionException {
    return this.completableFuture.get();
  }

  
  public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
    return this.completableFuture.get(timeout, unit);
  }
}
