package com.google.common.collect;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
































@GwtIncompatible
class CompactLinkedHashMap<K, V>
  extends CompactHashMap<K, V>
{
  private static final int ENDPOINT = -2;
  @VisibleForTesting
  transient long[] links;
  private transient int firstEntry;
  private transient int lastEntry;
  private final boolean accessOrder;
  
  public static <K, V> CompactLinkedHashMap<K, V> create() {
    return new CompactLinkedHashMap<>();
  }









  
  public static <K, V> CompactLinkedHashMap<K, V> createWithExpectedSize(int expectedSize) {
    return new CompactLinkedHashMap<>(expectedSize);
  }





















  
  CompactLinkedHashMap() {
    this(3);
  }
  
  CompactLinkedHashMap(int expectedSize) {
    this(expectedSize, 1.0F, false);
  }
  
  CompactLinkedHashMap(int expectedSize, float loadFactor, boolean accessOrder) {
    super(expectedSize, loadFactor);
    this.accessOrder = accessOrder;
  }

  
  void init(int expectedSize, float loadFactor) {
    super.init(expectedSize, loadFactor);
    this.firstEntry = -2;
    this.lastEntry = -2;
    this.links = new long[expectedSize];
    Arrays.fill(this.links, -1L);
  }
  
  private int getPredecessor(int entry) {
    return (int)(this.links[entry] >>> 32L);
  }

  
  int getSuccessor(int entry) {
    return (int)this.links[entry];
  }
  
  private void setSuccessor(int entry, int succ) {
    long succMask = 4294967295L;
    this.links[entry] = this.links[entry] & (succMask ^ 0xFFFFFFFFFFFFFFFFL) | succ & succMask;
  }
  
  private void setPredecessor(int entry, int pred) {
    long predMask = -4294967296L;
    this.links[entry] = this.links[entry] & (predMask ^ 0xFFFFFFFFFFFFFFFFL) | pred << 32L;
  }
  
  private void setSucceeds(int pred, int succ) {
    if (pred == -2) {
      this.firstEntry = succ;
    } else {
      setSuccessor(pred, succ);
    } 
    if (succ == -2) {
      this.lastEntry = pred;
    } else {
      setPredecessor(succ, pred);
    } 
  }

  
  void insertEntry(int entryIndex, K key, V value, int hash) {
    super.insertEntry(entryIndex, key, value, hash);
    setSucceeds(this.lastEntry, entryIndex);
    setSucceeds(entryIndex, -2);
  }

  
  void accessEntry(int index) {
    if (this.accessOrder) {
      
      setSucceeds(getPredecessor(index), getSuccessor(index));
      
      setSucceeds(this.lastEntry, index);
      setSucceeds(index, -2);
      this.modCount++;
    } 
  }

  
  void moveLastEntry(int dstIndex) {
    int srcIndex = size() - 1;
    setSucceeds(getPredecessor(dstIndex), getSuccessor(dstIndex));
    if (dstIndex < srcIndex) {
      setSucceeds(getPredecessor(srcIndex), dstIndex);
      setSucceeds(dstIndex, getSuccessor(srcIndex));
    } 
    super.moveLastEntry(dstIndex);
  }

  
  void resizeEntries(int newCapacity) {
    super.resizeEntries(newCapacity);
    this.links = Arrays.copyOf(this.links, newCapacity);
  }

  
  int firstEntryIndex() {
    return this.firstEntry;
  }

  
  int adjustAfterRemove(int indexBeforeRemove, int indexRemoved) {
    return (indexBeforeRemove >= size()) ? indexRemoved : indexBeforeRemove;
  }

  
  public void forEach(BiConsumer<? super K, ? super V> action) {
    Preconditions.checkNotNull(action);
    for (int i = this.firstEntry; i != -2; i = getSuccessor(i)) {
      action.accept((K)this.keys[i], (V)this.values[i]);
    }
  }

  
  Set<Map.Entry<K, V>> createEntrySet() {
    class EntrySetImpl
      extends CompactHashMap<K, V>.EntrySetView
    {
      public Spliterator<Map.Entry<K, V>> spliterator() {
        return Spliterators.spliterator(this, 17);
      }
    };
    return new EntrySetImpl();
  }

  
  Set<K> createKeySet() {
    class KeySetImpl
      extends CompactHashMap<K, V>.KeySetView
    {
      public Object[] toArray() {
        return ObjectArrays.toArrayImpl(this);
      }

      
      public <T> T[] toArray(T[] a) {
        return ObjectArrays.toArrayImpl(this, a);
      }

      
      public Spliterator<K> spliterator() {
        return Spliterators.spliterator(this, 17);
      }

      
      public void forEach(Consumer<? super K> action) {
        Preconditions.checkNotNull(action);
        for (int i = CompactLinkedHashMap.this.firstEntry; i != -2; i = CompactLinkedHashMap.this.getSuccessor(i)) {
          action.accept((K)CompactLinkedHashMap.this.keys[i]);
        }
      }
    };
    return new KeySetImpl();
  }

  
  Collection<V> createValues() {
    class ValuesImpl
      extends CompactHashMap<K, V>.ValuesView
    {
      public Object[] toArray() {
        return ObjectArrays.toArrayImpl(this);
      }

      
      public <T> T[] toArray(T[] a) {
        return ObjectArrays.toArrayImpl(this, a);
      }

      
      public Spliterator<V> spliterator() {
        return Spliterators.spliterator(this, 16);
      }

      
      public void forEach(Consumer<? super V> action) {
        Preconditions.checkNotNull(action);
        for (int i = CompactLinkedHashMap.this.firstEntry; i != -2; i = CompactLinkedHashMap.this.getSuccessor(i)) {
          action.accept((V)CompactLinkedHashMap.this.values[i]);
        }
      }
    };
    return new ValuesImpl();
  }

  
  public void clear() {
    super.clear();
    this.firstEntry = -2;
    this.lastEntry = -2;
  }
}
