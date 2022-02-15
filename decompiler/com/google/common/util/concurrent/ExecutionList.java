package com.google.common.util.concurrent;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.concurrent.GuardedBy;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;

































@GwtIncompatible
public final class ExecutionList
{
  private static final Logger log = Logger.getLogger(ExecutionList.class.getName());






  
  @GuardedBy("this")
  private RunnableExecutorPair runnables;





  
  @GuardedBy("this")
  private boolean executed;






  
  public void add(Runnable runnable, Executor executor) {
    Preconditions.checkNotNull(runnable, "Runnable was null.");
    Preconditions.checkNotNull(executor, "Executor was null.");



    
    synchronized (this) {
      if (!this.executed) {
        this.runnables = new RunnableExecutorPair(runnable, executor, this.runnables);

        
        return;
      } 
    } 

    
    executeListener(runnable, executor);
  }













  
  public void execute() {
    RunnableExecutorPair list;
    synchronized (this) {
      if (this.executed) {
        return;
      }
      this.executed = true;
      list = this.runnables;
      this.runnables = null;
    } 








    
    RunnableExecutorPair reversedList = null;
    while (list != null) {
      RunnableExecutorPair tmp = list;
      list = list.next;
      tmp.next = reversedList;
      reversedList = tmp;
    } 
    while (reversedList != null) {
      executeListener(reversedList.runnable, reversedList.executor);
      reversedList = reversedList.next;
    } 
  }




  
  private static void executeListener(Runnable runnable, Executor executor) {
    try {
      executor.execute(runnable);
    } catch (RuntimeException e) {


      
      log.log(Level.SEVERE, "RuntimeException while executing runnable " + runnable + " with executor " + executor, e);
    } 
  }

  
  private static final class RunnableExecutorPair
  {
    final Runnable runnable;
    
    final Executor executor;
    RunnableExecutorPair next;
    
    RunnableExecutorPair(Runnable runnable, Executor executor, RunnableExecutorPair next) {
      this.runnable = runnable;
      this.executor = executor;
      this.next = next;
    }
  }
}
