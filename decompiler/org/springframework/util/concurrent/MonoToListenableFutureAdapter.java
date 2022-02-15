package org.springframework.util.concurrent;

import reactor.core.publisher.Mono;


























public class MonoToListenableFutureAdapter<T>
  extends CompletableToListenableFutureAdapter<T>
{
  public MonoToListenableFutureAdapter(Mono<T> mono) {
    super(mono.toFuture());
  }
}
