package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.Serializable;
import java.util.Map;
import java.util.function.BiConsumer;




























@GwtCompatible(serializable = true, emulated = true)
final class RegularImmutableMap<K, V>
  extends ImmutableMap<K, V>
{
  static final ImmutableMap<Object, Object> EMPTY = new RegularImmutableMap((Map.Entry<K, V>[])ImmutableMap.EMPTY_ENTRY_ARRAY, null, 0);

  
  @VisibleForTesting
  static final double MAX_LOAD_FACTOR = 1.2D;

  
  @VisibleForTesting
  static final double HASH_FLOODING_FPP = 0.001D;

  
  @VisibleForTesting
  static final int MAX_HASH_BUCKET_LENGTH = 8;

  
  @VisibleForTesting
  final transient Map.Entry<K, V>[] entries;

  
  private final transient ImmutableMapEntry<K, V>[] table;

  
  private final transient int mask;

  
  private static final long serialVersionUID = 0L;


  
  static <K, V> ImmutableMap<K, V> fromEntries(Map.Entry<K, V>... entries) {
    return fromEntryArray(entries.length, entries);
  }




  
  static <K, V> ImmutableMap<K, V> fromEntryArray(int n, Map.Entry<K, V>[] entryArray) {
    ImmutableMapEntry[] arrayOfImmutableMapEntry1;
    Preconditions.checkPositionIndex(n, entryArray.length);
    if (n == 0) {
      return (RegularImmutableMap)EMPTY;
    }
    
    if (n == entryArray.length) {
      Map.Entry<K, V>[] entries = entryArray;
    } else {
      arrayOfImmutableMapEntry1 = ImmutableMapEntry.createEntryArray(n);
    } 
    int tableSize = Hashing.closedTableSize(n, 1.2D);
    ImmutableMapEntry[] arrayOfImmutableMapEntry2 = (ImmutableMapEntry[])ImmutableMapEntry.createEntryArray(tableSize);
    int mask = tableSize - 1;
    for (int entryIndex = 0; entryIndex < n; entryIndex++) {
      Map.Entry<K, V> entry = entryArray[entryIndex];
      K key = entry.getKey();
      V value = entry.getValue();
      CollectPreconditions.checkEntryNotNull(key, value);
      int tableIndex = Hashing.smear(key.hashCode()) & mask;
      ImmutableMapEntry<K, V> existing = arrayOfImmutableMapEntry2[tableIndex];


      
      ImmutableMapEntry<K, V> newEntry = (existing == null) ? makeImmutable(entry, key, value) : new ImmutableMapEntry.NonTerminalImmutableMapEntry<>(key, value, existing);
      
      arrayOfImmutableMapEntry2[tableIndex] = newEntry;
      arrayOfImmutableMapEntry1[entryIndex] = newEntry;
      int bucketSize = checkNoConflictInKeyBucket(key, newEntry, existing);
      if (bucketSize > 8)
      {
        
        return JdkBackedImmutableMap.create(n, entryArray);
      }
    } 
    return new RegularImmutableMap<>((Map.Entry<K, V>[])arrayOfImmutableMapEntry1, (ImmutableMapEntry<K, V>[])arrayOfImmutableMapEntry2, mask);
  }


  
  static <K, V> ImmutableMapEntry<K, V> makeImmutable(Map.Entry<K, V> entry, K key, V value) {
    boolean reusable = (entry instanceof ImmutableMapEntry && ((ImmutableMapEntry)entry).isReusable());
    return reusable ? (ImmutableMapEntry<K, V>)entry : new ImmutableMapEntry<>(key, value);
  }

  
  static <K, V> ImmutableMapEntry<K, V> makeImmutable(Map.Entry<K, V> entry) {
    return makeImmutable(entry, entry.getKey(), entry.getValue());
  }
  
  private RegularImmutableMap(Map.Entry<K, V>[] entries, ImmutableMapEntry<K, V>[] table, int mask) {
    this.entries = entries;
    this.table = table;
    this.mask = mask;
  }





  
  @CanIgnoreReturnValue
  static int checkNoConflictInKeyBucket(Object key, Map.Entry<?, ?> entry, ImmutableMapEntry<?, ?> keyBucketHead) {
    int bucketSize = 0;
    for (; keyBucketHead != null; keyBucketHead = keyBucketHead.getNextInKeyBucket()) {
      checkNoConflict(!key.equals(keyBucketHead.getKey()), "key", entry, keyBucketHead);
      bucketSize++;
    } 
    return bucketSize;
  }

  
  public V get(Object key) {
    return get(key, (ImmutableMapEntry<?, V>[])this.table, this.mask);
  }

  
  static <V> V get(Object key, ImmutableMapEntry<?, V>[] keyTable, int mask) {
    if (key == null || keyTable == null) {
      return null;
    }
    int index = Hashing.smear(key.hashCode()) & mask;
    ImmutableMapEntry<?, V> entry = keyTable[index];
    for (; entry != null; 
      entry = entry.getNextInKeyBucket()) {
      Object candidateKey = entry.getKey();






      
      if (key.equals(candidateKey)) {
        return entry.getValue();
      }
    } 
    return null;
  }

  
  public void forEach(BiConsumer<? super K, ? super V> action) {
    Preconditions.checkNotNull(action);
    for (Map.Entry<K, V> entry : this.entries) {
      action.accept(entry.getKey(), entry.getValue());
    }
  }

  
  public int size() {
    return this.entries.length;
  }

  
  boolean isPartialView() {
    return false;
  }

  
  ImmutableSet<Map.Entry<K, V>> createEntrySet() {
    return new ImmutableMapEntrySet.RegularEntrySet<>(this, this.entries);
  }

  
  ImmutableSet<K> createKeySet() {
    return new KeySet<>(this);
  }
  
  @GwtCompatible(emulated = true)
  private static final class KeySet<K, V> extends IndexedImmutableSet<K> {
    private final RegularImmutableMap<K, V> map;
    
    KeySet(RegularImmutableMap<K, V> map) {
      this.map = map;
    }

    
    K get(int index) {
      return this.map.entries[index].getKey();
    }

    
    public boolean contains(Object object) {
      return this.map.containsKey(object);
    }

    
    boolean isPartialView() {
      return true;
    }

    
    public int size() {
      return this.map.size();
    }

    
    @GwtIncompatible
    Object writeReplace() {
      return new SerializedForm<>(this.map);
    }
    
    @GwtIncompatible
    private static class SerializedForm<K> implements Serializable {
      final ImmutableMap<K, ?> map;
      
      SerializedForm(ImmutableMap<K, ?> map) {
        this.map = map;
      }
      private static final long serialVersionUID = 0L;
      Object readResolve() {
        return this.map.keySet();
      }
    }
  }



  
  ImmutableCollection<V> createValues() {
    return new Values<>(this);
  }
  
  @GwtCompatible(emulated = true)
  private static final class Values<K, V> extends ImmutableList<V> {
    final RegularImmutableMap<K, V> map;
    
    Values(RegularImmutableMap<K, V> map) {
      this.map = map;
    }

    
    public V get(int index) {
      return this.map.entries[index].getValue();
    }

    
    public int size() {
      return this.map.size();
    }

    
    boolean isPartialView() {
      return true;
    }

    
    @GwtIncompatible
    Object writeReplace() {
      return new SerializedForm<>(this.map);
    }
    
    @GwtIncompatible
    private static class SerializedForm<V> implements Serializable { final ImmutableMap<?, V> map;
      private static final long serialVersionUID = 0L;
      
      SerializedForm(ImmutableMap<?, V> map) {
        this.map = map;
      }
      
      Object readResolve() {
        return this.map.values();
      } }
  
  }
}
