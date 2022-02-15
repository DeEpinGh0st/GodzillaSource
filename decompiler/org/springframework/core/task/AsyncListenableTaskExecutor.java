package org.springframework.core.task;

import java.util.concurrent.Callable;
import org.springframework.util.concurrent.ListenableFuture;

public interface AsyncListenableTaskExecutor extends AsyncTaskExecutor {
  ListenableFuture<?> submitListenable(Runnable paramRunnable);
  
  <T> ListenableFuture<T> submitListenable(Callable<T> paramCallable);
}
