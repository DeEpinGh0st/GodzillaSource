package org.springframework.core.task;

@FunctionalInterface
public interface TaskDecorator {
  Runnable decorate(Runnable paramRunnable);
}
