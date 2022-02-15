package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;























@GwtCompatible
abstract class AbstractSortedKeySortedSetMultimap<K, V>
  extends AbstractSortedSetMultimap<K, V>
{
  AbstractSortedKeySortedSetMultimap(SortedMap<K, Collection<V>> map) {
    super(map);
  }

  
  public SortedMap<K, Collection<V>> asMap() {
    return (SortedMap<K, Collection<V>>)super.asMap();
  }

  
  SortedMap<K, Collection<V>> backingMap() {
    return (SortedMap<K, Collection<V>>)super.backingMap();
  }

  
  public SortedSet<K> keySet() {
    return (SortedSet<K>)super.keySet();
  }

  
  Set<K> createKeySet() {
    return createMaybeNavigableKeySet();
  }
}
