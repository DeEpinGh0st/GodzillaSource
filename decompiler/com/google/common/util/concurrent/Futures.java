package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Function;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;




























































































































@GwtCompatible(emulated = true)
public final class Futures
  extends GwtFuturesCatchingSpecialization
{
  @Deprecated
  @Beta
  @GwtIncompatible
  public static <V, X extends Exception> CheckedFuture<V, X> makeChecked(ListenableFuture<V> future, Function<? super Exception, X> mapper) {
    return new MappingCheckedFuture<>((ListenableFuture<V>)Preconditions.checkNotNull(future), mapper);
  }





  
  public static <V> ListenableFuture<V> immediateFuture(V value) {
    if (value == null) {

      
      ListenableFuture<V> typedNull = ImmediateFuture.ImmediateSuccessfulFuture.NULL;
      return typedNull;
    } 
    return new ImmediateFuture.ImmediateSuccessfulFuture<>(value);
  }


















  
  @Deprecated
  @Beta
  @GwtIncompatible
  public static <V, X extends Exception> CheckedFuture<V, X> immediateCheckedFuture(V value) {
    return new ImmediateFuture.ImmediateSuccessfulCheckedFuture<>(value);
  }







  
  public static <V> ListenableFuture<V> immediateFailedFuture(Throwable throwable) {
    Preconditions.checkNotNull(throwable);
    return new ImmediateFuture.ImmediateFailedFuture<>(throwable);
  }






  
  public static <V> ListenableFuture<V> immediateCancelledFuture() {
    return new ImmediateFuture.ImmediateCancelledFuture<>();
  }



















  
  @Deprecated
  @Beta
  @GwtIncompatible
  public static <V, X extends Exception> CheckedFuture<V, X> immediateFailedCheckedFuture(X exception) {
    Preconditions.checkNotNull(exception);
    return new ImmediateFuture.ImmediateFailedCheckedFuture<>(exception);
  }






  
  @Beta
  public static <O> ListenableFuture<O> submitAsync(AsyncCallable<O> callable, Executor executor) {
    TrustedListenableFutureTask<O> task = TrustedListenableFutureTask.create(callable);
    executor.execute(task);
    return task;
  }











  
  @Beta
  @GwtIncompatible
  public static <O> ListenableFuture<O> scheduleAsync(AsyncCallable<O> callable, long delay, TimeUnit timeUnit, ScheduledExecutorService executorService) {
    TrustedListenableFutureTask<O> task = TrustedListenableFutureTask.create(callable);
    final Future<?> scheduled = executorService.schedule(task, delay, timeUnit);
    task.addListener(new Runnable()
        {
          
          public void run()
          {
            scheduled.cancel(false);
          }
        }, 
        MoreExecutors.directExecutor());
    return task;
  }










































  
  @Beta
  @GwtIncompatible("AVAILABLE but requires exceptionType to be Throwable.class")
  public static <V, X extends Throwable> ListenableFuture<V> catching(ListenableFuture<? extends V> input, Class<X> exceptionType, Function<? super X, ? extends V> fallback, Executor executor) {
    return AbstractCatchingFuture.create(input, exceptionType, fallback, executor);
  }































































  
  @Beta
  @GwtIncompatible("AVAILABLE but requires exceptionType to be Throwable.class")
  public static <V, X extends Throwable> ListenableFuture<V> catchingAsync(ListenableFuture<? extends V> input, Class<X> exceptionType, AsyncFunction<? super X, ? extends V> fallback, Executor executor) {
    return AbstractCatchingFuture.create(input, exceptionType, fallback, executor);
  }

















  
  @Beta
  @GwtIncompatible
  public static <V> ListenableFuture<V> withTimeout(ListenableFuture<V> delegate, long time, TimeUnit unit, ScheduledExecutorService scheduledExecutor) {
    if (delegate.isDone()) {
      return delegate;
    }
    return TimeoutFuture.create(delegate, time, unit, scheduledExecutor);
  }







































  
  @Beta
  public static <I, O> ListenableFuture<O> transformAsync(ListenableFuture<I> input, AsyncFunction<? super I, ? extends O> function, Executor executor) {
    return AbstractTransformFuture.create(input, function, executor);
  }
































  
  @Beta
  public static <I, O> ListenableFuture<O> transform(ListenableFuture<I> input, Function<? super I, ? extends O> function, Executor executor) {
    return AbstractTransformFuture.create(input, function, executor);
  }





















  
  @Beta
  @GwtIncompatible
  public static <I, O> Future<O> lazyTransform(final Future<I> input, final Function<? super I, ? extends O> function) {
    Preconditions.checkNotNull(input);
    Preconditions.checkNotNull(function);
    return new Future<O>()
      {
        public boolean cancel(boolean mayInterruptIfRunning)
        {
          return input.cancel(mayInterruptIfRunning);
        }

        
        public boolean isCancelled() {
          return input.isCancelled();
        }

        
        public boolean isDone() {
          return input.isDone();
        }

        
        public O get() throws InterruptedException, ExecutionException {
          return applyTransformation(input.get());
        }


        
        public O get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
          return applyTransformation(input.get(timeout, unit));
        }
        
        private O applyTransformation(I input) throws ExecutionException {
          try {
            return (O)function.apply(input);
          } catch (Throwable t) {
            throw new ExecutionException(t);
          } 
        }
      };
  }













  
  @SafeVarargs
  @Beta
  public static <V> ListenableFuture<List<V>> allAsList(ListenableFuture<? extends V>... futures) {
    return new CollectionFuture.ListFuture<>((ImmutableCollection<? extends ListenableFuture<? extends V>>)ImmutableList.copyOf((Object[])futures), true);
  }














  
  @Beta
  public static <V> ListenableFuture<List<V>> allAsList(Iterable<? extends ListenableFuture<? extends V>> futures) {
    return new CollectionFuture.ListFuture<>((ImmutableCollection<? extends ListenableFuture<? extends V>>)ImmutableList.copyOf(futures), true);
  }






  
  @SafeVarargs
  @Beta
  public static <V> FutureCombiner<V> whenAllComplete(ListenableFuture<? extends V>... futures) {
    return new FutureCombiner<>(false, ImmutableList.copyOf((Object[])futures));
  }







  
  @Beta
  public static <V> FutureCombiner<V> whenAllComplete(Iterable<? extends ListenableFuture<? extends V>> futures) {
    return new FutureCombiner<>(false, ImmutableList.copyOf(futures));
  }







  
  @SafeVarargs
  @Beta
  public static <V> FutureCombiner<V> whenAllSucceed(ListenableFuture<? extends V>... futures) {
    return new FutureCombiner<>(true, ImmutableList.copyOf((Object[])futures));
  }








  
  @Beta
  public static <V> FutureCombiner<V> whenAllSucceed(Iterable<? extends ListenableFuture<? extends V>> futures) {
    return new FutureCombiner<>(true, ImmutableList.copyOf(futures));
  }









  
  @Beta
  @CanIgnoreReturnValue
  @GwtCompatible
  public static final class FutureCombiner<V>
  {
    private final boolean allMustSucceed;








    
    private final ImmutableList<ListenableFuture<? extends V>> futures;








    
    private FutureCombiner(boolean allMustSucceed, ImmutableList<ListenableFuture<? extends V>> futures) {
      this.allMustSucceed = allMustSucceed;
      this.futures = futures;
    }














    
    public <C> ListenableFuture<C> callAsync(AsyncCallable<C> combiner, Executor executor) {
      return new CombinedFuture<>((ImmutableCollection<? extends ListenableFuture<?>>)this.futures, this.allMustSucceed, executor, combiner);
    }














    
    @CanIgnoreReturnValue
    public <C> ListenableFuture<C> call(Callable<C> combiner, Executor executor) {
      return new CombinedFuture<>((ImmutableCollection<? extends ListenableFuture<?>>)this.futures, this.allMustSucceed, executor, combiner);
    }











    
    public ListenableFuture<?> run(final Runnable combiner, Executor executor) {
      return call(new Callable<Void>()
          {
            public Void call() throws Exception
            {
              combiner.run();
              return null;
            }
          },  executor);
    }
  }








  
  @Beta
  public static <V> ListenableFuture<V> nonCancellationPropagating(ListenableFuture<V> future) {
    if (future.isDone()) {
      return future;
    }
    NonCancellationPropagatingFuture<V> output = new NonCancellationPropagatingFuture<>(future);
    future.addListener(output, MoreExecutors.directExecutor());
    return output;
  }
  
  private static final class NonCancellationPropagatingFuture<V>
    extends AbstractFuture.TrustedFuture<V>
    implements Runnable {
    private ListenableFuture<V> delegate;
    
    NonCancellationPropagatingFuture(ListenableFuture<V> delegate) {
      this.delegate = delegate;
    }



    
    public void run() {
      ListenableFuture<V> localDelegate = this.delegate;
      if (localDelegate != null) {
        setFuture(localDelegate);
      }
    }

    
    protected String pendingToString() {
      ListenableFuture<V> localDelegate = this.delegate;
      if (localDelegate != null) {
        return "delegate=[" + localDelegate + "]";
      }
      return null;
    }

    
    protected void afterDone() {
      this.delegate = null;
    }
  }














  
  @SafeVarargs
  @Beta
  public static <V> ListenableFuture<List<V>> successfulAsList(ListenableFuture<? extends V>... futures) {
    return new CollectionFuture.ListFuture<>((ImmutableCollection<? extends ListenableFuture<? extends V>>)ImmutableList.copyOf((Object[])futures), false);
  }














  
  @Beta
  public static <V> ListenableFuture<List<V>> successfulAsList(Iterable<? extends ListenableFuture<? extends V>> futures) {
    return new CollectionFuture.ListFuture<>((ImmutableCollection<? extends ListenableFuture<? extends V>>)ImmutableList.copyOf(futures), false);
  }























  
  @Beta
  public static <T> ImmutableList<ListenableFuture<T>> inCompletionOrder(Iterable<? extends ListenableFuture<? extends T>> futures) {
    ImmutableList immutableList;
    if (futures instanceof Collection) {
      Collection<ListenableFuture<? extends T>> collection = (Collection)futures;
    } else {
      immutableList = ImmutableList.copyOf(futures);
    } 


    
    ListenableFuture[] arrayOfListenableFuture = (ListenableFuture[])immutableList.toArray((Object[])new ListenableFuture[immutableList.size()]);
    final InCompletionOrderState<T> state = new InCompletionOrderState<>(arrayOfListenableFuture);
    ImmutableList.Builder<AbstractFuture<T>> delegatesBuilder = ImmutableList.builder();
    for (int i = 0; i < arrayOfListenableFuture.length; i++) {
      delegatesBuilder.add(new InCompletionOrderFuture(state));
    }
    
    final ImmutableList<AbstractFuture<T>> delegates = delegatesBuilder.build();
    for (int j = 0; j < arrayOfListenableFuture.length; j++) {
      final int localI = j;
      arrayOfListenableFuture[j].addListener(new Runnable()
          {
            public void run()
            {
              state.recordInputCompletion(delegates, localI);
            }
          }MoreExecutors.directExecutor());
    } 

    
    return (ImmutableList)delegates;
  }

  
  private static final class InCompletionOrderFuture<T>
    extends AbstractFuture<T>
  {
    private Futures.InCompletionOrderState<T> state;

    
    private InCompletionOrderFuture(Futures.InCompletionOrderState<T> state) {
      this.state = state;
    }

    
    public boolean cancel(boolean interruptIfRunning) {
      Futures.InCompletionOrderState<T> localState = this.state;
      if (super.cancel(interruptIfRunning)) {
        localState.recordOutputCancellation(interruptIfRunning);
        return true;
      } 
      return false;
    }

    
    protected void afterDone() {
      this.state = null;
    }

    
    protected String pendingToString() {
      Futures.InCompletionOrderState<T> localState = this.state;
      if (localState != null)
      {
        
        return "inputCount=[" + localState
          .inputFutures.length + "], remaining=[" + localState
          
          .incompleteOutputCount.get() + "]";
      }
      
      return null;
    }
  }

  
  private static final class InCompletionOrderState<T>
  {
    private boolean wasCancelled = false;
    
    private boolean shouldInterrupt = true;
    private final AtomicInteger incompleteOutputCount;
    private final ListenableFuture<? extends T>[] inputFutures;
    private volatile int delegateIndex = 0;
    
    private InCompletionOrderState(ListenableFuture<? extends T>[] inputFutures) {
      this.inputFutures = inputFutures;
      this.incompleteOutputCount = new AtomicInteger(inputFutures.length);
    }
    
    private void recordOutputCancellation(boolean interruptIfRunning) {
      this.wasCancelled = true;

      
      if (!interruptIfRunning) {
        this.shouldInterrupt = false;
      }
      recordCompletion();
    }

    
    private void recordInputCompletion(ImmutableList<AbstractFuture<T>> delegates, int inputFutureIndex) {
      ListenableFuture<? extends T> inputFuture = this.inputFutures[inputFutureIndex];
      
      this.inputFutures[inputFutureIndex] = null;
      for (int i = this.delegateIndex; i < delegates.size(); i++) {
        if (((AbstractFuture<T>)delegates.get(i)).setFuture(inputFuture)) {
          recordCompletion();
          
          this.delegateIndex = i + 1;

          
          return;
        } 
      } 
      
      this.delegateIndex = delegates.size();
    }
    
    private void recordCompletion() {
      if (this.incompleteOutputCount.decrementAndGet() == 0 && this.wasCancelled) {
        for (ListenableFuture<?> toCancel : this.inputFutures) {
          if (toCancel != null) {
            toCancel.cancel(this.shouldInterrupt);
          }
        } 
      }
    }
  }









































  
  public static <V> void addCallback(ListenableFuture<V> future, FutureCallback<? super V> callback, Executor executor) {
    Preconditions.checkNotNull(callback);
    future.addListener(new CallbackListener<>(future, callback), executor);
  }
  
  private static final class CallbackListener<V>
    implements Runnable {
    final Future<V> future;
    final FutureCallback<? super V> callback;
    
    CallbackListener(Future<V> future, FutureCallback<? super V> callback) {
      this.future = future;
      this.callback = callback;
    }

    
    public void run() {
      V value;
      try {
        value = Futures.getDone(this.future);
      } catch (ExecutionException e) {
        this.callback.onFailure(e.getCause());
        return;
      } catch (RuntimeException|Error e) {
        this.callback.onFailure(e);
        return;
      } 
      this.callback.onSuccess(value);
    }

    
    public String toString() {
      return MoreObjects.toStringHelper(this).addValue(this.callback).toString();
    }
  }































  
  @CanIgnoreReturnValue
  public static <V> V getDone(Future<V> future) throws ExecutionException {
    Preconditions.checkState(future.isDone(), "Future was expected to be done: %s", future);
    return Uninterruptibles.getUninterruptibly(future);
  }











































  
  @Beta
  @CanIgnoreReturnValue
  @GwtIncompatible
  public static <V, X extends Exception> V getChecked(Future<V> future, Class<X> exceptionClass) throws X {
    return FuturesGetChecked.getChecked(future, exceptionClass);
  }













































  
  @Beta
  @CanIgnoreReturnValue
  @GwtIncompatible
  public static <V, X extends Exception> V getChecked(Future<V> future, Class<X> exceptionClass, long timeout, TimeUnit unit) throws X {
    return FuturesGetChecked.getChecked(future, exceptionClass, timeout, unit);
  }


































  
  @CanIgnoreReturnValue
  public static <V> V getUnchecked(Future<V> future) {
    Preconditions.checkNotNull(future);
    try {
      return Uninterruptibles.getUninterruptibly(future);
    } catch (ExecutionException e) {
      wrapAndThrowUnchecked(e.getCause());
      throw new AssertionError();
    } 
  }
  
  private static void wrapAndThrowUnchecked(Throwable cause) {
    if (cause instanceof Error) {
      throw new ExecutionError((Error)cause);
    }




    
    throw new UncheckedExecutionException(cause);
  }








  
  @GwtIncompatible
  private static class MappingCheckedFuture<V, X extends Exception>
    extends AbstractCheckedFuture<V, X>
  {
    final Function<? super Exception, X> mapper;







    
    MappingCheckedFuture(ListenableFuture<V> delegate, Function<? super Exception, X> mapper) {
      super(delegate);
      
      this.mapper = (Function<? super Exception, X>)Preconditions.checkNotNull(mapper);
    }

    
    protected X mapException(Exception e) {
      return (X)this.mapper.apply(e);
    }
  }
}
