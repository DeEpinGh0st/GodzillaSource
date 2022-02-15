package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Converter;
import com.google.common.base.Equivalence;
import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.j2objc.annotations.RetainedWith;
import com.google.j2objc.annotations.Weak;
import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Properties;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collector;









































@GwtCompatible(emulated = true)
public final class Maps
{
  private enum EntryFunction
    implements Function<Map.Entry<?, ?>, Object>
  {
    KEY
    {
      public Object apply(Map.Entry<?, ?> entry) {
        return entry.getKey();
      }
    },
    VALUE
    {
      public Object apply(Map.Entry<?, ?> entry) {
        return entry.getValue();
      }
    };
  }

  
  static <K> Function<Map.Entry<K, ?>, K> keyFunction() {
    return EntryFunction.KEY;
  }

  
  static <V> Function<Map.Entry<?, V>, V> valueFunction() {
    return EntryFunction.VALUE;
  }
  
  static <K, V> Iterator<K> keyIterator(Iterator<Map.Entry<K, V>> entryIterator) {
    return new TransformedIterator<Map.Entry<K, V>, K>(entryIterator)
      {
        K transform(Map.Entry<K, V> entry) {
          return entry.getKey();
        }
      };
  }
  
  static <K, V> Iterator<V> valueIterator(Iterator<Map.Entry<K, V>> entryIterator) {
    return new TransformedIterator<Map.Entry<K, V>, V>(entryIterator)
      {
        V transform(Map.Entry<K, V> entry) {
          return entry.getValue();
        }
      };
  }












  
  @GwtCompatible(serializable = true)
  public static <K extends Enum<K>, V> ImmutableMap<K, V> immutableEnumMap(Map<K, ? extends V> map) {
    if (map instanceof ImmutableEnumMap) {
      
      ImmutableEnumMap<K, V> result = (ImmutableEnumMap)map;
      return result;
    } 
    Iterator<? extends Map.Entry<K, ? extends V>> entryItr = map.entrySet().iterator();
    if (!entryItr.hasNext()) {
      return ImmutableMap.of();
    }
    Map.Entry<K, ? extends V> entry1 = entryItr.next();
    Enum<K> enum_ = (Enum)entry1.getKey();
    V value1 = entry1.getValue();
    CollectPreconditions.checkEntryNotNull(enum_, value1);
    Class<K> clazz = enum_.getDeclaringClass();
    EnumMap<K, V> enumMap = new EnumMap<>(clazz);
    enumMap.put((K)enum_, value1);
    while (entryItr.hasNext()) {
      Map.Entry<K, ? extends V> entry = entryItr.next();
      Enum enum_1 = (Enum)entry.getKey();
      V value = entry.getValue();
      CollectPreconditions.checkEntryNotNull(enum_1, value);
      enumMap.put((K)enum_1, value);
    } 
    return ImmutableEnumMap.asImmutable(enumMap);
  }
  
  private static class Accumulator<K extends Enum<K>, V> {
    private final BinaryOperator<V> mergeFunction;
    private EnumMap<K, V> map = null;
    
    Accumulator(BinaryOperator<V> mergeFunction) {
      this.mergeFunction = mergeFunction;
    }
    
    void put(K key, V value) {
      if (this.map == null) {
        this.map = new EnumMap<>(key.getDeclaringClass());
      }
      this.map.merge(key, value, this.mergeFunction);
    }
    
    Accumulator<K, V> combine(Accumulator<K, V> other) {
      if (this.map == null)
        return other; 
      if (other.map == null) {
        return this;
      }
      other.map.forEach(this::put);
      return this;
    }

    
    ImmutableMap<K, V> toImmutableMap() {
      return (this.map == null) ? ImmutableMap.<K, V>of() : ImmutableEnumMap.<K, V>asImmutable(this.map);
    }
  }
















  
  public static <T, K extends Enum<K>, V> Collector<T, ?, ImmutableMap<K, V>> toImmutableEnumMap(Function<? super T, ? extends K> keyFunction, Function<? super T, ? extends V> valueFunction) {
    Preconditions.checkNotNull(keyFunction);
    Preconditions.checkNotNull(valueFunction);
    return Collector.of(() -> new Accumulator<>(()), (accum, t) -> { Enum enum_ = (Enum)Preconditions.checkNotNull(keyFunction.apply(t), "Null key for input %s", t); V newValue = (V)Preconditions.checkNotNull(valueFunction.apply(t), "Null value for input %s", t); accum.put(enum_, newValue); }Accumulator::combine, Accumulator::toImmutableMap, new Collector.Characteristics[] { Collector.Characteristics.UNORDERED });
  }



























  
  public static <T, K extends Enum<K>, V> Collector<T, ?, ImmutableMap<K, V>> toImmutableEnumMap(Function<? super T, ? extends K> keyFunction, Function<? super T, ? extends V> valueFunction, BinaryOperator<V> mergeFunction) {
    Preconditions.checkNotNull(keyFunction);
    Preconditions.checkNotNull(valueFunction);
    Preconditions.checkNotNull(mergeFunction);
    
    return Collector.of(() -> new Accumulator<>(mergeFunction), (accum, t) -> { Enum enum_ = (Enum)Preconditions.checkNotNull(keyFunction.apply(t), "Null key for input %s", t); V newValue = (V)Preconditions.checkNotNull(valueFunction.apply(t), "Null value for input %s", t); accum.put(enum_, newValue); }Accumulator::combine, Accumulator::toImmutableMap, new Collector.Characteristics[0]);
  }





















  
  public static <K, V> HashMap<K, V> newHashMap() {
    return new HashMap<>();
  }














  
  public static <K, V> HashMap<K, V> newHashMap(Map<? extends K, ? extends V> map) {
    return new HashMap<>(map);
  }











  
  public static <K, V> HashMap<K, V> newHashMapWithExpectedSize(int expectedSize) {
    return new HashMap<>(capacity(expectedSize));
  }




  
  static int capacity(int expectedSize) {
    if (expectedSize < 3) {
      CollectPreconditions.checkNonnegative(expectedSize, "expectedSize");
      return expectedSize + 1;
    } 
    if (expectedSize < 1073741824)
    {

      
      return (int)(expectedSize / 0.75F + 1.0F);
    }
    return Integer.MAX_VALUE;
  }











  
  public static <K, V> LinkedHashMap<K, V> newLinkedHashMap() {
    return new LinkedHashMap<>();
  }













  
  public static <K, V> LinkedHashMap<K, V> newLinkedHashMap(Map<? extends K, ? extends V> map) {
    return new LinkedHashMap<>(map);
  }












  
  public static <K, V> LinkedHashMap<K, V> newLinkedHashMapWithExpectedSize(int expectedSize) {
    return new LinkedHashMap<>(capacity(expectedSize));
  }





  
  public static <K, V> ConcurrentMap<K, V> newConcurrentMap() {
    return new ConcurrentHashMap<>();
  }












  
  public static <K extends Comparable, V> TreeMap<K, V> newTreeMap() {
    return new TreeMap<>();
  }
















  
  public static <K, V> TreeMap<K, V> newTreeMap(SortedMap<K, ? extends V> map) {
    return new TreeMap<>(map);
  }


















  
  public static <C, K extends C, V> TreeMap<K, V> newTreeMap(Comparator<C> comparator) {
    return new TreeMap<>(comparator);
  }






  
  public static <K extends Enum<K>, V> EnumMap<K, V> newEnumMap(Class<K> type) {
    return new EnumMap<>((Class<K>)Preconditions.checkNotNull(type));
  }












  
  public static <K extends Enum<K>, V> EnumMap<K, V> newEnumMap(Map<K, ? extends V> map) {
    return new EnumMap<>(map);
  }









  
  public static <K, V> IdentityHashMap<K, V> newIdentityHashMap() {
    return new IdentityHashMap<>();
  }

















  
  public static <K, V> MapDifference<K, V> difference(Map<? extends K, ? extends V> left, Map<? extends K, ? extends V> right) {
    if (left instanceof SortedMap) {
      SortedMap<K, ? extends V> sortedLeft = (SortedMap)left;
      return difference(sortedLeft, right);
    } 
    return difference(left, right, Equivalence.equals());
  }

















  
  public static <K, V> MapDifference<K, V> difference(Map<? extends K, ? extends V> left, Map<? extends K, ? extends V> right, Equivalence<? super V> valueEquivalence) {
    Preconditions.checkNotNull(valueEquivalence);
    
    Map<K, V> onlyOnLeft = newLinkedHashMap();
    Map<K, V> onlyOnRight = new LinkedHashMap<>(right);
    Map<K, V> onBoth = newLinkedHashMap();
    Map<K, MapDifference.ValueDifference<V>> differences = newLinkedHashMap();
    doDifference(left, right, valueEquivalence, onlyOnLeft, onlyOnRight, onBoth, differences);
    return new MapDifferenceImpl<>(onlyOnLeft, onlyOnRight, onBoth, differences);
  }


















  
  public static <K, V> SortedMapDifference<K, V> difference(SortedMap<K, ? extends V> left, Map<? extends K, ? extends V> right) {
    Preconditions.checkNotNull(left);
    Preconditions.checkNotNull(right);
    Comparator<? super K> comparator = orNaturalOrder(left.comparator());
    SortedMap<K, V> onlyOnLeft = newTreeMap(comparator);
    SortedMap<K, V> onlyOnRight = newTreeMap(comparator);
    onlyOnRight.putAll(right);
    SortedMap<K, V> onBoth = newTreeMap(comparator);
    SortedMap<K, MapDifference.ValueDifference<V>> differences = newTreeMap(comparator);
    doDifference(left, right, Equivalence.equals(), onlyOnLeft, onlyOnRight, onBoth, differences);
    return new SortedMapDifferenceImpl<>(onlyOnLeft, onlyOnRight, onBoth, differences);
  }







  
  private static <K, V> void doDifference(Map<? extends K, ? extends V> left, Map<? extends K, ? extends V> right, Equivalence<? super V> valueEquivalence, Map<K, V> onlyOnLeft, Map<K, V> onlyOnRight, Map<K, V> onBoth, Map<K, MapDifference.ValueDifference<V>> differences) {
    for (Map.Entry<? extends K, ? extends V> entry : left.entrySet()) {
      K leftKey = entry.getKey();
      V leftValue = entry.getValue();
      if (right.containsKey(leftKey)) {
        V rightValue = onlyOnRight.remove(leftKey);
        if (valueEquivalence.equivalent(leftValue, rightValue)) {
          onBoth.put(leftKey, leftValue); continue;
        } 
        differences.put(leftKey, ValueDifferenceImpl.create(leftValue, rightValue));
        continue;
      } 
      onlyOnLeft.put(leftKey, leftValue);
    } 
  }

  
  private static <K, V> Map<K, V> unmodifiableMap(Map<K, ? extends V> map) {
    if (map instanceof SortedMap) {
      return Collections.unmodifiableSortedMap((SortedMap<K, ? extends V>)map);
    }
    return Collections.unmodifiableMap(map);
  }

  
  static class MapDifferenceImpl<K, V>
    implements MapDifference<K, V>
  {
    final Map<K, V> onlyOnLeft;
    
    final Map<K, V> onlyOnRight;
    
    final Map<K, V> onBoth;
    final Map<K, MapDifference.ValueDifference<V>> differences;
    
    MapDifferenceImpl(Map<K, V> onlyOnLeft, Map<K, V> onlyOnRight, Map<K, V> onBoth, Map<K, MapDifference.ValueDifference<V>> differences) {
      this.onlyOnLeft = Maps.unmodifiableMap(onlyOnLeft);
      this.onlyOnRight = Maps.unmodifiableMap(onlyOnRight);
      this.onBoth = Maps.unmodifiableMap(onBoth);
      this.differences = (Map)Maps.unmodifiableMap((Map)differences);
    }

    
    public boolean areEqual() {
      return (this.onlyOnLeft.isEmpty() && this.onlyOnRight.isEmpty() && this.differences.isEmpty());
    }

    
    public Map<K, V> entriesOnlyOnLeft() {
      return this.onlyOnLeft;
    }

    
    public Map<K, V> entriesOnlyOnRight() {
      return this.onlyOnRight;
    }

    
    public Map<K, V> entriesInCommon() {
      return this.onBoth;
    }

    
    public Map<K, MapDifference.ValueDifference<V>> entriesDiffering() {
      return this.differences;
    }

    
    public boolean equals(Object object) {
      if (object == this) {
        return true;
      }
      if (object instanceof MapDifference) {
        MapDifference<?, ?> other = (MapDifference<?, ?>)object;
        return (entriesOnlyOnLeft().equals(other.entriesOnlyOnLeft()) && 
          entriesOnlyOnRight().equals(other.entriesOnlyOnRight()) && 
          entriesInCommon().equals(other.entriesInCommon()) && 
          entriesDiffering().equals(other.entriesDiffering()));
      } 
      return false;
    }

    
    public int hashCode() {
      return Objects.hashCode(new Object[] {
            entriesOnlyOnLeft(), entriesOnlyOnRight(), entriesInCommon(), entriesDiffering()
          });
    }
    
    public String toString() {
      if (areEqual()) {
        return "equal";
      }
      
      StringBuilder result = new StringBuilder("not equal");
      if (!this.onlyOnLeft.isEmpty()) {
        result.append(": only on left=").append(this.onlyOnLeft);
      }
      if (!this.onlyOnRight.isEmpty()) {
        result.append(": only on right=").append(this.onlyOnRight);
      }
      if (!this.differences.isEmpty()) {
        result.append(": value differences=").append(this.differences);
      }
      return result.toString();
    }
  }
  
  static class ValueDifferenceImpl<V> implements MapDifference.ValueDifference<V> {
    private final V left;
    private final V right;
    
    static <V> MapDifference.ValueDifference<V> create(V left, V right) {
      return new ValueDifferenceImpl<>(left, right);
    }
    
    private ValueDifferenceImpl(V left, V right) {
      this.left = left;
      this.right = right;
    }

    
    public V leftValue() {
      return this.left;
    }

    
    public V rightValue() {
      return this.right;
    }

    
    public boolean equals(Object object) {
      if (object instanceof MapDifference.ValueDifference) {
        MapDifference.ValueDifference<?> that = (MapDifference.ValueDifference)object;
        return (Objects.equal(this.left, that.leftValue()) && 
          Objects.equal(this.right, that.rightValue()));
      } 
      return false;
    }

    
    public int hashCode() {
      return Objects.hashCode(new Object[] { this.left, this.right });
    }

    
    public String toString() {
      return "(" + this.left + ", " + this.right + ")";
    }
  }


  
  static class SortedMapDifferenceImpl<K, V>
    extends MapDifferenceImpl<K, V>
    implements SortedMapDifference<K, V>
  {
    SortedMapDifferenceImpl(SortedMap<K, V> onlyOnLeft, SortedMap<K, V> onlyOnRight, SortedMap<K, V> onBoth, SortedMap<K, MapDifference.ValueDifference<V>> differences) {
      super(onlyOnLeft, onlyOnRight, onBoth, differences);
    }

    
    public SortedMap<K, MapDifference.ValueDifference<V>> entriesDiffering() {
      return (SortedMap<K, MapDifference.ValueDifference<V>>)super.entriesDiffering();
    }

    
    public SortedMap<K, V> entriesInCommon() {
      return (SortedMap<K, V>)super.entriesInCommon();
    }

    
    public SortedMap<K, V> entriesOnlyOnLeft() {
      return (SortedMap<K, V>)super.entriesOnlyOnLeft();
    }

    
    public SortedMap<K, V> entriesOnlyOnRight() {
      return (SortedMap<K, V>)super.entriesOnlyOnRight();
    }
  }






  
  static <E> Comparator<? super E> orNaturalOrder(Comparator<? super E> comparator) {
    if (comparator != null) {
      return comparator;
    }
    return Ordering.natural();
  }
























  
  public static <K, V> Map<K, V> asMap(Set<K> set, Function<? super K, V> function) {
    return new AsMapView<>(set, function);
  }























  
  public static <K, V> SortedMap<K, V> asMap(SortedSet<K> set, Function<? super K, V> function) {
    return new SortedAsMapView<>(set, function);
  }
























  
  @GwtIncompatible
  public static <K, V> NavigableMap<K, V> asMap(NavigableSet<K> set, Function<? super K, V> function) {
    return new NavigableAsMapView<>(set, function);
  }
  
  private static class AsMapView<K, V>
    extends ViewCachingAbstractMap<K, V> {
    private final Set<K> set;
    final Function<? super K, V> function;
    
    Set<K> backingSet() {
      return this.set;
    }
    
    AsMapView(Set<K> set, Function<? super K, V> function) {
      this.set = (Set<K>)Preconditions.checkNotNull(set);
      this.function = (Function<? super K, V>)Preconditions.checkNotNull(function);
    }

    
    public Set<K> createKeySet() {
      return Maps.removeOnlySet(backingSet());
    }

    
    Collection<V> createValues() {
      return Collections2.transform(this.set, this.function);
    }

    
    public int size() {
      return backingSet().size();
    }

    
    public boolean containsKey(Object key) {
      return backingSet().contains(key);
    }

    
    public V get(Object key) {
      return getOrDefault(key, null);
    }

    
    public V getOrDefault(Object key, V defaultValue) {
      if (Collections2.safeContains(backingSet(), key)) {
        
        K k = (K)key;
        return (V)this.function.apply(k);
      } 
      return defaultValue;
    }


    
    public V remove(Object key) {
      if (backingSet().remove(key)) {
        
        K k = (K)key;
        return (V)this.function.apply(k);
      } 
      return null;
    }


    
    public void clear() {
      backingSet().clear();
    }

    
    protected Set<Map.Entry<K, V>> createEntrySet() {
      class EntrySetImpl
        extends Maps.EntrySet<K, V>
      {
        Map<K, V> map() {
          return Maps.AsMapView.this;
        }

        
        public Iterator<Map.Entry<K, V>> iterator() {
          return Maps.asMapEntryIterator(Maps.AsMapView.this.backingSet(), Maps.AsMapView.this.function);
        }
      };
      return new EntrySetImpl();
    }

    
    public void forEach(BiConsumer<? super K, ? super V> action) {
      Preconditions.checkNotNull(action);
      
      backingSet().forEach(k -> action.accept(k, this.function.apply(k)));
    }
  }

  
  static <K, V> Iterator<Map.Entry<K, V>> asMapEntryIterator(Set<K> set, final Function<? super K, V> function) {
    return new TransformedIterator<K, Map.Entry<K, V>>(set.iterator())
      {
        Map.Entry<K, V> transform(K key) {
          return Maps.immutableEntry(key, (V)function.apply(key));
        }
      };
  }
  
  private static class SortedAsMapView<K, V>
    extends AsMapView<K, V> implements SortedMap<K, V> {
    SortedAsMapView(SortedSet<K> set, Function<? super K, V> function) {
      super(set, function);
    }

    
    SortedSet<K> backingSet() {
      return (SortedSet<K>)super.backingSet();
    }

    
    public Comparator<? super K> comparator() {
      return backingSet().comparator();
    }

    
    public Set<K> keySet() {
      return Maps.removeOnlySortedSet(backingSet());
    }

    
    public SortedMap<K, V> subMap(K fromKey, K toKey) {
      return Maps.asMap(backingSet().subSet(fromKey, toKey), this.function);
    }

    
    public SortedMap<K, V> headMap(K toKey) {
      return Maps.asMap(backingSet().headSet(toKey), this.function);
    }

    
    public SortedMap<K, V> tailMap(K fromKey) {
      return Maps.asMap(backingSet().tailSet(fromKey), this.function);
    }

    
    public K firstKey() {
      return backingSet().first();
    }

    
    public K lastKey() {
      return backingSet().last();
    }
  }

  
  @GwtIncompatible
  private static final class NavigableAsMapView<K, V>
    extends AbstractNavigableMap<K, V>
  {
    private final NavigableSet<K> set;
    
    private final Function<? super K, V> function;

    
    NavigableAsMapView(NavigableSet<K> ks, Function<? super K, V> vFunction) {
      this.set = (NavigableSet<K>)Preconditions.checkNotNull(ks);
      this.function = (Function<? super K, V>)Preconditions.checkNotNull(vFunction);
    }


    
    public NavigableMap<K, V> subMap(K fromKey, boolean fromInclusive, K toKey, boolean toInclusive) {
      return Maps.asMap(this.set.subSet(fromKey, fromInclusive, toKey, toInclusive), this.function);
    }

    
    public NavigableMap<K, V> headMap(K toKey, boolean inclusive) {
      return Maps.asMap(this.set.headSet(toKey, inclusive), this.function);
    }

    
    public NavigableMap<K, V> tailMap(K fromKey, boolean inclusive) {
      return Maps.asMap(this.set.tailSet(fromKey, inclusive), this.function);
    }

    
    public Comparator<? super K> comparator() {
      return this.set.comparator();
    }

    
    public V get(Object key) {
      return getOrDefault(key, null);
    }

    
    public V getOrDefault(Object key, V defaultValue) {
      if (Collections2.safeContains(this.set, key)) {
        
        K k = (K)key;
        return (V)this.function.apply(k);
      } 
      return defaultValue;
    }


    
    public void clear() {
      this.set.clear();
    }

    
    Iterator<Map.Entry<K, V>> entryIterator() {
      return Maps.asMapEntryIterator(this.set, this.function);
    }

    
    Spliterator<Map.Entry<K, V>> entrySpliterator() {
      return CollectSpliterators.map(this.set.spliterator(), e -> Maps.immutableEntry(e, this.function.apply(e)));
    }

    
    public void forEach(BiConsumer<? super K, ? super V> action) {
      this.set.forEach(k -> action.accept(k, this.function.apply(k)));
    }

    
    Iterator<Map.Entry<K, V>> descendingEntryIterator() {
      return descendingMap().entrySet().iterator();
    }

    
    public NavigableSet<K> navigableKeySet() {
      return Maps.removeOnlyNavigableSet(this.set);
    }

    
    public int size() {
      return this.set.size();
    }

    
    public NavigableMap<K, V> descendingMap() {
      return Maps.asMap(this.set.descendingSet(), this.function);
    }
  }
  
  private static <E> Set<E> removeOnlySet(final Set<E> set) {
    return new ForwardingSet<E>()
      {
        protected Set<E> delegate() {
          return set;
        }

        
        public boolean add(E element) {
          throw new UnsupportedOperationException();
        }

        
        public boolean addAll(Collection<? extends E> es) {
          throw new UnsupportedOperationException();
        }
      };
  }
  
  private static <E> SortedSet<E> removeOnlySortedSet(final SortedSet<E> set) {
    return new ForwardingSortedSet<E>()
      {
        protected SortedSet<E> delegate() {
          return set;
        }

        
        public boolean add(E element) {
          throw new UnsupportedOperationException();
        }

        
        public boolean addAll(Collection<? extends E> es) {
          throw new UnsupportedOperationException();
        }

        
        public SortedSet<E> headSet(E toElement) {
          return Maps.removeOnlySortedSet(super.headSet(toElement));
        }

        
        public SortedSet<E> subSet(E fromElement, E toElement) {
          return Maps.removeOnlySortedSet(super.subSet(fromElement, toElement));
        }

        
        public SortedSet<E> tailSet(E fromElement) {
          return Maps.removeOnlySortedSet(super.tailSet(fromElement));
        }
      };
  }
  
  @GwtIncompatible
  private static <E> NavigableSet<E> removeOnlyNavigableSet(final NavigableSet<E> set) {
    return new ForwardingNavigableSet<E>()
      {
        protected NavigableSet<E> delegate() {
          return set;
        }

        
        public boolean add(E element) {
          throw new UnsupportedOperationException();
        }

        
        public boolean addAll(Collection<? extends E> es) {
          throw new UnsupportedOperationException();
        }

        
        public SortedSet<E> headSet(E toElement) {
          return Maps.removeOnlySortedSet(super.headSet(toElement));
        }

        
        public NavigableSet<E> headSet(E toElement, boolean inclusive) {
          return Maps.removeOnlyNavigableSet(super.headSet(toElement, inclusive));
        }

        
        public SortedSet<E> subSet(E fromElement, E toElement) {
          return Maps.removeOnlySortedSet(super.subSet(fromElement, toElement));
        }


        
        public NavigableSet<E> subSet(E fromElement, boolean fromInclusive, E toElement, boolean toInclusive) {
          return Maps.removeOnlyNavigableSet(super
              .subSet(fromElement, fromInclusive, toElement, toInclusive));
        }

        
        public SortedSet<E> tailSet(E fromElement) {
          return Maps.removeOnlySortedSet(super.tailSet(fromElement));
        }

        
        public NavigableSet<E> tailSet(E fromElement, boolean inclusive) {
          return Maps.removeOnlyNavigableSet(super.tailSet(fromElement, inclusive));
        }

        
        public NavigableSet<E> descendingSet() {
          return Maps.removeOnlyNavigableSet(super.descendingSet());
        }
      };
  }

















  
  public static <K, V> ImmutableMap<K, V> toMap(Iterable<K> keys, Function<? super K, V> valueFunction) {
    return toMap(keys.iterator(), valueFunction);
  }














  
  public static <K, V> ImmutableMap<K, V> toMap(Iterator<K> keys, Function<? super K, V> valueFunction) {
    Preconditions.checkNotNull(valueFunction);
    
    Map<K, V> builder = newLinkedHashMap();
    while (keys.hasNext()) {
      K key = keys.next();
      builder.put(key, (V)valueFunction.apply(key));
    } 
    return ImmutableMap.copyOf(builder);
  }






























  
  @CanIgnoreReturnValue
  public static <K, V> ImmutableMap<K, V> uniqueIndex(Iterable<V> values, Function<? super V, K> keyFunction) {
    return uniqueIndex(values.iterator(), keyFunction);
  }






























  
  @CanIgnoreReturnValue
  public static <K, V> ImmutableMap<K, V> uniqueIndex(Iterator<V> values, Function<? super V, K> keyFunction) {
    Preconditions.checkNotNull(keyFunction);
    ImmutableMap.Builder<K, V> builder = ImmutableMap.builder();
    while (values.hasNext()) {
      V value = values.next();
      builder.put((K)keyFunction.apply(value), value);
    } 
    try {
      return builder.build();
    } catch (IllegalArgumentException duplicateKeys) {
      throw new IllegalArgumentException(duplicateKeys
          .getMessage() + ". To index multiple values under a key, use Multimaps.index.");
    } 
  }











  
  @GwtIncompatible
  public static ImmutableMap<String, String> fromProperties(Properties properties) {
    ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
    
    for (Enumeration<?> e = properties.propertyNames(); e.hasMoreElements(); ) {
      String key = (String)e.nextElement();
      builder.put(key, properties.getProperty(key));
    } 
    
    return builder.build();
  }












  
  @GwtCompatible(serializable = true)
  public static <K, V> Map.Entry<K, V> immutableEntry(K key, V value) {
    return new ImmutableEntry<>(key, value);
  }








  
  static <K, V> Set<Map.Entry<K, V>> unmodifiableEntrySet(Set<Map.Entry<K, V>> entrySet) {
    return new UnmodifiableEntrySet<>(Collections.unmodifiableSet(entrySet));
  }









  
  static <K, V> Map.Entry<K, V> unmodifiableEntry(final Map.Entry<? extends K, ? extends V> entry) {
    Preconditions.checkNotNull(entry);
    return new AbstractMapEntry<K, V>()
      {
        public K getKey() {
          return (K)entry.getKey();
        }

        
        public V getValue() {
          return (V)entry.getValue();
        }
      };
  }

  
  static <K, V> UnmodifiableIterator<Map.Entry<K, V>> unmodifiableEntryIterator(final Iterator<Map.Entry<K, V>> entryIterator) {
    return new UnmodifiableIterator<Map.Entry<K, V>>()
      {
        public boolean hasNext() {
          return entryIterator.hasNext();
        }

        
        public Map.Entry<K, V> next() {
          return Maps.unmodifiableEntry(entryIterator.next());
        }
      };
  }
  
  static class UnmodifiableEntries<K, V>
    extends ForwardingCollection<Map.Entry<K, V>> {
    private final Collection<Map.Entry<K, V>> entries;
    
    UnmodifiableEntries(Collection<Map.Entry<K, V>> entries) {
      this.entries = entries;
    }

    
    protected Collection<Map.Entry<K, V>> delegate() {
      return this.entries;
    }

    
    public Iterator<Map.Entry<K, V>> iterator() {
      return Maps.unmodifiableEntryIterator(this.entries.iterator());
    }



    
    public Object[] toArray() {
      return standardToArray();
    }

    
    public <T> T[] toArray(T[] array) {
      return (T[])standardToArray((Object[])array);
    }
  }
  
  static class UnmodifiableEntrySet<K, V>
    extends UnmodifiableEntries<K, V>
    implements Set<Map.Entry<K, V>> {
    UnmodifiableEntrySet(Set<Map.Entry<K, V>> entries) {
      super(entries);
    }



    
    public boolean equals(Object object) {
      return Sets.equalsImpl(this, object);
    }

    
    public int hashCode() {
      return Sets.hashCodeImpl(this);
    }
  }










  
  public static <A, B> Converter<A, B> asConverter(BiMap<A, B> bimap) {
    return new BiMapConverter<>(bimap);
  }
  
  private static final class BiMapConverter<A, B> extends Converter<A, B> implements Serializable { private final BiMap<A, B> bimap;
    private static final long serialVersionUID = 0L;
    
    BiMapConverter(BiMap<A, B> bimap) {
      this.bimap = (BiMap<A, B>)Preconditions.checkNotNull(bimap);
    }

    
    protected B doForward(A a) {
      return convert(this.bimap, a);
    }

    
    protected A doBackward(B b) {
      return convert(this.bimap.inverse(), b);
    }
    
    private static <X, Y> Y convert(BiMap<X, Y> bimap, X input) {
      Y output = bimap.get(input);
      Preconditions.checkArgument((output != null), "No non-null mapping present for input: %s", input);
      return output;
    }

    
    public boolean equals(Object object) {
      if (object instanceof BiMapConverter) {
        BiMapConverter<?, ?> that = (BiMapConverter<?, ?>)object;
        return this.bimap.equals(that.bimap);
      } 
      return false;
    }

    
    public int hashCode() {
      return this.bimap.hashCode();
    }


    
    public String toString() {
      return "Maps.asConverter(" + this.bimap + ")";
    } }
































  
  public static <K, V> BiMap<K, V> synchronizedBiMap(BiMap<K, V> bimap) {
    return Synchronized.biMap(bimap, null);
  }











  
  public static <K, V> BiMap<K, V> unmodifiableBiMap(BiMap<? extends K, ? extends V> bimap) {
    return new UnmodifiableBiMap<>(bimap, null);
  }
  
  private static class UnmodifiableBiMap<K, V> extends ForwardingMap<K, V> implements BiMap<K, V>, Serializable {
    final Map<K, V> unmodifiableMap;
    final BiMap<? extends K, ? extends V> delegate;
    @RetainedWith
    BiMap<V, K> inverse;
    transient Set<V> values;
    private static final long serialVersionUID = 0L;
    
    UnmodifiableBiMap(BiMap<? extends K, ? extends V> delegate, BiMap<V, K> inverse) {
      this.unmodifiableMap = Collections.unmodifiableMap(delegate);
      this.delegate = delegate;
      this.inverse = inverse;
    }

    
    protected Map<K, V> delegate() {
      return this.unmodifiableMap;
    }

    
    public V forcePut(K key, V value) {
      throw new UnsupportedOperationException();
    }

    
    public BiMap<V, K> inverse() {
      BiMap<V, K> result = this.inverse;
      return (result == null) ? (this
        .inverse = new UnmodifiableBiMap(this.delegate.inverse(), this)) : result;
    }


    
    public Set<V> values() {
      Set<V> result = this.values;
      return (result == null) ? (this.values = Collections.unmodifiableSet(this.delegate.values())) : result;
    }
  }




































  
  public static <K, V1, V2> Map<K, V2> transformValues(Map<K, V1> fromMap, Function<? super V1, V2> function) {
    return transformEntries(fromMap, asEntryTransformer(function));
  }





































  
  public static <K, V1, V2> SortedMap<K, V2> transformValues(SortedMap<K, V1> fromMap, Function<? super V1, V2> function) {
    return transformEntries(fromMap, asEntryTransformer(function));
  }







































  
  @GwtIncompatible
  public static <K, V1, V2> NavigableMap<K, V2> transformValues(NavigableMap<K, V1> fromMap, Function<? super V1, V2> function) {
    return transformEntries(fromMap, asEntryTransformer(function));
  }
















































  
  public static <K, V1, V2> Map<K, V2> transformEntries(Map<K, V1> fromMap, EntryTransformer<? super K, ? super V1, V2> transformer) {
    return new TransformedEntriesMap<>(fromMap, transformer);
  }
















































  
  public static <K, V1, V2> SortedMap<K, V2> transformEntries(SortedMap<K, V1> fromMap, EntryTransformer<? super K, ? super V1, V2> transformer) {
    return new TransformedEntriesSortedMap<>(fromMap, transformer);
  }

















































  
  @GwtIncompatible
  public static <K, V1, V2> NavigableMap<K, V2> transformEntries(NavigableMap<K, V1> fromMap, EntryTransformer<? super K, ? super V1, V2> transformer) {
    return new TransformedEntriesNavigableMap<>(fromMap, transformer);
  }






























  
  static <K, V1, V2> EntryTransformer<K, V1, V2> asEntryTransformer(final Function<? super V1, V2> function) {
    Preconditions.checkNotNull(function);
    return new EntryTransformer<K, V1, V2>()
      {
        public V2 transformEntry(K key, V1 value) {
          return (V2)function.apply(value);
        }
      };
  }

  
  static <K, V1, V2> Function<V1, V2> asValueToValueFunction(final EntryTransformer<? super K, V1, V2> transformer, final K key) {
    Preconditions.checkNotNull(transformer);
    return new Function<V1, V2>()
      {
        public V2 apply(V1 v1) {
          return transformer.transformEntry(key, v1);
        }
      };
  }


  
  static <K, V1, V2> Function<Map.Entry<K, V1>, V2> asEntryToValueFunction(final EntryTransformer<? super K, ? super V1, V2> transformer) {
    Preconditions.checkNotNull(transformer);
    return new Function<Map.Entry<K, V1>, V2>()
      {
        public V2 apply(Map.Entry<K, V1> entry) {
          return (V2)transformer.transformEntry(entry.getKey(), entry.getValue());
        }
      };
  }


  
  static <V2, K, V1> Map.Entry<K, V2> transformEntry(final EntryTransformer<? super K, ? super V1, V2> transformer, final Map.Entry<K, V1> entry) {
    Preconditions.checkNotNull(transformer);
    Preconditions.checkNotNull(entry);
    return new AbstractMapEntry<K, V2>()
      {
        public K getKey() {
          return (K)entry.getKey();
        }

        
        public V2 getValue() {
          return (V2)transformer.transformEntry(entry.getKey(), entry.getValue());
        }
      };
  }


  
  static <K, V1, V2> Function<Map.Entry<K, V1>, Map.Entry<K, V2>> asEntryToEntryFunction(final EntryTransformer<? super K, ? super V1, V2> transformer) {
    Preconditions.checkNotNull(transformer);
    return new Function<Map.Entry<K, V1>, Map.Entry<K, V2>>()
      {
        public Map.Entry<K, V2> apply(Map.Entry<K, V1> entry) {
          return Maps.transformEntry(transformer, entry);
        }
      };
  }
  
  static class TransformedEntriesMap<K, V1, V2>
    extends IteratorBasedAbstractMap<K, V2> {
    final Map<K, V1> fromMap;
    final Maps.EntryTransformer<? super K, ? super V1, V2> transformer;
    
    TransformedEntriesMap(Map<K, V1> fromMap, Maps.EntryTransformer<? super K, ? super V1, V2> transformer) {
      this.fromMap = (Map<K, V1>)Preconditions.checkNotNull(fromMap);
      this.transformer = (Maps.EntryTransformer<? super K, ? super V1, V2>)Preconditions.checkNotNull(transformer);
    }

    
    public int size() {
      return this.fromMap.size();
    }

    
    public boolean containsKey(Object key) {
      return this.fromMap.containsKey(key);
    }

    
    public V2 get(Object key) {
      return getOrDefault(key, null);
    }



    
    public V2 getOrDefault(Object key, V2 defaultValue) {
      V1 value = this.fromMap.get(key);
      return (value != null || this.fromMap.containsKey(key)) ? this.transformer
        .transformEntry((K)key, value) : defaultValue;
    }




    
    public V2 remove(Object key) {
      return this.fromMap.containsKey(key) ? this.transformer
        .transformEntry((K)key, this.fromMap.remove(key)) : null;
    }


    
    public void clear() {
      this.fromMap.clear();
    }

    
    public Set<K> keySet() {
      return this.fromMap.keySet();
    }

    
    Iterator<Map.Entry<K, V2>> entryIterator() {
      return Iterators.transform(this.fromMap
          .entrySet().iterator(), Maps.asEntryToEntryFunction(this.transformer));
    }

    
    Spliterator<Map.Entry<K, V2>> entrySpliterator() {
      return CollectSpliterators.map(this.fromMap
          .entrySet().spliterator(), (Function<?, ? extends Map.Entry<K, V2>>)Maps.asEntryToEntryFunction(this.transformer));
    }

    
    public void forEach(BiConsumer<? super K, ? super V2> action) {
      Preconditions.checkNotNull(action);
      
      this.fromMap.forEach((k, v1) -> action.accept(k, this.transformer.transformEntry((K)k, (V1)v1)));
    }

    
    public Collection<V2> values() {
      return new Maps.Values<>(this);
    }
  }
  
  static class TransformedEntriesSortedMap<K, V1, V2>
    extends TransformedEntriesMap<K, V1, V2>
    implements SortedMap<K, V2> {
    protected SortedMap<K, V1> fromMap() {
      return (SortedMap<K, V1>)this.fromMap;
    }

    
    TransformedEntriesSortedMap(SortedMap<K, V1> fromMap, Maps.EntryTransformer<? super K, ? super V1, V2> transformer) {
      super(fromMap, transformer);
    }

    
    public Comparator<? super K> comparator() {
      return fromMap().comparator();
    }

    
    public K firstKey() {
      return fromMap().firstKey();
    }

    
    public SortedMap<K, V2> headMap(K toKey) {
      return Maps.transformEntries(fromMap().headMap(toKey), this.transformer);
    }

    
    public K lastKey() {
      return fromMap().lastKey();
    }

    
    public SortedMap<K, V2> subMap(K fromKey, K toKey) {
      return Maps.transformEntries(fromMap().subMap(fromKey, toKey), this.transformer);
    }

    
    public SortedMap<K, V2> tailMap(K fromKey) {
      return Maps.transformEntries(fromMap().tailMap(fromKey), this.transformer);
    }
  }
  
  @GwtIncompatible
  private static class TransformedEntriesNavigableMap<K, V1, V2>
    extends TransformedEntriesSortedMap<K, V1, V2>
    implements NavigableMap<K, V2>
  {
    TransformedEntriesNavigableMap(NavigableMap<K, V1> fromMap, Maps.EntryTransformer<? super K, ? super V1, V2> transformer) {
      super(fromMap, transformer);
    }

    
    public Map.Entry<K, V2> ceilingEntry(K key) {
      return transformEntry(fromMap().ceilingEntry(key));
    }

    
    public K ceilingKey(K key) {
      return fromMap().ceilingKey(key);
    }

    
    public NavigableSet<K> descendingKeySet() {
      return fromMap().descendingKeySet();
    }

    
    public NavigableMap<K, V2> descendingMap() {
      return Maps.transformEntries(fromMap().descendingMap(), this.transformer);
    }

    
    public Map.Entry<K, V2> firstEntry() {
      return transformEntry(fromMap().firstEntry());
    }

    
    public Map.Entry<K, V2> floorEntry(K key) {
      return transformEntry(fromMap().floorEntry(key));
    }

    
    public K floorKey(K key) {
      return fromMap().floorKey(key);
    }

    
    public NavigableMap<K, V2> headMap(K toKey) {
      return headMap(toKey, false);
    }

    
    public NavigableMap<K, V2> headMap(K toKey, boolean inclusive) {
      return Maps.transformEntries(fromMap().headMap(toKey, inclusive), this.transformer);
    }

    
    public Map.Entry<K, V2> higherEntry(K key) {
      return transformEntry(fromMap().higherEntry(key));
    }

    
    public K higherKey(K key) {
      return fromMap().higherKey(key);
    }

    
    public Map.Entry<K, V2> lastEntry() {
      return transformEntry(fromMap().lastEntry());
    }

    
    public Map.Entry<K, V2> lowerEntry(K key) {
      return transformEntry(fromMap().lowerEntry(key));
    }

    
    public K lowerKey(K key) {
      return fromMap().lowerKey(key);
    }

    
    public NavigableSet<K> navigableKeySet() {
      return fromMap().navigableKeySet();
    }

    
    public Map.Entry<K, V2> pollFirstEntry() {
      return transformEntry(fromMap().pollFirstEntry());
    }

    
    public Map.Entry<K, V2> pollLastEntry() {
      return transformEntry(fromMap().pollLastEntry());
    }


    
    public NavigableMap<K, V2> subMap(K fromKey, boolean fromInclusive, K toKey, boolean toInclusive) {
      return Maps.transformEntries(
          fromMap().subMap(fromKey, fromInclusive, toKey, toInclusive), this.transformer);
    }

    
    public NavigableMap<K, V2> subMap(K fromKey, K toKey) {
      return subMap(fromKey, true, toKey, false);
    }

    
    public NavigableMap<K, V2> tailMap(K fromKey) {
      return tailMap(fromKey, true);
    }

    
    public NavigableMap<K, V2> tailMap(K fromKey, boolean inclusive) {
      return Maps.transformEntries(fromMap().tailMap(fromKey, inclusive), this.transformer);
    }
    
    private Map.Entry<K, V2> transformEntry(Map.Entry<K, V1> entry) {
      return (entry == null) ? null : Maps.<V2, K, V1>transformEntry(this.transformer, entry);
    }

    
    protected NavigableMap<K, V1> fromMap() {
      return (NavigableMap<K, V1>)super.fromMap();
    }
  }
  
  static <K> Predicate<Map.Entry<K, ?>> keyPredicateOnEntries(Predicate<? super K> keyPredicate) {
    return Predicates.compose(keyPredicate, keyFunction());
  }
  
  static <V> Predicate<Map.Entry<?, V>> valuePredicateOnEntries(Predicate<? super V> valuePredicate) {
    return Predicates.compose(valuePredicate, valueFunction());
  }
























  
  public static <K, V> Map<K, V> filterKeys(Map<K, V> unfiltered, Predicate<? super K> keyPredicate) {
    Preconditions.checkNotNull(keyPredicate);
    Predicate<Map.Entry<K, ?>> entryPredicate = keyPredicateOnEntries(keyPredicate);
    return (unfiltered instanceof AbstractFilteredMap) ? 
      filterFiltered((AbstractFilteredMap<K, V>)unfiltered, (Predicate)entryPredicate) : new FilteredKeyMap<>(
        (Map<K, V>)Preconditions.checkNotNull(unfiltered), keyPredicate, (Predicate)entryPredicate);
  }





























  
  public static <K, V> SortedMap<K, V> filterKeys(SortedMap<K, V> unfiltered, Predicate<? super K> keyPredicate) {
    return filterEntries(unfiltered, (Predicate)keyPredicateOnEntries(keyPredicate));
  }





























  
  @GwtIncompatible
  public static <K, V> NavigableMap<K, V> filterKeys(NavigableMap<K, V> unfiltered, Predicate<? super K> keyPredicate) {
    return filterEntries(unfiltered, (Predicate)keyPredicateOnEntries(keyPredicate));
  }

























  
  public static <K, V> BiMap<K, V> filterKeys(BiMap<K, V> unfiltered, Predicate<? super K> keyPredicate) {
    Preconditions.checkNotNull(keyPredicate);
    return filterEntries(unfiltered, (Predicate)keyPredicateOnEntries(keyPredicate));
  }
























  
  public static <K, V> Map<K, V> filterValues(Map<K, V> unfiltered, Predicate<? super V> valuePredicate) {
    return filterEntries(unfiltered, (Predicate)valuePredicateOnEntries(valuePredicate));
  }



























  
  public static <K, V> SortedMap<K, V> filterValues(SortedMap<K, V> unfiltered, Predicate<? super V> valuePredicate) {
    return filterEntries(unfiltered, (Predicate)valuePredicateOnEntries(valuePredicate));
  }



























  
  @GwtIncompatible
  public static <K, V> NavigableMap<K, V> filterValues(NavigableMap<K, V> unfiltered, Predicate<? super V> valuePredicate) {
    return filterEntries(unfiltered, (Predicate)valuePredicateOnEntries(valuePredicate));
  }




























  
  public static <K, V> BiMap<K, V> filterValues(BiMap<K, V> unfiltered, Predicate<? super V> valuePredicate) {
    return filterEntries(unfiltered, (Predicate)valuePredicateOnEntries(valuePredicate));
  }

























  
  public static <K, V> Map<K, V> filterEntries(Map<K, V> unfiltered, Predicate<? super Map.Entry<K, V>> entryPredicate) {
    Preconditions.checkNotNull(entryPredicate);
    return (unfiltered instanceof AbstractFilteredMap) ? 
      filterFiltered((AbstractFilteredMap<K, V>)unfiltered, entryPredicate) : new FilteredEntryMap<>(
        (Map<K, V>)Preconditions.checkNotNull(unfiltered), entryPredicate);
  }



























  
  public static <K, V> SortedMap<K, V> filterEntries(SortedMap<K, V> unfiltered, Predicate<? super Map.Entry<K, V>> entryPredicate) {
    Preconditions.checkNotNull(entryPredicate);
    return (unfiltered instanceof FilteredEntrySortedMap) ? 
      filterFiltered((FilteredEntrySortedMap<K, V>)unfiltered, entryPredicate) : new FilteredEntrySortedMap<>(
        (SortedMap<K, V>)Preconditions.checkNotNull(unfiltered), entryPredicate);
  }



























  
  @GwtIncompatible
  public static <K, V> NavigableMap<K, V> filterEntries(NavigableMap<K, V> unfiltered, Predicate<? super Map.Entry<K, V>> entryPredicate) {
    Preconditions.checkNotNull(entryPredicate);
    return (unfiltered instanceof FilteredEntryNavigableMap) ? 
      filterFiltered((FilteredEntryNavigableMap<K, V>)unfiltered, entryPredicate) : new FilteredEntryNavigableMap<>(
        (NavigableMap<K, V>)Preconditions.checkNotNull(unfiltered), entryPredicate);
  }




























  
  public static <K, V> BiMap<K, V> filterEntries(BiMap<K, V> unfiltered, Predicate<? super Map.Entry<K, V>> entryPredicate) {
    Preconditions.checkNotNull(unfiltered);
    Preconditions.checkNotNull(entryPredicate);
    return (unfiltered instanceof FilteredEntryBiMap) ? 
      filterFiltered((FilteredEntryBiMap<K, V>)unfiltered, entryPredicate) : new FilteredEntryBiMap<>(unfiltered, entryPredicate);
  }






  
  private static <K, V> Map<K, V> filterFiltered(AbstractFilteredMap<K, V> map, Predicate<? super Map.Entry<K, V>> entryPredicate) {
    return new FilteredEntryMap<>(map.unfiltered, 
        Predicates.and(map.predicate, entryPredicate));
  }





  
  private static <K, V> SortedMap<K, V> filterFiltered(FilteredEntrySortedMap<K, V> map, Predicate<? super Map.Entry<K, V>> entryPredicate) {
    Predicate<Map.Entry<K, V>> predicate = Predicates.and(map.predicate, entryPredicate);
    return new FilteredEntrySortedMap<>(map.sortedMap(), predicate);
  }






  
  @GwtIncompatible
  private static <K, V> NavigableMap<K, V> filterFiltered(FilteredEntryNavigableMap<K, V> map, Predicate<? super Map.Entry<K, V>> entryPredicate) {
    Predicate<Map.Entry<K, V>> predicate = Predicates.and(map.entryPredicate, entryPredicate);
    return new FilteredEntryNavigableMap<>(map.unfiltered, predicate);
  }





  
  private static <K, V> BiMap<K, V> filterFiltered(FilteredEntryBiMap<K, V> map, Predicate<? super Map.Entry<K, V>> entryPredicate) {
    Predicate<Map.Entry<K, V>> predicate = Predicates.and(map.predicate, entryPredicate);
    return new FilteredEntryBiMap<>(map.unfiltered(), predicate);
  }
  
  private static abstract class AbstractFilteredMap<K, V> extends ViewCachingAbstractMap<K, V> {
    final Map<K, V> unfiltered;
    final Predicate<? super Map.Entry<K, V>> predicate;
    
    AbstractFilteredMap(Map<K, V> unfiltered, Predicate<? super Map.Entry<K, V>> predicate) {
      this.unfiltered = unfiltered;
      this.predicate = predicate;
    }



    
    boolean apply(Object key, V value) {
      K k = (K)key;
      return this.predicate.apply(Maps.immutableEntry(k, value));
    }

    
    public V put(K key, V value) {
      Preconditions.checkArgument(apply(key, value));
      return this.unfiltered.put(key, value);
    }

    
    public void putAll(Map<? extends K, ? extends V> map) {
      for (Map.Entry<? extends K, ? extends V> entry : map.entrySet()) {
        Preconditions.checkArgument(apply(entry.getKey(), entry.getValue()));
      }
      this.unfiltered.putAll(map);
    }

    
    public boolean containsKey(Object key) {
      return (this.unfiltered.containsKey(key) && apply(key, this.unfiltered.get(key)));
    }

    
    public V get(Object key) {
      V value = this.unfiltered.get(key);
      return (value != null && apply(key, value)) ? value : null;
    }

    
    public boolean isEmpty() {
      return entrySet().isEmpty();
    }

    
    public V remove(Object key) {
      return containsKey(key) ? this.unfiltered.remove(key) : null;
    }

    
    Collection<V> createValues() {
      return new Maps.FilteredMapValues<>(this, this.unfiltered, this.predicate);
    }
  }
  
  private static final class FilteredMapValues<K, V>
    extends Values<K, V> {
    final Map<K, V> unfiltered;
    final Predicate<? super Map.Entry<K, V>> predicate;
    
    FilteredMapValues(Map<K, V> filteredMap, Map<K, V> unfiltered, Predicate<? super Map.Entry<K, V>> predicate) {
      super(filteredMap);
      this.unfiltered = unfiltered;
      this.predicate = predicate;
    }

    
    public boolean remove(Object o) {
      Iterator<Map.Entry<K, V>> entryItr = this.unfiltered.entrySet().iterator();
      while (entryItr.hasNext()) {
        Map.Entry<K, V> entry = entryItr.next();
        if (this.predicate.apply(entry) && Objects.equal(entry.getValue(), o)) {
          entryItr.remove();
          return true;
        } 
      } 
      return false;
    }

    
    public boolean removeAll(Collection<?> collection) {
      Iterator<Map.Entry<K, V>> entryItr = this.unfiltered.entrySet().iterator();
      boolean result = false;
      while (entryItr.hasNext()) {
        Map.Entry<K, V> entry = entryItr.next();
        if (this.predicate.apply(entry) && collection.contains(entry.getValue())) {
          entryItr.remove();
          result = true;
        } 
      } 
      return result;
    }

    
    public boolean retainAll(Collection<?> collection) {
      Iterator<Map.Entry<K, V>> entryItr = this.unfiltered.entrySet().iterator();
      boolean result = false;
      while (entryItr.hasNext()) {
        Map.Entry<K, V> entry = entryItr.next();
        if (this.predicate.apply(entry) && !collection.contains(entry.getValue())) {
          entryItr.remove();
          result = true;
        } 
      } 
      return result;
    }


    
    public Object[] toArray() {
      return Lists.<V>newArrayList(iterator()).toArray();
    }

    
    public <T> T[] toArray(T[] array) {
      return (T[])Lists.<V>newArrayList(iterator()).toArray((Object[])array);
    }
  }

  
  private static class FilteredKeyMap<K, V>
    extends AbstractFilteredMap<K, V>
  {
    final Predicate<? super K> keyPredicate;
    
    FilteredKeyMap(Map<K, V> unfiltered, Predicate<? super K> keyPredicate, Predicate<? super Map.Entry<K, V>> entryPredicate) {
      super(unfiltered, entryPredicate);
      this.keyPredicate = keyPredicate;
    }

    
    protected Set<Map.Entry<K, V>> createEntrySet() {
      return Sets.filter(this.unfiltered.entrySet(), this.predicate);
    }

    
    Set<K> createKeySet() {
      return Sets.filter(this.unfiltered.keySet(), this.keyPredicate);
    }




    
    public boolean containsKey(Object key) {
      return (this.unfiltered.containsKey(key) && this.keyPredicate.apply(key));
    }
  }

  
  static class FilteredEntryMap<K, V>
    extends AbstractFilteredMap<K, V>
  {
    final Set<Map.Entry<K, V>> filteredEntrySet;

    
    FilteredEntryMap(Map<K, V> unfiltered, Predicate<? super Map.Entry<K, V>> entryPredicate) {
      super(unfiltered, entryPredicate);
      this.filteredEntrySet = Sets.filter(unfiltered.entrySet(), this.predicate);
    }

    
    protected Set<Map.Entry<K, V>> createEntrySet() {
      return new EntrySet();
    }
    
    private class EntrySet extends ForwardingSet<Map.Entry<K, V>> {
      private EntrySet() {}
      
      protected Set<Map.Entry<K, V>> delegate() {
        return Maps.FilteredEntryMap.this.filteredEntrySet;
      }

      
      public Iterator<Map.Entry<K, V>> iterator() {
        return new TransformedIterator<Map.Entry<K, V>, Map.Entry<K, V>>(Maps.FilteredEntryMap.this.filteredEntrySet.iterator())
          {
            Map.Entry<K, V> transform(final Map.Entry<K, V> entry) {
              return new ForwardingMapEntry<K, V>()
                {
                  protected Map.Entry<K, V> delegate() {
                    return entry;
                  }

                  
                  public V setValue(V newValue) {
                    Preconditions.checkArgument(Maps.FilteredEntryMap.this.apply(getKey(), newValue));
                    return super.setValue(newValue);
                  }
                };
            }
          };
      }
    }

    
    Set<K> createKeySet() {
      return new KeySet();
    }

    
    static <K, V> boolean removeAllKeys(Map<K, V> map, Predicate<? super Map.Entry<K, V>> entryPredicate, Collection<?> keyCollection) {
      Iterator<Map.Entry<K, V>> entryItr = map.entrySet().iterator();
      boolean result = false;
      while (entryItr.hasNext()) {
        Map.Entry<K, V> entry = entryItr.next();
        if (entryPredicate.apply(entry) && keyCollection.contains(entry.getKey())) {
          entryItr.remove();
          result = true;
        } 
      } 
      return result;
    }

    
    static <K, V> boolean retainAllKeys(Map<K, V> map, Predicate<? super Map.Entry<K, V>> entryPredicate, Collection<?> keyCollection) {
      Iterator<Map.Entry<K, V>> entryItr = map.entrySet().iterator();
      boolean result = false;
      while (entryItr.hasNext()) {
        Map.Entry<K, V> entry = entryItr.next();
        if (entryPredicate.apply(entry) && !keyCollection.contains(entry.getKey())) {
          entryItr.remove();
          result = true;
        } 
      } 
      return result;
    }
    
    class KeySet
      extends Maps.KeySet<K, V> {
      KeySet() {
        super(Maps.FilteredEntryMap.this);
      }

      
      public boolean remove(Object o) {
        if (Maps.FilteredEntryMap.this.containsKey(o)) {
          Maps.FilteredEntryMap.this.unfiltered.remove(o);
          return true;
        } 
        return false;
      }

      
      public boolean removeAll(Collection<?> collection) {
        return Maps.FilteredEntryMap.removeAllKeys(Maps.FilteredEntryMap.this.unfiltered, Maps.FilteredEntryMap.this.predicate, collection);
      }

      
      public boolean retainAll(Collection<?> collection) {
        return Maps.FilteredEntryMap.retainAllKeys(Maps.FilteredEntryMap.this.unfiltered, Maps.FilteredEntryMap.this.predicate, collection);
      }


      
      public Object[] toArray() {
        return Lists.<K>newArrayList(iterator()).toArray();
      }

      
      public <T> T[] toArray(T[] array) {
        return (T[])Lists.<K>newArrayList(iterator()).toArray((Object[])array);
      }
    }
  }
  
  private static class FilteredEntrySortedMap<K, V>
    extends FilteredEntryMap<K, V>
    implements SortedMap<K, V>
  {
    FilteredEntrySortedMap(SortedMap<K, V> unfiltered, Predicate<? super Map.Entry<K, V>> entryPredicate) {
      super(unfiltered, entryPredicate);
    }
    
    SortedMap<K, V> sortedMap() {
      return (SortedMap<K, V>)this.unfiltered;
    }

    
    public SortedSet<K> keySet() {
      return (SortedSet<K>)super.keySet();
    }

    
    SortedSet<K> createKeySet() {
      return new SortedKeySet();
    }
    
    class SortedKeySet
      extends Maps.FilteredEntryMap<K, V>.KeySet
      implements SortedSet<K> {
      public Comparator<? super K> comparator() {
        return Maps.FilteredEntrySortedMap.this.sortedMap().comparator();
      }

      
      public SortedSet<K> subSet(K fromElement, K toElement) {
        return (SortedSet<K>)Maps.FilteredEntrySortedMap.this.subMap(fromElement, toElement).keySet();
      }

      
      public SortedSet<K> headSet(K toElement) {
        return (SortedSet<K>)Maps.FilteredEntrySortedMap.this.headMap(toElement).keySet();
      }

      
      public SortedSet<K> tailSet(K fromElement) {
        return (SortedSet<K>)Maps.FilteredEntrySortedMap.this.tailMap(fromElement).keySet();
      }

      
      public K first() {
        return (K)Maps.FilteredEntrySortedMap.this.firstKey();
      }

      
      public K last() {
        return (K)Maps.FilteredEntrySortedMap.this.lastKey();
      }
    }

    
    public Comparator<? super K> comparator() {
      return sortedMap().comparator();
    }


    
    public K firstKey() {
      return keySet().iterator().next();
    }

    
    public K lastKey() {
      SortedMap<K, V> headMap = sortedMap();
      
      while (true) {
        K key = headMap.lastKey();
        if (apply(key, this.unfiltered.get(key))) {
          return key;
        }
        headMap = sortedMap().headMap(key);
      } 
    }

    
    public SortedMap<K, V> headMap(K toKey) {
      return new FilteredEntrySortedMap(sortedMap().headMap(toKey), this.predicate);
    }

    
    public SortedMap<K, V> subMap(K fromKey, K toKey) {
      return new FilteredEntrySortedMap(sortedMap().subMap(fromKey, toKey), this.predicate);
    }

    
    public SortedMap<K, V> tailMap(K fromKey) {
      return new FilteredEntrySortedMap(sortedMap().tailMap(fromKey), this.predicate);
    }
  }


  
  @GwtIncompatible
  private static class FilteredEntryNavigableMap<K, V>
    extends AbstractNavigableMap<K, V>
  {
    private final NavigableMap<K, V> unfiltered;
    
    private final Predicate<? super Map.Entry<K, V>> entryPredicate;
    
    private final Map<K, V> filteredDelegate;

    
    FilteredEntryNavigableMap(NavigableMap<K, V> unfiltered, Predicate<? super Map.Entry<K, V>> entryPredicate) {
      this.unfiltered = (NavigableMap<K, V>)Preconditions.checkNotNull(unfiltered);
      this.entryPredicate = entryPredicate;
      this.filteredDelegate = new Maps.FilteredEntryMap<>(unfiltered, entryPredicate);
    }

    
    public Comparator<? super K> comparator() {
      return this.unfiltered.comparator();
    }

    
    public NavigableSet<K> navigableKeySet() {
      return new Maps.NavigableKeySet<K, V>(this)
        {
          public boolean removeAll(Collection<?> collection) {
            return Maps.FilteredEntryMap.removeAllKeys(Maps.FilteredEntryNavigableMap.this.unfiltered, Maps.FilteredEntryNavigableMap.this.entryPredicate, collection);
          }

          
          public boolean retainAll(Collection<?> collection) {
            return Maps.FilteredEntryMap.retainAllKeys(Maps.FilteredEntryNavigableMap.this.unfiltered, Maps.FilteredEntryNavigableMap.this.entryPredicate, collection);
          }
        };
    }

    
    public Collection<V> values() {
      return new Maps.FilteredMapValues<>(this, this.unfiltered, this.entryPredicate);
    }

    
    Iterator<Map.Entry<K, V>> entryIterator() {
      return Iterators.filter(this.unfiltered.entrySet().iterator(), this.entryPredicate);
    }

    
    Iterator<Map.Entry<K, V>> descendingEntryIterator() {
      return Iterators.filter(this.unfiltered.descendingMap().entrySet().iterator(), this.entryPredicate);
    }

    
    public int size() {
      return this.filteredDelegate.size();
    }

    
    public boolean isEmpty() {
      return !Iterables.any(this.unfiltered.entrySet(), this.entryPredicate);
    }

    
    public V get(Object key) {
      return this.filteredDelegate.get(key);
    }

    
    public boolean containsKey(Object key) {
      return this.filteredDelegate.containsKey(key);
    }

    
    public V put(K key, V value) {
      return this.filteredDelegate.put(key, value);
    }

    
    public V remove(Object key) {
      return this.filteredDelegate.remove(key);
    }

    
    public void putAll(Map<? extends K, ? extends V> m) {
      this.filteredDelegate.putAll(m);
    }

    
    public void clear() {
      this.filteredDelegate.clear();
    }

    
    public Set<Map.Entry<K, V>> entrySet() {
      return this.filteredDelegate.entrySet();
    }

    
    public Map.Entry<K, V> pollFirstEntry() {
      return Iterables.<Map.Entry<K, V>>removeFirstMatching(this.unfiltered.entrySet(), this.entryPredicate);
    }

    
    public Map.Entry<K, V> pollLastEntry() {
      return Iterables.<Map.Entry<K, V>>removeFirstMatching(this.unfiltered.descendingMap().entrySet(), this.entryPredicate);
    }

    
    public NavigableMap<K, V> descendingMap() {
      return Maps.filterEntries(this.unfiltered.descendingMap(), this.entryPredicate);
    }


    
    public NavigableMap<K, V> subMap(K fromKey, boolean fromInclusive, K toKey, boolean toInclusive) {
      return Maps.filterEntries(this.unfiltered
          .subMap(fromKey, fromInclusive, toKey, toInclusive), this.entryPredicate);
    }

    
    public NavigableMap<K, V> headMap(K toKey, boolean inclusive) {
      return Maps.filterEntries(this.unfiltered.headMap(toKey, inclusive), this.entryPredicate);
    }

    
    public NavigableMap<K, V> tailMap(K fromKey, boolean inclusive) {
      return Maps.filterEntries(this.unfiltered.tailMap(fromKey, inclusive), this.entryPredicate);
    }
  }
  
  static final class FilteredEntryBiMap<K, V>
    extends FilteredEntryMap<K, V> implements BiMap<K, V> {
    @RetainedWith
    private final BiMap<V, K> inverse;
    
    private static <K, V> Predicate<Map.Entry<V, K>> inversePredicate(final Predicate<? super Map.Entry<K, V>> forwardPredicate) {
      return new Predicate<Map.Entry<V, K>>()
        {
          public boolean apply(Map.Entry<V, K> input) {
            return forwardPredicate.apply(Maps.immutableEntry(input.getValue(), input.getKey()));
          }
        };
    }
    
    FilteredEntryBiMap(BiMap<K, V> delegate, Predicate<? super Map.Entry<K, V>> predicate) {
      super(delegate, predicate);
      this
        .inverse = new FilteredEntryBiMap(delegate.inverse(), inversePredicate(predicate), this);
    }

    
    private FilteredEntryBiMap(BiMap<K, V> delegate, Predicate<? super Map.Entry<K, V>> predicate, BiMap<V, K> inverse) {
      super(delegate, predicate);
      this.inverse = inverse;
    }
    
    BiMap<K, V> unfiltered() {
      return (BiMap<K, V>)this.unfiltered;
    }

    
    public V forcePut(K key, V value) {
      Preconditions.checkArgument(apply(key, value));
      return unfiltered().forcePut(key, value);
    }

    
    public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
      unfiltered()
        .replaceAll((key, value) -> this.predicate.apply(Maps.immutableEntry(key, value)) ? function.apply(key, value) : value);
    }





    
    public BiMap<V, K> inverse() {
      return this.inverse;
    }

    
    public Set<V> values() {
      return this.inverse.keySet();
    }
  }



















  
  @GwtIncompatible
  public static <K, V> NavigableMap<K, V> unmodifiableNavigableMap(NavigableMap<K, ? extends V> map) {
    Preconditions.checkNotNull(map);
    if (map instanceof UnmodifiableNavigableMap)
    {
      return (NavigableMap)map;
    }
    
    return new UnmodifiableNavigableMap<>(map);
  }


  
  private static <K, V> Map.Entry<K, V> unmodifiableOrNull(Map.Entry<K, ? extends V> entry) {
    return (entry == null) ? null : unmodifiableEntry(entry);
  }
  
  @GwtIncompatible
  static class UnmodifiableNavigableMap<K, V> extends ForwardingSortedMap<K, V> implements NavigableMap<K, V>, Serializable {
    private final NavigableMap<K, ? extends V> delegate;
    private transient UnmodifiableNavigableMap<K, V> descendingMap;
    
    UnmodifiableNavigableMap(NavigableMap<K, ? extends V> delegate) {
      this.delegate = delegate;
    }

    
    UnmodifiableNavigableMap(NavigableMap<K, ? extends V> delegate, UnmodifiableNavigableMap<K, V> descendingMap) {
      this.delegate = delegate;
      this.descendingMap = descendingMap;
    }

    
    protected SortedMap<K, V> delegate() {
      return Collections.unmodifiableSortedMap(this.delegate);
    }

    
    public Map.Entry<K, V> lowerEntry(K key) {
      return Maps.unmodifiableOrNull(this.delegate.lowerEntry(key));
    }

    
    public K lowerKey(K key) {
      return this.delegate.lowerKey(key);
    }

    
    public Map.Entry<K, V> floorEntry(K key) {
      return Maps.unmodifiableOrNull(this.delegate.floorEntry(key));
    }

    
    public K floorKey(K key) {
      return this.delegate.floorKey(key);
    }

    
    public Map.Entry<K, V> ceilingEntry(K key) {
      return Maps.unmodifiableOrNull(this.delegate.ceilingEntry(key));
    }

    
    public K ceilingKey(K key) {
      return this.delegate.ceilingKey(key);
    }

    
    public Map.Entry<K, V> higherEntry(K key) {
      return Maps.unmodifiableOrNull(this.delegate.higherEntry(key));
    }

    
    public K higherKey(K key) {
      return this.delegate.higherKey(key);
    }

    
    public Map.Entry<K, V> firstEntry() {
      return Maps.unmodifiableOrNull(this.delegate.firstEntry());
    }

    
    public Map.Entry<K, V> lastEntry() {
      return Maps.unmodifiableOrNull(this.delegate.lastEntry());
    }

    
    public final Map.Entry<K, V> pollFirstEntry() {
      throw new UnsupportedOperationException();
    }

    
    public final Map.Entry<K, V> pollLastEntry() {
      throw new UnsupportedOperationException();
    }



    
    public NavigableMap<K, V> descendingMap() {
      UnmodifiableNavigableMap<K, V> result = this.descendingMap;
      return (result == null) ? (this
        .descendingMap = new UnmodifiableNavigableMap(this.delegate.descendingMap(), this)) : result;
    }


    
    public Set<K> keySet() {
      return navigableKeySet();
    }

    
    public NavigableSet<K> navigableKeySet() {
      return Sets.unmodifiableNavigableSet(this.delegate.navigableKeySet());
    }

    
    public NavigableSet<K> descendingKeySet() {
      return Sets.unmodifiableNavigableSet(this.delegate.descendingKeySet());
    }

    
    public SortedMap<K, V> subMap(K fromKey, K toKey) {
      return subMap(fromKey, true, toKey, false);
    }


    
    public NavigableMap<K, V> subMap(K fromKey, boolean fromInclusive, K toKey, boolean toInclusive) {
      return Maps.unmodifiableNavigableMap(this.delegate
          .subMap(fromKey, fromInclusive, toKey, toInclusive));
    }

    
    public SortedMap<K, V> headMap(K toKey) {
      return headMap(toKey, false);
    }

    
    public NavigableMap<K, V> headMap(K toKey, boolean inclusive) {
      return Maps.unmodifiableNavigableMap(this.delegate.headMap(toKey, inclusive));
    }

    
    public SortedMap<K, V> tailMap(K fromKey) {
      return tailMap(fromKey, true);
    }

    
    public NavigableMap<K, V> tailMap(K fromKey, boolean inclusive) {
      return Maps.unmodifiableNavigableMap(this.delegate.tailMap(fromKey, inclusive));
    }
  }


















































  
  @GwtIncompatible
  public static <K, V> NavigableMap<K, V> synchronizedNavigableMap(NavigableMap<K, V> navigableMap) {
    return Synchronized.navigableMap(navigableMap);
  }

  
  @GwtCompatible
  static abstract class ViewCachingAbstractMap<K, V>
    extends AbstractMap<K, V>
  {
    private transient Set<Map.Entry<K, V>> entrySet;
    
    private transient Set<K> keySet;
    
    private transient Collection<V> values;

    
    abstract Set<Map.Entry<K, V>> createEntrySet();

    
    public Set<Map.Entry<K, V>> entrySet() {
      Set<Map.Entry<K, V>> result = this.entrySet;
      return (result == null) ? (this.entrySet = createEntrySet()) : result;
    }



    
    public Set<K> keySet() {
      Set<K> result = this.keySet;
      return (result == null) ? (this.keySet = createKeySet()) : result;
    }
    
    Set<K> createKeySet() {
      return new Maps.KeySet<>(this);
    }



    
    public Collection<V> values() {
      Collection<V> result = this.values;
      return (result == null) ? (this.values = createValues()) : result;
    }
    
    Collection<V> createValues() {
      return new Maps.Values<>(this);
    }
  }
  
  static abstract class IteratorBasedAbstractMap<K, V>
    extends AbstractMap<K, V> {
    public abstract int size();
    
    abstract Iterator<Map.Entry<K, V>> entryIterator();
    
    Spliterator<Map.Entry<K, V>> entrySpliterator() {
      return Spliterators.spliterator(
          entryIterator(), size(), 65);
    }

    
    public Set<Map.Entry<K, V>> entrySet() {
      return new Maps.EntrySet<K, V>()
        {
          Map<K, V> map() {
            return Maps.IteratorBasedAbstractMap.this;
          }

          
          public Iterator<Map.Entry<K, V>> iterator() {
            return Maps.IteratorBasedAbstractMap.this.entryIterator();
          }

          
          public Spliterator<Map.Entry<K, V>> spliterator() {
            return Maps.IteratorBasedAbstractMap.this.entrySpliterator();
          }

          
          public void forEach(Consumer<? super Map.Entry<K, V>> action) {
            Maps.IteratorBasedAbstractMap.this.forEachEntry(action);
          }
        };
    }
    
    void forEachEntry(Consumer<? super Map.Entry<K, V>> action) {
      entryIterator().forEachRemaining(action);
    }

    
    public void clear() {
      Iterators.clear(entryIterator());
    }
  }




  
  static <V> V safeGet(Map<?, V> map, Object key) {
    Preconditions.checkNotNull(map);
    try {
      return map.get(key);
    } catch (ClassCastException|NullPointerException e) {
      return null;
    } 
  }




  
  static boolean safeContainsKey(Map<?, ?> map, Object key) {
    Preconditions.checkNotNull(map);
    try {
      return map.containsKey(key);
    } catch (ClassCastException|NullPointerException e) {
      return false;
    } 
  }




  
  static <V> V safeRemove(Map<?, V> map, Object key) {
    Preconditions.checkNotNull(map);
    try {
      return map.remove(key);
    } catch (ClassCastException|NullPointerException e) {
      return null;
    } 
  }

  
  static boolean containsKeyImpl(Map<?, ?> map, Object key) {
    return Iterators.contains(keyIterator(map.entrySet().iterator()), key);
  }

  
  static boolean containsValueImpl(Map<?, ?> map, Object value) {
    return Iterators.contains(valueIterator(map.entrySet().iterator()), value);
  }












  
  static <K, V> boolean containsEntryImpl(Collection<Map.Entry<K, V>> c, Object o) {
    if (!(o instanceof Map.Entry)) {
      return false;
    }
    return c.contains(unmodifiableEntry((Map.Entry<?, ?>)o));
  }











  
  static <K, V> boolean removeEntryImpl(Collection<Map.Entry<K, V>> c, Object o) {
    if (!(o instanceof Map.Entry)) {
      return false;
    }
    return c.remove(unmodifiableEntry((Map.Entry<?, ?>)o));
  }

  
  static boolean equalsImpl(Map<?, ?> map, Object object) {
    if (map == object)
      return true; 
    if (object instanceof Map) {
      Map<?, ?> o = (Map<?, ?>)object;
      return map.entrySet().equals(o.entrySet());
    } 
    return false;
  }

  
  static String toStringImpl(Map<?, ?> map) {
    StringBuilder sb = Collections2.newStringBuilderForCollection(map.size()).append('{');
    boolean first = true;
    for (Map.Entry<?, ?> entry : map.entrySet()) {
      if (!first) {
        sb.append(", ");
      }
      first = false;
      sb.append(entry.getKey()).append('=').append(entry.getValue());
    } 
    return sb.append('}').toString();
  }

  
  static <K, V> void putAllImpl(Map<K, V> self, Map<? extends K, ? extends V> map) {
    for (Map.Entry<? extends K, ? extends V> entry : map.entrySet())
      self.put(entry.getKey(), entry.getValue()); 
  }
  
  static class KeySet<K, V> extends Sets.ImprovedAbstractSet<K> {
    @Weak
    final Map<K, V> map;
    
    KeySet(Map<K, V> map) {
      this.map = (Map<K, V>)Preconditions.checkNotNull(map);
    }
    
    Map<K, V> map() {
      return this.map;
    }

    
    public Iterator<K> iterator() {
      return Maps.keyIterator(map().entrySet().iterator());
    }

    
    public void forEach(Consumer<? super K> action) {
      Preconditions.checkNotNull(action);
      
      this.map.forEach((k, v) -> action.accept(k));
    }

    
    public int size() {
      return map().size();
    }

    
    public boolean isEmpty() {
      return map().isEmpty();
    }

    
    public boolean contains(Object o) {
      return map().containsKey(o);
    }

    
    public boolean remove(Object o) {
      if (contains(o)) {
        map().remove(o);
        return true;
      } 
      return false;
    }

    
    public void clear() {
      map().clear();
    }
  }
  
  static <K> K keyOrNull(Map.Entry<K, ?> entry) {
    return (entry == null) ? null : entry.getKey();
  }
  
  static <V> V valueOrNull(Map.Entry<?, V> entry) {
    return (entry == null) ? null : entry.getValue();
  }
  
  static class SortedKeySet<K, V> extends KeySet<K, V> implements SortedSet<K> {
    SortedKeySet(SortedMap<K, V> map) {
      super(map);
    }

    
    SortedMap<K, V> map() {
      return (SortedMap<K, V>)super.map();
    }

    
    public Comparator<? super K> comparator() {
      return map().comparator();
    }

    
    public SortedSet<K> subSet(K fromElement, K toElement) {
      return new SortedKeySet(map().subMap(fromElement, toElement));
    }

    
    public SortedSet<K> headSet(K toElement) {
      return new SortedKeySet(map().headMap(toElement));
    }

    
    public SortedSet<K> tailSet(K fromElement) {
      return new SortedKeySet(map().tailMap(fromElement));
    }

    
    public K first() {
      return map().firstKey();
    }

    
    public K last() {
      return map().lastKey();
    }
  }
  
  @GwtIncompatible
  static class NavigableKeySet<K, V> extends SortedKeySet<K, V> implements NavigableSet<K> {
    NavigableKeySet(NavigableMap<K, V> map) {
      super(map);
    }

    
    NavigableMap<K, V> map() {
      return (NavigableMap<K, V>)this.map;
    }

    
    public K lower(K e) {
      return map().lowerKey(e);
    }

    
    public K floor(K e) {
      return map().floorKey(e);
    }

    
    public K ceiling(K e) {
      return map().ceilingKey(e);
    }

    
    public K higher(K e) {
      return map().higherKey(e);
    }

    
    public K pollFirst() {
      return Maps.keyOrNull(map().pollFirstEntry());
    }

    
    public K pollLast() {
      return Maps.keyOrNull(map().pollLastEntry());
    }

    
    public NavigableSet<K> descendingSet() {
      return map().descendingKeySet();
    }

    
    public Iterator<K> descendingIterator() {
      return descendingSet().iterator();
    }


    
    public NavigableSet<K> subSet(K fromElement, boolean fromInclusive, K toElement, boolean toInclusive) {
      return map().subMap(fromElement, fromInclusive, toElement, toInclusive).navigableKeySet();
    }

    
    public SortedSet<K> subSet(K fromElement, K toElement) {
      return subSet(fromElement, true, toElement, false);
    }

    
    public NavigableSet<K> headSet(K toElement, boolean inclusive) {
      return map().headMap(toElement, inclusive).navigableKeySet();
    }

    
    public SortedSet<K> headSet(K toElement) {
      return headSet(toElement, false);
    }

    
    public NavigableSet<K> tailSet(K fromElement, boolean inclusive) {
      return map().tailMap(fromElement, inclusive).navigableKeySet();
    }

    
    public SortedSet<K> tailSet(K fromElement) {
      return tailSet(fromElement, true);
    } }
  
  static class Values<K, V> extends AbstractCollection<V> {
    @Weak
    final Map<K, V> map;
    
    Values(Map<K, V> map) {
      this.map = (Map<K, V>)Preconditions.checkNotNull(map);
    }
    
    final Map<K, V> map() {
      return this.map;
    }

    
    public Iterator<V> iterator() {
      return Maps.valueIterator(map().entrySet().iterator());
    }

    
    public void forEach(Consumer<? super V> action) {
      Preconditions.checkNotNull(action);
      
      this.map.forEach((k, v) -> action.accept(v));
    }

    
    public boolean remove(Object o) {
      try {
        return super.remove(o);
      } catch (UnsupportedOperationException e) {
        for (Map.Entry<K, V> entry : map().entrySet()) {
          if (Objects.equal(o, entry.getValue())) {
            map().remove(entry.getKey());
            return true;
          } 
        } 
        return false;
      } 
    }

    
    public boolean removeAll(Collection<?> c) {
      try {
        return super.removeAll((Collection)Preconditions.checkNotNull(c));
      } catch (UnsupportedOperationException e) {
        Set<K> toRemove = Sets.newHashSet();
        for (Map.Entry<K, V> entry : map().entrySet()) {
          if (c.contains(entry.getValue())) {
            toRemove.add(entry.getKey());
          }
        } 
        return map().keySet().removeAll(toRemove);
      } 
    }

    
    public boolean retainAll(Collection<?> c) {
      try {
        return super.retainAll((Collection)Preconditions.checkNotNull(c));
      } catch (UnsupportedOperationException e) {
        Set<K> toRetain = Sets.newHashSet();
        for (Map.Entry<K, V> entry : map().entrySet()) {
          if (c.contains(entry.getValue())) {
            toRetain.add(entry.getKey());
          }
        } 
        return map().keySet().retainAll(toRetain);
      } 
    }

    
    public int size() {
      return map().size();
    }

    
    public boolean isEmpty() {
      return map().isEmpty();
    }

    
    public boolean contains(Object o) {
      return map().containsValue(o);
    }

    
    public void clear() {
      map().clear();
    }
  }
  
  static abstract class EntrySet<K, V>
    extends Sets.ImprovedAbstractSet<Map.Entry<K, V>> {
    abstract Map<K, V> map();
    
    public int size() {
      return map().size();
    }

    
    public void clear() {
      map().clear();
    }

    
    public boolean contains(Object o) {
      if (o instanceof Map.Entry) {
        Map.Entry<?, ?> entry = (Map.Entry<?, ?>)o;
        Object key = entry.getKey();
        V value = Maps.safeGet(map(), key);
        return (Objects.equal(value, entry.getValue()) && (value != null || map().containsKey(key)));
      } 
      return false;
    }

    
    public boolean isEmpty() {
      return map().isEmpty();
    }

    
    public boolean remove(Object o) {
      if (contains(o)) {
        Map.Entry<?, ?> entry = (Map.Entry<?, ?>)o;
        return map().keySet().remove(entry.getKey());
      } 
      return false;
    }

    
    public boolean removeAll(Collection<?> c) {
      try {
        return super.removeAll((Collection)Preconditions.checkNotNull(c));
      } catch (UnsupportedOperationException e) {
        
        return Sets.removeAllImpl(this, c.iterator());
      } 
    }

    
    public boolean retainAll(Collection<?> c) {
      try {
        return super.retainAll((Collection)Preconditions.checkNotNull(c));
      } catch (UnsupportedOperationException e) {
        
        Set<Object> keys = Sets.newHashSetWithExpectedSize(c.size());
        for (Object o : c) {
          if (contains(o)) {
            Map.Entry<?, ?> entry = (Map.Entry<?, ?>)o;
            keys.add(entry.getKey());
          } 
        } 
        return map().keySet().retainAll(keys);
      } 
    }
  }
  
  @GwtIncompatible
  static abstract class DescendingMap<K, V>
    extends ForwardingMap<K, V> implements NavigableMap<K, V> {
    private transient Comparator<? super K> comparator;
    private transient Set<Map.Entry<K, V>> entrySet;
    private transient NavigableSet<K> navigableKeySet;
    
    protected final Map<K, V> delegate() {
      return forward();
    }




    
    public Comparator<? super K> comparator() {
      Comparator<? super K> result = this.comparator;
      if (result == null) {
        Comparator<? super K> forwardCmp = forward().comparator();
        if (forwardCmp == null) {
          forwardCmp = Ordering.natural();
        }
        result = this.comparator = reverse(forwardCmp);
      } 
      return result;
    }

    
    private static <T> Ordering<T> reverse(Comparator<T> forward) {
      return Ordering.<T>from(forward).reverse();
    }

    
    public K firstKey() {
      return forward().lastKey();
    }

    
    public K lastKey() {
      return forward().firstKey();
    }

    
    public Map.Entry<K, V> lowerEntry(K key) {
      return forward().higherEntry(key);
    }

    
    public K lowerKey(K key) {
      return forward().higherKey(key);
    }

    
    public Map.Entry<K, V> floorEntry(K key) {
      return forward().ceilingEntry(key);
    }

    
    public K floorKey(K key) {
      return forward().ceilingKey(key);
    }

    
    public Map.Entry<K, V> ceilingEntry(K key) {
      return forward().floorEntry(key);
    }

    
    public K ceilingKey(K key) {
      return forward().floorKey(key);
    }

    
    public Map.Entry<K, V> higherEntry(K key) {
      return forward().lowerEntry(key);
    }

    
    public K higherKey(K key) {
      return forward().lowerKey(key);
    }

    
    public Map.Entry<K, V> firstEntry() {
      return forward().lastEntry();
    }

    
    public Map.Entry<K, V> lastEntry() {
      return forward().firstEntry();
    }

    
    public Map.Entry<K, V> pollFirstEntry() {
      return forward().pollLastEntry();
    }

    
    public Map.Entry<K, V> pollLastEntry() {
      return forward().pollFirstEntry();
    }

    
    public NavigableMap<K, V> descendingMap() {
      return forward();
    }



    
    public Set<Map.Entry<K, V>> entrySet() {
      Set<Map.Entry<K, V>> result = this.entrySet;
      return (result == null) ? (this.entrySet = createEntrySet()) : result;
    }


    
    Set<Map.Entry<K, V>> createEntrySet() {
      class EntrySetImpl
        extends Maps.EntrySet<K, V>
      {
        Map<K, V> map() {
          return Maps.DescendingMap.this;
        }

        
        public Iterator<Map.Entry<K, V>> iterator() {
          return Maps.DescendingMap.this.entryIterator();
        }
      };
      return new EntrySetImpl();
    }

    
    public Set<K> keySet() {
      return navigableKeySet();
    }



    
    public NavigableSet<K> navigableKeySet() {
      NavigableSet<K> result = this.navigableKeySet;
      return (result == null) ? (this.navigableKeySet = new Maps.NavigableKeySet<>(this)) : result;
    }

    
    public NavigableSet<K> descendingKeySet() {
      return forward().navigableKeySet();
    }


    
    public NavigableMap<K, V> subMap(K fromKey, boolean fromInclusive, K toKey, boolean toInclusive) {
      return forward().subMap(toKey, toInclusive, fromKey, fromInclusive).descendingMap();
    }

    
    public SortedMap<K, V> subMap(K fromKey, K toKey) {
      return subMap(fromKey, true, toKey, false);
    }

    
    public NavigableMap<K, V> headMap(K toKey, boolean inclusive) {
      return forward().tailMap(toKey, inclusive).descendingMap();
    }

    
    public SortedMap<K, V> headMap(K toKey) {
      return headMap(toKey, false);
    }

    
    public NavigableMap<K, V> tailMap(K fromKey, boolean inclusive) {
      return forward().headMap(fromKey, inclusive).descendingMap();
    }

    
    public SortedMap<K, V> tailMap(K fromKey) {
      return tailMap(fromKey, true);
    }

    
    public Collection<V> values() {
      return new Maps.Values<>(this);
    }

    
    public String toString() {
      return standardToString();
    }
    abstract NavigableMap<K, V> forward();
    abstract Iterator<Map.Entry<K, V>> entryIterator(); }
  
  static <E> ImmutableMap<E, Integer> indexMap(Collection<E> list) {
    ImmutableMap.Builder<E, Integer> builder = new ImmutableMap.Builder<>(list.size());
    int i = 0;
    for (E e : list) {
      builder.put(e, Integer.valueOf(i++));
    }
    return builder.build();
  }

















  
  @Beta
  @GwtIncompatible
  public static <K extends Comparable<? super K>, V> NavigableMap<K, V> subMap(NavigableMap<K, V> map, Range<K> range) {
    if (map.comparator() != null && map
      .comparator() != Ordering.natural() && range
      .hasLowerBound() && range
      .hasUpperBound()) {
      Preconditions.checkArgument(
          (map.comparator().compare(range.lowerEndpoint(), range.upperEndpoint()) <= 0), "map is using a custom comparator which is inconsistent with the natural ordering.");
    }
    
    if (range.hasLowerBound() && range.hasUpperBound())
      return map.subMap(range
          .lowerEndpoint(), 
          (range.lowerBoundType() == BoundType.CLOSED), range
          .upperEndpoint(), 
          (range.upperBoundType() == BoundType.CLOSED)); 
    if (range.hasLowerBound())
      return map.tailMap(range.lowerEndpoint(), (range.lowerBoundType() == BoundType.CLOSED)); 
    if (range.hasUpperBound()) {
      return map.headMap(range.upperEndpoint(), (range.upperBoundType() == BoundType.CLOSED));
    }
    return (NavigableMap<K, V>)Preconditions.checkNotNull(map);
  }
  
  @FunctionalInterface
  public static interface EntryTransformer<K, V1, V2> {
    V2 transformEntry(K param1K, V1 param1V1);
  }
}
