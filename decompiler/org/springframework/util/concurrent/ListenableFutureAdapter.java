package org.springframework.util.concurrent;

import java.util.concurrent.ExecutionException;
import org.springframework.lang.Nullable;
































public abstract class ListenableFutureAdapter<T, S>
  extends FutureAdapter<T, S>
  implements ListenableFuture<T>
{
  protected ListenableFutureAdapter(ListenableFuture<S> adaptee) {
    super(adaptee);
  }


  
  public void addCallback(ListenableFutureCallback<? super T> callback) {
    addCallback(callback, callback);
  }

  
  public void addCallback(final SuccessCallback<? super T> successCallback, final FailureCallback failureCallback) {
    ListenableFuture<S> listenableAdaptee = (ListenableFuture<S>)getAdaptee();
    listenableAdaptee.addCallback(new ListenableFutureCallback<S>()
        {
          public void onSuccess(@Nullable S result) {
            T adapted = null;
            if (result != null) {
              try {
                adapted = ListenableFutureAdapter.this.adaptInternal(result);
              }
              catch (ExecutionException ex) {
                Throwable cause = ex.getCause();
                onFailure((cause != null) ? cause : ex);
                
                return;
              } catch (Throwable ex) {
                onFailure(ex);
                return;
              } 
            }
            successCallback.onSuccess(adapted);
          }
          
          public void onFailure(Throwable ex) {
            failureCallback.onFailure(ex);
          }
        });
  }
}
