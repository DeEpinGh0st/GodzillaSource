package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.util.Collection;
import java.util.Map;
























@GwtCompatible(emulated = true)
abstract class HashMultimapGwtSerializationDependencies<K, V>
  extends AbstractSetMultimap<K, V>
{
  HashMultimapGwtSerializationDependencies(Map<K, Collection<V>> map) {
    super(map);
  }
}
