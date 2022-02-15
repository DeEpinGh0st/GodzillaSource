package com.google.common.util.concurrent;

import com.google.common.annotations.GwtCompatible;
import com.google.j2objc.annotations.ReflectionSupport;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;





















@GwtCompatible(emulated = true)
@ReflectionSupport(ReflectionSupport.Level.FULL)
abstract class InterruptibleTask<T>
  extends AtomicReference<Runnable>
  implements Runnable
{
  static {
    Class<LockSupport> clazz = LockSupport.class;
  }
  
  private static final class DoNothingRunnable implements Runnable {
    private DoNothingRunnable() {}
    
    public void run() {}
  }
  
  private static final Runnable DONE = new DoNothingRunnable();
  private static final Runnable INTERRUPTING = new DoNothingRunnable();
  private static final Runnable PARKED = new DoNothingRunnable();




  
  private static final int MAX_BUSY_WAIT_SPINS = 1000;




  
  public final void run() {
    Thread currentThread = Thread.currentThread();
    if (!compareAndSet(null, currentThread)) {
      return;
    }
    
    boolean run = !isDone();
    T result = null;
    Throwable error = null;
    try {
      if (run) {
        result = runInterruptibly();
      }
    } catch (Throwable t) {
      error = t;
    } finally {
      
      if (!compareAndSet(currentThread, DONE)) {





        
        boolean restoreInterruptedBit = false;
        int spinCount = 0;








        
        Runnable state = get();
        while (state == INTERRUPTING || state == PARKED) {
          spinCount++;
          if (spinCount > 1000) {





            
            if (state == PARKED || compareAndSet(INTERRUPTING, PARKED)) {










              
              restoreInterruptedBit = (Thread.interrupted() || restoreInterruptedBit);
              LockSupport.park(this);
            } 
          } else {
            Thread.yield();
          } 
          state = get();
        } 
        if (restoreInterruptedBit) {
          currentThread.interrupt();
        }
      } 




      
      if (run) {
        afterRanInterruptibly(result, error);
      }
    } 
  }

























  
  final void interruptTask() {
    Runnable currentRunner = get();
    if (currentRunner instanceof Thread && compareAndSet(currentRunner, INTERRUPTING)) {
      
      try {


        
        ((Thread)currentRunner).interrupt();
      } finally {
        Runnable prev = getAndSet(DONE);
        if (prev == PARKED) {
          LockSupport.unpark((Thread)currentRunner);
        }
      } 
    }
  }
  
  public final String toString() {
    String result;
    Runnable state = get();
    
    if (state == DONE) {
      result = "running=[DONE]";
    } else if (state == INTERRUPTING) {
      result = "running=[INTERRUPTED]";
    } else if (state instanceof Thread) {
      
      result = "running=[RUNNING ON " + ((Thread)state).getName() + "]";
    } else {
      result = "running=[NOT STARTED YET]";
    } 
    return result + ", " + toPendingString();
  }
  
  abstract boolean isDone();
  
  abstract T runInterruptibly() throws Exception;
  
  abstract void afterRanInterruptibly(T paramT, Throwable paramThrowable);
  
  abstract String toPendingString();
}
