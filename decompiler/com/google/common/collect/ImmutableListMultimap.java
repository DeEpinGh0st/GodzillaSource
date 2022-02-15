package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.concurrent.LazyInit;
import com.google.j2objc.annotations.RetainedWith;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;



















































@GwtCompatible(serializable = true, emulated = true)
public class ImmutableListMultimap<K, V>
  extends ImmutableMultimap<K, V>
  implements ListMultimap<K, V>
{
  @LazyInit
  @RetainedWith
  private transient ImmutableListMultimap<V, K> inverse;
  @GwtIncompatible
  private static final long serialVersionUID = 0L;
  
  public static <T, K, V> Collector<T, ?, ImmutableListMultimap<K, V>> toImmutableListMultimap(Function<? super T, ? extends K> keyFunction, Function<? super T, ? extends V> valueFunction) {
    Preconditions.checkNotNull(keyFunction, "keyFunction");
    Preconditions.checkNotNull(valueFunction, "valueFunction");
    return Collector.of(ImmutableListMultimap::builder, (builder, t) -> builder.put(keyFunction.apply(t), valueFunction.apply(t)), Builder::combine, Builder::build, new Collector.Characteristics[0]);
  }






































  
  public static <T, K, V> Collector<T, ?, ImmutableListMultimap<K, V>> flatteningToImmutableListMultimap(Function<? super T, ? extends K> keyFunction, Function<? super T, ? extends Stream<? extends V>> valuesFunction) {
    Preconditions.checkNotNull(keyFunction);
    Preconditions.checkNotNull(valuesFunction);
    return Collectors.collectingAndThen(
        Multimaps.flatteningToMultimap(input -> Preconditions.checkNotNull(keyFunction.apply(input)), input -> ((Stream)valuesFunction.apply(input)).peek(Preconditions::checkNotNull), 

          
          MultimapBuilder.linkedHashKeys().arrayListValues()::build), ImmutableListMultimap::copyOf);
  }




  
  public static <K, V> ImmutableListMultimap<K, V> of() {
    return EmptyImmutableListMultimap.INSTANCE;
  }

  
  public static <K, V> ImmutableListMultimap<K, V> of(K k1, V v1) {
    Builder<K, V> builder = builder();
    builder.put(k1, v1);
    return builder.build();
  }

  
  public static <K, V> ImmutableListMultimap<K, V> of(K k1, V v1, K k2, V v2) {
    Builder<K, V> builder = builder();
    builder.put(k1, v1);
    builder.put(k2, v2);
    return builder.build();
  }

  
  public static <K, V> ImmutableListMultimap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3) {
    Builder<K, V> builder = builder();
    builder.put(k1, v1);
    builder.put(k2, v2);
    builder.put(k3, v3);
    return builder.build();
  }


  
  public static <K, V> ImmutableListMultimap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4) {
    Builder<K, V> builder = builder();
    builder.put(k1, v1);
    builder.put(k2, v2);
    builder.put(k3, v3);
    builder.put(k4, v4);
    return builder.build();
  }


  
  public static <K, V> ImmutableListMultimap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5) {
    Builder<K, V> builder = builder();
    builder.put(k1, v1);
    builder.put(k2, v2);
    builder.put(k3, v3);
    builder.put(k4, v4);
    builder.put(k5, v5);
    return builder.build();
  }






  
  public static <K, V> Builder<K, V> builder() {
    return new Builder<>();
  }
























  
  public static final class Builder<K, V>
    extends ImmutableMultimap.Builder<K, V>
  {
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
    @Beta
    public Builder<K, V> putAll(Iterable<? extends Map.Entry<? extends K, ? extends V>> entries) {
      super.putAll(entries);
      return this;
    }

    
    @CanIgnoreReturnValue
    public Builder<K, V> putAll(K key, Iterable<? extends V> values) {
      super.putAll(key, values);
      return this;
    }

    
    @CanIgnoreReturnValue
    public Builder<K, V> putAll(K key, V... values) {
      super.putAll(key, values);
      return this;
    }

    
    @CanIgnoreReturnValue
    public Builder<K, V> putAll(Multimap<? extends K, ? extends V> multimap) {
      super.putAll(multimap);
      return this;
    }

    
    @CanIgnoreReturnValue
    Builder<K, V> combine(ImmutableMultimap.Builder<K, V> other) {
      super.combine(other);
      return this;
    }






    
    @CanIgnoreReturnValue
    public Builder<K, V> orderKeysBy(Comparator<? super K> keyComparator) {
      super.orderKeysBy(keyComparator);
      return this;
    }






    
    @CanIgnoreReturnValue
    public Builder<K, V> orderValuesBy(Comparator<? super V> valueComparator) {
      super.orderValuesBy(valueComparator);
      return this;
    }


    
    public ImmutableListMultimap<K, V> build() {
      return (ImmutableListMultimap<K, V>)super.build();
    }
  }












  
  public static <K, V> ImmutableListMultimap<K, V> copyOf(Multimap<? extends K, ? extends V> multimap) {
    if (multimap.isEmpty()) {
      return of();
    }

    
    if (multimap instanceof ImmutableListMultimap) {
      
      ImmutableListMultimap<K, V> kvMultimap = (ImmutableListMultimap)multimap;
      if (!kvMultimap.isPartialView()) {
        return kvMultimap;
      }
    } 
    
    return fromMapEntries(multimap.asMap().entrySet(), (Comparator<? super V>)null);
  }









  
  @Beta
  public static <K, V> ImmutableListMultimap<K, V> copyOf(Iterable<? extends Map.Entry<? extends K, ? extends V>> entries) {
    return (new Builder<>()).putAll(entries).build();
  }



  
  static <K, V> ImmutableListMultimap<K, V> fromMapEntries(Collection<? extends Map.Entry<? extends K, ? extends Collection<? extends V>>> mapEntries, Comparator<? super V> valueComparator) {
    if (mapEntries.isEmpty()) {
      return of();
    }
    
    ImmutableMap.Builder<K, ImmutableList<V>> builder = new ImmutableMap.Builder<>(mapEntries.size());
    int size = 0;
    
    for (Map.Entry<? extends K, ? extends Collection<? extends V>> entry : mapEntries) {
      K key = entry.getKey();
      Collection<? extends V> values = entry.getValue();


      
      ImmutableList<V> list = (valueComparator == null) ? ImmutableList.<V>copyOf(values) : ImmutableList.<V>sortedCopyOf(valueComparator, values);
      if (!list.isEmpty()) {
        builder.put(key, list);
        size += list.size();
      } 
    } 
    
    return new ImmutableListMultimap<>(builder.build(), size);
  }
  
  ImmutableListMultimap(ImmutableMap<K, ImmutableList<V>> map, int size) {
    super((ImmutableMap)map, size);
  }









  
  public ImmutableList<V> get(K key) {
    ImmutableList<V> list = (ImmutableList<V>)this.map.get(key);
    return (list == null) ? ImmutableList.<V>of() : list;
  }












  
  public ImmutableListMultimap<V, K> inverse() {
    ImmutableListMultimap<V, K> result = this.inverse;
    return (result == null) ? (this.inverse = invert()) : result;
  }
  
  private ImmutableListMultimap<V, K> invert() {
    Builder<V, K> builder = builder();
    for (UnmodifiableIterator<Map.Entry<K, V>> unmodifiableIterator = entries().iterator(); unmodifiableIterator.hasNext(); ) { Map.Entry<K, V> entry = unmodifiableIterator.next();
      builder.put(entry.getValue(), entry.getKey()); }
    
    ImmutableListMultimap<V, K> invertedMultimap = builder.build();
    invertedMultimap.inverse = this;
    return invertedMultimap;
  }







  
  @Deprecated
  @CanIgnoreReturnValue
  public ImmutableList<V> removeAll(Object key) {
    throw new UnsupportedOperationException();
  }







  
  @Deprecated
  @CanIgnoreReturnValue
  public ImmutableList<V> replaceValues(K key, Iterable<? extends V> values) {
    throw new UnsupportedOperationException();
  }




  
  @GwtIncompatible
  private void writeObject(ObjectOutputStream stream) throws IOException {
    stream.defaultWriteObject();
    Serialization.writeMultimap(this, stream);
  }
  @GwtIncompatible
  private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
    ImmutableMap<Object, ImmutableList<Object>> tmpMap;
    stream.defaultReadObject();
    int keyCount = stream.readInt();
    if (keyCount < 0) {
      throw new InvalidObjectException("Invalid key count " + keyCount);
    }
    ImmutableMap.Builder<Object, ImmutableList<Object>> builder = ImmutableMap.builder();
    int tmpSize = 0;
    
    for (int i = 0; i < keyCount; i++) {
      Object key = stream.readObject();
      int valueCount = stream.readInt();
      if (valueCount <= 0) {
        throw new InvalidObjectException("Invalid value count " + valueCount);
      }
      
      ImmutableList.Builder<Object> valuesBuilder = ImmutableList.builder();
      for (int j = 0; j < valueCount; j++) {
        valuesBuilder.add(stream.readObject());
      }
      builder.put(key, valuesBuilder.build());
      tmpSize += valueCount;
    } 

    
    try {
      tmpMap = builder.build();
    } catch (IllegalArgumentException e) {
      throw (InvalidObjectException)(new InvalidObjectException(e.getMessage())).initCause(e);
    } 
    
    ImmutableMultimap.FieldSettersHolder.MAP_FIELD_SETTER.set(this, tmpMap);
    ImmutableMultimap.FieldSettersHolder.SIZE_FIELD_SETTER.set(this, tmpSize);
  }
}
