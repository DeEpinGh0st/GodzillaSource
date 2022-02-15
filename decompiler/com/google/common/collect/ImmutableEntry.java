package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.io.Serializable;















@GwtCompatible(serializable = true)
class ImmutableEntry<K, V>
  extends AbstractMapEntry<K, V>
  implements Serializable
{
  final K key;
  final V value;
  private static final long serialVersionUID = 0L;
  
  ImmutableEntry(K key, V value) {
    this.key = key;
    this.value = value;
  }

  
  public final K getKey() {
    return this.key;
  }

  
  public final V getValue() {
    return this.value;
  }

  
  public final V setValue(V value) {
    throw new UnsupportedOperationException();
  }
}
