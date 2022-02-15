package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import java.util.concurrent.Callable;

























@GwtCompatible(emulated = true)
public final class Callables
{
  public static <T> Callable<T> returning(final T value) {
    return new Callable<T>()
      {
        public T call() {
          return (T)value;
        }
      };
  }









  
  @Beta
  @GwtIncompatible
  public static <T> AsyncCallable<T> asAsyncCallable(final Callable<T> callable, final ListeningExecutorService listeningExecutorService) {
    Preconditions.checkNotNull(callable);
    Preconditions.checkNotNull(listeningExecutorService);
    return new AsyncCallable<T>()
      {
        public ListenableFuture<T> call() throws Exception {
          return listeningExecutorService.submit(callable);
        }
      };
  }










  
  @GwtIncompatible
  static <T> Callable<T> threadRenaming(final Callable<T> callable, final Supplier<String> nameSupplier) {
    Preconditions.checkNotNull(nameSupplier);
    Preconditions.checkNotNull(callable);
    return new Callable<T>()
      {
        public T call() throws Exception {
          Thread currentThread = Thread.currentThread();
          String oldName = currentThread.getName();
          boolean restoreName = Callables.trySetName((String)nameSupplier.get(), currentThread);
          try {
            return (T)callable.call();
          } finally {
            if (restoreName) {
              boolean bool = Callables.trySetName(oldName, currentThread);
            }
          } 
        }
      };
  }









  
  @GwtIncompatible
  static Runnable threadRenaming(final Runnable task, final Supplier<String> nameSupplier) {
    Preconditions.checkNotNull(nameSupplier);
    Preconditions.checkNotNull(task);
    return new Runnable()
      {
        public void run() {
          Thread currentThread = Thread.currentThread();
          String oldName = currentThread.getName();
          boolean restoreName = Callables.trySetName((String)nameSupplier.get(), currentThread);
          try {
            task.run();
          } finally {
            if (restoreName) {
              boolean bool = Callables.trySetName(oldName, currentThread);
            }
          } 
        }
      };
  }




  
  @GwtIncompatible
  private static boolean trySetName(String threadName, Thread currentThread) {
    try {
      currentThread.setName(threadName);
      return true;
    } catch (SecurityException e) {
      return false;
    } 
  }
}
