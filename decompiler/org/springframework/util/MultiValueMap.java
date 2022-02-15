package org.springframework.util;

import java.util.List;
import java.util.Map;
import org.springframework.lang.Nullable;





















































public interface MultiValueMap<K, V>
  extends Map<K, List<V>>
{
  @Nullable
  V getFirst(K paramK);
  
  void add(K paramK, @Nullable V paramV);
  
  void addAll(K paramK, List<? extends V> paramList);
  
  void addAll(MultiValueMap<K, V> paramMultiValueMap);
  
  default void addIfAbsent(K key, @Nullable V value) {
    if (!containsKey(key))
      add(key, value); 
  }
  
  void set(K paramK, @Nullable V paramV);
  
  void setAll(Map<K, V> paramMap);
  
  Map<K, V> toSingleValueMap();
}
