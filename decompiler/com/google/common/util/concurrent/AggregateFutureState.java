package com.google.common.util.concurrent;

import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.Sets;
import com.google.j2objc.annotations.ReflectionSupport;
import java.util.Set;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import java.util.logging.Level;
import java.util.logging.Logger;
























@GwtCompatible(emulated = true)
@ReflectionSupport(ReflectionSupport.Level.FULL)
abstract class AggregateFutureState
{
  static {
    AtomicHelper helper;
  }
  
  private volatile Set<Throwable> seenExceptions = null;
  
  private volatile int remaining;
  
  private static final AtomicHelper ATOMIC_HELPER;
  
  private static final Logger log = Logger.getLogger(AggregateFutureState.class.getName());

  
  static {
    Throwable thrownReflectionFailure = null;


    
    try {
      helper = new SafeAtomicHelper(AtomicReferenceFieldUpdater.newUpdater(AggregateFutureState.class, Set.class, "seenExceptions"), AtomicIntegerFieldUpdater.newUpdater(AggregateFutureState.class, "remaining"));
    } catch (Throwable reflectionFailure) {



      
      thrownReflectionFailure = reflectionFailure;
      helper = new SynchronizedAtomicHelper();
    } 
    ATOMIC_HELPER = helper;

    
    if (thrownReflectionFailure != null) {
      log.log(Level.SEVERE, "SafeAtomicHelper is broken!", thrownReflectionFailure);
    }
  }
  
  AggregateFutureState(int remainingFutures) {
    this.remaining = remainingFutures;
  }
















  
  final Set<Throwable> getOrInitSeenExceptions() {
    Set<Throwable> seenExceptionsLocal = this.seenExceptions;
    if (seenExceptionsLocal == null) {
      seenExceptionsLocal = Sets.newConcurrentHashSet();




      
      addInitialException(seenExceptionsLocal);
      
      ATOMIC_HELPER.compareAndSetSeenExceptions(this, null, seenExceptionsLocal);





      
      seenExceptionsLocal = this.seenExceptions;
    } 
    return seenExceptionsLocal;
  }



  
  final int decrementRemainingAndGet() {
    return ATOMIC_HELPER.decrementAndGetRemainingCount(this);
  }
  
  abstract void addInitialException(Set<Throwable> paramSet);
  
  private static abstract class AtomicHelper
  {
    private AtomicHelper() {}
    
    abstract void compareAndSetSeenExceptions(AggregateFutureState param1AggregateFutureState, Set<Throwable> param1Set1, Set<Throwable> param1Set2);
    
    abstract int decrementAndGetRemainingCount(AggregateFutureState param1AggregateFutureState);
  }
  
  private static final class SafeAtomicHelper
    extends AtomicHelper {
    final AtomicReferenceFieldUpdater<AggregateFutureState, Set<Throwable>> seenExceptionsUpdater;
    final AtomicIntegerFieldUpdater<AggregateFutureState> remainingCountUpdater;
    
    SafeAtomicHelper(AtomicReferenceFieldUpdater<AggregateFutureState, Set<Throwable>> seenExceptionsUpdater, AtomicIntegerFieldUpdater<AggregateFutureState> remainingCountUpdater) {
      this.seenExceptionsUpdater = seenExceptionsUpdater;
      this.remainingCountUpdater = remainingCountUpdater;
    }


    
    void compareAndSetSeenExceptions(AggregateFutureState state, Set<Throwable> expect, Set<Throwable> update) {
      this.seenExceptionsUpdater.compareAndSet(state, expect, update);
    }

    
    int decrementAndGetRemainingCount(AggregateFutureState state) {
      return this.remainingCountUpdater.decrementAndGet(state);
    }
  }
  
  private static final class SynchronizedAtomicHelper extends AtomicHelper {
    private SynchronizedAtomicHelper() {}
    
    void compareAndSetSeenExceptions(AggregateFutureState state, Set<Throwable> expect, Set<Throwable> update) {
      synchronized (state) {
        if (state.seenExceptions == expect) {
          state.seenExceptions = update;
        }
      } 
    }

    
    int decrementAndGetRemainingCount(AggregateFutureState state) {
      synchronized (state) {
        state.remaining--;
        return state.remaining;
      } 
    }
  }
}
