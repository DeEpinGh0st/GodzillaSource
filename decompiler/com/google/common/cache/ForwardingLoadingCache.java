package com.google.common.cache;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import java.util.concurrent.ExecutionException;
































@GwtIncompatible
public abstract class ForwardingLoadingCache<K, V>
  extends ForwardingCache<K, V>
  implements LoadingCache<K, V>
{
  public V get(K key) throws ExecutionException {
    return delegate().get(key);
  }

  
  public V getUnchecked(K key) {
    return delegate().getUnchecked(key);
  }

  
  public ImmutableMap<K, V> getAll(Iterable<? extends K> keys) throws ExecutionException {
    return delegate().getAll(keys);
  }

  
  public V apply(K key) {
    return delegate().apply(key);
  }

  
  public void refresh(K key) {
    delegate().refresh(key);
  }

  
  protected abstract LoadingCache<K, V> delegate();

  
  public static abstract class SimpleForwardingLoadingCache<K, V>
    extends ForwardingLoadingCache<K, V>
  {
    private final LoadingCache<K, V> delegate;

    
    protected SimpleForwardingLoadingCache(LoadingCache<K, V> delegate) {
      this.delegate = (LoadingCache<K, V>)Preconditions.checkNotNull(delegate);
    }

    
    protected final LoadingCache<K, V> delegate() {
      return this.delegate;
    }
  }
}
