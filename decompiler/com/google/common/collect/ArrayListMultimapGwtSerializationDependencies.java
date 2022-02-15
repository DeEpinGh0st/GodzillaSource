package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.util.Collection;
import java.util.Map;

























@GwtCompatible(emulated = true)
abstract class ArrayListMultimapGwtSerializationDependencies<K, V>
  extends AbstractListMultimap<K, V>
{
  ArrayListMultimapGwtSerializationDependencies(Map<K, Collection<V>> map) {
    super(map);
  }
}
