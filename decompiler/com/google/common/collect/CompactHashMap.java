package com.google.common.collect;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;



































@GwtIncompatible
class CompactHashMap<K, V>
  extends AbstractMap<K, V>
  implements Serializable
{
  private static final int MAXIMUM_CAPACITY = 1073741824;
  static final float DEFAULT_LOAD_FACTOR = 1.0F;
  private static final long NEXT_MASK = 4294967295L;
  private static final long HASH_MASK = -4294967296L;
  static final int DEFAULT_SIZE = 3;
  static final int UNSET = -1;
  private transient int[] table;
  @VisibleForTesting
  transient long[] entries;
  @VisibleForTesting
  transient Object[] keys;
  @VisibleForTesting
  transient Object[] values;
  transient float loadFactor;
  transient int modCount;
  private transient int threshold;
  private transient int size;
  private transient Set<K> keySetView;
  private transient Set<Map.Entry<K, V>> entrySetView;
  private transient Collection<V> valuesView;
  
  public static <K, V> CompactHashMap<K, V> create() {
    return new CompactHashMap<>();
  }









  
  public static <K, V> CompactHashMap<K, V> createWithExpectedSize(int expectedSize) {
    return new CompactHashMap<>(expectedSize);
  }
































































  
  CompactHashMap() {
    init(3, 1.0F);
  }





  
  CompactHashMap(int capacity) {
    this(capacity, 1.0F);
  }
  
  CompactHashMap(int expectedSize, float loadFactor) {
    init(expectedSize, loadFactor);
  }

  
  void init(int expectedSize, float loadFactor) {
    Preconditions.checkArgument((expectedSize >= 0), "Initial capacity must be non-negative");
    Preconditions.checkArgument((loadFactor > 0.0F), "Illegal load factor");
    int buckets = Hashing.closedTableSize(expectedSize, loadFactor);
    this.table = newTable(buckets);
    this.loadFactor = loadFactor;
    
    this.keys = new Object[expectedSize];
    this.values = new Object[expectedSize];
    
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
  
  private int hashTableMask() {
    return this.table.length - 1;
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

















  
  void accessEntry(int index) {}

















  
  @CanIgnoreReturnValue
  public V put(K key, V value) {
    // Byte code:
    //   0: aload_0
    //   1: getfield entries : [J
    //   4: astore_3
    //   5: aload_0
    //   6: getfield keys : [Ljava/lang/Object;
    //   9: astore #4
    //   11: aload_0
    //   12: getfield values : [Ljava/lang/Object;
    //   15: astore #5
    //   17: aload_1
    //   18: invokestatic smearedHash : (Ljava/lang/Object;)I
    //   21: istore #6
    //   23: iload #6
    //   25: aload_0
    //   26: invokespecial hashTableMask : ()I
    //   29: iand
    //   30: istore #7
    //   32: aload_0
    //   33: getfield size : I
    //   36: istore #8
    //   38: aload_0
    //   39: getfield table : [I
    //   42: iload #7
    //   44: iaload
    //   45: istore #9
    //   47: iload #9
    //   49: iconst_m1
    //   50: if_icmpne -> 65
    //   53: aload_0
    //   54: getfield table : [I
    //   57: iload #7
    //   59: iload #8
    //   61: iastore
    //   62: goto -> 143
    //   65: iload #9
    //   67: istore #10
    //   69: aload_3
    //   70: iload #9
    //   72: laload
    //   73: lstore #11
    //   75: lload #11
    //   77: invokestatic getHash : (J)I
    //   80: iload #6
    //   82: if_icmpne -> 119
    //   85: aload_1
    //   86: aload #4
    //   88: iload #9
    //   90: aaload
    //   91: invokestatic equal : (Ljava/lang/Object;Ljava/lang/Object;)Z
    //   94: ifeq -> 119
    //   97: aload #5
    //   99: iload #9
    //   101: aaload
    //   102: astore #13
    //   104: aload #5
    //   106: iload #9
    //   108: aload_2
    //   109: aastore
    //   110: aload_0
    //   111: iload #9
    //   113: invokevirtual accessEntry : (I)V
    //   116: aload #13
    //   118: areturn
    //   119: lload #11
    //   121: invokestatic getNext : (J)I
    //   124: istore #9
    //   126: iload #9
    //   128: iconst_m1
    //   129: if_icmpne -> 65
    //   132: aload_3
    //   133: iload #10
    //   135: lload #11
    //   137: iload #8
    //   139: invokestatic swapNext : (JI)J
    //   142: lastore
    //   143: iload #8
    //   145: ldc 2147483647
    //   147: if_icmpne -> 160
    //   150: new java/lang/IllegalStateException
    //   153: dup
    //   154: ldc 'Cannot contain more than Integer.MAX_VALUE elements!'
    //   156: invokespecial <init> : (Ljava/lang/String;)V
    //   159: athrow
    //   160: iload #8
    //   162: iconst_1
    //   163: iadd
    //   164: istore #10
    //   166: aload_0
    //   167: iload #10
    //   169: invokespecial resizeMeMaybe : (I)V
    //   172: aload_0
    //   173: iload #8
    //   175: aload_1
    //   176: aload_2
    //   177: iload #6
    //   179: invokevirtual insertEntry : (ILjava/lang/Object;Ljava/lang/Object;I)V
    //   182: aload_0
    //   183: iload #10
    //   185: putfield size : I
    //   188: iload #8
    //   190: aload_0
    //   191: getfield threshold : I
    //   194: if_icmplt -> 208
    //   197: aload_0
    //   198: iconst_2
    //   199: aload_0
    //   200: getfield table : [I
    //   203: arraylength
    //   204: imul
    //   205: invokespecial resizeTable : (I)V
    //   208: aload_0
    //   209: dup
    //   210: getfield modCount : I
    //   213: iconst_1
    //   214: iadd
    //   215: putfield modCount : I
    //   218: aconst_null
    //   219: areturn
    // Line number table:
    //   Java source line number -> byte code offset
    //   #240	-> 0
    //   #241	-> 5
    //   #242	-> 11
    //   #244	-> 17
    //   #245	-> 23
    //   #246	-> 32
    //   #247	-> 38
    //   #248	-> 47
    //   #249	-> 53
    //   #254	-> 65
    //   #255	-> 69
    //   #256	-> 75
    //   #259	-> 97
    //   #261	-> 104
    //   #262	-> 110
    //   #263	-> 116
    //   #265	-> 119
    //   #266	-> 126
    //   #267	-> 132
    //   #269	-> 143
    //   #270	-> 150
    //   #272	-> 160
    //   #273	-> 166
    //   #274	-> 172
    //   #275	-> 182
    //   #276	-> 188
    //   #277	-> 197
    //   #279	-> 208
    //   #280	-> 218
    // Local variable table:
    //   start	length	slot	name	descriptor
    //   104	15	13	oldValue	Ljava/lang/Object;
    //   69	74	10	last	I
    //   75	68	11	entry	J
    //   0	220	0	this	Lcom/google/common/collect/CompactHashMap;
    //   0	220	1	key	Ljava/lang/Object;
    //   0	220	2	value	Ljava/lang/Object;
    //   5	215	3	entries	[J
    //   11	209	4	keys	[Ljava/lang/Object;
    //   17	203	5	values	[Ljava/lang/Object;
    //   23	197	6	hash	I
    //   32	188	7	tableIndex	I
    //   38	182	8	newEntryIndex	I
    //   47	173	9	next	I
    //   166	54	10	newSize	I
    // Local variable type table:
    //   start	length	slot	name	signature
    //   104	15	13	oldValue	TV;
    //   0	220	0	this	Lcom/google/common/collect/CompactHashMap<TK;TV;>;
    //   0	220	1	key	TK;
    //   0	220	2	value	TV;
  }
















  
  void insertEntry(int entryIndex, K key, V value, int hash) {
    this.entries[entryIndex] = hash << 32L | 0xFFFFFFFFL;
    this.keys[entryIndex] = key;
    this.values[entryIndex] = value;
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
    this.keys = Arrays.copyOf(this.keys, newCapacity);
    this.values = Arrays.copyOf(this.values, newCapacity);
    long[] entries = this.entries;
    int oldCapacity = entries.length;
    entries = Arrays.copyOf(entries, newCapacity);
    if (newCapacity > oldCapacity) {
      Arrays.fill(entries, oldCapacity, newCapacity, -1L);
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
  
  private int indexOf(Object key) {
    int hash = Hashing.smearedHash(key);
    int next = this.table[hash & hashTableMask()];
    while (next != -1) {
      long entry = this.entries[next];
      if (getHash(entry) == hash && Objects.equal(key, this.keys[next])) {
        return next;
      }
      next = getNext(entry);
    } 
    return -1;
  }

  
  public boolean containsKey(Object key) {
    return (indexOf(key) != -1);
  }


  
  public V get(Object key) {
    int index = indexOf(key);
    accessEntry(index);
    return (index == -1) ? null : (V)this.values[index];
  }

  
  @CanIgnoreReturnValue
  public V remove(Object key) {
    return remove(key, Hashing.smearedHash(key));
  }
  
  private V remove(Object key, int hash) {
    int tableIndex = hash & hashTableMask();
    int next = this.table[tableIndex];
    if (next == -1) {
      return null;
    }
    int last = -1;
    while (true) {
      if (getHash(this.entries[next]) == hash && 
        Objects.equal(key, this.keys[next])) {

        
        V oldValue = (V)this.values[next];
        
        if (last == -1) {
          
          this.table[tableIndex] = getNext(this.entries[next]);
        } else {
          
          this.entries[last] = swapNext(this.entries[last], getNext(this.entries[next]));
        } 
        
        moveLastEntry(next);
        this.size--;
        this.modCount++;
        return oldValue;
      } 
      
      last = next;
      next = getNext(this.entries[next]);
      if (next == -1)
        return null; 
    } 
  }
  @CanIgnoreReturnValue
  private V removeEntry(int entryIndex) {
    return remove(this.keys[entryIndex], getHash(this.entries[entryIndex]));
  }



  
  void moveLastEntry(int dstIndex) {
    int srcIndex = size() - 1;
    if (dstIndex < srcIndex) {
      
      this.keys[dstIndex] = this.keys[srcIndex];
      this.values[dstIndex] = this.values[srcIndex];
      this.keys[srcIndex] = null;
      this.values[srcIndex] = null;

      
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
      this.keys[dstIndex] = null;
      this.values[dstIndex] = null;
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
  
  private abstract class Itr<T> implements Iterator<T> {
    int expectedModCount = CompactHashMap.this.modCount;
    int currentIndex = CompactHashMap.this.firstEntryIndex();
    int indexToRemove = -1;

    
    public boolean hasNext() {
      return (this.currentIndex >= 0);
    }



    
    public T next() {
      checkForConcurrentModification();
      if (!hasNext()) {
        throw new NoSuchElementException();
      }
      this.indexToRemove = this.currentIndex;
      T result = getOutput(this.currentIndex);
      this.currentIndex = CompactHashMap.this.getSuccessor(this.currentIndex);
      return result;
    }

    
    public void remove() {
      checkForConcurrentModification();
      CollectPreconditions.checkRemove((this.indexToRemove >= 0));
      this.expectedModCount++;
      CompactHashMap.this.removeEntry(this.indexToRemove);
      this.currentIndex = CompactHashMap.this.adjustAfterRemove(this.currentIndex, this.indexToRemove);
      this.indexToRemove = -1;
    }
    private Itr() {}
    private void checkForConcurrentModification() {
      if (CompactHashMap.this.modCount != this.expectedModCount)
        throw new ConcurrentModificationException(); 
    }
    
    abstract T getOutput(int param1Int);
  }
  
  public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
    Preconditions.checkNotNull(function);
    for (int i = 0; i < this.size; i++) {
      this.values[i] = function.apply((K)this.keys[i], (V)this.values[i]);
    }
  }



  
  public Set<K> keySet() {
    return (this.keySetView == null) ? (this.keySetView = createKeySet()) : this.keySetView;
  }
  
  Set<K> createKeySet() {
    return new KeySetView();
  }
  
  class KeySetView
    extends Maps.KeySet<K, V> {
    KeySetView() {
      super(CompactHashMap.this);
    }

    
    public Object[] toArray() {
      return ObjectArrays.copyAsObjectArray(CompactHashMap.this.keys, 0, CompactHashMap.this.size);
    }

    
    public <T> T[] toArray(T[] a) {
      return ObjectArrays.toArrayImpl(CompactHashMap.this.keys, 0, CompactHashMap.this.size, a);
    }

    
    public boolean remove(Object o) {
      int index = CompactHashMap.this.indexOf(o);
      if (index == -1) {
        return false;
      }
      CompactHashMap.this.removeEntry(index);
      return true;
    }


    
    public Iterator<K> iterator() {
      return CompactHashMap.this.keySetIterator();
    }

    
    public Spliterator<K> spliterator() {
      return Spliterators.spliterator(CompactHashMap.this.keys, 0, CompactHashMap.this.size, 17);
    }

    
    public void forEach(Consumer<? super K> action) {
      Preconditions.checkNotNull(action);
      for (int i = 0; i < CompactHashMap.this.size; i++) {
        action.accept((K)CompactHashMap.this.keys[i]);
      }
    }
  }
  
  Iterator<K> keySetIterator() {
    return new Itr<K>()
      {
        K getOutput(int entry)
        {
          return (K)CompactHashMap.this.keys[entry];
        }
      };
  }

  
  public void forEach(BiConsumer<? super K, ? super V> action) {
    Preconditions.checkNotNull(action);
    for (int i = 0; i < this.size; i++) {
      action.accept((K)this.keys[i], (V)this.values[i]);
    }
  }



  
  public Set<Map.Entry<K, V>> entrySet() {
    return (this.entrySetView == null) ? (this.entrySetView = createEntrySet()) : this.entrySetView;
  }
  
  Set<Map.Entry<K, V>> createEntrySet() {
    return new EntrySetView();
  }
  
  class EntrySetView
    extends Maps.EntrySet<K, V>
  {
    Map<K, V> map() {
      return CompactHashMap.this;
    }

    
    public Iterator<Map.Entry<K, V>> iterator() {
      return CompactHashMap.this.entrySetIterator();
    }

    
    public Spliterator<Map.Entry<K, V>> spliterator() {
      return CollectSpliterators.indexed(CompactHashMap.this
          .size, 17, x$0 -> new CompactHashMap.MapEntry(x$0));
    }

    
    public boolean contains(Object o) {
      if (o instanceof Map.Entry) {
        Map.Entry<?, ?> entry = (Map.Entry<?, ?>)o;
        int index = CompactHashMap.this.indexOf(entry.getKey());
        return (index != -1 && Objects.equal(CompactHashMap.this.values[index], entry.getValue()));
      } 
      return false;
    }

    
    public boolean remove(Object o) {
      if (o instanceof Map.Entry) {
        Map.Entry<?, ?> entry = (Map.Entry<?, ?>)o;
        int index = CompactHashMap.this.indexOf(entry.getKey());
        if (index != -1 && Objects.equal(CompactHashMap.this.values[index], entry.getValue())) {
          CompactHashMap.this.removeEntry(index);
          return true;
        } 
      } 
      return false;
    }
  }
  
  Iterator<Map.Entry<K, V>> entrySetIterator() {
    return new Itr<Map.Entry<K, V>>()
      {
        Map.Entry<K, V> getOutput(int entry) {
          return new CompactHashMap.MapEntry(entry);
        }
      };
  }
  
  final class MapEntry
    extends AbstractMapEntry<K, V>
  {
    private final K key;
    private int lastKnownIndex;
    
    MapEntry(int index) {
      this.key = (K)CompactHashMap.this.keys[index];
      this.lastKnownIndex = index;
    }

    
    public K getKey() {
      return this.key;
    }
    
    private void updateLastKnownIndex() {
      if (this.lastKnownIndex == -1 || this.lastKnownIndex >= CompactHashMap.this
        .size() || 
        !Objects.equal(this.key, CompactHashMap.this.keys[this.lastKnownIndex])) {
        this.lastKnownIndex = CompactHashMap.this.indexOf(this.key);
      }
    }


    
    public V getValue() {
      updateLastKnownIndex();
      return (this.lastKnownIndex == -1) ? null : (V)CompactHashMap.this.values[this.lastKnownIndex];
    }


    
    public V setValue(V value) {
      updateLastKnownIndex();
      if (this.lastKnownIndex == -1) {
        CompactHashMap.this.put(this.key, value);
        return null;
      } 
      V old = (V)CompactHashMap.this.values[this.lastKnownIndex];
      CompactHashMap.this.values[this.lastKnownIndex] = value;
      return old;
    }
  }


  
  public int size() {
    return this.size;
  }

  
  public boolean isEmpty() {
    return (this.size == 0);
  }

  
  public boolean containsValue(Object value) {
    for (int i = 0; i < this.size; i++) {
      if (Objects.equal(value, this.values[i])) {
        return true;
      }
    } 
    return false;
  }



  
  public Collection<V> values() {
    return (this.valuesView == null) ? (this.valuesView = createValues()) : this.valuesView;
  }
  
  Collection<V> createValues() {
    return new ValuesView();
  }
  
  class ValuesView
    extends Maps.Values<K, V> {
    ValuesView() {
      super(CompactHashMap.this);
    }

    
    public Iterator<V> iterator() {
      return CompactHashMap.this.valuesIterator();
    }

    
    public void forEach(Consumer<? super V> action) {
      Preconditions.checkNotNull(action);
      for (int i = 0; i < CompactHashMap.this.size; i++) {
        action.accept((V)CompactHashMap.this.values[i]);
      }
    }

    
    public Spliterator<V> spliterator() {
      return Spliterators.spliterator(CompactHashMap.this.values, 0, CompactHashMap.this.size, 16);
    }

    
    public Object[] toArray() {
      return ObjectArrays.copyAsObjectArray(CompactHashMap.this.values, 0, CompactHashMap.this.size);
    }

    
    public <T> T[] toArray(T[] a) {
      return ObjectArrays.toArrayImpl(CompactHashMap.this.values, 0, CompactHashMap.this.size, a);
    }
  }
  
  Iterator<V> valuesIterator() {
    return new Itr<V>()
      {
        V getOutput(int entry)
        {
          return (V)CompactHashMap.this.values[entry];
        }
      };
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
    Arrays.fill(this.keys, 0, this.size, (Object)null);
    Arrays.fill(this.values, 0, this.size, (Object)null);
    Arrays.fill(this.table, -1);
    Arrays.fill(this.entries, -1L);
    this.size = 0;
  }




  
  private void writeObject(ObjectOutputStream stream) throws IOException {
    stream.defaultWriteObject();
    stream.writeInt(this.size);
    for (int i = 0; i < this.size; i++) {
      stream.writeObject(this.keys[i]);
      stream.writeObject(this.values[i]);
    } 
  }

  
  private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
    stream.defaultReadObject();
    init(3, 1.0F);
    int elementCount = stream.readInt();
    for (int i = elementCount; --i >= 0; ) {
      K key = (K)stream.readObject();
      V value = (V)stream.readObject();
      put(key, value);
    } 
  }
}
