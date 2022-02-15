package org.springframework.util.concurrent;

import java.util.ArrayDeque;
import java.util.Queue;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;






























public class ListenableFutureCallbackRegistry<T>
{
  private final Queue<SuccessCallback<? super T>> successCallbacks = new ArrayDeque<>(1);
  
  private final Queue<FailureCallback> failureCallbacks = new ArrayDeque<>(1);
  
  private State state = State.NEW;
  
  @Nullable
  private Object result;
  
  private final Object mutex = new Object();





  
  public void addCallback(ListenableFutureCallback<? super T> callback) {
    Assert.notNull(callback, "'callback' must not be null");
    synchronized (this.mutex) {
      switch (this.state) {
        case NEW:
          this.successCallbacks.add(callback);
          this.failureCallbacks.add(callback);
          break;
        case SUCCESS:
          notifySuccess(callback);
          break;
        case FAILURE:
          notifyFailure(callback);
          break;
      } 
    } 
  }

  
  private void notifySuccess(SuccessCallback<? super T> callback) {
    try {
      callback.onSuccess((T)this.result);
    }
    catch (Throwable throwable) {}
  }


  
  private void notifyFailure(FailureCallback callback) {
    Assert.state(this.result instanceof Throwable, "No Throwable result for failure state");
    try {
      callback.onFailure((Throwable)this.result);
    }
    catch (Throwable throwable) {}
  }







  
  public void addSuccessCallback(SuccessCallback<? super T> callback) {
    Assert.notNull(callback, "'callback' must not be null");
    synchronized (this.mutex) {
      switch (this.state) {
        case NEW:
          this.successCallbacks.add(callback);
          break;
        case SUCCESS:
          notifySuccess(callback);
          break;
      } 
    } 
  }





  
  public void addFailureCallback(FailureCallback callback) {
    Assert.notNull(callback, "'callback' must not be null");
    synchronized (this.mutex) {
      switch (this.state) {
        case NEW:
          this.failureCallbacks.add(callback);
          break;
        case FAILURE:
          notifyFailure(callback);
          break;
      } 
    } 
  }





  
  public void success(@Nullable T result) {
    synchronized (this.mutex) {
      this.state = State.SUCCESS;
      this.result = result;
      SuccessCallback<? super T> callback;
      while ((callback = this.successCallbacks.poll()) != null) {
        notifySuccess(callback);
      }
    } 
  }





  
  public void failure(Throwable ex) {
    synchronized (this.mutex) {
      this.state = State.FAILURE;
      this.result = ex;
      FailureCallback callback;
      while ((callback = this.failureCallbacks.poll()) != null)
        notifyFailure(callback); 
    } 
  }
  
  private enum State
  {
    NEW, SUCCESS, FAILURE;
  }
}
