package org.springframework.core.task;































public class TaskTimeoutException
  extends TaskRejectedException
{
  public TaskTimeoutException(String msg) {
    super(msg);
  }








  
  public TaskTimeoutException(String msg, Throwable cause) {
    super(msg, cause);
  }
}
