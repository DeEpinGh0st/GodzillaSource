package com.google.common.cache;

import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.ImmutableMap;
import com.google.errorprone.annotations.CompatibleWith;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;

@GwtCompatible
public interface Cache<K, V> {
  V getIfPresent(@CompatibleWith("K") Object paramObject);
  
  V get(K paramK, Callable<? extends V> paramCallable) throws ExecutionException;
  
  ImmutableMap<K, V> getAllPresent(Iterable<?> paramIterable);
  
  void put(K paramK, V paramV);
  
  void putAll(Map<? extends K, ? extends V> paramMap);
  
  void invalidate(@CompatibleWith("K") Object paramObject);
  
  void invalidateAll(Iterable<?> paramIterable);
  
  void invalidateAll();
  
  long size();
  
  CacheStats stats();
  
  ConcurrentMap<K, V> asMap();
  
  void cleanUp();
}
