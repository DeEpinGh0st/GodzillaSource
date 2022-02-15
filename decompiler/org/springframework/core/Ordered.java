package org.springframework.core;

public interface Ordered {
  public static final int HIGHEST_PRECEDENCE = -2147483648;
  
  public static final int LOWEST_PRECEDENCE = 2147483647;
  
  int getOrder();
}
