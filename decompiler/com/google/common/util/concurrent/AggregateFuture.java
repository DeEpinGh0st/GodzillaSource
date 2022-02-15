package com.google.common.util.concurrent;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.UnmodifiableIterator;
import com.google.errorprone.annotations.ForOverride;
import com.google.errorprone.annotations.OverridingMethodsMustInvokeSuper;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;























@GwtCompatible
abstract class AggregateFuture<InputT, OutputT>
  extends AbstractFuture.TrustedFuture<OutputT>
{
  private static final Logger logger = Logger.getLogger(AggregateFuture.class.getName());


  
  private RunningState runningState;



  
  protected final void afterDone() {
    super.afterDone();
    RunningState localRunningState = this.runningState;
    if (localRunningState != null) {
      
      this.runningState = null;
      
      ImmutableCollection<? extends ListenableFuture<? extends InputT>> futures = localRunningState.futures;
      boolean wasInterrupted = wasInterrupted();
      
      if (wasInterrupted) {
        localRunningState.interruptTask();
      }
      
      if ((isCancelled() & ((futures != null) ? 1 : 0)) != 0) {
        for (UnmodifiableIterator<ListenableFuture> unmodifiableIterator = futures.iterator(); unmodifiableIterator.hasNext(); ) { ListenableFuture<?> future = unmodifiableIterator.next();
          future.cancel(wasInterrupted); }
      
      }
    } 
  }

  
  protected String pendingToString() {
    RunningState localRunningState = this.runningState;
    if (localRunningState == null) {
      return null;
    }
    
    ImmutableCollection<? extends ListenableFuture<? extends InputT>> localFutures = localRunningState.futures;
    if (localFutures != null) {
      return "futures=[" + localFutures + "]";
    }
    return null;
  }

  
  final void init(RunningState runningState) {
    this.runningState = runningState;
    runningState.init();
  }
  
  abstract class RunningState
    extends AggregateFutureState
    implements Runnable
  {
    private ImmutableCollection<? extends ListenableFuture<? extends InputT>> futures;
    private final boolean allMustSucceed;
    private final boolean collectsValues;
    
    RunningState(ImmutableCollection<? extends ListenableFuture<? extends InputT>> futures, boolean allMustSucceed, boolean collectsValues) {
      super(futures.size());
      this.futures = (ImmutableCollection<? extends ListenableFuture<? extends InputT>>)Preconditions.checkNotNull(futures);
      this.allMustSucceed = allMustSucceed;
      this.collectsValues = collectsValues;
    }


    
    public final void run() {
      decrementCountAndMaybeComplete();
    }







    
    private void init() {
      if (this.futures.isEmpty()) {
        handleAllCompleted();

        
        return;
      } 

      
      if (this.allMustSucceed) {








        
        int i = 0;
        for (UnmodifiableIterator<ListenableFuture<? extends InputT>> unmodifiableIterator = this.futures.iterator(); unmodifiableIterator.hasNext(); ) { final ListenableFuture<? extends InputT> listenable = unmodifiableIterator.next();
          final int index = i++;
          listenable.addListener(new Runnable()
              {
                public void run()
                {
                  try {
                    AggregateFuture.RunningState.this.handleOneInputDone(index, listenable);
                  } finally {
                    AggregateFuture.RunningState.this.decrementCountAndMaybeComplete();
                  }
                
                }
              }MoreExecutors.directExecutor()); }

      
      } else {
        
        for (UnmodifiableIterator<ListenableFuture<? extends InputT>> unmodifiableIterator = this.futures.iterator(); unmodifiableIterator.hasNext(); ) { final ListenableFuture<? extends InputT> listenable = unmodifiableIterator.next();
          listenable.addListener(this, MoreExecutors.directExecutor()); }
      
      } 
    }






    
    private void handleException(Throwable throwable) {
      Preconditions.checkNotNull(throwable);
      
      boolean completedWithFailure = false;
      boolean firstTimeSeeingThisException = true;
      if (this.allMustSucceed) {

        
        completedWithFailure = AggregateFuture.this.setException(throwable);
        if (completedWithFailure) {
          releaseResourcesAfterFailure();
        }
        else {
          
          firstTimeSeeingThisException = AggregateFuture.addCausalChain(getOrInitSeenExceptions(), throwable);
        } 
      } 

      
      if ((throwable instanceof Error | this.allMustSucceed & (!completedWithFailure ? 1 : 0) & firstTimeSeeingThisException) != 0) {
        
        String message = (throwable instanceof Error) ? "Input Future failed with Error" : "Got more than one input Future failure. Logging failures after the first";


        
        AggregateFuture.logger.log(Level.SEVERE, message, throwable);
      } 
    }

    
    final void addInitialException(Set<Throwable> seen) {
      if (!AggregateFuture.this.isCancelled())
      {
        boolean bool = AggregateFuture.addCausalChain(seen, AggregateFuture.this.tryInternalFastPathGetFailure());
      }
    }



    
    private void handleOneInputDone(int index, Future<? extends InputT> future) {
      Preconditions.checkState((this.allMustSucceed || 
          !AggregateFuture.this.isDone() || AggregateFuture.this.isCancelled()), "Future was done before all dependencies completed");

      
      try {
        Preconditions.checkState(future.isDone(), "Tried to set value from future which is not done");
        if (this.allMustSucceed) {
          if (future.isCancelled()) {

            
            AggregateFuture.this.runningState = null;
            AggregateFuture.this.cancel(false);
          } else {
            
            InputT result = Futures.getDone((Future)future);
            if (this.collectsValues) {
              collectOneValue(this.allMustSucceed, index, result);
            }
          } 
        } else if (this.collectsValues && !future.isCancelled()) {
          collectOneValue(this.allMustSucceed, index, Futures.getDone((Future)future));
        } 
      } catch (ExecutionException e) {
        handleException(e.getCause());
      } catch (Throwable t) {
        handleException(t);
      } 
    }
    
    private void decrementCountAndMaybeComplete() {
      int newRemaining = decrementRemainingAndGet();
      Preconditions.checkState((newRemaining >= 0), "Less than 0 remaining futures");
      if (newRemaining == 0) {
        processCompleted();
      }
    }


    
    private void processCompleted() {
      if ((this.collectsValues & (!this.allMustSucceed ? 1 : 0)) != 0) {
        int i = 0;
        for (UnmodifiableIterator<ListenableFuture<? extends InputT>> unmodifiableIterator = this.futures.iterator(); unmodifiableIterator.hasNext(); ) { ListenableFuture<? extends InputT> listenable = unmodifiableIterator.next();
          handleOneInputDone(i++, listenable); }
      
      } 
      handleAllCompleted();
    }









    
    @ForOverride
    @OverridingMethodsMustInvokeSuper
    void releaseResourcesAfterFailure() {
      this.futures = null;
    }


    
    void interruptTask() {}


    
    abstract void collectOneValue(boolean param1Boolean, int param1Int, InputT param1InputT);


    
    abstract void handleAllCompleted();
  }

  
  private static boolean addCausalChain(Set<Throwable> seen, Throwable t) {
    for (; t != null; t = t.getCause()) {
      boolean firstTimeSeen = seen.add(t);
      if (!firstTimeSeen)
      {




        
        return false;
      }
    } 
    return true;
  }
}
