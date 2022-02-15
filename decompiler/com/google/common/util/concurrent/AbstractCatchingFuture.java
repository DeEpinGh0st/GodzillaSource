package com.google.common.util.concurrent;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.ForOverride;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;


















@GwtCompatible
abstract class AbstractCatchingFuture<V, X extends Throwable, F, T>
  extends FluentFuture.TrustedFuture<V>
  implements Runnable
{
  ListenableFuture<? extends V> inputFuture;
  Class<X> exceptionType;
  F fallback;
  
  static <V, X extends Throwable> ListenableFuture<V> create(ListenableFuture<? extends V> input, Class<X> exceptionType, Function<? super X, ? extends V> fallback, Executor executor) {
    CatchingFuture<V, X> future = new CatchingFuture<>(input, exceptionType, fallback);
    input.addListener(future, MoreExecutors.rejectionPropagatingExecutor(executor, future));
    return future;
  }




  
  static <X extends Throwable, V> ListenableFuture<V> create(ListenableFuture<? extends V> input, Class<X> exceptionType, AsyncFunction<? super X, ? extends V> fallback, Executor executor) {
    AsyncCatchingFuture<V, X> future = new AsyncCatchingFuture<>(input, exceptionType, fallback);
    input.addListener(future, MoreExecutors.rejectionPropagatingExecutor(executor, future));
    return future;
  }









  
  AbstractCatchingFuture(ListenableFuture<? extends V> inputFuture, Class<X> exceptionType, F fallback) {
    this.inputFuture = (ListenableFuture<? extends V>)Preconditions.checkNotNull(inputFuture);
    this.exceptionType = (Class<X>)Preconditions.checkNotNull(exceptionType);
    this.fallback = (F)Preconditions.checkNotNull(fallback);
  }
  
  public final void run() {
    T fallbackResult;
    ListenableFuture<? extends V> localInputFuture = this.inputFuture;
    Class<X> localExceptionType = this.exceptionType;
    F localFallback = this.fallback;
    if ((((localInputFuture == null) ? 1 : 0) | ((localExceptionType == null) ? 1 : 0) | ((localFallback == null) ? 1 : 0) | 

      
      isCancelled()) != 0) {
      return;
    }
    this.inputFuture = null;

    
    V sourceResult = null;
    Throwable throwable = null;
    try {
      sourceResult = Futures.getDone((Future)localInputFuture);
    } catch (ExecutionException e) {
      throwable = (Throwable)Preconditions.checkNotNull(e.getCause());
    } catch (Throwable e) {
      throwable = e;
    } 
    
    if (throwable == null) {
      set(sourceResult);
      
      return;
    } 
    if (!Platform.isInstanceOfThrowableClass(throwable, localExceptionType)) {
      setFuture(localInputFuture);

      
      return;
    } 
    
    Throwable throwable1 = throwable;
    
    try {
      fallbackResult = doFallback(localFallback, (X)throwable1);
    } catch (Throwable t) {
      setException(t);
      return;
    } finally {
      this.exceptionType = null;
      this.fallback = null;
    } 
    
    setResult(fallbackResult);
  }

  
  protected String pendingToString() {
    ListenableFuture<? extends V> localInputFuture = this.inputFuture;
    Class<X> localExceptionType = this.exceptionType;
    F localFallback = this.fallback;
    String superString = super.pendingToString();
    String resultString = "";
    if (localInputFuture != null) {
      resultString = "inputFuture=[" + localInputFuture + "], ";
    }
    if (localExceptionType != null && localFallback != null) {
      return resultString + "exceptionType=[" + localExceptionType + "], fallback=[" + localFallback + "]";
    }



    
    if (superString != null) {
      return resultString + superString;
    }
    return null;
  }

  
  @ForOverride
  abstract T doFallback(F paramF, X paramX) throws Exception;

  
  @ForOverride
  abstract void setResult(T paramT);

  
  protected final void afterDone() {
    maybePropagateCancellationTo(this.inputFuture);
    this.inputFuture = null;
    this.exceptionType = null;
    this.fallback = null;
  }







  
  private static final class AsyncCatchingFuture<V, X extends Throwable>
    extends AbstractCatchingFuture<V, X, AsyncFunction<? super X, ? extends V>, ListenableFuture<? extends V>>
  {
    AsyncCatchingFuture(ListenableFuture<? extends V> input, Class<X> exceptionType, AsyncFunction<? super X, ? extends V> fallback) {
      super(input, exceptionType, fallback);
    }


    
    ListenableFuture<? extends V> doFallback(AsyncFunction<? super X, ? extends V> fallback, X cause) throws Exception {
      ListenableFuture<? extends V> replacement = fallback.apply(cause);
      Preconditions.checkNotNull(replacement, "AsyncFunction.apply returned null instead of a Future. Did you mean to return immediateFuture(null)? %s", fallback);



      
      return replacement;
    }

    
    void setResult(ListenableFuture<? extends V> result) {
      setFuture(result);
    }
  }






  
  private static final class CatchingFuture<V, X extends Throwable>
    extends AbstractCatchingFuture<V, X, Function<? super X, ? extends V>, V>
  {
    CatchingFuture(ListenableFuture<? extends V> input, Class<X> exceptionType, Function<? super X, ? extends V> fallback) {
      super(input, exceptionType, fallback);
    }


    
    V doFallback(Function<? super X, ? extends V> fallback, X cause) throws Exception {
      return (V)fallback.apply(cause);
    }

    
    void setResult(V result) {
      set(result);
    }
  }
}
