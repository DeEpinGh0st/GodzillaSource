package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.concurrent.LazyInit;
import com.google.j2objc.annotations.RetainedWith;
import java.util.Map;
import java.util.function.BiConsumer;






















@GwtCompatible(serializable = true, emulated = true)
final class SingletonImmutableBiMap<K, V>
  extends ImmutableBiMap<K, V>
{
  final transient K singleKey;
  final transient V singleValue;
  @LazyInit
  @RetainedWith
  transient ImmutableBiMap<V, K> inverse;
  
  SingletonImmutableBiMap(K singleKey, V singleValue) {
    CollectPreconditions.checkEntryNotNull(singleKey, singleValue);
    this.singleKey = singleKey;
    this.singleValue = singleValue;
  }
  
  private SingletonImmutableBiMap(K singleKey, V singleValue, ImmutableBiMap<V, K> inverse) {
    this.singleKey = singleKey;
    this.singleValue = singleValue;
    this.inverse = inverse;
  }

  
  public V get(Object key) {
    return this.singleKey.equals(key) ? this.singleValue : null;
  }

  
  public int size() {
    return 1;
  }

  
  public void forEach(BiConsumer<? super K, ? super V> action) {
    ((BiConsumer<K, V>)Preconditions.checkNotNull(action)).accept(this.singleKey, this.singleValue);
  }

  
  public boolean containsKey(Object key) {
    return this.singleKey.equals(key);
  }

  
  public boolean containsValue(Object value) {
    return this.singleValue.equals(value);
  }

  
  boolean isPartialView() {
    return false;
  }

  
  ImmutableSet<Map.Entry<K, V>> createEntrySet() {
    return ImmutableSet.of(Maps.immutableEntry(this.singleKey, this.singleValue));
  }

  
  ImmutableSet<K> createKeySet() {
    return ImmutableSet.of(this.singleKey);
  }




  
  public ImmutableBiMap<V, K> inverse() {
    ImmutableBiMap<V, K> result = this.inverse;
    if (result == null) {
      return this.inverse = new SingletonImmutableBiMap((K)this.singleValue, (V)this.singleKey, this);
    }
    return result;
  }
}
