package org.springframework.util.concurrent;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;












































public interface ListenableFuture<T>
  extends Future<T>
{
  void addCallback(ListenableFutureCallback<? super T> paramListenableFutureCallback);
  
  void addCallback(SuccessCallback<? super T> paramSuccessCallback, FailureCallback paramFailureCallback);
  
  default CompletableFuture<T> completable() {
    CompletableFuture<T> completable = new DelegatingCompletableFuture<>(this);
    addCallback(completable::complete, completable::completeExceptionally);
    return completable;
  }
}
