package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.CompatibleWith;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;




























































































































































































































































































@GwtCompatible
public interface Multimap<K, V>
{
  int size();
  
  boolean isEmpty();
  
  boolean containsKey(@CompatibleWith("K") Object paramObject);
  
  boolean containsValue(@CompatibleWith("V") Object paramObject);
  
  boolean containsEntry(@CompatibleWith("K") Object paramObject1, @CompatibleWith("V") Object paramObject2);
  
  @CanIgnoreReturnValue
  boolean put(K paramK, V paramV);
  
  @CanIgnoreReturnValue
  boolean remove(@CompatibleWith("K") Object paramObject1, @CompatibleWith("V") Object paramObject2);
  
  @CanIgnoreReturnValue
  boolean putAll(K paramK, Iterable<? extends V> paramIterable);
  
  @CanIgnoreReturnValue
  boolean putAll(Multimap<? extends K, ? extends V> paramMultimap);
  
  @CanIgnoreReturnValue
  Collection<V> replaceValues(K paramK, Iterable<? extends V> paramIterable);
  
  @CanIgnoreReturnValue
  Collection<V> removeAll(@CompatibleWith("K") Object paramObject);
  
  void clear();
  
  Collection<V> get(K paramK);
  
  Set<K> keySet();
  
  Multiset<K> keys();
  
  Collection<V> values();
  
  Collection<Map.Entry<K, V>> entries();
  
  default void forEach(BiConsumer<? super K, ? super V> action) {
    Preconditions.checkNotNull(action);
    entries().forEach(entry -> action.accept(entry.getKey(), entry.getValue()));
  }
  
  Map<K, Collection<V>> asMap();
  
  boolean equals(Object paramObject);
  
  int hashCode();
}
