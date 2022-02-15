package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.j2objc.annotations.RetainedWith;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Queue;
import java.util.RandomAccess;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.Spliterator;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;





























@GwtCompatible(emulated = true)
final class Synchronized
{
  static class SynchronizedObject
    implements Serializable
  {
    final Object delegate;
    final Object mutex;
    @GwtIncompatible
    private static final long serialVersionUID = 0L;
    
    SynchronizedObject(Object delegate, Object mutex) {
      this.delegate = Preconditions.checkNotNull(delegate);
      this.mutex = (mutex == null) ? this : mutex;
    }
    
    Object delegate() {
      return this.delegate;
    }



    
    public String toString() {
      synchronized (this.mutex) {
        return this.delegate.toString();
      } 
    }





    
    @GwtIncompatible
    private void writeObject(ObjectOutputStream stream) throws IOException {
      synchronized (this.mutex) {
        stream.defaultWriteObject();
      } 
    }
  }



  
  private static <E> Collection<E> collection(Collection<E> collection, Object mutex) {
    return new SynchronizedCollection<>(collection, mutex);
  }
  @VisibleForTesting
  static class SynchronizedCollection<E> extends SynchronizedObject implements Collection<E> { private static final long serialVersionUID = 0L;
    
    private SynchronizedCollection(Collection<E> delegate, Object mutex) {
      super(delegate, mutex);
    }


    
    Collection<E> delegate() {
      return (Collection<E>)super.delegate();
    }

    
    public boolean add(E e) {
      synchronized (this.mutex) {
        return delegate().add(e);
      } 
    }

    
    public boolean addAll(Collection<? extends E> c) {
      synchronized (this.mutex) {
        return delegate().addAll(c);
      } 
    }

    
    public void clear() {
      synchronized (this.mutex) {
        delegate().clear();
      } 
    }

    
    public boolean contains(Object o) {
      synchronized (this.mutex) {
        return delegate().contains(o);
      } 
    }

    
    public boolean containsAll(Collection<?> c) {
      synchronized (this.mutex) {
        return delegate().containsAll(c);
      } 
    }

    
    public boolean isEmpty() {
      synchronized (this.mutex) {
        return delegate().isEmpty();
      } 
    }

    
    public Iterator<E> iterator() {
      return delegate().iterator();
    }

    
    public Spliterator<E> spliterator() {
      synchronized (this.mutex) {
        return delegate().spliterator();
      } 
    }

    
    public Stream<E> stream() {
      synchronized (this.mutex) {
        return delegate().stream();
      } 
    }

    
    public Stream<E> parallelStream() {
      synchronized (this.mutex) {
        return delegate().parallelStream();
      } 
    }

    
    public void forEach(Consumer<? super E> action) {
      synchronized (this.mutex) {
        delegate().forEach(action);
      } 
    }

    
    public boolean remove(Object o) {
      synchronized (this.mutex) {
        return delegate().remove(o);
      } 
    }

    
    public boolean removeAll(Collection<?> c) {
      synchronized (this.mutex) {
        return delegate().removeAll(c);
      } 
    }

    
    public boolean retainAll(Collection<?> c) {
      synchronized (this.mutex) {
        return delegate().retainAll(c);
      } 
    }

    
    public boolean removeIf(Predicate<? super E> filter) {
      synchronized (this.mutex) {
        return delegate().removeIf(filter);
      } 
    }

    
    public int size() {
      synchronized (this.mutex) {
        return delegate().size();
      } 
    }

    
    public Object[] toArray() {
      synchronized (this.mutex) {
        return delegate().toArray();
      } 
    }

    
    public <T> T[] toArray(T[] a) {
      synchronized (this.mutex) {
        return delegate().toArray(a);
      } 
    } }



  
  @VisibleForTesting
  static <E> Set<E> set(Set<E> set, Object mutex) {
    return new SynchronizedSet<>(set, mutex);
  }
  
  static class SynchronizedSet<E> extends SynchronizedCollection<E> implements Set<E> { private static final long serialVersionUID = 0L;
    
    SynchronizedSet(Set<E> delegate, Object mutex) {
      super(delegate, mutex);
    }

    
    Set<E> delegate() {
      return (Set<E>)super.delegate();
    }

    
    public boolean equals(Object o) {
      if (o == this) {
        return true;
      }
      synchronized (this.mutex) {
        return delegate().equals(o);
      } 
    }

    
    public int hashCode() {
      synchronized (this.mutex) {
        return delegate().hashCode();
      } 
    } }



  
  private static <E> SortedSet<E> sortedSet(SortedSet<E> set, Object mutex) {
    return new SynchronizedSortedSet<>(set, mutex);
  }
  static class SynchronizedSortedSet<E> extends SynchronizedSet<E> implements SortedSet<E> { private static final long serialVersionUID = 0L;
    
    SynchronizedSortedSet(SortedSet<E> delegate, Object mutex) {
      super(delegate, mutex);
    }

    
    SortedSet<E> delegate() {
      return (SortedSet<E>)super.delegate();
    }

    
    public Comparator<? super E> comparator() {
      synchronized (this.mutex) {
        return delegate().comparator();
      } 
    }

    
    public SortedSet<E> subSet(E fromElement, E toElement) {
      synchronized (this.mutex) {
        return Synchronized.sortedSet(delegate().subSet(fromElement, toElement), this.mutex);
      } 
    }

    
    public SortedSet<E> headSet(E toElement) {
      synchronized (this.mutex) {
        return Synchronized.sortedSet(delegate().headSet(toElement), this.mutex);
      } 
    }

    
    public SortedSet<E> tailSet(E fromElement) {
      synchronized (this.mutex) {
        return Synchronized.sortedSet(delegate().tailSet(fromElement), this.mutex);
      } 
    }

    
    public E first() {
      synchronized (this.mutex) {
        return delegate().first();
      } 
    }

    
    public E last() {
      synchronized (this.mutex) {
        return delegate().last();
      } 
    } }



  
  private static <E> List<E> list(List<E> list, Object mutex) {
    return (list instanceof RandomAccess) ? new SynchronizedRandomAccessList<>(list, mutex) : new SynchronizedList<>(list, mutex);
  }
  
  private static class SynchronizedList<E> extends SynchronizedCollection<E> implements List<E> {
    private static final long serialVersionUID = 0L;
    
    SynchronizedList(List<E> delegate, Object mutex) {
      super(delegate, mutex);
    }

    
    List<E> delegate() {
      return (List<E>)super.delegate();
    }

    
    public void add(int index, E element) {
      synchronized (this.mutex) {
        delegate().add(index, element);
      } 
    }

    
    public boolean addAll(int index, Collection<? extends E> c) {
      synchronized (this.mutex) {
        return delegate().addAll(index, c);
      } 
    }

    
    public E get(int index) {
      synchronized (this.mutex) {
        return delegate().get(index);
      } 
    }

    
    public int indexOf(Object o) {
      synchronized (this.mutex) {
        return delegate().indexOf(o);
      } 
    }

    
    public int lastIndexOf(Object o) {
      synchronized (this.mutex) {
        return delegate().lastIndexOf(o);
      } 
    }

    
    public ListIterator<E> listIterator() {
      return delegate().listIterator();
    }

    
    public ListIterator<E> listIterator(int index) {
      return delegate().listIterator(index);
    }

    
    public E remove(int index) {
      synchronized (this.mutex) {
        return delegate().remove(index);
      } 
    }

    
    public E set(int index, E element) {
      synchronized (this.mutex) {
        return delegate().set(index, element);
      } 
    }

    
    public void replaceAll(UnaryOperator<E> operator) {
      synchronized (this.mutex) {
        delegate().replaceAll(operator);
      } 
    }

    
    public void sort(Comparator<? super E> c) {
      synchronized (this.mutex) {
        delegate().sort(c);
      } 
    }

    
    public List<E> subList(int fromIndex, int toIndex) {
      synchronized (this.mutex) {
        return Synchronized.list(delegate().subList(fromIndex, toIndex), this.mutex);
      } 
    }

    
    public boolean equals(Object o) {
      if (o == this) {
        return true;
      }
      synchronized (this.mutex) {
        return delegate().equals(o);
      } 
    }

    
    public int hashCode() {
      synchronized (this.mutex) {
        return delegate().hashCode();
      } 
    }
  }
  
  private static class SynchronizedRandomAccessList<E>
    extends SynchronizedList<E> implements RandomAccess {
    private static final long serialVersionUID = 0L;
    
    SynchronizedRandomAccessList(List<E> list, Object mutex) {
      super(list, mutex);
    }
  }


  
  static <E> Multiset<E> multiset(Multiset<E> multiset, Object mutex) {
    if (multiset instanceof SynchronizedMultiset || multiset instanceof ImmutableMultiset) {
      return multiset;
    }
    return new SynchronizedMultiset<>(multiset, mutex);
  }
  
  private static class SynchronizedMultiset<E> extends SynchronizedCollection<E> implements Multiset<E> {
    transient Set<E> elementSet;
    transient Set<Multiset.Entry<E>> entrySet;
    private static final long serialVersionUID = 0L;
    
    SynchronizedMultiset(Multiset<E> delegate, Object mutex) {
      super(delegate, mutex);
    }

    
    Multiset<E> delegate() {
      return (Multiset<E>)super.delegate();
    }

    
    public int count(Object o) {
      synchronized (this.mutex) {
        return delegate().count(o);
      } 
    }

    
    public int add(E e, int n) {
      synchronized (this.mutex) {
        return delegate().add(e, n);
      } 
    }

    
    public int remove(Object o, int n) {
      synchronized (this.mutex) {
        return delegate().remove(o, n);
      } 
    }

    
    public int setCount(E element, int count) {
      synchronized (this.mutex) {
        return delegate().setCount(element, count);
      } 
    }

    
    public boolean setCount(E element, int oldCount, int newCount) {
      synchronized (this.mutex) {
        return delegate().setCount(element, oldCount, newCount);
      } 
    }

    
    public Set<E> elementSet() {
      synchronized (this.mutex) {
        if (this.elementSet == null) {
          this.elementSet = Synchronized.typePreservingSet(delegate().elementSet(), this.mutex);
        }
        return this.elementSet;
      } 
    }

    
    public Set<Multiset.Entry<E>> entrySet() {
      synchronized (this.mutex) {
        if (this.entrySet == null) {
          this.entrySet = (Set)Synchronized.typePreservingSet((Set)delegate().entrySet(), this.mutex);
        }
        return this.entrySet;
      } 
    }

    
    public boolean equals(Object o) {
      if (o == this) {
        return true;
      }
      synchronized (this.mutex) {
        return delegate().equals(o);
      } 
    }

    
    public int hashCode() {
      synchronized (this.mutex) {
        return delegate().hashCode();
      } 
    }
  }


  
  static <K, V> Multimap<K, V> multimap(Multimap<K, V> multimap, Object mutex) {
    if (multimap instanceof SynchronizedMultimap || multimap instanceof BaseImmutableMultimap) {
      return multimap;
    }
    return new SynchronizedMultimap<>(multimap, mutex);
  }
  
  private static class SynchronizedMultimap<K, V>
    extends SynchronizedObject
    implements Multimap<K, V> {
    transient Set<K> keySet;
    transient Collection<V> valuesCollection;
    transient Collection<Map.Entry<K, V>> entries;
    transient Map<K, Collection<V>> asMap;
    transient Multiset<K> keys;
    private static final long serialVersionUID = 0L;
    
    Multimap<K, V> delegate() {
      return (Multimap<K, V>)super.delegate();
    }
    
    SynchronizedMultimap(Multimap<K, V> delegate, Object mutex) {
      super(delegate, mutex);
    }

    
    public int size() {
      synchronized (this.mutex) {
        return delegate().size();
      } 
    }

    
    public boolean isEmpty() {
      synchronized (this.mutex) {
        return delegate().isEmpty();
      } 
    }

    
    public boolean containsKey(Object key) {
      synchronized (this.mutex) {
        return delegate().containsKey(key);
      } 
    }

    
    public boolean containsValue(Object value) {
      synchronized (this.mutex) {
        return delegate().containsValue(value);
      } 
    }

    
    public boolean containsEntry(Object key, Object value) {
      synchronized (this.mutex) {
        return delegate().containsEntry(key, value);
      } 
    }

    
    public Collection<V> get(K key) {
      synchronized (this.mutex) {
        return Synchronized.typePreservingCollection(delegate().get(key), this.mutex);
      } 
    }

    
    public boolean put(K key, V value) {
      synchronized (this.mutex) {
        return delegate().put(key, value);
      } 
    }

    
    public boolean putAll(K key, Iterable<? extends V> values) {
      synchronized (this.mutex) {
        return delegate().putAll(key, values);
      } 
    }

    
    public boolean putAll(Multimap<? extends K, ? extends V> multimap) {
      synchronized (this.mutex) {
        return delegate().putAll(multimap);
      } 
    }

    
    public Collection<V> replaceValues(K key, Iterable<? extends V> values) {
      synchronized (this.mutex) {
        return delegate().replaceValues(key, values);
      } 
    }

    
    public boolean remove(Object key, Object value) {
      synchronized (this.mutex) {
        return delegate().remove(key, value);
      } 
    }

    
    public Collection<V> removeAll(Object key) {
      synchronized (this.mutex) {
        return delegate().removeAll(key);
      } 
    }

    
    public void clear() {
      synchronized (this.mutex) {
        delegate().clear();
      } 
    }

    
    public Set<K> keySet() {
      synchronized (this.mutex) {
        if (this.keySet == null) {
          this.keySet = Synchronized.typePreservingSet(delegate().keySet(), this.mutex);
        }
        return this.keySet;
      } 
    }

    
    public Collection<V> values() {
      synchronized (this.mutex) {
        if (this.valuesCollection == null) {
          this.valuesCollection = Synchronized.collection(delegate().values(), this.mutex);
        }
        return this.valuesCollection;
      } 
    }

    
    public Collection<Map.Entry<K, V>> entries() {
      synchronized (this.mutex) {
        if (this.entries == null) {
          this.entries = (Collection)Synchronized.typePreservingCollection((Collection)delegate().entries(), this.mutex);
        }
        return this.entries;
      } 
    }

    
    public void forEach(BiConsumer<? super K, ? super V> action) {
      synchronized (this.mutex) {
        delegate().forEach(action);
      } 
    }

    
    public Map<K, Collection<V>> asMap() {
      synchronized (this.mutex) {
        if (this.asMap == null) {
          this.asMap = new Synchronized.SynchronizedAsMap<>(delegate().asMap(), this.mutex);
        }
        return this.asMap;
      } 
    }

    
    public Multiset<K> keys() {
      synchronized (this.mutex) {
        if (this.keys == null) {
          this.keys = Synchronized.multiset(delegate().keys(), this.mutex);
        }
        return this.keys;
      } 
    }

    
    public boolean equals(Object o) {
      if (o == this) {
        return true;
      }
      synchronized (this.mutex) {
        return delegate().equals(o);
      } 
    }

    
    public int hashCode() {
      synchronized (this.mutex) {
        return delegate().hashCode();
      } 
    }
  }



  
  static <K, V> ListMultimap<K, V> listMultimap(ListMultimap<K, V> multimap, Object mutex) {
    if (multimap instanceof SynchronizedListMultimap || multimap instanceof BaseImmutableMultimap) {
      return multimap;
    }
    return new SynchronizedListMultimap<>(multimap, mutex);
  }
  
  private static class SynchronizedListMultimap<K, V> extends SynchronizedMultimap<K, V> implements ListMultimap<K, V> { private static final long serialVersionUID = 0L;
    
    SynchronizedListMultimap(ListMultimap<K, V> delegate, Object mutex) {
      super(delegate, mutex);
    }

    
    ListMultimap<K, V> delegate() {
      return (ListMultimap<K, V>)super.delegate();
    }

    
    public List<V> get(K key) {
      synchronized (this.mutex) {
        return Synchronized.list(delegate().get(key), this.mutex);
      } 
    }

    
    public List<V> removeAll(Object key) {
      synchronized (this.mutex) {
        return delegate().removeAll(key);
      } 
    }

    
    public List<V> replaceValues(K key, Iterable<? extends V> values) {
      synchronized (this.mutex) {
        return delegate().replaceValues(key, values);
      } 
    } }



  
  static <K, V> SetMultimap<K, V> setMultimap(SetMultimap<K, V> multimap, Object mutex) {
    if (multimap instanceof SynchronizedSetMultimap || multimap instanceof BaseImmutableMultimap) {
      return multimap;
    }
    return new SynchronizedSetMultimap<>(multimap, mutex);
  }
  
  private static class SynchronizedSetMultimap<K, V> extends SynchronizedMultimap<K, V> implements SetMultimap<K, V> {
    transient Set<Map.Entry<K, V>> entrySet;
    private static final long serialVersionUID = 0L;
    
    SynchronizedSetMultimap(SetMultimap<K, V> delegate, Object mutex) {
      super(delegate, mutex);
    }

    
    SetMultimap<K, V> delegate() {
      return (SetMultimap<K, V>)super.delegate();
    }

    
    public Set<V> get(K key) {
      synchronized (this.mutex) {
        return Synchronized.set(delegate().get(key), this.mutex);
      } 
    }

    
    public Set<V> removeAll(Object key) {
      synchronized (this.mutex) {
        return delegate().removeAll(key);
      } 
    }

    
    public Set<V> replaceValues(K key, Iterable<? extends V> values) {
      synchronized (this.mutex) {
        return delegate().replaceValues(key, values);
      } 
    }

    
    public Set<Map.Entry<K, V>> entries() {
      synchronized (this.mutex) {
        if (this.entrySet == null) {
          this.entrySet = Synchronized.set(delegate().entries(), this.mutex);
        }
        return this.entrySet;
      } 
    }
  }



  
  static <K, V> SortedSetMultimap<K, V> sortedSetMultimap(SortedSetMultimap<K, V> multimap, Object mutex) {
    if (multimap instanceof SynchronizedSortedSetMultimap) {
      return multimap;
    }
    return new SynchronizedSortedSetMultimap<>(multimap, mutex);
  }
  
  private static class SynchronizedSortedSetMultimap<K, V> extends SynchronizedSetMultimap<K, V> implements SortedSetMultimap<K, V> { private static final long serialVersionUID = 0L;
    
    SynchronizedSortedSetMultimap(SortedSetMultimap<K, V> delegate, Object mutex) {
      super(delegate, mutex);
    }

    
    SortedSetMultimap<K, V> delegate() {
      return (SortedSetMultimap<K, V>)super.delegate();
    }

    
    public SortedSet<V> get(K key) {
      synchronized (this.mutex) {
        return Synchronized.sortedSet(delegate().get(key), this.mutex);
      } 
    }

    
    public SortedSet<V> removeAll(Object key) {
      synchronized (this.mutex) {
        return delegate().removeAll(key);
      } 
    }

    
    public SortedSet<V> replaceValues(K key, Iterable<? extends V> values) {
      synchronized (this.mutex) {
        return delegate().replaceValues(key, values);
      } 
    }

    
    public Comparator<? super V> valueComparator() {
      synchronized (this.mutex) {
        return delegate().valueComparator();
      } 
    } }




  
  private static <E> Collection<E> typePreservingCollection(Collection<E> collection, Object mutex) {
    if (collection instanceof SortedSet) {
      return sortedSet((SortedSet<E>)collection, mutex);
    }
    if (collection instanceof Set) {
      return set((Set<E>)collection, mutex);
    }
    if (collection instanceof List) {
      return list((List<E>)collection, mutex);
    }
    return collection(collection, mutex);
  }
  
  private static <E> Set<E> typePreservingSet(Set<E> set, Object mutex) {
    if (set instanceof SortedSet) {
      return sortedSet((SortedSet<E>)set, mutex);
    }
    return set(set, mutex);
  }
  
  private static class SynchronizedAsMapEntries<K, V> extends SynchronizedSet<Map.Entry<K, Collection<V>>> {
    private static final long serialVersionUID = 0L;
    
    SynchronizedAsMapEntries(Set<Map.Entry<K, Collection<V>>> delegate, Object mutex) {
      super(delegate, mutex);
    }


    
    public Iterator<Map.Entry<K, Collection<V>>> iterator() {
      return new TransformedIterator<Map.Entry<K, Collection<V>>, Map.Entry<K, Collection<V>>>(super
          .iterator())
        {
          Map.Entry<K, Collection<V>> transform(final Map.Entry<K, Collection<V>> entry) {
            return (Map.Entry)new ForwardingMapEntry<K, Collection<Collection<V>>>()
              {
                protected Map.Entry<K, Collection<V>> delegate() {
                  return entry;
                }

                
                public Collection<V> getValue() {
                  return Synchronized.typePreservingCollection((Collection)entry.getValue(), Synchronized.SynchronizedAsMapEntries.this.mutex);
                }
              };
          }
        };
    }



    
    public Object[] toArray() {
      synchronized (this.mutex) {
        return ObjectArrays.toArrayImpl(delegate());
      } 
    }

    
    public <T> T[] toArray(T[] array) {
      synchronized (this.mutex) {
        return ObjectArrays.toArrayImpl(delegate(), array);
      } 
    }

    
    public boolean contains(Object o) {
      synchronized (this.mutex) {
        return Maps.containsEntryImpl(delegate(), o);
      } 
    }

    
    public boolean containsAll(Collection<?> c) {
      synchronized (this.mutex) {
        return Collections2.containsAllImpl(delegate(), c);
      } 
    }

    
    public boolean equals(Object o) {
      if (o == this) {
        return true;
      }
      synchronized (this.mutex) {
        return Sets.equalsImpl(delegate(), o);
      } 
    }

    
    public boolean remove(Object o) {
      synchronized (this.mutex) {
        return Maps.removeEntryImpl(delegate(), o);
      } 
    }

    
    public boolean removeAll(Collection<?> c) {
      synchronized (this.mutex) {
        return Iterators.removeAll(delegate().iterator(), c);
      } 
    }

    
    public boolean retainAll(Collection<?> c) {
      synchronized (this.mutex) {
        return Iterators.retainAll(delegate().iterator(), c);
      } 
    }
  }


  
  @VisibleForTesting
  static <K, V> Map<K, V> map(Map<K, V> map, Object mutex) {
    return new SynchronizedMap<>(map, mutex);
  }
  
  private static class SynchronizedMap<K, V> extends SynchronizedObject implements Map<K, V> { transient Set<K> keySet;
    transient Collection<V> values;
    transient Set<Map.Entry<K, V>> entrySet;
    private static final long serialVersionUID = 0L;
    
    SynchronizedMap(Map<K, V> delegate, Object mutex) {
      super(delegate, mutex);
    }


    
    Map<K, V> delegate() {
      return (Map<K, V>)super.delegate();
    }

    
    public void clear() {
      synchronized (this.mutex) {
        delegate().clear();
      } 
    }

    
    public boolean containsKey(Object key) {
      synchronized (this.mutex) {
        return delegate().containsKey(key);
      } 
    }

    
    public boolean containsValue(Object value) {
      synchronized (this.mutex) {
        return delegate().containsValue(value);
      } 
    }

    
    public Set<Map.Entry<K, V>> entrySet() {
      synchronized (this.mutex) {
        if (this.entrySet == null) {
          this.entrySet = Synchronized.set(delegate().entrySet(), this.mutex);
        }
        return this.entrySet;
      } 
    }

    
    public void forEach(BiConsumer<? super K, ? super V> action) {
      synchronized (this.mutex) {
        delegate().forEach(action);
      } 
    }

    
    public V get(Object key) {
      synchronized (this.mutex) {
        return delegate().get(key);
      } 
    }

    
    public V getOrDefault(Object key, V defaultValue) {
      synchronized (this.mutex) {
        return delegate().getOrDefault(key, defaultValue);
      } 
    }

    
    public boolean isEmpty() {
      synchronized (this.mutex) {
        return delegate().isEmpty();
      } 
    }

    
    public Set<K> keySet() {
      synchronized (this.mutex) {
        if (this.keySet == null) {
          this.keySet = Synchronized.set(delegate().keySet(), this.mutex);
        }
        return this.keySet;
      } 
    }

    
    public V put(K key, V value) {
      synchronized (this.mutex) {
        return delegate().put(key, value);
      } 
    }

    
    public V putIfAbsent(K key, V value) {
      synchronized (this.mutex) {
        return delegate().putIfAbsent(key, value);
      } 
    }

    
    public boolean replace(K key, V oldValue, V newValue) {
      synchronized (this.mutex) {
        return delegate().replace(key, oldValue, newValue);
      } 
    }

    
    public V replace(K key, V value) {
      synchronized (this.mutex) {
        return delegate().replace(key, value);
      } 
    }

    
    public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
      synchronized (this.mutex) {
        return delegate().computeIfAbsent(key, mappingFunction);
      } 
    }


    
    public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
      synchronized (this.mutex) {
        return delegate().computeIfPresent(key, remappingFunction);
      } 
    }

    
    public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
      synchronized (this.mutex) {
        return delegate().compute(key, remappingFunction);
      } 
    }


    
    public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
      synchronized (this.mutex) {
        return delegate().merge(key, value, remappingFunction);
      } 
    }

    
    public void putAll(Map<? extends K, ? extends V> map) {
      synchronized (this.mutex) {
        delegate().putAll(map);
      } 
    }

    
    public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
      synchronized (this.mutex) {
        delegate().replaceAll(function);
      } 
    }

    
    public V remove(Object key) {
      synchronized (this.mutex) {
        return delegate().remove(key);
      } 
    }

    
    public boolean remove(Object key, Object value) {
      synchronized (this.mutex) {
        return delegate().remove(key, value);
      } 
    }

    
    public int size() {
      synchronized (this.mutex) {
        return delegate().size();
      } 
    }

    
    public Collection<V> values() {
      synchronized (this.mutex) {
        if (this.values == null) {
          this.values = Synchronized.collection(delegate().values(), this.mutex);
        }
        return this.values;
      } 
    }

    
    public boolean equals(Object o) {
      if (o == this) {
        return true;
      }
      synchronized (this.mutex) {
        return delegate().equals(o);
      } 
    }

    
    public int hashCode() {
      synchronized (this.mutex) {
        return delegate().hashCode();
      } 
    } }



  
  static <K, V> SortedMap<K, V> sortedMap(SortedMap<K, V> sortedMap, Object mutex) {
    return new SynchronizedSortedMap<>(sortedMap, mutex);
  }
  
  static class SynchronizedSortedMap<K, V> extends SynchronizedMap<K, V> implements SortedMap<K, V> {
    private static final long serialVersionUID = 0L;
    
    SynchronizedSortedMap(SortedMap<K, V> delegate, Object mutex) {
      super(delegate, mutex);
    }

    
    SortedMap<K, V> delegate() {
      return (SortedMap<K, V>)super.delegate();
    }

    
    public Comparator<? super K> comparator() {
      synchronized (this.mutex) {
        return delegate().comparator();
      } 
    }

    
    public K firstKey() {
      synchronized (this.mutex) {
        return delegate().firstKey();
      } 
    }

    
    public SortedMap<K, V> headMap(K toKey) {
      synchronized (this.mutex) {
        return Synchronized.sortedMap(delegate().headMap(toKey), this.mutex);
      } 
    }

    
    public K lastKey() {
      synchronized (this.mutex) {
        return delegate().lastKey();
      } 
    }

    
    public SortedMap<K, V> subMap(K fromKey, K toKey) {
      synchronized (this.mutex) {
        return Synchronized.sortedMap(delegate().subMap(fromKey, toKey), this.mutex);
      } 
    }

    
    public SortedMap<K, V> tailMap(K fromKey) {
      synchronized (this.mutex) {
        return Synchronized.sortedMap(delegate().tailMap(fromKey), this.mutex);
      } 
    }
  }


  
  static <K, V> BiMap<K, V> biMap(BiMap<K, V> bimap, Object mutex) {
    if (bimap instanceof SynchronizedBiMap || bimap instanceof ImmutableBiMap) {
      return bimap;
    }
    return new SynchronizedBiMap<>(bimap, mutex, null);
  }
  
  @VisibleForTesting
  static class SynchronizedBiMap<K, V> extends SynchronizedMap<K, V> implements BiMap<K, V>, Serializable {
    private transient Set<V> valueSet;
    @RetainedWith
    private transient BiMap<V, K> inverse;
    private static final long serialVersionUID = 0L;
    
    private SynchronizedBiMap(BiMap<K, V> delegate, Object mutex, BiMap<V, K> inverse) {
      super(delegate, mutex);
      this.inverse = inverse;
    }

    
    BiMap<K, V> delegate() {
      return (BiMap<K, V>)super.delegate();
    }

    
    public Set<V> values() {
      synchronized (this.mutex) {
        if (this.valueSet == null) {
          this.valueSet = Synchronized.set(delegate().values(), this.mutex);
        }
        return this.valueSet;
      } 
    }

    
    public V forcePut(K key, V value) {
      synchronized (this.mutex) {
        return delegate().forcePut(key, value);
      } 
    }

    
    public BiMap<V, K> inverse() {
      synchronized (this.mutex) {
        if (this.inverse == null) {
          this.inverse = new SynchronizedBiMap(delegate().inverse(), this.mutex, this);
        }
        return this.inverse;
      } 
    }
  }
  
  private static class SynchronizedAsMap<K, V>
    extends SynchronizedMap<K, Collection<V>> {
    transient Set<Map.Entry<K, Collection<V>>> asMapEntrySet;
    transient Collection<Collection<V>> asMapValues;
    private static final long serialVersionUID = 0L;
    
    SynchronizedAsMap(Map<K, Collection<V>> delegate, Object mutex) {
      super(delegate, mutex);
    }

    
    public Collection<V> get(Object key) {
      synchronized (this.mutex) {
        Collection<V> collection = super.get(key);
        return (collection == null) ? null : Synchronized.typePreservingCollection(collection, this.mutex);
      } 
    }

    
    public Set<Map.Entry<K, Collection<V>>> entrySet() {
      synchronized (this.mutex) {
        if (this.asMapEntrySet == null) {
          this.asMapEntrySet = new Synchronized.SynchronizedAsMapEntries<>(delegate().entrySet(), this.mutex);
        }
        return this.asMapEntrySet;
      } 
    }

    
    public Collection<Collection<V>> values() {
      synchronized (this.mutex) {
        if (this.asMapValues == null) {
          this.asMapValues = new Synchronized.SynchronizedAsMapValues<>(delegate().values(), this.mutex);
        }
        return this.asMapValues;
      } 
    }


    
    public boolean containsValue(Object o) {
      return values().contains(o);
    }
  }
  
  private static class SynchronizedAsMapValues<V> extends SynchronizedCollection<Collection<V>> {
    private static final long serialVersionUID = 0L;
    
    SynchronizedAsMapValues(Collection<Collection<V>> delegate, Object mutex) {
      super(delegate, mutex);
    }


    
    public Iterator<Collection<V>> iterator() {
      return new TransformedIterator<Collection<V>, Collection<V>>(super.iterator())
        {
          Collection<V> transform(Collection<V> from) {
            return Synchronized.typePreservingCollection(from, Synchronized.SynchronizedAsMapValues.this.mutex);
          }
        };
    }
  }
  
  @GwtIncompatible
  @VisibleForTesting
  static class SynchronizedNavigableSet<E> extends SynchronizedSortedSet<E> implements NavigableSet<E> {
    transient NavigableSet<E> descendingSet;
    private static final long serialVersionUID = 0L;
    
    SynchronizedNavigableSet(NavigableSet<E> delegate, Object mutex) {
      super(delegate, mutex);
    }

    
    NavigableSet<E> delegate() {
      return (NavigableSet<E>)super.delegate();
    }

    
    public E ceiling(E e) {
      synchronized (this.mutex) {
        return delegate().ceiling(e);
      } 
    }

    
    public Iterator<E> descendingIterator() {
      return delegate().descendingIterator();
    }



    
    public NavigableSet<E> descendingSet() {
      synchronized (this.mutex) {
        if (this.descendingSet == null) {
          NavigableSet<E> dS = Synchronized.navigableSet(delegate().descendingSet(), this.mutex);
          this.descendingSet = dS;
          return dS;
        } 
        return this.descendingSet;
      } 
    }

    
    public E floor(E e) {
      synchronized (this.mutex) {
        return delegate().floor(e);
      } 
    }

    
    public NavigableSet<E> headSet(E toElement, boolean inclusive) {
      synchronized (this.mutex) {
        return Synchronized.navigableSet(delegate().headSet(toElement, inclusive), this.mutex);
      } 
    }

    
    public SortedSet<E> headSet(E toElement) {
      return headSet(toElement, false);
    }

    
    public E higher(E e) {
      synchronized (this.mutex) {
        return delegate().higher(e);
      } 
    }

    
    public E lower(E e) {
      synchronized (this.mutex) {
        return delegate().lower(e);
      } 
    }

    
    public E pollFirst() {
      synchronized (this.mutex) {
        return delegate().pollFirst();
      } 
    }

    
    public E pollLast() {
      synchronized (this.mutex) {
        return delegate().pollLast();
      } 
    }


    
    public NavigableSet<E> subSet(E fromElement, boolean fromInclusive, E toElement, boolean toInclusive) {
      synchronized (this.mutex) {
        return Synchronized.navigableSet(
            delegate().subSet(fromElement, fromInclusive, toElement, toInclusive), this.mutex);
      } 
    }

    
    public SortedSet<E> subSet(E fromElement, E toElement) {
      return subSet(fromElement, true, toElement, false);
    }

    
    public NavigableSet<E> tailSet(E fromElement, boolean inclusive) {
      synchronized (this.mutex) {
        return Synchronized.navigableSet(delegate().tailSet(fromElement, inclusive), this.mutex);
      } 
    }

    
    public SortedSet<E> tailSet(E fromElement) {
      return tailSet(fromElement, true);
    }
  }


  
  @GwtIncompatible
  static <E> NavigableSet<E> navigableSet(NavigableSet<E> navigableSet, Object mutex) {
    return new SynchronizedNavigableSet<>(navigableSet, mutex);
  }
  
  @GwtIncompatible
  static <E> NavigableSet<E> navigableSet(NavigableSet<E> navigableSet) {
    return navigableSet(navigableSet, null);
  }
  
  @GwtIncompatible
  static <K, V> NavigableMap<K, V> navigableMap(NavigableMap<K, V> navigableMap) {
    return navigableMap(navigableMap, null);
  }

  
  @GwtIncompatible
  static <K, V> NavigableMap<K, V> navigableMap(NavigableMap<K, V> navigableMap, Object mutex) {
    return new SynchronizedNavigableMap<>(navigableMap, mutex);
  }
  
  @GwtIncompatible
  @VisibleForTesting
  static class SynchronizedNavigableMap<K, V> extends SynchronizedSortedMap<K, V> implements NavigableMap<K, V> { transient NavigableSet<K> descendingKeySet;
    transient NavigableMap<K, V> descendingMap;
    
    SynchronizedNavigableMap(NavigableMap<K, V> delegate, Object mutex) {
      super(delegate, mutex);
    }
    transient NavigableSet<K> navigableKeySet; private static final long serialVersionUID = 0L;
    
    NavigableMap<K, V> delegate() {
      return (NavigableMap<K, V>)super.delegate();
    }

    
    public Map.Entry<K, V> ceilingEntry(K key) {
      synchronized (this.mutex) {
        return Synchronized.nullableSynchronizedEntry(delegate().ceilingEntry(key), this.mutex);
      } 
    }

    
    public K ceilingKey(K key) {
      synchronized (this.mutex) {
        return delegate().ceilingKey(key);
      } 
    }



    
    public NavigableSet<K> descendingKeySet() {
      synchronized (this.mutex) {
        if (this.descendingKeySet == null) {
          return this.descendingKeySet = Synchronized.navigableSet(delegate().descendingKeySet(), this.mutex);
        }
        return this.descendingKeySet;
      } 
    }



    
    public NavigableMap<K, V> descendingMap() {
      synchronized (this.mutex) {
        if (this.descendingMap == null) {
          return this.descendingMap = Synchronized.<K, V>navigableMap(delegate().descendingMap(), this.mutex);
        }
        return this.descendingMap;
      } 
    }

    
    public Map.Entry<K, V> firstEntry() {
      synchronized (this.mutex) {
        return Synchronized.nullableSynchronizedEntry(delegate().firstEntry(), this.mutex);
      } 
    }

    
    public Map.Entry<K, V> floorEntry(K key) {
      synchronized (this.mutex) {
        return Synchronized.nullableSynchronizedEntry(delegate().floorEntry(key), this.mutex);
      } 
    }

    
    public K floorKey(K key) {
      synchronized (this.mutex) {
        return delegate().floorKey(key);
      } 
    }

    
    public NavigableMap<K, V> headMap(K toKey, boolean inclusive) {
      synchronized (this.mutex) {
        return Synchronized.navigableMap(delegate().headMap(toKey, inclusive), this.mutex);
      } 
    }

    
    public SortedMap<K, V> headMap(K toKey) {
      return headMap(toKey, false);
    }

    
    public Map.Entry<K, V> higherEntry(K key) {
      synchronized (this.mutex) {
        return Synchronized.nullableSynchronizedEntry(delegate().higherEntry(key), this.mutex);
      } 
    }

    
    public K higherKey(K key) {
      synchronized (this.mutex) {
        return delegate().higherKey(key);
      } 
    }

    
    public Map.Entry<K, V> lastEntry() {
      synchronized (this.mutex) {
        return Synchronized.nullableSynchronizedEntry(delegate().lastEntry(), this.mutex);
      } 
    }

    
    public Map.Entry<K, V> lowerEntry(K key) {
      synchronized (this.mutex) {
        return Synchronized.nullableSynchronizedEntry(delegate().lowerEntry(key), this.mutex);
      } 
    }

    
    public K lowerKey(K key) {
      synchronized (this.mutex) {
        return delegate().lowerKey(key);
      } 
    }

    
    public Set<K> keySet() {
      return navigableKeySet();
    }



    
    public NavigableSet<K> navigableKeySet() {
      synchronized (this.mutex) {
        if (this.navigableKeySet == null) {
          return this.navigableKeySet = Synchronized.navigableSet(delegate().navigableKeySet(), this.mutex);
        }
        return this.navigableKeySet;
      } 
    }

    
    public Map.Entry<K, V> pollFirstEntry() {
      synchronized (this.mutex) {
        return Synchronized.nullableSynchronizedEntry(delegate().pollFirstEntry(), this.mutex);
      } 
    }

    
    public Map.Entry<K, V> pollLastEntry() {
      synchronized (this.mutex) {
        return Synchronized.nullableSynchronizedEntry(delegate().pollLastEntry(), this.mutex);
      } 
    }


    
    public NavigableMap<K, V> subMap(K fromKey, boolean fromInclusive, K toKey, boolean toInclusive) {
      synchronized (this.mutex) {
        return Synchronized.navigableMap(delegate().subMap(fromKey, fromInclusive, toKey, toInclusive), this.mutex);
      } 
    }

    
    public SortedMap<K, V> subMap(K fromKey, K toKey) {
      return subMap(fromKey, true, toKey, false);
    }

    
    public NavigableMap<K, V> tailMap(K fromKey, boolean inclusive) {
      synchronized (this.mutex) {
        return Synchronized.navigableMap(delegate().tailMap(fromKey, inclusive), this.mutex);
      } 
    }

    
    public SortedMap<K, V> tailMap(K fromKey) {
      return tailMap(fromKey, true);
    } }




  
  @GwtIncompatible
  private static <K, V> Map.Entry<K, V> nullableSynchronizedEntry(Map.Entry<K, V> entry, Object mutex) {
    if (entry == null) {
      return null;
    }
    return new SynchronizedEntry<>(entry, mutex);
  }
  
  @GwtIncompatible
  private static class SynchronizedEntry<K, V> extends SynchronizedObject implements Map.Entry<K, V> { private static final long serialVersionUID = 0L;
    
    SynchronizedEntry(Map.Entry<K, V> delegate, Object mutex) {
      super(delegate, mutex);
    }


    
    Map.Entry<K, V> delegate() {
      return (Map.Entry<K, V>)super.delegate();
    }

    
    public boolean equals(Object obj) {
      synchronized (this.mutex) {
        return delegate().equals(obj);
      } 
    }

    
    public int hashCode() {
      synchronized (this.mutex) {
        return delegate().hashCode();
      } 
    }

    
    public K getKey() {
      synchronized (this.mutex) {
        return delegate().getKey();
      } 
    }

    
    public V getValue() {
      synchronized (this.mutex) {
        return delegate().getValue();
      } 
    }

    
    public V setValue(V value) {
      synchronized (this.mutex) {
        return delegate().setValue(value);
      } 
    } }



  
  static <E> Queue<E> queue(Queue<E> queue, Object mutex) {
    return (queue instanceof SynchronizedQueue) ? queue : new SynchronizedQueue<>(queue, mutex);
  }
  
  private static class SynchronizedQueue<E> extends SynchronizedCollection<E> implements Queue<E> { private static final long serialVersionUID = 0L;
    
    SynchronizedQueue(Queue<E> delegate, Object mutex) {
      super(delegate, mutex);
    }

    
    Queue<E> delegate() {
      return (Queue<E>)super.delegate();
    }

    
    public E element() {
      synchronized (this.mutex) {
        return delegate().element();
      } 
    }

    
    public boolean offer(E e) {
      synchronized (this.mutex) {
        return delegate().offer(e);
      } 
    }

    
    public E peek() {
      synchronized (this.mutex) {
        return delegate().peek();
      } 
    }

    
    public E poll() {
      synchronized (this.mutex) {
        return delegate().poll();
      } 
    }

    
    public E remove() {
      synchronized (this.mutex) {
        return delegate().remove();
      } 
    } }



  
  static <E> Deque<E> deque(Deque<E> deque, Object mutex) {
    return new SynchronizedDeque<>(deque, mutex);
  }
  
  private static final class SynchronizedDeque<E> extends SynchronizedQueue<E> implements Deque<E> { private static final long serialVersionUID = 0L;
    
    SynchronizedDeque(Deque<E> delegate, Object mutex) {
      super(delegate, mutex);
    }

    
    Deque<E> delegate() {
      return (Deque<E>)super.delegate();
    }

    
    public void addFirst(E e) {
      synchronized (this.mutex) {
        delegate().addFirst(e);
      } 
    }

    
    public void addLast(E e) {
      synchronized (this.mutex) {
        delegate().addLast(e);
      } 
    }

    
    public boolean offerFirst(E e) {
      synchronized (this.mutex) {
        return delegate().offerFirst(e);
      } 
    }

    
    public boolean offerLast(E e) {
      synchronized (this.mutex) {
        return delegate().offerLast(e);
      } 
    }

    
    public E removeFirst() {
      synchronized (this.mutex) {
        return delegate().removeFirst();
      } 
    }

    
    public E removeLast() {
      synchronized (this.mutex) {
        return delegate().removeLast();
      } 
    }

    
    public E pollFirst() {
      synchronized (this.mutex) {
        return delegate().pollFirst();
      } 
    }

    
    public E pollLast() {
      synchronized (this.mutex) {
        return delegate().pollLast();
      } 
    }

    
    public E getFirst() {
      synchronized (this.mutex) {
        return delegate().getFirst();
      } 
    }

    
    public E getLast() {
      synchronized (this.mutex) {
        return delegate().getLast();
      } 
    }

    
    public E peekFirst() {
      synchronized (this.mutex) {
        return delegate().peekFirst();
      } 
    }

    
    public E peekLast() {
      synchronized (this.mutex) {
        return delegate().peekLast();
      } 
    }

    
    public boolean removeFirstOccurrence(Object o) {
      synchronized (this.mutex) {
        return delegate().removeFirstOccurrence(o);
      } 
    }

    
    public boolean removeLastOccurrence(Object o) {
      synchronized (this.mutex) {
        return delegate().removeLastOccurrence(o);
      } 
    }

    
    public void push(E e) {
      synchronized (this.mutex) {
        delegate().push(e);
      } 
    }

    
    public E pop() {
      synchronized (this.mutex) {
        return delegate().pop();
      } 
    }

    
    public Iterator<E> descendingIterator() {
      synchronized (this.mutex) {
        return delegate().descendingIterator();
      } 
    } }



  
  static <R, C, V> Table<R, C, V> table(Table<R, C, V> table, Object mutex) {
    return new SynchronizedTable<>(table, mutex);
  }
  
  private static final class SynchronizedTable<R, C, V>
    extends SynchronizedObject
    implements Table<R, C, V> {
    SynchronizedTable(Table<R, C, V> delegate, Object mutex) {
      super(delegate, mutex);
    }


    
    Table<R, C, V> delegate() {
      return (Table<R, C, V>)super.delegate();
    }

    
    public boolean contains(Object rowKey, Object columnKey) {
      synchronized (this.mutex) {
        return delegate().contains(rowKey, columnKey);
      } 
    }

    
    public boolean containsRow(Object rowKey) {
      synchronized (this.mutex) {
        return delegate().containsRow(rowKey);
      } 
    }

    
    public boolean containsColumn(Object columnKey) {
      synchronized (this.mutex) {
        return delegate().containsColumn(columnKey);
      } 
    }

    
    public boolean containsValue(Object value) {
      synchronized (this.mutex) {
        return delegate().containsValue(value);
      } 
    }

    
    public V get(Object rowKey, Object columnKey) {
      synchronized (this.mutex) {
        return delegate().get(rowKey, columnKey);
      } 
    }

    
    public boolean isEmpty() {
      synchronized (this.mutex) {
        return delegate().isEmpty();
      } 
    }

    
    public int size() {
      synchronized (this.mutex) {
        return delegate().size();
      } 
    }

    
    public void clear() {
      synchronized (this.mutex) {
        delegate().clear();
      } 
    }

    
    public V put(R rowKey, C columnKey, V value) {
      synchronized (this.mutex) {
        return delegate().put(rowKey, columnKey, value);
      } 
    }

    
    public void putAll(Table<? extends R, ? extends C, ? extends V> table) {
      synchronized (this.mutex) {
        delegate().putAll(table);
      } 
    }

    
    public V remove(Object rowKey, Object columnKey) {
      synchronized (this.mutex) {
        return delegate().remove(rowKey, columnKey);
      } 
    }

    
    public Map<C, V> row(R rowKey) {
      synchronized (this.mutex) {
        return Synchronized.map(delegate().row(rowKey), this.mutex);
      } 
    }

    
    public Map<R, V> column(C columnKey) {
      synchronized (this.mutex) {
        return Synchronized.map(delegate().column(columnKey), this.mutex);
      } 
    }

    
    public Set<Table.Cell<R, C, V>> cellSet() {
      synchronized (this.mutex) {
        return Synchronized.set(delegate().cellSet(), this.mutex);
      } 
    }

    
    public Set<R> rowKeySet() {
      synchronized (this.mutex) {
        return Synchronized.set(delegate().rowKeySet(), this.mutex);
      } 
    }

    
    public Set<C> columnKeySet() {
      synchronized (this.mutex) {
        return Synchronized.set(delegate().columnKeySet(), this.mutex);
      } 
    }

    
    public Collection<V> values() {
      synchronized (this.mutex) {
        return Synchronized.collection(delegate().values(), this.mutex);
      } 
    }

    
    public Map<R, Map<C, V>> rowMap() {
      synchronized (this.mutex) {
        return Synchronized.map(
            Maps.transformValues(
              delegate().rowMap(), new Function<Map<C, V>, Map<C, V>>()
              {
                public Map<C, V> apply(Map<C, V> t)
                {
                  return Synchronized.map(t, Synchronized.SynchronizedTable.this.mutex);
                }
              }), this.mutex);
      } 
    }


    
    public Map<C, Map<R, V>> columnMap() {
      synchronized (this.mutex) {
        return Synchronized.map(
            Maps.transformValues(
              delegate().columnMap(), new Function<Map<R, V>, Map<R, V>>()
              {
                public Map<R, V> apply(Map<R, V> t)
                {
                  return Synchronized.map(t, Synchronized.SynchronizedTable.this.mutex);
                }
              }), this.mutex);
      } 
    }


    
    public int hashCode() {
      synchronized (this.mutex) {
        return delegate().hashCode();
      } 
    }

    
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }
      synchronized (this.mutex) {
        return delegate().equals(obj);
      } 
    }
  }
}
