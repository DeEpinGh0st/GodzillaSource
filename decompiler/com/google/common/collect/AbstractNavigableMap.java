package com.google.common.collect;

import com.google.common.annotations.GwtIncompatible;
import java.util.Iterator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedMap;
























@GwtIncompatible
abstract class AbstractNavigableMap<K, V>
  extends Maps.IteratorBasedAbstractMap<K, V>
  implements NavigableMap<K, V>
{
  public abstract V get(Object paramObject);
  
  public Map.Entry<K, V> firstEntry() {
    return Iterators.<Map.Entry<K, V>>getNext(entryIterator(), null);
  }

  
  public Map.Entry<K, V> lastEntry() {
    return Iterators.<Map.Entry<K, V>>getNext(descendingEntryIterator(), null);
  }

  
  public Map.Entry<K, V> pollFirstEntry() {
    return Iterators.<Map.Entry<K, V>>pollNext(entryIterator());
  }

  
  public Map.Entry<K, V> pollLastEntry() {
    return Iterators.<Map.Entry<K, V>>pollNext(descendingEntryIterator());
  }

  
  public K firstKey() {
    Map.Entry<K, V> entry = firstEntry();
    if (entry == null) {
      throw new NoSuchElementException();
    }
    return entry.getKey();
  }


  
  public K lastKey() {
    Map.Entry<K, V> entry = lastEntry();
    if (entry == null) {
      throw new NoSuchElementException();
    }
    return entry.getKey();
  }


  
  public Map.Entry<K, V> lowerEntry(K key) {
    return headMap(key, false).lastEntry();
  }

  
  public Map.Entry<K, V> floorEntry(K key) {
    return headMap(key, true).lastEntry();
  }

  
  public Map.Entry<K, V> ceilingEntry(K key) {
    return tailMap(key, true).firstEntry();
  }

  
  public Map.Entry<K, V> higherEntry(K key) {
    return tailMap(key, false).firstEntry();
  }

  
  public K lowerKey(K key) {
    return Maps.keyOrNull(lowerEntry(key));
  }

  
  public K floorKey(K key) {
    return Maps.keyOrNull(floorEntry(key));
  }

  
  public K ceilingKey(K key) {
    return Maps.keyOrNull(ceilingEntry(key));
  }

  
  public K higherKey(K key) {
    return Maps.keyOrNull(higherEntry(key));
  }

  
  abstract Iterator<Map.Entry<K, V>> descendingEntryIterator();
  
  public SortedMap<K, V> subMap(K fromKey, K toKey) {
    return subMap(fromKey, true, toKey, false);
  }

  
  public SortedMap<K, V> headMap(K toKey) {
    return headMap(toKey, false);
  }

  
  public SortedMap<K, V> tailMap(K fromKey) {
    return tailMap(fromKey, true);
  }

  
  public NavigableSet<K> navigableKeySet() {
    return new Maps.NavigableKeySet<>(this);
  }

  
  public Set<K> keySet() {
    return navigableKeySet();
  }

  
  public NavigableSet<K> descendingKeySet() {
    return descendingMap().navigableKeySet();
  }

  
  public NavigableMap<K, V> descendingMap() {
    return new DescendingMap();
  }
  
  private final class DescendingMap
    extends Maps.DescendingMap<K, V> {
    NavigableMap<K, V> forward() {
      return AbstractNavigableMap.this;
    }
    private DescendingMap() {}
    
    Iterator<Map.Entry<K, V>> entryIterator() {
      return AbstractNavigableMap.this.descendingEntryIterator();
    }
  }
}
