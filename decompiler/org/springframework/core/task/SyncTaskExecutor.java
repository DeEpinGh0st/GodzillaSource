package org.springframework.core.task;

import java.io.Serializable;
import org.springframework.util.Assert;








































public class SyncTaskExecutor
  implements TaskExecutor, Serializable
{
  public void execute(Runnable task) {
    Assert.notNull(task, "Runnable must not be null");
    task.run();
  }
}
