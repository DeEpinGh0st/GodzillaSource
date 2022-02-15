package org.springframework.core.task.support;

import java.util.concurrent.Executor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.util.Assert;




































public class ConcurrentExecutorAdapter
  implements Executor
{
  private final TaskExecutor taskExecutor;
  
  public ConcurrentExecutorAdapter(TaskExecutor taskExecutor) {
    Assert.notNull(taskExecutor, "TaskExecutor must not be null");
    this.taskExecutor = taskExecutor;
  }


  
  public void execute(Runnable command) {
    this.taskExecutor.execute(command);
  }
}
