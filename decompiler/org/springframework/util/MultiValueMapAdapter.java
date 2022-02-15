package org.springframework.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.lang.Nullable;


































public class MultiValueMapAdapter<K, V>
  implements MultiValueMap<K, V>, Serializable
{
  private final Map<K, List<V>> targetMap;
  
  public MultiValueMapAdapter(Map<K, List<V>> targetMap) {
    Assert.notNull(targetMap, "'targetMap' must not be null");
    this.targetMap = targetMap;
  }




  
  @Nullable
  public V getFirst(K key) {
    List<V> values = this.targetMap.get(key);
    return (values != null && !values.isEmpty()) ? values.get(0) : null;
  }

  
  public void add(K key, @Nullable V value) {
    List<V> values = this.targetMap.computeIfAbsent(key, k -> new ArrayList(1));
    values.add(value);
  }

  
  public void addAll(K key, List<? extends V> values) {
    List<V> currentValues = this.targetMap.computeIfAbsent(key, k -> new ArrayList(1));
    currentValues.addAll(values);
  }

  
  public void addAll(MultiValueMap<K, V> values) {
    for (Map.Entry<K, List<V>> entry : values.entrySet()) {
      addAll(entry.getKey(), entry.getValue());
    }
  }

  
  public void set(K key, @Nullable V value) {
    List<V> values = new ArrayList<>(1);
    values.add(value);
    this.targetMap.put(key, values);
  }

  
  public void setAll(Map<K, V> values) {
    values.forEach(this::set);
  }

  
  public Map<K, V> toSingleValueMap() {
    Map<K, V> singleValueMap = CollectionUtils.newLinkedHashMap(this.targetMap.size());
    this.targetMap.forEach((key, values) -> {
          if (values != null && !values.isEmpty()) {
            singleValueMap.put(key, values.get(0));
          }
        });
    return singleValueMap;
  }




  
  public int size() {
    return this.targetMap.size();
  }

  
  public boolean isEmpty() {
    return this.targetMap.isEmpty();
  }

  
  public boolean containsKey(Object key) {
    return this.targetMap.containsKey(key);
  }

  
  public boolean containsValue(Object value) {
    return this.targetMap.containsValue(value);
  }

  
  @Nullable
  public List<V> get(Object key) {
    return this.targetMap.get(key);
  }

  
  @Nullable
  public List<V> put(K key, List<V> value) {
    return this.targetMap.put(key, value);
  }

  
  @Nullable
  public List<V> remove(Object key) {
    return this.targetMap.remove(key);
  }

  
  public void putAll(Map<? extends K, ? extends List<V>> map) {
    this.targetMap.putAll(map);
  }

  
  public void clear() {
    this.targetMap.clear();
  }

  
  public Set<K> keySet() {
    return this.targetMap.keySet();
  }

  
  public Collection<List<V>> values() {
    return this.targetMap.values();
  }

  
  public Set<Map.Entry<K, List<V>>> entrySet() {
    return this.targetMap.entrySet();
  }

  
  public boolean equals(@Nullable Object other) {
    return (this == other || this.targetMap.equals(other));
  }

  
  public int hashCode() {
    return this.targetMap.hashCode();
  }

  
  public String toString() {
    return this.targetMap.toString();
  }
}
