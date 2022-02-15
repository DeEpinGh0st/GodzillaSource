package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.errorprone.annotations.concurrent.LazyInit;
import com.google.j2objc.annotations.RetainedWith;
import java.util.Map;













@GwtCompatible(emulated = true)
final class JdkBackedImmutableBiMap<K, V>
  extends ImmutableBiMap<K, V>
{
  private final transient ImmutableList<Map.Entry<K, V>> entries;
  private final Map<K, V> forwardDelegate;
  private final Map<V, K> backwardDelegate;
  @LazyInit
  @RetainedWith
  private transient JdkBackedImmutableBiMap<V, K> inverse;
  
  @VisibleForTesting
  static <K, V> ImmutableBiMap<K, V> create(int n, Map.Entry<K, V>[] entryArray) {
    Map<K, V> forwardDelegate = Maps.newHashMapWithExpectedSize(n);
    Map<V, K> backwardDelegate = Maps.newHashMapWithExpectedSize(n);
    for (int i = 0; i < n; i++) {
      Map.Entry<K, V> e = RegularImmutableMap.makeImmutable(entryArray[i]);
      entryArray[i] = e;
      V oldValue = forwardDelegate.putIfAbsent(e.getKey(), e.getValue());
      if (oldValue != null) {
        throw conflictException("key", (new StringBuilder()).append(e.getKey()).append("=").append(oldValue).toString(), entryArray[i]);
      }
      K oldKey = backwardDelegate.putIfAbsent(e.getValue(), e.getKey());
      if (oldKey != null) {
        throw conflictException("value", (new StringBuilder()).append(oldKey).append("=").append(e.getValue()).toString(), entryArray[i]);
      }
    } 
    ImmutableList<Map.Entry<K, V>> entryList = ImmutableList.asImmutableList((Object[])entryArray, n);
    return new JdkBackedImmutableBiMap<>(entryList, forwardDelegate, backwardDelegate);
  }





  
  private JdkBackedImmutableBiMap(ImmutableList<Map.Entry<K, V>> entries, Map<K, V> forwardDelegate, Map<V, K> backwardDelegate) {
    this.entries = entries;
    this.forwardDelegate = forwardDelegate;
    this.backwardDelegate = backwardDelegate;
  }

  
  public int size() {
    return this.entries.size();
  }



  
  public ImmutableBiMap<V, K> inverse() {
    JdkBackedImmutableBiMap<V, K> result = this.inverse;
    if (result == null) {
      this.inverse = result = new JdkBackedImmutableBiMap(new InverseEntries(), this.backwardDelegate, this.forwardDelegate);


      
      result.inverse = this;
    } 
    return result;
  }
  
  private final class InverseEntries extends ImmutableList<Map.Entry<V, K>> {
    private InverseEntries() {}
    
    public Map.Entry<V, K> get(int index) {
      Map.Entry<K, V> entry = JdkBackedImmutableBiMap.this.entries.get(index);
      return Maps.immutableEntry(entry.getValue(), entry.getKey());
    }

    
    boolean isPartialView() {
      return false;
    }

    
    public int size() {
      return JdkBackedImmutableBiMap.this.entries.size();
    }
  }

  
  public V get(Object key) {
    return this.forwardDelegate.get(key);
  }

  
  ImmutableSet<Map.Entry<K, V>> createEntrySet() {
    return new ImmutableMapEntrySet.RegularEntrySet<>(this, this.entries);
  }

  
  ImmutableSet<K> createKeySet() {
    return new ImmutableMapKeySet<>(this);
  }

  
  boolean isPartialView() {
    return false;
  }
}
