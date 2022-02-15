package com.google.common.util.concurrent;

import com.google.common.annotations.GwtCompatible;
import java.util.concurrent.Executor;

















@GwtCompatible
enum DirectExecutor
  implements Executor
{
  INSTANCE;

  
  public void execute(Runnable command) {
    command.run();
  }

  
  public String toString() {
    return "MoreExecutors.directExecutor()";
  }
}
