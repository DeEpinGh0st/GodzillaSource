package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.primitives.Ints;
import com.google.errorprone.annotations.concurrent.LazyInit;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;






















@GwtCompatible(emulated = true, serializable = true)
class RegularImmutableMultiset<E>
  extends ImmutableMultiset<E>
{
  static final ImmutableMultiset<Object> EMPTY = create(ImmutableList.of()); @VisibleForTesting
  static final double MAX_LOAD_FACTOR = 1.0D; @VisibleForTesting
  static final double HASH_FLOODING_FPP = 0.001D; @VisibleForTesting
  static final int MAX_HASH_BUCKET_LENGTH = 9; private final transient Multisets.ImmutableEntry<E>[] entries; static <E> ImmutableMultiset<E> create(Collection<? extends Multiset.Entry<? extends E>> entries) { int distinct = entries.size();
    
    Multisets.ImmutableEntry[] arrayOfImmutableEntry1 = new Multisets.ImmutableEntry[distinct];
    if (distinct == 0) {
      return new RegularImmutableMultiset<>((Multisets.ImmutableEntry<E>[])arrayOfImmutableEntry1, null, 0, 0, ImmutableSet.of());
    }
    int tableSize = Hashing.closedTableSize(distinct, 1.0D);
    int mask = tableSize - 1;
    
    Multisets.ImmutableEntry[] arrayOfImmutableEntry2 = new Multisets.ImmutableEntry[tableSize];
    
    int index = 0;
    int hashCode = 0;
    long size = 0L;
    for (Multiset.Entry<? extends E> entry : entries) {
      Multisets.ImmutableEntry<E> newEntry; E element = (E)Preconditions.checkNotNull(entry.getElement());
      int count = entry.getCount();
      int hash = element.hashCode();
      int bucket = Hashing.smear(hash) & mask;
      Multisets.ImmutableEntry<E> bucketHead = arrayOfImmutableEntry2[bucket];
      
      if (bucketHead == null) {
        boolean canReuseEntry = (entry instanceof Multisets.ImmutableEntry && !(entry instanceof NonTerminalEntry));
        
        newEntry = canReuseEntry ? (Multisets.ImmutableEntry)entry : new Multisets.ImmutableEntry<>(element, count);
      
      }
      else {
        
        newEntry = new NonTerminalEntry<>(element, count, bucketHead);
      } 
      hashCode += hash ^ count;
      arrayOfImmutableEntry1[index++] = newEntry;
      arrayOfImmutableEntry2[bucket] = newEntry;
      size += count;
    } 
    
    return hashFloodingDetected((Multisets.ImmutableEntry<?>[])arrayOfImmutableEntry2) ? 
      JdkBackedImmutableMultiset.<E>create(ImmutableList.asImmutableList((Object[])arrayOfImmutableEntry1)) : new RegularImmutableMultiset<>((Multisets.ImmutableEntry<E>[])arrayOfImmutableEntry1, (Multisets.ImmutableEntry<E>[])arrayOfImmutableEntry2, 
        
        Ints.saturatedCast(size), hashCode, null); }
   private final transient Multisets.ImmutableEntry<E>[] hashTable; private final transient int size; private final transient int hashCode; @LazyInit
  private transient ImmutableSet<E> elementSet;
  private static boolean hashFloodingDetected(Multisets.ImmutableEntry<?>[] hashTable) {
    for (int i = 0; i < hashTable.length; i++) {
      int bucketLength = 0;
      Multisets.ImmutableEntry<?> entry = hashTable[i];
      for (; entry != null; 
        entry = entry.nextInBucket()) {
        bucketLength++;
        if (bucketLength > 9) {
          return true;
        }
      } 
    } 
    return false;
  }































  
  private RegularImmutableMultiset(Multisets.ImmutableEntry<E>[] entries, Multisets.ImmutableEntry<E>[] hashTable, int size, int hashCode, ImmutableSet<E> elementSet) {
    this.entries = entries;
    this.hashTable = hashTable;
    this.size = size;
    this.hashCode = hashCode;
    this.elementSet = elementSet;
  }
  
  private static final class NonTerminalEntry<E> extends Multisets.ImmutableEntry<E> {
    private final Multisets.ImmutableEntry<E> nextInBucket;
    
    NonTerminalEntry(E element, int count, Multisets.ImmutableEntry<E> nextInBucket) {
      super(element, count);
      this.nextInBucket = nextInBucket;
    }

    
    public Multisets.ImmutableEntry<E> nextInBucket() {
      return this.nextInBucket;
    }
  }

  
  boolean isPartialView() {
    return false;
  }

  
  public int count(Object element) {
    Multisets.ImmutableEntry<E>[] hashTable = this.hashTable;
    if (element == null || hashTable == null) {
      return 0;
    }
    int hash = Hashing.smearedHash(element);
    int mask = hashTable.length - 1;
    Multisets.ImmutableEntry<E> entry = hashTable[hash & mask];
    for (; entry != null; 
      entry = entry.nextInBucket()) {
      if (Objects.equal(element, entry.getElement())) {
        return entry.getCount();
      }
    } 
    return 0;
  }

  
  public int size() {
    return this.size;
  }

  
  public ImmutableSet<E> elementSet() {
    ImmutableSet<E> result = this.elementSet;
    return (result == null) ? (this.elementSet = new ImmutableMultiset.ElementSet<>(Arrays.asList((Multiset.Entry[])this.entries), this)) : result;
  }

  
  Multiset.Entry<E> getEntry(int index) {
    return this.entries[index];
  }

  
  public int hashCode() {
    return this.hashCode;
  }
}
