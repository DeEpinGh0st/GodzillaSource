package org.springframework.util;

@FunctionalInterface
public interface ErrorHandler {
  void handleError(Throwable paramThrowable);
}
