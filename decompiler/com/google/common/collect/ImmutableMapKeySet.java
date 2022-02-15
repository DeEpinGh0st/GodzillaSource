package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.Spliterator;
import java.util.function.Consumer;





















@GwtCompatible(emulated = true)
final class ImmutableMapKeySet<K, V>
  extends IndexedImmutableSet<K>
{
  private final ImmutableMap<K, V> map;
  
  ImmutableMapKeySet(ImmutableMap<K, V> map) {
    this.map = map;
  }

  
  public int size() {
    return this.map.size();
  }

  
  public UnmodifiableIterator<K> iterator() {
    return this.map.keyIterator();
  }

  
  public Spliterator<K> spliterator() {
    return this.map.keySpliterator();
  }

  
  public boolean contains(Object object) {
    return this.map.containsKey(object);
  }

  
  K get(int index) {
    return (K)((Map.Entry)this.map.entrySet().asList().get(index)).getKey();
  }

  
  public void forEach(Consumer<? super K> action) {
    Preconditions.checkNotNull(action);
    this.map.forEach((k, v) -> action.accept(k));
  }

  
  boolean isPartialView() {
    return true;
  }

  
  @GwtIncompatible
  Object writeReplace() {
    return new KeySetSerializedForm<>(this.map);
  }
  
  @GwtIncompatible
  private static class KeySetSerializedForm<K> implements Serializable {
    final ImmutableMap<K, ?> map;
    
    KeySetSerializedForm(ImmutableMap<K, ?> map) {
      this.map = map;
    }
    private static final long serialVersionUID = 0L;
    Object readResolve() {
      return this.map.keySet();
    }
  }
}
