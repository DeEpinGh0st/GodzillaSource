package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
































@GwtCompatible
public abstract class ForwardingSetMultimap<K, V>
  extends ForwardingMultimap<K, V>
  implements SetMultimap<K, V>
{
  public Set<Map.Entry<K, V>> entries() {
    return delegate().entries();
  }

  
  public Set<V> get(K key) {
    return delegate().get(key);
  }

  
  @CanIgnoreReturnValue
  public Set<V> removeAll(Object key) {
    return delegate().removeAll(key);
  }

  
  @CanIgnoreReturnValue
  public Set<V> replaceValues(K key, Iterable<? extends V> values) {
    return delegate().replaceValues(key, values);
  }
  
  protected abstract SetMultimap<K, V> delegate();
}
