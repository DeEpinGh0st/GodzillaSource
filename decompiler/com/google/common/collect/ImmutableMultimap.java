package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.j2objc.annotations.Weak;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.BiConsumer;













































@GwtCompatible(emulated = true)
public abstract class ImmutableMultimap<K, V>
  extends BaseImmutableMultimap<K, V>
  implements Serializable
{
  final transient ImmutableMap<K, ? extends ImmutableCollection<V>> map;
  final transient int size;
  private static final long serialVersionUID = 0L;
  
  public static <K, V> ImmutableMultimap<K, V> of() {
    return ImmutableListMultimap.of();
  }

  
  public static <K, V> ImmutableMultimap<K, V> of(K k1, V v1) {
    return ImmutableListMultimap.of(k1, v1);
  }

  
  public static <K, V> ImmutableMultimap<K, V> of(K k1, V v1, K k2, V v2) {
    return ImmutableListMultimap.of(k1, v1, k2, v2);
  }




  
  public static <K, V> ImmutableMultimap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3) {
    return ImmutableListMultimap.of(k1, v1, k2, v2, k3, v3);
  }




  
  public static <K, V> ImmutableMultimap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4) {
    return ImmutableListMultimap.of(k1, v1, k2, v2, k3, v3, k4, v4);
  }





  
  public static <K, V> ImmutableMultimap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5) {
    return ImmutableListMultimap.of(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5);
  }






  
  public static <K, V> Builder<K, V> builder() {
    return new Builder<>();
  }



























  
  public static class Builder<K, V>
  {
    Map<K, Collection<V>> builderMap = Platform.preservesInsertionOrderOnPutsMap(); Comparator<? super K> keyComparator;
    Comparator<? super V> valueComparator;
    
    Collection<V> newMutableValueCollection() {
      return new ArrayList<>();
    }

    
    @CanIgnoreReturnValue
    public Builder<K, V> put(K key, V value) {
      CollectPreconditions.checkEntryNotNull(key, value);
      Collection<V> valueCollection = this.builderMap.get(key);
      if (valueCollection == null) {
        this.builderMap.put(key, valueCollection = newMutableValueCollection());
      }
      valueCollection.add(value);
      return this;
    }





    
    @CanIgnoreReturnValue
    public Builder<K, V> put(Map.Entry<? extends K, ? extends V> entry) {
      return put(entry.getKey(), entry.getValue());
    }





    
    @CanIgnoreReturnValue
    @Beta
    public Builder<K, V> putAll(Iterable<? extends Map.Entry<? extends K, ? extends V>> entries) {
      for (Map.Entry<? extends K, ? extends V> entry : entries) {
        put(entry);
      }
      return this;
    }






    
    @CanIgnoreReturnValue
    public Builder<K, V> putAll(K key, Iterable<? extends V> values) {
      if (key == null) {
        throw new NullPointerException("null key in entry: null=" + Iterables.toString(values));
      }
      Collection<V> valueCollection = this.builderMap.get(key);
      if (valueCollection != null) {
        for (V value : values) {
          CollectPreconditions.checkEntryNotNull(key, value);
          valueCollection.add(value);
        } 
        return this;
      } 
      Iterator<? extends V> valuesItr = values.iterator();
      if (!valuesItr.hasNext()) {
        return this;
      }
      valueCollection = newMutableValueCollection();
      while (valuesItr.hasNext()) {
        V value = valuesItr.next();
        CollectPreconditions.checkEntryNotNull(key, value);
        valueCollection.add(value);
      } 
      this.builderMap.put(key, valueCollection);
      return this;
    }






    
    @CanIgnoreReturnValue
    public Builder<K, V> putAll(K key, V... values) {
      return putAll(key, Arrays.asList(values));
    }









    
    @CanIgnoreReturnValue
    public Builder<K, V> putAll(Multimap<? extends K, ? extends V> multimap) {
      for (Map.Entry<? extends K, ? extends Collection<? extends V>> entry : (Iterable<Map.Entry<? extends K, ? extends Collection<? extends V>>>)multimap.asMap().entrySet()) {
        putAll(entry.getKey(), entry.getValue());
      }
      return this;
    }





    
    @CanIgnoreReturnValue
    public Builder<K, V> orderKeysBy(Comparator<? super K> keyComparator) {
      this.keyComparator = (Comparator<? super K>)Preconditions.checkNotNull(keyComparator);
      return this;
    }





    
    @CanIgnoreReturnValue
    public Builder<K, V> orderValuesBy(Comparator<? super V> valueComparator) {
      this.valueComparator = (Comparator<? super V>)Preconditions.checkNotNull(valueComparator);
      return this;
    }
    
    @CanIgnoreReturnValue
    Builder<K, V> combine(Builder<K, V> other) {
      for (Map.Entry<K, Collection<V>> entry : other.builderMap.entrySet()) {
        putAll(entry.getKey(), entry.getValue());
      }
      return this;
    }

    
    public ImmutableMultimap<K, V> build() {
      Collection<Map.Entry<K, Collection<V>>> mapEntries = this.builderMap.entrySet();
      if (this.keyComparator != null) {
        mapEntries = Ordering.<K>from(this.keyComparator).onKeys().immutableSortedCopy(mapEntries);
      }
      return ImmutableListMultimap.fromMapEntries(mapEntries, this.valueComparator);
    }
  }










  
  public static <K, V> ImmutableMultimap<K, V> copyOf(Multimap<? extends K, ? extends V> multimap) {
    if (multimap instanceof ImmutableMultimap) {
      
      ImmutableMultimap<K, V> kvMultimap = (ImmutableMultimap)multimap;
      if (!kvMultimap.isPartialView()) {
        return kvMultimap;
      }
    } 
    return ImmutableListMultimap.copyOf(multimap);
  }









  
  @Beta
  public static <K, V> ImmutableMultimap<K, V> copyOf(Iterable<? extends Map.Entry<? extends K, ? extends V>> entries) {
    return ImmutableListMultimap.copyOf(entries);
  }






  
  @GwtIncompatible
  static class FieldSettersHolder
  {
    static final Serialization.FieldSetter<ImmutableMultimap> MAP_FIELD_SETTER = Serialization.getFieldSetter(ImmutableMultimap.class, "map");
    
    static final Serialization.FieldSetter<ImmutableMultimap> SIZE_FIELD_SETTER = Serialization.getFieldSetter(ImmutableMultimap.class, "size");
  }
  
  ImmutableMultimap(ImmutableMap<K, ? extends ImmutableCollection<V>> map, int size) {
    this.map = map;
    this.size = size;
  }









  
  @Deprecated
  @CanIgnoreReturnValue
  public ImmutableCollection<V> removeAll(Object key) {
    throw new UnsupportedOperationException();
  }







  
  @Deprecated
  @CanIgnoreReturnValue
  public ImmutableCollection<V> replaceValues(K key, Iterable<? extends V> values) {
    throw new UnsupportedOperationException();
  }







  
  @Deprecated
  public void clear() {
    throw new UnsupportedOperationException();
  }























  
  @Deprecated
  @CanIgnoreReturnValue
  public boolean put(K key, V value) {
    throw new UnsupportedOperationException();
  }







  
  @Deprecated
  @CanIgnoreReturnValue
  public boolean putAll(K key, Iterable<? extends V> values) {
    throw new UnsupportedOperationException();
  }







  
  @Deprecated
  @CanIgnoreReturnValue
  public boolean putAll(Multimap<? extends K, ? extends V> multimap) {
    throw new UnsupportedOperationException();
  }







  
  @Deprecated
  @CanIgnoreReturnValue
  public boolean remove(Object key, Object value) {
    throw new UnsupportedOperationException();
  }






  
  boolean isPartialView() {
    return this.map.isPartialView();
  }



  
  public boolean containsKey(Object key) {
    return this.map.containsKey(key);
  }

  
  public boolean containsValue(Object value) {
    return (value != null && super.containsValue(value));
  }

  
  public int size() {
    return this.size;
  }







  
  public ImmutableSet<K> keySet() {
    return this.map.keySet();
  }

  
  Set<K> createKeySet() {
    throw new AssertionError("unreachable");
  }






  
  public ImmutableMap<K, Collection<V>> asMap() {
    return (ImmutableMap)this.map;
  }

  
  Map<K, Collection<V>> createAsMap() {
    throw new AssertionError("should never be called");
  }


  
  public ImmutableCollection<Map.Entry<K, V>> entries() {
    return (ImmutableCollection<Map.Entry<K, V>>)super.entries();
  }

  
  ImmutableCollection<Map.Entry<K, V>> createEntries() {
    return new EntryCollection<>(this);
  }
  private static class EntryCollection<K, V> extends ImmutableCollection<Map.Entry<K, V>> { @Weak
    final ImmutableMultimap<K, V> multimap;
    private static final long serialVersionUID = 0L;
    
    EntryCollection(ImmutableMultimap<K, V> multimap) {
      this.multimap = multimap;
    }

    
    public UnmodifiableIterator<Map.Entry<K, V>> iterator() {
      return this.multimap.entryIterator();
    }

    
    boolean isPartialView() {
      return this.multimap.isPartialView();
    }

    
    public int size() {
      return this.multimap.size();
    }

    
    public boolean contains(Object object) {
      if (object instanceof Map.Entry) {
        Map.Entry<?, ?> entry = (Map.Entry<?, ?>)object;
        return this.multimap.containsEntry(entry.getKey(), entry.getValue());
      } 
      return false;
    } }




  
  UnmodifiableIterator<Map.Entry<K, V>> entryIterator() {
    return new UnmodifiableIterator<Map.Entry<K, V>>() {
        final Iterator<? extends Map.Entry<K, ? extends ImmutableCollection<V>>> asMapItr = ImmutableMultimap.this.map
          .entrySet().iterator();
        K currentKey = null;
        Iterator<V> valueItr = Iterators.emptyIterator();

        
        public boolean hasNext() {
          return (this.valueItr.hasNext() || this.asMapItr.hasNext());
        }

        
        public Map.Entry<K, V> next() {
          if (!this.valueItr.hasNext()) {
            Map.Entry<K, ? extends ImmutableCollection<V>> entry = this.asMapItr.next();
            this.currentKey = entry.getKey();
            this.valueItr = ((ImmutableCollection<V>)entry.getValue()).iterator();
          } 
          return Maps.immutableEntry(this.currentKey, this.valueItr.next());
        }
      };
  }

  
  Spliterator<Map.Entry<K, V>> entrySpliterator() {
    return CollectSpliterators.flatMap(
        asMap().entrySet().spliterator(), keyToValueCollectionEntry -> { K key = (K)keyToValueCollectionEntry.getKey(); Collection<V> valueCollection = (Collection<V>)keyToValueCollectionEntry.getValue(); return CollectSpliterators.map(valueCollection.spliterator(), ()); }0x40 | ((this instanceof SetMultimap) ? 1 : 0), 






        
        size());
  }

  
  public void forEach(BiConsumer<? super K, ? super V> action) {
    Preconditions.checkNotNull(action);
    asMap()
      .forEach((key, valueCollection) -> valueCollection.forEach(()));
  }







  
  public ImmutableMultiset<K> keys() {
    return (ImmutableMultiset<K>)super.keys();
  }

  
  ImmutableMultiset<K> createKeys() {
    return new Keys();
  }

  
  class Keys
    extends ImmutableMultiset<K>
  {
    public boolean contains(Object object) {
      return ImmutableMultimap.this.containsKey(object);
    }

    
    public int count(Object element) {
      Collection<V> values = (Collection<V>)ImmutableMultimap.this.map.get(element);
      return (values == null) ? 0 : values.size();
    }

    
    public ImmutableSet<K> elementSet() {
      return ImmutableMultimap.this.keySet();
    }

    
    public int size() {
      return ImmutableMultimap.this.size();
    }

    
    Multiset.Entry<K> getEntry(int index) {
      Map.Entry<K, ? extends Collection<V>> entry = ImmutableMultimap.this.map.entrySet().asList().get(index);
      return Multisets.immutableEntry(entry.getKey(), ((Collection)entry.getValue()).size());
    }

    
    boolean isPartialView() {
      return true;
    }

    
    @GwtIncompatible
    Object writeReplace() {
      return new ImmutableMultimap.KeysSerializedForm(ImmutableMultimap.this);
    }
  }
  
  @GwtIncompatible
  private static final class KeysSerializedForm implements Serializable {
    final ImmutableMultimap<?, ?> multimap;
    
    KeysSerializedForm(ImmutableMultimap<?, ?> multimap) {
      this.multimap = multimap;
    }
    
    Object readResolve() {
      return this.multimap.keys();
    }
  }





  
  public ImmutableCollection<V> values() {
    return (ImmutableCollection<V>)super.values();
  }

  
  ImmutableCollection<V> createValues() {
    return new Values<>(this);
  }

  
  UnmodifiableIterator<V> valueIterator() {
    return new UnmodifiableIterator<V>() {
        Iterator<? extends ImmutableCollection<V>> valueCollectionItr = ImmutableMultimap.this.map.values().iterator();
        Iterator<V> valueItr = Iterators.emptyIterator();

        
        public boolean hasNext() {
          return (this.valueItr.hasNext() || this.valueCollectionItr.hasNext());
        }

        
        public V next() {
          if (!this.valueItr.hasNext()) {
            this.valueItr = ((ImmutableCollection<V>)this.valueCollectionItr.next()).iterator();
          }
          return this.valueItr.next();
        }
      };
  }
  public abstract ImmutableCollection<V> get(K paramK);
  public abstract ImmutableMultimap<V, K> inverse();
  private static final class Values<K, V> extends ImmutableCollection<V> { @Weak
    private final transient ImmutableMultimap<K, V> multimap;
    Values(ImmutableMultimap<K, V> multimap) {
      this.multimap = multimap;
    }
    private static final long serialVersionUID = 0L;
    
    public boolean contains(Object object) {
      return this.multimap.containsValue(object);
    }

    
    public UnmodifiableIterator<V> iterator() {
      return this.multimap.valueIterator();
    }

    
    @GwtIncompatible
    int copyIntoArray(Object[] dst, int offset) {
      for (UnmodifiableIterator<ImmutableCollection<V>> unmodifiableIterator = this.multimap.map.values().iterator(); unmodifiableIterator.hasNext(); ) { ImmutableCollection<V> valueCollection = unmodifiableIterator.next();
        offset = valueCollection.copyIntoArray(dst, offset); }
      
      return offset;
    }

    
    public int size() {
      return this.multimap.size();
    }

    
    boolean isPartialView() {
      return true;
    } }

}
