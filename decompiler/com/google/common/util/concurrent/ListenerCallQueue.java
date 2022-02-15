package com.google.common.util.concurrent;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.Queues;
import com.google.errorprone.annotations.concurrent.GuardedBy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;









































@GwtIncompatible
final class ListenerCallQueue<L>
{
  private static final Logger logger = Logger.getLogger(ListenerCallQueue.class.getName());


  
  private final List<PerListenerQueue<L>> listeners = Collections.synchronizedList(new ArrayList<>());










  
  public void addListener(L listener, Executor executor) {
    Preconditions.checkNotNull(listener, "listener");
    Preconditions.checkNotNull(executor, "executor");
    this.listeners.add(new PerListenerQueue<>(listener, executor));
  }








  
  public void enqueue(Event<L> event) {
    enqueueHelper(event, event);
  }






  
  public void enqueue(Event<L> event, String label) {
    enqueueHelper(event, label);
  }
  
  private void enqueueHelper(Event<L> event, Object label) {
    Preconditions.checkNotNull(event, "event");
    Preconditions.checkNotNull(label, "label");
    synchronized (this.listeners) {
      for (PerListenerQueue<L> queue : this.listeners) {
        queue.add(event, label);
      }
    } 
  }






  
  public void dispatch() {
    for (int i = 0; i < this.listeners.size(); i++) {
      ((PerListenerQueue)this.listeners.get(i)).dispatch();
    }
  }


  
  private static final class PerListenerQueue<L>
    implements Runnable
  {
    final L listener;

    
    final Executor executor;

    
    @GuardedBy("this")
    final Queue<ListenerCallQueue.Event<L>> waitQueue = Queues.newArrayDeque();
    
    @GuardedBy("this")
    final Queue<Object> labelQueue = Queues.newArrayDeque();
    
    @GuardedBy("this")
    boolean isThreadScheduled;
    
    PerListenerQueue(L listener, Executor executor) {
      this.listener = (L)Preconditions.checkNotNull(listener);
      this.executor = (Executor)Preconditions.checkNotNull(executor);
    }

    
    synchronized void add(ListenerCallQueue.Event<L> event, Object label) {
      this.waitQueue.add(event);
      this.labelQueue.add(label);
    }




    
    void dispatch() {
      boolean scheduleEventRunner = false;
      synchronized (this) {
        if (!this.isThreadScheduled) {
          this.isThreadScheduled = true;
          scheduleEventRunner = true;
        } 
      } 
      if (scheduleEventRunner) {
        try {
          this.executor.execute(this);
        } catch (RuntimeException e) {
          
          synchronized (this) {
            this.isThreadScheduled = false;
          } 
          
          ListenerCallQueue.logger.log(Level.SEVERE, "Exception while running callbacks for " + this.listener + " on " + this.executor, e);


          
          throw e;
        } 
      }
    }

    
    public void run() {
      boolean stillRunning = true;
      try {
        while (true) {
          ListenerCallQueue.Event<L> nextToRun;
          Object nextLabel;
          synchronized (this) {
            Preconditions.checkState(this.isThreadScheduled);
            nextToRun = this.waitQueue.poll();
            nextLabel = this.labelQueue.poll();
            if (nextToRun == null) {
              this.isThreadScheduled = false;
              stillRunning = false;
              
              break;
            } 
          } 
          
          try {
            nextToRun.call(this.listener);
          } catch (RuntimeException e) {
            
            ListenerCallQueue.logger.log(Level.SEVERE, "Exception while executing callback: " + this.listener + " " + nextLabel, e);
          }
        
        }
      
      } finally {
        
        if (stillRunning)
        {
          
          synchronized (this) {
            this.isThreadScheduled = false;
          } 
        }
      } 
    }
  }
  
  static interface Event<L> {
    void call(L param1L);
  }
}
