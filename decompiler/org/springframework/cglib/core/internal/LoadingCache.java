package org.springframework.cglib.core.internal;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class LoadingCache<K, KK, V> {
  protected final ConcurrentMap<KK, Object> map;
  
  public static final Function IDENTITY = new Function<Object, Object>() {
      public Object apply(Object key) {
        return key;
      }
    };
  protected final Function<K, V> loader; protected final Function<K, KK> keyMapper;
  public LoadingCache(Function<K, KK> keyMapper, Function<K, V> loader) {
    this.keyMapper = keyMapper;
    this.loader = loader;
    this.map = new ConcurrentHashMap<KK, Object>();
  }

  
  public static <K> Function<K, K> identity() {
    return IDENTITY;
  }
  
  public V get(K key) {
    KK cacheKey = this.keyMapper.apply(key);
    Object v = this.map.get(cacheKey);
    if (v != null && !(v instanceof FutureTask)) {
      return (V)v;
    }
    
    return createEntry(key, cacheKey, v);
  }







  
  protected V createEntry(final K key, KK cacheKey, Object v) {
    FutureTask<V> task;
    V result;
    boolean creator = false;
    if (v != null) {
      
      task = (FutureTask<V>)v;
    } else {
      task = new FutureTask<V>(new Callable<V>() {
            public V call() throws Exception {
              return LoadingCache.this.loader.apply(key);
            }
          });
      Object prevTask = this.map.putIfAbsent(cacheKey, task);
      if (prevTask == null) {
        
        creator = true;
        task.run();
      } else if (prevTask instanceof FutureTask) {
        task = (FutureTask<V>)prevTask;
      } else {
        return (V)prevTask;
      } 
    } 

    
    try {
      result = task.get();
    } catch (InterruptedException e) {
      throw new IllegalStateException("Interrupted while loading cache item", e);
    } catch (ExecutionException e) {
      Throwable cause = e.getCause();
      if (cause instanceof RuntimeException) {
        throw (RuntimeException)cause;
      }
      throw new IllegalStateException("Unable to load cache item", cause);
    } 
    if (creator) {
      this.map.put(cacheKey, result);
    }
    return result;
  }
}
