package com.google.common.graph;

import java.util.Map;






















class MapRetrievalCache<K, V>
  extends MapIteratorCache<K, V>
{
  private transient CacheEntry<K, V> cacheEntry1;
  private transient CacheEntry<K, V> cacheEntry2;
  
  MapRetrievalCache(Map<K, V> backingMap) {
    super(backingMap);
  }


  
  public V get(Object key) {
    V value = getIfCached(key);
    if (value != null) {
      return value;
    }
    
    value = getWithoutCaching(key);
    if (value != null) {
      addToCache((K)key, value);
    }
    return value;
  }



  
  protected V getIfCached(Object key) {
    V value = super.getIfCached(key);
    if (value != null) {
      return value;
    }





    
    CacheEntry<K, V> entry = this.cacheEntry1;
    if (entry != null && entry.key == key) {
      return entry.value;
    }
    entry = this.cacheEntry2;
    if (entry != null && entry.key == key) {

      
      addToCache(entry);
      return entry.value;
    } 
    return null;
  }

  
  protected void clearCache() {
    super.clearCache();
    this.cacheEntry1 = null;
    this.cacheEntry2 = null;
  }
  
  private void addToCache(K key, V value) {
    addToCache(new CacheEntry<>(key, value));
  }

  
  private void addToCache(CacheEntry<K, V> entry) {
    this.cacheEntry2 = this.cacheEntry1;
    this.cacheEntry1 = entry;
  }
  
  private static final class CacheEntry<K, V> {
    final K key;
    final V value;
    
    CacheEntry(K key, V value) {
      this.key = key;
      this.value = value;
    }
  }
}
