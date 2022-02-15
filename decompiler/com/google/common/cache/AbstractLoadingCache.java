package com.google.common.cache;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.UncheckedExecutionException;
import java.util.Map;
import java.util.concurrent.ExecutionException;


































@GwtIncompatible
public abstract class AbstractLoadingCache<K, V>
  extends AbstractCache<K, V>
  implements LoadingCache<K, V>
{
  public V getUnchecked(K key) {
    try {
      return get(key);
    } catch (ExecutionException e) {
      throw new UncheckedExecutionException(e.getCause());
    } 
  }

  
  public ImmutableMap<K, V> getAll(Iterable<? extends K> keys) throws ExecutionException {
    Map<K, V> result = Maps.newLinkedHashMap();
    for (K key : keys) {
      if (!result.containsKey(key)) {
        result.put(key, get(key));
      }
    } 
    return ImmutableMap.copyOf(result);
  }

  
  public final V apply(K key) {
    return getUnchecked(key);
  }

  
  public void refresh(K key) {
    throw new UnsupportedOperationException();
  }
}
