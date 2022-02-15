package org.springframework.core.task.support;

import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.TimeUnit;
import org.springframework.core.task.TaskExecutor;
import org.springframework.util.Assert;









































public class ExecutorServiceAdapter
  extends AbstractExecutorService
{
  private final TaskExecutor taskExecutor;
  
  public ExecutorServiceAdapter(TaskExecutor taskExecutor) {
    Assert.notNull(taskExecutor, "TaskExecutor must not be null");
    this.taskExecutor = taskExecutor;
  }


  
  public void execute(Runnable task) {
    this.taskExecutor.execute(task);
  }

  
  public void shutdown() {
    throw new IllegalStateException("Manual shutdown not supported - ExecutorServiceAdapter is dependent on an external lifecycle");
  }


  
  public List<Runnable> shutdownNow() {
    throw new IllegalStateException("Manual shutdown not supported - ExecutorServiceAdapter is dependent on an external lifecycle");
  }


  
  public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
    throw new IllegalStateException("Manual shutdown not supported - ExecutorServiceAdapter is dependent on an external lifecycle");
  }


  
  public boolean isShutdown() {
    return false;
  }

  
  public boolean isTerminated() {
    return false;
  }
}
