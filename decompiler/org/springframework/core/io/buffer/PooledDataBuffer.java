package org.springframework.core.io.buffer;

public interface PooledDataBuffer extends DataBuffer {
  boolean isAllocated();
  
  PooledDataBuffer retain();
  
  PooledDataBuffer touch(Object paramObject);
  
  boolean release();
}
