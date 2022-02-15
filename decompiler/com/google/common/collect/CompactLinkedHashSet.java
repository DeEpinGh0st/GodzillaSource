package com.google.common.collect;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;


































@GwtIncompatible
class CompactLinkedHashSet<E>
  extends CompactHashSet<E>
{
  private static final int ENDPOINT = -2;
  private transient int[] predecessor;
  private transient int[] successor;
  private transient int firstEntry;
  private transient int lastEntry;
  
  public static <E> CompactLinkedHashSet<E> create() {
    return new CompactLinkedHashSet<>();
  }







  
  public static <E> CompactLinkedHashSet<E> create(Collection<? extends E> collection) {
    CompactLinkedHashSet<E> set = createWithExpectedSize(collection.size());
    set.addAll(collection);
    return set;
  }







  
  public static <E> CompactLinkedHashSet<E> create(E... elements) {
    CompactLinkedHashSet<E> set = createWithExpectedSize(elements.length);
    Collections.addAll(set, elements);
    return set;
  }









  
  public static <E> CompactLinkedHashSet<E> createWithExpectedSize(int expectedSize) {
    return new CompactLinkedHashSet<>(expectedSize);
  }












  
  CompactLinkedHashSet() {}











  
  CompactLinkedHashSet(int expectedSize) {
    super(expectedSize);
  }

  
  void init(int expectedSize, float loadFactor) {
    super.init(expectedSize, loadFactor);
    this.predecessor = new int[expectedSize];
    this.successor = new int[expectedSize];
    
    Arrays.fill(this.predecessor, -1);
    Arrays.fill(this.successor, -1);
    this.firstEntry = -2;
    this.lastEntry = -2;
  }
  
  private void succeeds(int pred, int succ) {
    if (pred == -2) {
      this.firstEntry = succ;
    } else {
      this.successor[pred] = succ;
    } 
    
    if (succ == -2) {
      this.lastEntry = pred;
    } else {
      this.predecessor[succ] = pred;
    } 
  }

  
  void insertEntry(int entryIndex, E object, int hash) {
    super.insertEntry(entryIndex, object, hash);
    succeeds(this.lastEntry, entryIndex);
    succeeds(entryIndex, -2);
  }

  
  void moveEntry(int dstIndex) {
    int srcIndex = size() - 1;
    super.moveEntry(dstIndex);
    
    succeeds(this.predecessor[dstIndex], this.successor[dstIndex]);
    if (srcIndex != dstIndex) {
      succeeds(this.predecessor[srcIndex], dstIndex);
      succeeds(dstIndex, this.successor[srcIndex]);
    } 
    this.predecessor[srcIndex] = -1;
    this.successor[srcIndex] = -1;
  }

  
  public void clear() {
    super.clear();
    this.firstEntry = -2;
    this.lastEntry = -2;
    Arrays.fill(this.predecessor, -1);
    Arrays.fill(this.successor, -1);
  }

  
  void resizeEntries(int newCapacity) {
    super.resizeEntries(newCapacity);
    int oldCapacity = this.predecessor.length;
    this.predecessor = Arrays.copyOf(this.predecessor, newCapacity);
    this.successor = Arrays.copyOf(this.successor, newCapacity);
    
    if (oldCapacity < newCapacity) {
      Arrays.fill(this.predecessor, oldCapacity, newCapacity, -1);
      Arrays.fill(this.successor, oldCapacity, newCapacity, -1);
    } 
  }

  
  public Object[] toArray() {
    return ObjectArrays.toArrayImpl(this);
  }

  
  public <T> T[] toArray(T[] a) {
    return ObjectArrays.toArrayImpl(this, a);
  }

  
  int firstEntryIndex() {
    return this.firstEntry;
  }

  
  int adjustAfterRemove(int indexBeforeRemove, int indexRemoved) {
    return (indexBeforeRemove == size()) ? indexRemoved : indexBeforeRemove;
  }

  
  int getSuccessor(int entryIndex) {
    return this.successor[entryIndex];
  }

  
  public Spliterator<E> spliterator() {
    return Spliterators.spliterator(this, 17);
  }

  
  public void forEach(Consumer<? super E> action) {
    Preconditions.checkNotNull(action);
    for (int i = this.firstEntry; i != -2; i = this.successor[i])
      action.accept((E)this.elements[i]); 
  }
}
