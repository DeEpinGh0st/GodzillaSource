package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.concurrent.LazyInit;
import com.google.j2objc.annotations.RetainedWith;
import java.io.Serializable;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;


















































@GwtCompatible(serializable = true, emulated = true)
public abstract class ImmutableMap<K, V>
  implements Map<K, V>, Serializable
{
  public static <T, K, V> Collector<T, ?, ImmutableMap<K, V>> toImmutableMap(Function<? super T, ? extends K> keyFunction, Function<? super T, ? extends V> valueFunction) {
    return CollectCollectors.toImmutableMap(keyFunction, valueFunction);
  }













  
  public static <T, K, V> Collector<T, ?, ImmutableMap<K, V>> toImmutableMap(Function<? super T, ? extends K> keyFunction, Function<? super T, ? extends V> valueFunction, BinaryOperator<V> mergeFunction) {
    Preconditions.checkNotNull(keyFunction);
    Preconditions.checkNotNull(valueFunction);
    Preconditions.checkNotNull(mergeFunction);
    return Collectors.collectingAndThen(
        Collectors.toMap(keyFunction, valueFunction, mergeFunction, java.util.LinkedHashMap::new), ImmutableMap::copyOf);
  }







  
  public static <K, V> ImmutableMap<K, V> of() {
    return (ImmutableMap)RegularImmutableMap.EMPTY;
  }





  
  public static <K, V> ImmutableMap<K, V> of(K k1, V v1) {
    return ImmutableBiMap.of(k1, v1);
  }





  
  public static <K, V> ImmutableMap<K, V> of(K k1, V v1, K k2, V v2) {
    return RegularImmutableMap.fromEntries((Map.Entry<K, V>[])new Map.Entry[] { entryOf(k1, v1), entryOf(k2, v2) });
  }





  
  public static <K, V> ImmutableMap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3) {
    return RegularImmutableMap.fromEntries((Map.Entry<K, V>[])new Map.Entry[] { entryOf(k1, v1), entryOf(k2, v2), entryOf(k3, v3) });
  }





  
  public static <K, V> ImmutableMap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4) {
    return RegularImmutableMap.fromEntries((Map.Entry<K, V>[])new Map.Entry[] {
          entryOf(k1, v1), entryOf(k2, v2), entryOf(k3, v3), entryOf(k4, v4)
        });
  }





  
  public static <K, V> ImmutableMap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5) {
    return RegularImmutableMap.fromEntries((Map.Entry<K, V>[])new Map.Entry[] {
          entryOf(k1, v1), entryOf(k2, v2), entryOf(k3, v3), entryOf(k4, v4), entryOf(k5, v5)
        });
  }








  
  static <K, V> Map.Entry<K, V> entryOf(K key, V value) {
    CollectPreconditions.checkEntryNotNull(key, value);
    return new AbstractMap.SimpleImmutableEntry<>(key, value);
  }




  
  public static <K, V> Builder<K, V> builder() {
    return new Builder<>();
  }












  
  @Beta
  public static <K, V> Builder<K, V> builderWithExpectedSize(int expectedSize) {
    CollectPreconditions.checkNonnegative(expectedSize, "expectedSize");
    return new Builder<>(expectedSize);
  }

  
  static void checkNoConflict(boolean safe, String conflictDescription, Map.Entry<?, ?> entry1, Map.Entry<?, ?> entry2) {
    if (!safe) {
      throw conflictException(conflictDescription, entry1, entry2);
    }
  }

  
  static IllegalArgumentException conflictException(String conflictDescription, Object entry1, Object entry2) {
    return new IllegalArgumentException("Multiple entries with same " + conflictDescription + ": " + entry1 + " and " + entry2);
  }







  
  public static class Builder<K, V>
  {
    Comparator<? super V> valueComparator;






    
    Map.Entry<K, V>[] entries;






    
    int size;





    
    boolean entriesUsed;






    
    public Builder() {
      this(4);
    }

    
    Builder(int initialCapacity) {
      this.entries = (Map.Entry<K, V>[])new Map.Entry[initialCapacity];
      this.size = 0;
      this.entriesUsed = false;
    }
    
    private void ensureCapacity(int minCapacity) {
      if (minCapacity > this.entries.length) {
        this
          .entries = Arrays.<Map.Entry<K, V>>copyOf(this.entries, 
            ImmutableCollection.Builder.expandedCapacity(this.entries.length, minCapacity));
        this.entriesUsed = false;
      } 
    }




    
    @CanIgnoreReturnValue
    public Builder<K, V> put(K key, V value) {
      ensureCapacity(this.size + 1);
      Map.Entry<K, V> entry = ImmutableMap.entryOf(key, value);
      
      this.entries[this.size++] = entry;
      return this;
    }






    
    @CanIgnoreReturnValue
    public Builder<K, V> put(Map.Entry<? extends K, ? extends V> entry) {
      return put(entry.getKey(), entry.getValue());
    }






    
    @CanIgnoreReturnValue
    public Builder<K, V> putAll(Map<? extends K, ? extends V> map) {
      return putAll(map.entrySet());
    }







    
    @CanIgnoreReturnValue
    @Beta
    public Builder<K, V> putAll(Iterable<? extends Map.Entry<? extends K, ? extends V>> entries) {
      if (entries instanceof Collection) {
        ensureCapacity(this.size + ((Collection)entries).size());
      }
      for (Map.Entry<? extends K, ? extends V> entry : entries) {
        put(entry);
      }
      return this;
    }










    
    @CanIgnoreReturnValue
    @Beta
    public Builder<K, V> orderEntriesByValue(Comparator<? super V> valueComparator) {
      Preconditions.checkState((this.valueComparator == null), "valueComparator was already set");
      this.valueComparator = (Comparator<? super V>)Preconditions.checkNotNull(valueComparator, "valueComparator");
      return this;
    }
    
    @CanIgnoreReturnValue
    Builder<K, V> combine(Builder<K, V> other) {
      Preconditions.checkNotNull(other);
      ensureCapacity(this.size + other.size);
      System.arraycopy(other.entries, 0, this.entries, this.size, other.size);
      this.size += other.size;
      return this;
    }



















    
    public ImmutableMap<K, V> build() {
      if (this.valueComparator != null) {
        if (this.entriesUsed) {
          this.entries = Arrays.<Map.Entry<K, V>>copyOf(this.entries, this.size);
        }
        Arrays.sort(this.entries, 0, this.size, 
            Ordering.<V>from(this.valueComparator).onResultOf(Maps.valueFunction()));
      } 
      switch (this.size) {
        case 0:
          return ImmutableMap.of();
        case 1:
          return ImmutableMap.of(this.entries[0].getKey(), this.entries[0].getValue());
      } 
      this.entriesUsed = true;
      return RegularImmutableMap.fromEntryArray(this.size, this.entries);
    }

    
    @VisibleForTesting
    ImmutableMap<K, V> buildJdkBacked() {
      Preconditions.checkState((this.valueComparator == null), "buildJdkBacked is only for testing; can't use valueComparator");
      
      switch (this.size) {
        case 0:
          return ImmutableMap.of();
        case 1:
          return ImmutableMap.of(this.entries[0].getKey(), this.entries[0].getValue());
      } 
      this.entriesUsed = true;
      return JdkBackedImmutableMap.create(this.size, this.entries);
    }
  }













  
  public static <K, V> ImmutableMap<K, V> copyOf(Map<? extends K, ? extends V> map) {
    if (map instanceof ImmutableMap && !(map instanceof java.util.SortedMap)) {
      
      ImmutableMap<K, V> kvMap = (ImmutableMap)map;
      if (!kvMap.isPartialView()) {
        return kvMap;
      }
    } else if (map instanceof EnumMap) {
      
      ImmutableMap<K, V> kvMap = (ImmutableMap)copyOfEnumMap((EnumMap)map);
      return kvMap;
    } 
    return copyOf(map.entrySet());
  }









  
  @Beta
  public static <K, V> ImmutableMap<K, V> copyOf(Iterable<? extends Map.Entry<? extends K, ? extends V>> entries) {
    Map.Entry<K, V> onlyEntry;
    Map.Entry[] arrayOfEntry = Iterables.<Map.Entry>toArray((Iterable)entries, (Map.Entry[])EMPTY_ENTRY_ARRAY);
    switch (arrayOfEntry.length) {
      case 0:
        return of();
      case 1:
        onlyEntry = arrayOfEntry[0];
        return of(onlyEntry.getKey(), onlyEntry.getValue());
    } 



    
    return RegularImmutableMap.fromEntries((Map.Entry<K, V>[])arrayOfEntry);
  }


  
  private static <K extends Enum<K>, V> ImmutableMap<K, V> copyOfEnumMap(EnumMap<K, ? extends V> original) {
    EnumMap<K, V> copy = new EnumMap<>(original);
    for (Map.Entry<?, ?> entry : copy.entrySet()) {
      CollectPreconditions.checkEntryNotNull(entry.getKey(), entry.getValue());
    }
    return ImmutableEnumMap.asImmutable(copy); } @LazyInit
  private transient ImmutableSet<Map.Entry<K, V>> entrySet; @LazyInit
  @RetainedWith
  private transient ImmutableSet<K> keySet; static final Map.Entry<?, ?>[] EMPTY_ENTRY_ARRAY = (Map.Entry<?, ?>[])new Map.Entry[0]; @LazyInit
  @RetainedWith
  private transient ImmutableCollection<V> values; @LazyInit
  private transient ImmutableSetMultimap<K, V> multimapView;
  
  static abstract class IteratorBasedImmutableMap<K, V> extends ImmutableMap<K, V> { Spliterator<Map.Entry<K, V>> entrySpliterator() {
      return Spliterators.spliterator(
          entryIterator(), 
          size(), 1297);
    }


    
    ImmutableSet<K> createKeySet() {
      return new ImmutableMapKeySet<>(this);
    }

    
    ImmutableSet<Map.Entry<K, V>> createEntrySet() {
      class EntrySetImpl
        extends ImmutableMapEntrySet<K, V>
      {
        ImmutableMap<K, V> map() {
          return ImmutableMap.IteratorBasedImmutableMap.this;
        }

        
        public UnmodifiableIterator<Map.Entry<K, V>> iterator() {
          return ImmutableMap.IteratorBasedImmutableMap.this.entryIterator();
        }
      };
      return new EntrySetImpl();
    }

    
    ImmutableCollection<V> createValues() {
      return new ImmutableMapValues<>(this);
    }




    
    abstract UnmodifiableIterator<Map.Entry<K, V>> entryIterator(); }




  
  @Deprecated
  @CanIgnoreReturnValue
  public final V put(K k, V v) {
    throw new UnsupportedOperationException();
  }







  
  @Deprecated
  @CanIgnoreReturnValue
  public final V putIfAbsent(K key, V value) {
    throw new UnsupportedOperationException();
  }







  
  @Deprecated
  public final boolean replace(K key, V oldValue, V newValue) {
    throw new UnsupportedOperationException();
  }







  
  @Deprecated
  public final V replace(K key, V value) {
    throw new UnsupportedOperationException();
  }







  
  @Deprecated
  public final V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
    throw new UnsupportedOperationException();
  }








  
  @Deprecated
  public final V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
    throw new UnsupportedOperationException();
  }







  
  @Deprecated
  public final V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
    throw new UnsupportedOperationException();
  }








  
  @Deprecated
  public final V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
    throw new UnsupportedOperationException();
  }







  
  @Deprecated
  public final void putAll(Map<? extends K, ? extends V> map) {
    throw new UnsupportedOperationException();
  }







  
  @Deprecated
  public final void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
    throw new UnsupportedOperationException();
  }







  
  @Deprecated
  public final V remove(Object o) {
    throw new UnsupportedOperationException();
  }







  
  @Deprecated
  public final boolean remove(Object key, Object value) {
    throw new UnsupportedOperationException();
  }







  
  @Deprecated
  public final void clear() {
    throw new UnsupportedOperationException();
  }

  
  public boolean isEmpty() {
    return (size() == 0);
  }

  
  public boolean containsKey(Object key) {
    return (get(key) != null);
  }

  
  public boolean containsValue(Object value) {
    return values().contains(value);
  }










  
  public final V getOrDefault(Object key, V defaultValue) {
    V result = get(key);
    return (result != null) ? result : defaultValue;
  }







  
  public ImmutableSet<Map.Entry<K, V>> entrySet() {
    ImmutableSet<Map.Entry<K, V>> result = this.entrySet;
    return (result == null) ? (this.entrySet = createEntrySet()) : result;
  }









  
  public ImmutableSet<K> keySet() {
    ImmutableSet<K> result = this.keySet;
    return (result == null) ? (this.keySet = createKeySet()) : result;
  }







  
  UnmodifiableIterator<K> keyIterator() {
    final UnmodifiableIterator<Map.Entry<K, V>> entryIterator = entrySet().iterator();
    return new UnmodifiableIterator<K>()
      {
        public boolean hasNext() {
          return entryIterator.hasNext();
        }

        
        public K next() {
          return (K)((Map.Entry)entryIterator.next()).getKey();
        }
      };
  }
  
  Spliterator<K> keySpliterator() {
    return CollectSpliterators.map(entrySet().spliterator(), Map.Entry::getKey);
  }







  
  public ImmutableCollection<V> values() {
    ImmutableCollection<V> result = this.values;
    return (result == null) ? (this.values = createValues()) : result;
  }















  
  public ImmutableSetMultimap<K, V> asMultimap() {
    if (isEmpty()) {
      return ImmutableSetMultimap.of();
    }
    ImmutableSetMultimap<K, V> result = this.multimapView;
    return (result == null) ? (this
      
      .multimapView = new ImmutableSetMultimap<>(new MapViewOfValuesAsSingletonSets(), size(), null)) : result;
  }

  
  private final class MapViewOfValuesAsSingletonSets
    extends IteratorBasedImmutableMap<K, ImmutableSet<V>>
  {
    private MapViewOfValuesAsSingletonSets() {}
    
    public int size() {
      return ImmutableMap.this.size();
    }

    
    ImmutableSet<K> createKeySet() {
      return ImmutableMap.this.keySet();
    }

    
    public boolean containsKey(Object key) {
      return ImmutableMap.this.containsKey(key);
    }

    
    public ImmutableSet<V> get(Object key) {
      V outerValue = (V)ImmutableMap.this.get(key);
      return (outerValue == null) ? null : ImmutableSet.<V>of(outerValue);
    }

    
    boolean isPartialView() {
      return ImmutableMap.this.isPartialView();
    }


    
    public int hashCode() {
      return ImmutableMap.this.hashCode();
    }

    
    boolean isHashCodeFast() {
      return ImmutableMap.this.isHashCodeFast();
    }

    
    UnmodifiableIterator<Map.Entry<K, ImmutableSet<V>>> entryIterator() {
      final Iterator<Map.Entry<K, V>> backingIterator = ImmutableMap.this.entrySet().iterator();
      return new UnmodifiableIterator<Map.Entry<K, ImmutableSet<V>>>()
        {
          public boolean hasNext() {
            return backingIterator.hasNext();
          }

          
          public Map.Entry<K, ImmutableSet<V>> next() {
            final Map.Entry<K, V> backingEntry = backingIterator.next();
            return (Map.Entry)new AbstractMapEntry<K, ImmutableSet<ImmutableSet<V>>>()
              {
                public K getKey() {
                  return (K)backingEntry.getKey();
                }

                
                public ImmutableSet<V> getValue() {
                  return ImmutableSet.of((V)backingEntry.getValue());
                }
              };
          }
        };
    }
  }

  
  public boolean equals(Object object) {
    return Maps.equalsImpl(this, object);
  }



  
  public int hashCode() {
    return Sets.hashCodeImpl(entrySet());
  }
  
  boolean isHashCodeFast() {
    return false;
  }

  
  public String toString() {
    return Maps.toStringImpl(this);
  }

  
  static class SerializedForm
    implements Serializable
  {
    private final Object[] keys;
    
    private final Object[] values;
    private static final long serialVersionUID = 0L;
    
    SerializedForm(ImmutableMap<?, ?> map) {
      this.keys = new Object[map.size()];
      this.values = new Object[map.size()];
      int i = 0;
      for (UnmodifiableIterator<Map.Entry<?, ?>> unmodifiableIterator = map.entrySet().iterator(); unmodifiableIterator.hasNext(); ) { Map.Entry<?, ?> entry = unmodifiableIterator.next();
        this.keys[i] = entry.getKey();
        this.values[i] = entry.getValue();
        i++; }
    
    }
    
    Object readResolve() {
      ImmutableMap.Builder<Object, Object> builder = new ImmutableMap.Builder<>(this.keys.length);
      return createMap(builder);
    }
    
    Object createMap(ImmutableMap.Builder<Object, Object> builder) {
      for (int i = 0; i < this.keys.length; i++) {
        builder.put(this.keys[i], this.values[i]);
      }
      return builder.build();
    }
  }


  
  Object writeReplace() {
    return new SerializedForm(this);
  }
  
  public abstract V get(Object paramObject);
  
  abstract ImmutableSet<Map.Entry<K, V>> createEntrySet();
  
  abstract ImmutableSet<K> createKeySet();
  
  abstract ImmutableCollection<V> createValues();
  
  abstract boolean isPartialView();
}
