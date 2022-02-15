package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.Spliterator;
import java.util.TreeMap;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

















































@GwtCompatible(serializable = true, emulated = true)
public final class ImmutableSortedMap<K, V>
  extends ImmutableSortedMapFauxverideShim<K, V>
  implements NavigableMap<K, V>
{
  public static <T, K, V> Collector<T, ?, ImmutableSortedMap<K, V>> toImmutableSortedMap(Comparator<? super K> comparator, Function<? super T, ? extends K> keyFunction, Function<? super T, ? extends V> valueFunction) {
    return CollectCollectors.toImmutableSortedMap(comparator, keyFunction, valueFunction);
  }















  
  public static <T, K, V> Collector<T, ?, ImmutableSortedMap<K, V>> toImmutableSortedMap(Comparator<? super K> comparator, Function<? super T, ? extends K> keyFunction, Function<? super T, ? extends V> valueFunction, BinaryOperator<V> mergeFunction) {
    Preconditions.checkNotNull(comparator);
    Preconditions.checkNotNull(keyFunction);
    Preconditions.checkNotNull(valueFunction);
    Preconditions.checkNotNull(mergeFunction);
    return Collectors.collectingAndThen(
        Collectors.toMap(keyFunction, valueFunction, mergeFunction, () -> new TreeMap<>(comparator)), ImmutableSortedMap::copyOfSorted);
  }






  
  private static final Comparator<Comparable> NATURAL_ORDER = Ordering.natural();
  
  private static final ImmutableSortedMap<Comparable, Object> NATURAL_EMPTY_MAP = new ImmutableSortedMap(
      
      ImmutableSortedSet.emptySet(Ordering.natural()), ImmutableList.of());
  
  static <K, V> ImmutableSortedMap<K, V> emptyMap(Comparator<? super K> comparator) {
    if (Ordering.<Comparable>natural().equals(comparator)) {
      return of();
    }
    return new ImmutableSortedMap<>(
        ImmutableSortedSet.emptySet(comparator), ImmutableList.of());
  }
  
  private final transient RegularImmutableSortedSet<K> keySet;
  private final transient ImmutableList<V> valueList;
  private transient ImmutableSortedMap<K, V> descendingMap;
  private static final long serialVersionUID = 0L;
  
  public static <K, V> ImmutableSortedMap<K, V> of() {
    return (ImmutableSortedMap)NATURAL_EMPTY_MAP;
  }

  
  public static <K extends Comparable<? super K>, V> ImmutableSortedMap<K, V> of(K k1, V v1) {
    return of(Ordering.natural(), k1, v1);
  }

  
  private static <K, V> ImmutableSortedMap<K, V> of(Comparator<? super K> comparator, K k1, V v1) {
    return new ImmutableSortedMap<>(new RegularImmutableSortedSet<>(
          ImmutableList.of(k1), (Comparator<? super K>)Preconditions.checkNotNull(comparator)), 
        ImmutableList.of(v1));
  }








  
  public static <K extends Comparable<? super K>, V> ImmutableSortedMap<K, V> of(K k1, V v1, K k2, V v2) {
    return ofEntries((Map.Entry<K, V>[])new Map.Entry[] { entryOf(k1, v1), entryOf(k2, v2) });
  }








  
  public static <K extends Comparable<? super K>, V> ImmutableSortedMap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3) {
    return ofEntries((Map.Entry<K, V>[])new Map.Entry[] { entryOf(k1, v1), entryOf(k2, v2), entryOf(k3, v3) });
  }








  
  public static <K extends Comparable<? super K>, V> ImmutableSortedMap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4) {
    return ofEntries((Map.Entry<K, V>[])new Map.Entry[] { entryOf(k1, v1), entryOf(k2, v2), entryOf(k3, v3), entryOf(k4, v4) });
  }








  
  public static <K extends Comparable<? super K>, V> ImmutableSortedMap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5) {
    return ofEntries((Map.Entry<K, V>[])new Map.Entry[] {
          entryOf(k1, v1), entryOf(k2, v2), entryOf(k3, v3), entryOf(k4, v4), entryOf(k5, v5)
        });
  }
  
  private static <K extends Comparable<? super K>, V> ImmutableSortedMap<K, V> ofEntries(Map.Entry<K, V>... entries) {
    return fromEntries(Ordering.natural(), false, entries, entries.length);
  }


















  
  public static <K, V> ImmutableSortedMap<K, V> copyOf(Map<? extends K, ? extends V> map) {
    Ordering<K> naturalOrder = (Ordering)NATURAL_ORDER;
    return copyOfInternal(map, naturalOrder);
  }












  
  public static <K, V> ImmutableSortedMap<K, V> copyOf(Map<? extends K, ? extends V> map, Comparator<? super K> comparator) {
    return copyOfInternal(map, (Comparator<? super K>)Preconditions.checkNotNull(comparator));
  }















  
  @Beta
  public static <K, V> ImmutableSortedMap<K, V> copyOf(Iterable<? extends Map.Entry<? extends K, ? extends V>> entries) {
    Ordering<K> naturalOrder = (Ordering)NATURAL_ORDER;
    return copyOf(entries, naturalOrder);
  }










  
  @Beta
  public static <K, V> ImmutableSortedMap<K, V> copyOf(Iterable<? extends Map.Entry<? extends K, ? extends V>> entries, Comparator<? super K> comparator) {
    return fromEntries((Comparator<? super K>)Preconditions.checkNotNull(comparator), false, entries);
  }










  
  public static <K, V> ImmutableSortedMap<K, V> copyOfSorted(SortedMap<K, ? extends V> map) {
    Comparator<Comparable> comparator1;
    Comparator<? super K> comparator = map.comparator();
    if (comparator == null)
    {
      
      comparator1 = NATURAL_ORDER;
    }
    if (map instanceof ImmutableSortedMap) {


      
      ImmutableSortedMap<K, V> kvMap = (ImmutableSortedMap)map;
      if (!kvMap.isPartialView()) {
        return kvMap;
      }
    } 
    return fromEntries((Comparator)comparator1, true, map.entrySet());
  }

  
  private static <K, V> ImmutableSortedMap<K, V> copyOfInternal(Map<? extends K, ? extends V> map, Comparator<? super K> comparator) {
    boolean sameComparator = false;
    if (map instanceof SortedMap) {
      SortedMap<?, ?> sortedMap = (SortedMap<?, ?>)map;
      Comparator<?> comparator2 = sortedMap.comparator();
      
      sameComparator = (comparator2 == null) ? ((comparator == NATURAL_ORDER)) : comparator.equals(comparator2);
    } 
    
    if (sameComparator && map instanceof ImmutableSortedMap) {


      
      ImmutableSortedMap<K, V> kvMap = (ImmutableSortedMap)map;
      if (!kvMap.isPartialView()) {
        return kvMap;
      }
    } 
    return fromEntries(comparator, sameComparator, map.entrySet());
  }











  
  private static <K, V> ImmutableSortedMap<K, V> fromEntries(Comparator<? super K> comparator, boolean sameComparator, Iterable<? extends Map.Entry<? extends K, ? extends V>> entries) {
    Map.Entry[] arrayOfEntry = Iterables.<Map.Entry>toArray((Iterable)entries, (Map.Entry[])EMPTY_ENTRY_ARRAY);
    return fromEntries(comparator, sameComparator, (Map.Entry<K, V>[])arrayOfEntry, arrayOfEntry.length);
  }




  
  private static <K, V> ImmutableSortedMap<K, V> fromEntries(final Comparator<? super K> comparator, boolean sameComparator, Map.Entry<K, V>[] entryArray, int size) {
    switch (size) {
      case 0:
        return emptyMap(comparator);
      case 1:
        return of(comparator, entryArray[0]
            .getKey(), entryArray[0].getValue());
    } 
    Object[] keys = new Object[size];
    Object[] values = new Object[size];
    if (sameComparator) {
      
      for (int i = 0; i < size; i++) {
        Object key = entryArray[i].getKey();
        Object value = entryArray[i].getValue();
        CollectPreconditions.checkEntryNotNull(key, value);
        keys[i] = key;
        values[i] = value;
      }
    
    }
    else {
      
      Arrays.sort(entryArray, 0, size, new Comparator<Map.Entry<K, V>>()
          {


            
            public int compare(Map.Entry<K, V> e1, Map.Entry<K, V> e2)
            {
              return comparator.compare(e1.getKey(), e2.getKey());
            }
          });
      K prevKey = entryArray[0].getKey();
      keys[0] = prevKey;
      values[0] = entryArray[0].getValue();
      CollectPreconditions.checkEntryNotNull(keys[0], values[0]);
      for (int i = 1; i < size; i++) {
        K key = entryArray[i].getKey();
        V value = entryArray[i].getValue();
        CollectPreconditions.checkEntryNotNull(key, value);
        keys[i] = key;
        values[i] = value;
        checkNoConflict(
            (comparator.compare(prevKey, key) != 0), "key", entryArray[i - 1], entryArray[i]);
        prevKey = key;
      } 
    } 
    return new ImmutableSortedMap<>(new RegularImmutableSortedSet<>(new RegularImmutableList<>(keys), comparator), new RegularImmutableList<>(values));
  }







  
  public static <K extends Comparable<?>, V> Builder<K, V> naturalOrder() {
    return new Builder<>(Ordering.natural());
  }








  
  public static <K, V> Builder<K, V> orderedBy(Comparator<K> comparator) {
    return new Builder<>(comparator);
  }




  
  public static <K extends Comparable<?>, V> Builder<K, V> reverseOrder() {
    return new Builder<>(Ordering.<Comparable>natural().reverse());
  }












  
  public static class Builder<K, V>
    extends ImmutableMap.Builder<K, V>
  {
    private final Comparator<? super K> comparator;












    
    public Builder(Comparator<? super K> comparator) {
      this.comparator = (Comparator<? super K>)Preconditions.checkNotNull(comparator);
    }






    
    @CanIgnoreReturnValue
    public Builder<K, V> put(K key, V value) {
      super.put(key, value);
      return this;
    }








    
    @CanIgnoreReturnValue
    public Builder<K, V> put(Map.Entry<? extends K, ? extends V> entry) {
      super.put(entry);
      return this;
    }








    
    @CanIgnoreReturnValue
    public Builder<K, V> putAll(Map<? extends K, ? extends V> map) {
      super.putAll(map);
      return this;
    }









    
    @CanIgnoreReturnValue
    @Beta
    public Builder<K, V> putAll(Iterable<? extends Map.Entry<? extends K, ? extends V>> entries) {
      super.putAll(entries);
      return this;
    }







    
    @Deprecated
    @CanIgnoreReturnValue
    @Beta
    public Builder<K, V> orderEntriesByValue(Comparator<? super V> valueComparator) {
      throw new UnsupportedOperationException("Not available on ImmutableSortedMap.Builder");
    }

    
    Builder<K, V> combine(ImmutableMap.Builder<K, V> other) {
      super.combine(other);
      return this;
    }







    
    public ImmutableSortedMap<K, V> build() {
      switch (this.size) {
        case 0:
          return ImmutableSortedMap.emptyMap(this.comparator);
        case 1:
          return ImmutableSortedMap.of(this.comparator, this.entries[0].getKey(), this.entries[0].getValue());
      } 
      return ImmutableSortedMap.fromEntries(this.comparator, false, this.entries, this.size);
    }
  }





  
  ImmutableSortedMap(RegularImmutableSortedSet<K> keySet, ImmutableList<V> valueList) {
    this(keySet, valueList, (ImmutableSortedMap<K, V>)null);
  }



  
  ImmutableSortedMap(RegularImmutableSortedSet<K> keySet, ImmutableList<V> valueList, ImmutableSortedMap<K, V> descendingMap) {
    this.keySet = keySet;
    this.valueList = valueList;
    this.descendingMap = descendingMap;
  }

  
  public int size() {
    return this.valueList.size();
  }

  
  public void forEach(BiConsumer<? super K, ? super V> action) {
    Preconditions.checkNotNull(action);
    ImmutableList<K> keyList = this.keySet.asList();
    for (int i = 0; i < size(); i++) {
      action.accept(keyList.get(i), this.valueList.get(i));
    }
  }

  
  public V get(Object key) {
    int index = this.keySet.indexOf(key);
    return (index == -1) ? null : this.valueList.get(index);
  }

  
  boolean isPartialView() {
    return (this.keySet.isPartialView() || this.valueList.isPartialView());
  }


  
  public ImmutableSet<Map.Entry<K, V>> entrySet() {
    return super.entrySet();
  }

  
  ImmutableSet<Map.Entry<K, V>> createEntrySet() {
    class EntrySet
      extends ImmutableMapEntrySet<K, V>
    {
      public UnmodifiableIterator<Map.Entry<K, V>> iterator() {
        return asList().iterator();
      }

      
      public Spliterator<Map.Entry<K, V>> spliterator() {
        return asList().spliterator();
      }

      
      public void forEach(Consumer<? super Map.Entry<K, V>> action) {
        asList().forEach(action);
      }

      
      ImmutableList<Map.Entry<K, V>> createAsList() {
        return new ImmutableAsList<Map.Entry<K, V>>()
          {
            public Map.Entry<K, V> get(int index) {
              return new AbstractMap.SimpleImmutableEntry<>(ImmutableSortedMap.this
                  .keySet.asList().get(index), (V)ImmutableSortedMap.this.valueList.get(index));
            }

            
            public Spliterator<Map.Entry<K, V>> spliterator() {
              return CollectSpliterators.indexed(
                  size(), 1297, this::get);
            }

            
            ImmutableCollection<Map.Entry<K, V>> delegateCollection() {
              return ImmutableSortedMap.EntrySet.this;
            }
          };
      }

      
      ImmutableMap<K, V> map() {
        return ImmutableSortedMap.this;
      }
    };
    return isEmpty() ? ImmutableSet.<Map.Entry<K, V>>of() : new EntrySet();
  }


  
  public ImmutableSortedSet<K> keySet() {
    return this.keySet;
  }

  
  ImmutableSet<K> createKeySet() {
    throw new AssertionError("should never be called");
  }





  
  public ImmutableCollection<V> values() {
    return this.valueList;
  }

  
  ImmutableCollection<V> createValues() {
    throw new AssertionError("should never be called");
  }






  
  public Comparator<? super K> comparator() {
    return keySet().comparator();
  }

  
  public K firstKey() {
    return keySet().first();
  }

  
  public K lastKey() {
    return keySet().last();
  }
  
  private ImmutableSortedMap<K, V> getSubMap(int fromIndex, int toIndex) {
    if (fromIndex == 0 && toIndex == size())
      return this; 
    if (fromIndex == toIndex) {
      return emptyMap(comparator());
    }
    return new ImmutableSortedMap(this.keySet
        .getSubSet(fromIndex, toIndex), this.valueList.subList(fromIndex, toIndex));
  }











  
  public ImmutableSortedMap<K, V> headMap(K toKey) {
    return headMap(toKey, false);
  }












  
  public ImmutableSortedMap<K, V> headMap(K toKey, boolean inclusive) {
    return getSubMap(0, this.keySet.headIndex((K)Preconditions.checkNotNull(toKey), inclusive));
  }











  
  public ImmutableSortedMap<K, V> subMap(K fromKey, K toKey) {
    return subMap(fromKey, true, toKey, false);
  }















  
  public ImmutableSortedMap<K, V> subMap(K fromKey, boolean fromInclusive, K toKey, boolean toInclusive) {
    Preconditions.checkNotNull(fromKey);
    Preconditions.checkNotNull(toKey);
    Preconditions.checkArgument(
        (comparator().compare(fromKey, toKey) <= 0), "expected fromKey <= toKey but %s > %s", fromKey, toKey);


    
    return headMap(toKey, toInclusive).tailMap(fromKey, fromInclusive);
  }










  
  public ImmutableSortedMap<K, V> tailMap(K fromKey) {
    return tailMap(fromKey, true);
  }












  
  public ImmutableSortedMap<K, V> tailMap(K fromKey, boolean inclusive) {
    return getSubMap(this.keySet.tailIndex((K)Preconditions.checkNotNull(fromKey), inclusive), size());
  }

  
  public Map.Entry<K, V> lowerEntry(K key) {
    return headMap(key, false).lastEntry();
  }

  
  public K lowerKey(K key) {
    return Maps.keyOrNull(lowerEntry(key));
  }

  
  public Map.Entry<K, V> floorEntry(K key) {
    return headMap(key, true).lastEntry();
  }

  
  public K floorKey(K key) {
    return Maps.keyOrNull(floorEntry(key));
  }

  
  public Map.Entry<K, V> ceilingEntry(K key) {
    return tailMap(key, true).firstEntry();
  }

  
  public K ceilingKey(K key) {
    return Maps.keyOrNull(ceilingEntry(key));
  }

  
  public Map.Entry<K, V> higherEntry(K key) {
    return tailMap(key, false).firstEntry();
  }

  
  public K higherKey(K key) {
    return Maps.keyOrNull(higherEntry(key));
  }

  
  public Map.Entry<K, V> firstEntry() {
    return isEmpty() ? null : entrySet().asList().get(0);
  }

  
  public Map.Entry<K, V> lastEntry() {
    return isEmpty() ? null : entrySet().asList().get(size() - 1);
  }







  
  @Deprecated
  @CanIgnoreReturnValue
  public final Map.Entry<K, V> pollFirstEntry() {
    throw new UnsupportedOperationException();
  }







  
  @Deprecated
  @CanIgnoreReturnValue
  public final Map.Entry<K, V> pollLastEntry() {
    throw new UnsupportedOperationException();
  }



  
  public ImmutableSortedMap<K, V> descendingMap() {
    ImmutableSortedMap<K, V> result = this.descendingMap;
    if (result == null) {
      if (isEmpty()) {
        return result = emptyMap(Ordering.from(comparator()).reverse());
      }
      return 
        
        result = new ImmutableSortedMap((RegularImmutableSortedSet<K>)this.keySet.descendingSet(), this.valueList.reverse(), this);
    } 
    
    return result;
  }

  
  public ImmutableSortedSet<K> navigableKeySet() {
    return this.keySet;
  }

  
  public ImmutableSortedSet<K> descendingKeySet() {
    return this.keySet.descendingSet();
  }

  
  private static class SerializedForm
    extends ImmutableMap.SerializedForm
  {
    private final Comparator<Object> comparator;
    
    private static final long serialVersionUID = 0L;

    
    SerializedForm(ImmutableSortedMap<?, ?> sortedMap) {
      super(sortedMap);
      this.comparator = (Comparator)sortedMap.comparator();
    }

    
    Object readResolve() {
      ImmutableSortedMap.Builder<Object, Object> builder = new ImmutableSortedMap.Builder<>(this.comparator);
      return createMap(builder);
    }
  }



  
  Object writeReplace() {
    return new SerializedForm(this);
  }
}
