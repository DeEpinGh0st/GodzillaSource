package com.google.common.hash;

interface LongAddable {
  void increment();
  
  void add(long paramLong);
  
  long sum();
}
