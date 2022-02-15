package org.springframework.util.concurrent;

@FunctionalInterface
public interface FailureCallback {
  void onFailure(Throwable paramThrowable);
}
