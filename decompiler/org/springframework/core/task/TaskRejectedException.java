package org.springframework.core.task;

import java.util.concurrent.RejectedExecutionException;































public class TaskRejectedException
  extends RejectedExecutionException
{
  public TaskRejectedException(String msg) {
    super(msg);
  }








  
  public TaskRejectedException(String msg, Throwable cause) {
    super(msg, cause);
  }
}
