package com.google.common.graph;

import com.google.common.base.Preconditions;
import com.google.common.collect.UnmodifiableIterator;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;





































class MapIteratorCache<K, V>
{
  private final Map<K, V> backingMap;
  private transient Map.Entry<K, V> entrySetCache;
  
  MapIteratorCache(Map<K, V> backingMap) {
    this.backingMap = (Map<K, V>)Preconditions.checkNotNull(backingMap);
  }
  
  @CanIgnoreReturnValue
  public V put(K key, V value) {
    clearCache();
    return this.backingMap.put(key, value);
  }
  
  @CanIgnoreReturnValue
  public V remove(Object key) {
    clearCache();
    return this.backingMap.remove(key);
  }
  
  public void clear() {
    clearCache();
    this.backingMap.clear();
  }
  
  public V get(Object key) {
    V value = getIfCached(key);
    return (value != null) ? value : getWithoutCaching(key);
  }
  
  public final V getWithoutCaching(Object key) {
    return this.backingMap.get(key);
  }
  
  public final boolean containsKey(Object key) {
    return (getIfCached(key) != null || this.backingMap.containsKey(key));
  }
  
  public final Set<K> unmodifiableKeySet() {
    return new AbstractSet<K>()
      {
        public UnmodifiableIterator<K> iterator() {
          final Iterator<Map.Entry<K, V>> entryIterator = MapIteratorCache.this.backingMap.entrySet().iterator();
          
          return new UnmodifiableIterator<K>()
            {
              public boolean hasNext() {
                return entryIterator.hasNext();
              }

              
              public K next() {
                Map.Entry<K, V> entry = entryIterator.next();
                MapIteratorCache.this.entrySetCache = entry;
                return entry.getKey();
              }
            };
        }

        
        public int size() {
          return MapIteratorCache.this.backingMap.size();
        }

        
        public boolean contains(Object key) {
          return MapIteratorCache.this.containsKey(key);
        }
      };
  }


  
  protected V getIfCached(Object key) {
    Map.Entry<K, V> entry = this.entrySetCache;

    
    if (entry != null && entry.getKey() == key) {
      return entry.getValue();
    }
    return null;
  }
  
  protected void clearCache() {
    this.entrySetCache = null;
  }
}
