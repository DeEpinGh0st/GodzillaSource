package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.util.Map;
import java.util.function.BiConsumer;
























@GwtCompatible(emulated = true)
final class JdkBackedImmutableMap<K, V>
  extends ImmutableMap<K, V>
{
  private final transient Map<K, V> delegateMap;
  private final transient ImmutableList<Map.Entry<K, V>> entries;
  
  static <K, V> ImmutableMap<K, V> create(int n, Map.Entry<K, V>[] entryArray) {
    Map<K, V> delegateMap = Maps.newHashMapWithExpectedSize(n);
    for (int i = 0; i < n; i++) {
      entryArray[i] = RegularImmutableMap.makeImmutable(entryArray[i]);
      V oldValue = delegateMap.putIfAbsent(entryArray[i].getKey(), entryArray[i].getValue());
      if (oldValue != null) {
        throw conflictException("key", entryArray[i], (new StringBuilder()).append(entryArray[i].getKey()).append("=").append(oldValue).toString());
      }
    } 
    return new JdkBackedImmutableMap<>(delegateMap, ImmutableList.asImmutableList((Object[])entryArray, n));
  }



  
  JdkBackedImmutableMap(Map<K, V> delegateMap, ImmutableList<Map.Entry<K, V>> entries) {
    this.delegateMap = delegateMap;
    this.entries = entries;
  }

  
  public int size() {
    return this.entries.size();
  }

  
  public V get(Object key) {
    return this.delegateMap.get(key);
  }

  
  ImmutableSet<Map.Entry<K, V>> createEntrySet() {
    return new ImmutableMapEntrySet.RegularEntrySet<>(this, this.entries);
  }

  
  public void forEach(BiConsumer<? super K, ? super V> action) {
    Preconditions.checkNotNull(action);
    this.entries.forEach(e -> action.accept(e.getKey(), e.getValue()));
  }

  
  ImmutableSet<K> createKeySet() {
    return new ImmutableMapKeySet<>(this);
  }

  
  ImmutableCollection<V> createValues() {
    return new ImmutableMapValues<>(this);
  }

  
  boolean isPartialView() {
    return false;
  }
}
