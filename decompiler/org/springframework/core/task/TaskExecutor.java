package org.springframework.core.task;

import java.util.concurrent.Executor;

@FunctionalInterface
public interface TaskExecutor extends Executor {
  void execute(Runnable paramRunnable);
}
