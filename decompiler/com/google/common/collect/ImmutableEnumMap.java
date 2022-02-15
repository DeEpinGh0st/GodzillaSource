package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.io.Serializable;
import java.util.EnumMap;
import java.util.Map;
import java.util.Spliterator;
import java.util.function.BiConsumer;




















@GwtCompatible(serializable = true, emulated = true)
final class ImmutableEnumMap<K extends Enum<K>, V>
  extends ImmutableMap.IteratorBasedImmutableMap<K, V>
{
  private final transient EnumMap<K, V> delegate;
  
  static <K extends Enum<K>, V> ImmutableMap<K, V> asImmutable(EnumMap<K, V> map) {
    Map.Entry<K, V> entry;
    switch (map.size()) {
      case 0:
        return ImmutableMap.of();
      case 1:
        entry = Iterables.<Map.Entry<K, V>>getOnlyElement(map.entrySet());
        return ImmutableMap.of(entry.getKey(), entry.getValue());
    } 
    return new ImmutableEnumMap<>(map);
  }



  
  private ImmutableEnumMap(EnumMap<K, V> delegate) {
    this.delegate = delegate;
    Preconditions.checkArgument(!delegate.isEmpty());
  }

  
  UnmodifiableIterator<K> keyIterator() {
    return Iterators.unmodifiableIterator(this.delegate.keySet().iterator());
  }

  
  Spliterator<K> keySpliterator() {
    return this.delegate.keySet().spliterator();
  }

  
  public int size() {
    return this.delegate.size();
  }

  
  public boolean containsKey(Object key) {
    return this.delegate.containsKey(key);
  }

  
  public V get(Object key) {
    return this.delegate.get(key);
  }

  
  public boolean equals(Object<K, V> object) {
    if (object == this) {
      return true;
    }
    if (object instanceof ImmutableEnumMap) {
      object = (Object<K, V>)((ImmutableEnumMap)object).delegate;
    }
    return this.delegate.equals(object);
  }

  
  UnmodifiableIterator<Map.Entry<K, V>> entryIterator() {
    return Maps.unmodifiableEntryIterator(this.delegate.entrySet().iterator());
  }

  
  Spliterator<Map.Entry<K, V>> entrySpliterator() {
    return CollectSpliterators.map(this.delegate.entrySet().spliterator(), Maps::unmodifiableEntry);
  }

  
  public void forEach(BiConsumer<? super K, ? super V> action) {
    this.delegate.forEach(action);
  }

  
  boolean isPartialView() {
    return false;
  }


  
  Object writeReplace() {
    return new EnumSerializedForm<>(this.delegate);
  }
  
  private static class EnumSerializedForm<K extends Enum<K>, V>
    implements Serializable
  {
    final EnumMap<K, V> delegate;
    private static final long serialVersionUID = 0L;
    
    EnumSerializedForm(EnumMap<K, V> delegate) {
      this.delegate = delegate;
    }
    
    Object readResolve() {
      return new ImmutableEnumMap<>(this.delegate);
    }
  }
}
