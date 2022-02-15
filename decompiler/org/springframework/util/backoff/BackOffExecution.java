package org.springframework.util.backoff;

@FunctionalInterface
public interface BackOffExecution {
  public static final long STOP = -1L;
  
  long nextBackOff();
}
