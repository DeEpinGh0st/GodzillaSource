package org.springframework.util.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;































public class SettableListenableFuture<T>
  implements ListenableFuture<T>
{
  private static final Callable<Object> DUMMY_CALLABLE = () -> {
      throw new IllegalStateException("Should never be called");
    };
  private final SettableTask<T> settableTask = new SettableTask<>();








  
  public boolean set(@Nullable T value) {
    return this.settableTask.setResultValue(value);
  }







  
  public boolean setException(Throwable exception) {
    Assert.notNull(exception, "Exception must not be null");
    return this.settableTask.setExceptionResult(exception);
  }


  
  public void addCallback(ListenableFutureCallback<? super T> callback) {
    this.settableTask.addCallback(callback);
  }

  
  public void addCallback(SuccessCallback<? super T> successCallback, FailureCallback failureCallback) {
    this.settableTask.addCallback(successCallback, failureCallback);
  }

  
  public CompletableFuture<T> completable() {
    return this.settableTask.completable();
  }


  
  public boolean cancel(boolean mayInterruptIfRunning) {
    boolean cancelled = this.settableTask.cancel(mayInterruptIfRunning);
    if (cancelled && mayInterruptIfRunning) {
      interruptTask();
    }
    return cancelled;
  }

  
  public boolean isCancelled() {
    return this.settableTask.isCancelled();
  }

  
  public boolean isDone() {
    return this.settableTask.isDone();
  }









  
  public T get() throws InterruptedException, ExecutionException {
    return this.settableTask.get();
  }











  
  public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
    return this.settableTask.get(timeout, unit);
  }



  
  protected void interruptTask() {}



  
  private static class SettableTask<T>
    extends ListenableFutureTask<T>
  {
    @Nullable
    private volatile Thread completingThread;


    
    public SettableTask() {
      super((Callable)SettableListenableFuture.DUMMY_CALLABLE);
    }
    
    public boolean setResultValue(@Nullable T value) {
      set(value);
      return checkCompletingThread();
    }
    
    public boolean setExceptionResult(Throwable exception) {
      setException(exception);
      return checkCompletingThread();
    }

    
    protected void done() {
      if (!isCancelled())
      {

        
        this.completingThread = Thread.currentThread();
      }
      super.done();
    }
    
    private boolean checkCompletingThread() {
      boolean check = (this.completingThread == Thread.currentThread());
      if (check) {
        this.completingThread = null;
      }
      return check;
    }
  }
}
