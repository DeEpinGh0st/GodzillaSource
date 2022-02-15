package com.google.common.util.concurrent;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.ForOverride;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;

















@GwtCompatible
abstract class AbstractTransformFuture<I, O, F, T>
  extends FluentFuture.TrustedFuture<O>
  implements Runnable
{
  ListenableFuture<? extends I> inputFuture;
  F function;
  
  static <I, O> ListenableFuture<O> create(ListenableFuture<I> input, AsyncFunction<? super I, ? extends O> function, Executor executor) {
    Preconditions.checkNotNull(executor);
    AsyncTransformFuture<I, O> output = new AsyncTransformFuture<>(input, function);
    input.addListener(output, MoreExecutors.rejectionPropagatingExecutor(executor, output));
    return output;
  }

  
  static <I, O> ListenableFuture<O> create(ListenableFuture<I> input, Function<? super I, ? extends O> function, Executor executor) {
    Preconditions.checkNotNull(function);
    TransformFuture<I, O> output = new TransformFuture<>(input, function);
    input.addListener(output, MoreExecutors.rejectionPropagatingExecutor(executor, output));
    return output;
  }







  
  AbstractTransformFuture(ListenableFuture<? extends I> inputFuture, F function) {
    this.inputFuture = (ListenableFuture<? extends I>)Preconditions.checkNotNull(inputFuture);
    this.function = (F)Preconditions.checkNotNull(function);
  }
  public final void run() {
    I sourceResult;
    T transformResult;
    ListenableFuture<? extends I> localInputFuture = this.inputFuture;
    F localFunction = this.function;
    if ((isCancelled() | ((localInputFuture == null) ? 1 : 0) | ((localFunction == null) ? 1 : 0)) != 0) {
      return;
    }
    this.inputFuture = null;
    
    if (localInputFuture.isCancelled()) {

      
      boolean unused = setFuture(localInputFuture);




      
      return;
    } 




    
    try {
      sourceResult = Futures.getDone((Future)localInputFuture);
    } catch (CancellationException e) {




      
      cancel(false);
      return;
    } catch (ExecutionException e) {
      
      setException(e.getCause());
      return;
    } catch (RuntimeException e) {
      
      setException(e);
      return;
    } catch (Error e) {




      
      setException(e);
      
      return;
    } 
    
    try {
      transformResult = doTransform(localFunction, sourceResult);
    } catch (Throwable t) {
      
      setException(t);
      return;
    } finally {
      this.function = null;
    } 




































    
    setResult(transformResult);
  }

  
  @ForOverride
  abstract T doTransform(F paramF, I paramI) throws Exception;

  
  @ForOverride
  abstract void setResult(T paramT);

  
  protected final void afterDone() {
    maybePropagateCancellationTo(this.inputFuture);
    this.inputFuture = null;
    this.function = null;
  }

  
  protected String pendingToString() {
    ListenableFuture<? extends I> localInputFuture = this.inputFuture;
    F localFunction = this.function;
    String superString = super.pendingToString();
    String resultString = "";
    if (localInputFuture != null) {
      resultString = "inputFuture=[" + localInputFuture + "], ";
    }
    if (localFunction != null)
      return resultString + "function=[" + localFunction + "]"; 
    if (superString != null) {
      return resultString + superString;
    }
    return null;
  }





  
  private static final class AsyncTransformFuture<I, O>
    extends AbstractTransformFuture<I, O, AsyncFunction<? super I, ? extends O>, ListenableFuture<? extends O>>
  {
    AsyncTransformFuture(ListenableFuture<? extends I> inputFuture, AsyncFunction<? super I, ? extends O> function) {
      super(inputFuture, function);
    }


    
    ListenableFuture<? extends O> doTransform(AsyncFunction<? super I, ? extends O> function, I input) throws Exception {
      ListenableFuture<? extends O> outputFuture = function.apply(input);
      Preconditions.checkNotNull(outputFuture, "AsyncFunction.apply returned null instead of a Future. Did you mean to return immediateFuture(null)? %s", function);



      
      return outputFuture;
    }

    
    void setResult(ListenableFuture<? extends O> result) {
      setFuture(result);
    }
  }




  
  private static final class TransformFuture<I, O>
    extends AbstractTransformFuture<I, O, Function<? super I, ? extends O>, O>
  {
    TransformFuture(ListenableFuture<? extends I> inputFuture, Function<? super I, ? extends O> function) {
      super(inputFuture, function);
    }


    
    O doTransform(Function<? super I, ? extends O> function, I input) {
      return (O)function.apply(input);
    }

    
    void setResult(O result) {
      set(result);
    }
  }
}
