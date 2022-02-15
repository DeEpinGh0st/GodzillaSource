package org.springframework.util.backoff;

@FunctionalInterface
public interface BackOff {
  BackOffExecution start();
}
