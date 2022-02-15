package com.google.common.collect;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;



































@GwtIncompatible
class CompactHashSet<E>
  extends AbstractSet<E>
  implements Serializable
{
  private static final int MAXIMUM_CAPACITY = 1073741824;
  private static final float DEFAULT_LOAD_FACTOR = 1.0F;
  private static final long NEXT_MASK = 4294967295L;
  private static final long HASH_MASK = -4294967296L;
  private static final int DEFAULT_SIZE = 3;
  static final int UNSET = -1;
  private transient int[] table;
  private transient long[] entries;
  transient Object[] elements;
  transient float loadFactor;
  transient int modCount;
  private transient int threshold;
  private transient int size;
  
  public static <E> CompactHashSet<E> create() {
    return new CompactHashSet<>();
  }







  
  public static <E> CompactHashSet<E> create(Collection<? extends E> collection) {
    CompactHashSet<E> set = createWithExpectedSize(collection.size());
    set.addAll(collection);
    return set;
  }







  
  public static <E> CompactHashSet<E> create(E... elements) {
    CompactHashSet<E> set = createWithExpectedSize(elements.length);
    Collections.addAll(set, elements);
    return set;
  }









  
  public static <E> CompactHashSet<E> createWithExpectedSize(int expectedSize) {
    return new CompactHashSet<>(expectedSize);
  }






















































  
  CompactHashSet() {
    init(3, 1.0F);
  }





  
  CompactHashSet(int expectedSize) {
    init(expectedSize, 1.0F);
  }

  
  void init(int expectedSize, float loadFactor) {
    Preconditions.checkArgument((expectedSize >= 0), "Initial capacity must be non-negative");
    Preconditions.checkArgument((loadFactor > 0.0F), "Illegal load factor");
    int buckets = Hashing.closedTableSize(expectedSize, loadFactor);
    this.table = newTable(buckets);
    this.loadFactor = loadFactor;
    this.elements = new Object[expectedSize];
    this.entries = newEntries(expectedSize);
    this.threshold = Math.max(1, (int)(buckets * loadFactor));
  }
  
  private static int[] newTable(int size) {
    int[] array = new int[size];
    Arrays.fill(array, -1);
    return array;
  }
  
  private static long[] newEntries(int size) {
    long[] array = new long[size];
    Arrays.fill(array, -1L);
    return array;
  }
  
  private static int getHash(long entry) {
    return (int)(entry >>> 32L);
  }

  
  private static int getNext(long entry) {
    return (int)entry;
  }

  
  private static long swapNext(long entry, int newNext) {
    return 0xFFFFFFFF00000000L & entry | 0xFFFFFFFFL & newNext;
  }
  
  private int hashTableMask() {
    return this.table.length - 1;
  }


















  
  @CanIgnoreReturnValue
  public boolean add(E object) {
    // Byte code:
    //   0: aload_0
    //   1: getfield entries : [J
    //   4: astore_2
    //   5: aload_0
    //   6: getfield elements : [Ljava/lang/Object;
    //   9: astore_3
    //   10: aload_1
    //   11: invokestatic smearedHash : (Ljava/lang/Object;)I
    //   14: istore #4
    //   16: iload #4
    //   18: aload_0
    //   19: invokespecial hashTableMask : ()I
    //   22: iand
    //   23: istore #5
    //   25: aload_0
    //   26: getfield size : I
    //   29: istore #6
    //   31: aload_0
    //   32: getfield table : [I
    //   35: iload #5
    //   37: iaload
    //   38: istore #7
    //   40: iload #7
    //   42: iconst_m1
    //   43: if_icmpne -> 58
    //   46: aload_0
    //   47: getfield table : [I
    //   50: iload #5
    //   52: iload #6
    //   54: iastore
    //   55: goto -> 115
    //   58: iload #7
    //   60: istore #8
    //   62: aload_2
    //   63: iload #7
    //   65: laload
    //   66: lstore #9
    //   68: lload #9
    //   70: invokestatic getHash : (J)I
    //   73: iload #4
    //   75: if_icmpne -> 91
    //   78: aload_1
    //   79: aload_3
    //   80: iload #7
    //   82: aaload
    //   83: invokestatic equal : (Ljava/lang/Object;Ljava/lang/Object;)Z
    //   86: ifeq -> 91
    //   89: iconst_0
    //   90: ireturn
    //   91: lload #9
    //   93: invokestatic getNext : (J)I
    //   96: istore #7
    //   98: iload #7
    //   100: iconst_m1
    //   101: if_icmpne -> 58
    //   104: aload_2
    //   105: iload #8
    //   107: lload #9
    //   109: iload #6
    //   111: invokestatic swapNext : (JI)J
    //   114: lastore
    //   115: iload #6
    //   117: ldc 2147483647
    //   119: if_icmpne -> 132
    //   122: new java/lang/IllegalStateException
    //   125: dup
    //   126: ldc 'Cannot contain more than Integer.MAX_VALUE elements!'
    //   128: invokespecial <init> : (Ljava/lang/String;)V
    //   131: athrow
    //   132: iload #6
    //   134: iconst_1
    //   135: iadd
    //   136: istore #8
    //   138: aload_0
    //   139: iload #8
    //   141: invokespecial resizeMeMaybe : (I)V
    //   144: aload_0
    //   145: iload #6
    //   147: aload_1
    //   148: iload #4
    //   150: invokevirtual insertEntry : (ILjava/lang/Object;I)V
    //   153: aload_0
    //   154: iload #8
    //   156: putfield size : I
    //   159: iload #6
    //   161: aload_0
    //   162: getfield threshold : I
    //   165: if_icmplt -> 179
    //   168: aload_0
    //   169: iconst_2
    //   170: aload_0
    //   171: getfield table : [I
    //   174: arraylength
    //   175: imul
    //   176: invokespecial resizeTable : (I)V
    //   179: aload_0
    //   180: dup
    //   181: getfield modCount : I
    //   184: iconst_1
    //   185: iadd
    //   186: putfield modCount : I
    //   189: iconst_1
    //   190: ireturn
    // Line number table:
    //   Java source line number -> byte code offset
    //   #230	-> 0
    //   #231	-> 5
    //   #232	-> 10
    //   #233	-> 16
    //   #234	-> 25
    //   #235	-> 31
    //   #236	-> 40
    //   #237	-> 46
    //   #242	-> 58
    //   #243	-> 62
    //   #244	-> 68
    //   #245	-> 89
    //   #247	-> 91
    //   #248	-> 98
    //   #249	-> 104
    //   #251	-> 115
    //   #252	-> 122
    //   #254	-> 132
    //   #255	-> 138
    //   #256	-> 144
    //   #257	-> 153
    //   #258	-> 159
    //   #259	-> 168
    //   #261	-> 179
    //   #262	-> 189
    // Local variable table:
    //   start	length	slot	name	descriptor
    //   62	53	8	last	I
    //   68	47	9	entry	J
    //   0	191	0	this	Lcom/google/common/collect/CompactHashSet;
    //   0	191	1	object	Ljava/lang/Object;
    //   5	186	2	entries	[J
    //   10	181	3	elements	[Ljava/lang/Object;
    //   16	175	4	hash	I
    //   25	166	5	tableIndex	I
    //   31	160	6	newEntryIndex	I
    //   40	151	7	next	I
    //   138	53	8	newSize	I
    // Local variable type table:
    //   start	length	slot	name	signature
    //   0	191	0	this	Lcom/google/common/collect/CompactHashSet<TE;>;
    //   0	191	1	object	TE;
  }


















  
  void insertEntry(int entryIndex, E object, int hash) {
    this.entries[entryIndex] = hash << 32L | 0xFFFFFFFFL;
    this.elements[entryIndex] = object;
  }

  
  private void resizeMeMaybe(int newSize) {
    int entriesSize = this.entries.length;
    if (newSize > entriesSize) {
      int newCapacity = entriesSize + Math.max(1, entriesSize >>> 1);
      if (newCapacity < 0) {
        newCapacity = Integer.MAX_VALUE;
      }
      if (newCapacity != entriesSize) {
        resizeEntries(newCapacity);
      }
    } 
  }




  
  void resizeEntries(int newCapacity) {
    this.elements = Arrays.copyOf(this.elements, newCapacity);
    long[] entries = this.entries;
    int oldSize = entries.length;
    entries = Arrays.copyOf(entries, newCapacity);
    if (newCapacity > oldSize) {
      Arrays.fill(entries, oldSize, newCapacity, -1L);
    }
    this.entries = entries;
  }
  
  private void resizeTable(int newCapacity) {
    int[] oldTable = this.table;
    int oldCapacity = oldTable.length;
    if (oldCapacity >= 1073741824) {
      this.threshold = Integer.MAX_VALUE;
      return;
    } 
    int newThreshold = 1 + (int)(newCapacity * this.loadFactor);
    int[] newTable = newTable(newCapacity);
    long[] entries = this.entries;
    
    int mask = newTable.length - 1;
    for (int i = 0; i < this.size; i++) {
      long oldEntry = entries[i];
      int hash = getHash(oldEntry);
      int tableIndex = hash & mask;
      int next = newTable[tableIndex];
      newTable[tableIndex] = i;
      entries[i] = hash << 32L | 0xFFFFFFFFL & next;
    } 
    
    this.threshold = newThreshold;
    this.table = newTable;
  }

  
  public boolean contains(Object object) {
    int hash = Hashing.smearedHash(object);
    int next = this.table[hash & hashTableMask()];
    while (next != -1) {
      long entry = this.entries[next];
      if (getHash(entry) == hash && Objects.equal(object, this.elements[next])) {
        return true;
      }
      next = getNext(entry);
    } 
    return false;
  }

  
  @CanIgnoreReturnValue
  public boolean remove(Object object) {
    return remove(object, Hashing.smearedHash(object));
  }
  
  @CanIgnoreReturnValue
  private boolean remove(Object object, int hash) {
    int tableIndex = hash & hashTableMask();
    int next = this.table[tableIndex];
    if (next == -1) {
      return false;
    }
    int last = -1;
    while (true) {
      if (getHash(this.entries[next]) == hash && Objects.equal(object, this.elements[next])) {
        if (last == -1) {
          
          this.table[tableIndex] = getNext(this.entries[next]);
        } else {
          
          this.entries[last] = swapNext(this.entries[last], getNext(this.entries[next]));
        } 
        
        moveEntry(next);
        this.size--;
        this.modCount++;
        return true;
      } 
      last = next;
      next = getNext(this.entries[next]);
      if (next == -1) {
        return false;
      }
    } 
  }

  
  void moveEntry(int dstIndex) {
    int srcIndex = size() - 1;
    if (dstIndex < srcIndex) {
      
      this.elements[dstIndex] = this.elements[srcIndex];
      this.elements[srcIndex] = null;

      
      long lastEntry = this.entries[srcIndex];
      this.entries[dstIndex] = lastEntry;
      this.entries[srcIndex] = -1L;


      
      int tableIndex = getHash(lastEntry) & hashTableMask();
      int lastNext = this.table[tableIndex];
      if (lastNext == srcIndex) {
        
        this.table[tableIndex] = dstIndex;
      } else {
        int previous;
        
        long entry;
        do {
          previous = lastNext;
          lastNext = getNext(entry = this.entries[lastNext]);
        } while (lastNext != srcIndex);
        
        this.entries[previous] = swapNext(entry, dstIndex);
      } 
    } else {
      this.elements[dstIndex] = null;
      this.entries[dstIndex] = -1L;
    } 
  }
  
  int firstEntryIndex() {
    return isEmpty() ? -1 : 0;
  }
  
  int getSuccessor(int entryIndex) {
    return (entryIndex + 1 < this.size) ? (entryIndex + 1) : -1;
  }





  
  int adjustAfterRemove(int indexBeforeRemove, int indexRemoved) {
    return indexBeforeRemove - 1;
  }

  
  public Iterator<E> iterator() {
    return new Iterator<E>() {
        int expectedModCount = CompactHashSet.this.modCount;
        int index = CompactHashSet.this.firstEntryIndex();
        int indexToRemove = -1;

        
        public boolean hasNext() {
          return (this.index >= 0);
        }


        
        public E next() {
          checkForConcurrentModification();
          if (!hasNext()) {
            throw new NoSuchElementException();
          }
          this.indexToRemove = this.index;
          E result = (E)CompactHashSet.this.elements[this.index];
          this.index = CompactHashSet.this.getSuccessor(this.index);
          return result;
        }

        
        public void remove() {
          checkForConcurrentModification();
          CollectPreconditions.checkRemove((this.indexToRemove >= 0));
          this.expectedModCount++;
          CompactHashSet.this.remove(CompactHashSet.this.elements[this.indexToRemove], CompactHashSet.getHash(CompactHashSet.this.entries[this.indexToRemove]));
          this.index = CompactHashSet.this.adjustAfterRemove(this.index, this.indexToRemove);
          this.indexToRemove = -1;
        }
        
        private void checkForConcurrentModification() {
          if (CompactHashSet.this.modCount != this.expectedModCount) {
            throw new ConcurrentModificationException();
          }
        }
      };
  }

  
  public Spliterator<E> spliterator() {
    return Spliterators.spliterator(this.elements, 0, this.size, 17);
  }

  
  public void forEach(Consumer<? super E> action) {
    Preconditions.checkNotNull(action);
    for (int i = 0; i < this.size; i++) {
      action.accept((E)this.elements[i]);
    }
  }

  
  public int size() {
    return this.size;
  }

  
  public boolean isEmpty() {
    return (this.size == 0);
  }

  
  public Object[] toArray() {
    return Arrays.copyOf(this.elements, this.size);
  }

  
  @CanIgnoreReturnValue
  public <T> T[] toArray(T[] a) {
    return ObjectArrays.toArrayImpl(this.elements, 0, this.size, a);
  }




  
  public void trimToSize() {
    int size = this.size;
    if (size < this.entries.length) {
      resizeEntries(size);
    }



    
    int minimumTableSize = Math.max(1, Integer.highestOneBit((int)(size / this.loadFactor)));
    if (minimumTableSize < 1073741824) {
      double load = size / minimumTableSize;
      if (load > this.loadFactor) {
        minimumTableSize <<= 1;
      }
    } 
    
    if (minimumTableSize < this.table.length) {
      resizeTable(minimumTableSize);
    }
  }

  
  public void clear() {
    this.modCount++;
    Arrays.fill(this.elements, 0, this.size, (Object)null);
    Arrays.fill(this.table, -1);
    Arrays.fill(this.entries, -1L);
    this.size = 0;
  }




  
  private void writeObject(ObjectOutputStream stream) throws IOException {
    stream.defaultWriteObject();
    stream.writeInt(this.size);
    for (E e : this) {
      stream.writeObject(e);
    }
  }

  
  private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
    stream.defaultReadObject();
    init(3, 1.0F);
    int elementCount = stream.readInt();
    for (int i = elementCount; --i >= 0; ) {
      E element = (E)stream.readObject();
      add(element);
    } 
  }
}
