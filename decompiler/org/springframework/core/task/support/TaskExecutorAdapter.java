package org.springframework.core.task.support;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RejectedExecutionException;
import org.springframework.core.task.AsyncListenableTaskExecutor;
import org.springframework.core.task.TaskDecorator;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureTask;




































public class TaskExecutorAdapter
  implements AsyncListenableTaskExecutor
{
  private final Executor concurrentExecutor;
  @Nullable
  private TaskDecorator taskDecorator;
  
  public TaskExecutorAdapter(Executor concurrentExecutor) {
    Assert.notNull(concurrentExecutor, "Executor must not be null");
    this.concurrentExecutor = concurrentExecutor;
  }
















  
  public final void setTaskDecorator(TaskDecorator taskDecorator) {
    this.taskDecorator = taskDecorator;
  }






  
  public void execute(Runnable task) {
    try {
      doExecute(this.concurrentExecutor, this.taskDecorator, task);
    }
    catch (RejectedExecutionException ex) {
      throw new TaskRejectedException("Executor [" + this.concurrentExecutor + "] did not accept task: " + task, ex);
    } 
  }


  
  public void execute(Runnable task, long startTimeout) {
    execute(task);
  }

  
  public Future<?> submit(Runnable task) {
    try {
      if (this.taskDecorator == null && this.concurrentExecutor instanceof ExecutorService) {
        return ((ExecutorService)this.concurrentExecutor).submit(task);
      }
      
      FutureTask<Object> future = new FutureTask(task, null);
      doExecute(this.concurrentExecutor, this.taskDecorator, future);
      return future;
    
    }
    catch (RejectedExecutionException ex) {
      throw new TaskRejectedException("Executor [" + this.concurrentExecutor + "] did not accept task: " + task, ex);
    } 
  }


  
  public <T> Future<T> submit(Callable<T> task) {
    try {
      if (this.taskDecorator == null && this.concurrentExecutor instanceof ExecutorService) {
        return ((ExecutorService)this.concurrentExecutor).submit(task);
      }
      
      FutureTask<T> future = new FutureTask<>(task);
      doExecute(this.concurrentExecutor, this.taskDecorator, future);
      return future;
    
    }
    catch (RejectedExecutionException ex) {
      throw new TaskRejectedException("Executor [" + this.concurrentExecutor + "] did not accept task: " + task, ex);
    } 
  }


  
  public ListenableFuture<?> submitListenable(Runnable task) {
    try {
      ListenableFutureTask<Object> future = new ListenableFutureTask(task, null);
      doExecute(this.concurrentExecutor, this.taskDecorator, (Runnable)future);
      return (ListenableFuture<?>)future;
    }
    catch (RejectedExecutionException ex) {
      throw new TaskRejectedException("Executor [" + this.concurrentExecutor + "] did not accept task: " + task, ex);
    } 
  }


  
  public <T> ListenableFuture<T> submitListenable(Callable<T> task) {
    try {
      ListenableFutureTask<T> future = new ListenableFutureTask(task);
      doExecute(this.concurrentExecutor, this.taskDecorator, (Runnable)future);
      return (ListenableFuture<T>)future;
    }
    catch (RejectedExecutionException ex) {
      throw new TaskRejectedException("Executor [" + this.concurrentExecutor + "] did not accept task: " + task, ex);
    } 
  }













  
  protected void doExecute(Executor concurrentExecutor, @Nullable TaskDecorator taskDecorator, Runnable runnable) throws RejectedExecutionException {
    concurrentExecutor.execute((taskDecorator != null) ? taskDecorator.decorate(runnable) : runnable);
  }
}
