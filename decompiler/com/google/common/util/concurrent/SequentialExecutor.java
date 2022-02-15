package com.google.common.util.concurrent;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.concurrent.GuardedBy;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;



































@GwtIncompatible
final class SequentialExecutor
  implements Executor
{
  private static final Logger log = Logger.getLogger(SequentialExecutor.class.getName());
  private final Executor executor;
  
  enum WorkerRunningState {
    IDLE,
    
    QUEUING,
    
    QUEUED,
    RUNNING;
  }


  
  @GuardedBy("queue")
  private final Deque<Runnable> queue = new ArrayDeque<>();

  
  @GuardedBy("queue")
  private WorkerRunningState workerRunningState = WorkerRunningState.IDLE;







  
  @GuardedBy("queue")
  private long workerRunCount = 0L;

  
  private final QueueWorker worker = new QueueWorker();

  
  SequentialExecutor(Executor executor) {
    this.executor = (Executor)Preconditions.checkNotNull(executor);
  }





  
  public void execute(final Runnable task) {
    Runnable submittedTask;
    long oldRunCount;
    Preconditions.checkNotNull(task);

    
    synchronized (this.queue) {

      
      if (this.workerRunningState == WorkerRunningState.RUNNING || this.workerRunningState == WorkerRunningState.QUEUED) {
        this.queue.add(task);
        
        return;
      } 
      oldRunCount = this.workerRunCount;






      
      submittedTask = new Runnable()
        {
          public void run()
          {
            task.run();
          }
        };
      this.queue.add(submittedTask);
      this.workerRunningState = WorkerRunningState.QUEUING;
    } 
    
    try {
      this.executor.execute(this.worker);
    } catch (RuntimeException|Error t) {
      synchronized (this.queue) {

        
        boolean removed = ((this.workerRunningState == WorkerRunningState.IDLE || this.workerRunningState == WorkerRunningState.QUEUING) && this.queue.removeLastOccurrence(submittedTask));

        
        if (!(t instanceof java.util.concurrent.RejectedExecutionException) || removed) {
          throw t;
        }
      } 





      
      return;
    } 





    
    boolean alreadyMarkedQueued = (this.workerRunningState != WorkerRunningState.QUEUING);
    if (alreadyMarkedQueued) {
      return;
    }
    synchronized (this.queue) {
      if (this.workerRunCount == oldRunCount && this.workerRunningState == WorkerRunningState.QUEUING)
        this.workerRunningState = WorkerRunningState.QUEUED; 
    } 
  }
  
  private final class QueueWorker
    implements Runnable
  {
    private QueueWorker() {}
    
    public void run() {
      try {
        workOnQueue();
      } catch (Error e) {
        synchronized (SequentialExecutor.this.queue) {
          SequentialExecutor.this.workerRunningState = SequentialExecutor.WorkerRunningState.IDLE;
        } 
        throw e;
      } 
    }














    
    private void workOnQueue() {
      boolean interruptedDuringTask = false;
      boolean hasSetRunning = false;
      try {
        while (true) {
          Runnable task;
          synchronized (SequentialExecutor.this.queue) {

            
            if (!hasSetRunning) {
              if (SequentialExecutor.this.workerRunningState == SequentialExecutor.WorkerRunningState.RUNNING) {
                return;
              }



              
              SequentialExecutor.this.workerRunCount++;
              SequentialExecutor.this.workerRunningState = SequentialExecutor.WorkerRunningState.RUNNING;
              hasSetRunning = true;
            } 
            
            task = SequentialExecutor.this.queue.poll();
            if (task == null) {
              SequentialExecutor.this.workerRunningState = SequentialExecutor.WorkerRunningState.IDLE;

              
              return;
            } 
          } 
          
          interruptedDuringTask |= Thread.interrupted();
          try {
            task.run();
          } catch (RuntimeException e) {
            SequentialExecutor.log.log(Level.SEVERE, "Exception while executing runnable " + task, e);
          }
        
        }
      
      } finally {
        
        if (interruptedDuringTask)
          Thread.currentThread().interrupt(); 
      } 
    }
  }
}
