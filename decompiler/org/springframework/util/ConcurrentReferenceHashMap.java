package org.springframework.util;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import org.springframework.lang.Nullable;












































public class ConcurrentReferenceHashMap<K, V>
  extends AbstractMap<K, V>
  implements ConcurrentMap<K, V>
{
  private static final int DEFAULT_INITIAL_CAPACITY = 16;
  private static final float DEFAULT_LOAD_FACTOR = 0.75F;
  private static final int DEFAULT_CONCURRENCY_LEVEL = 16;
  private static final ReferenceType DEFAULT_REFERENCE_TYPE = ReferenceType.SOFT;



  
  private static final int MAXIMUM_CONCURRENCY_LEVEL = 65536;



  
  private static final int MAXIMUM_SEGMENT_SIZE = 1073741824;



  
  private final Segment[] segments;


  
  private final float loadFactor;


  
  private final ReferenceType referenceType;


  
  private final int shift;


  
  @Nullable
  private volatile Set<Map.Entry<K, V>> entrySet;



  
  public ConcurrentReferenceHashMap() {
    this(16, 0.75F, 16, DEFAULT_REFERENCE_TYPE);
  }




  
  public ConcurrentReferenceHashMap(int initialCapacity) {
    this(initialCapacity, 0.75F, 16, DEFAULT_REFERENCE_TYPE);
  }






  
  public ConcurrentReferenceHashMap(int initialCapacity, float loadFactor) {
    this(initialCapacity, loadFactor, 16, DEFAULT_REFERENCE_TYPE);
  }






  
  public ConcurrentReferenceHashMap(int initialCapacity, int concurrencyLevel) {
    this(initialCapacity, 0.75F, concurrencyLevel, DEFAULT_REFERENCE_TYPE);
  }





  
  public ConcurrentReferenceHashMap(int initialCapacity, ReferenceType referenceType) {
    this(initialCapacity, 0.75F, 16, referenceType);
  }








  
  public ConcurrentReferenceHashMap(int initialCapacity, float loadFactor, int concurrencyLevel) {
    this(initialCapacity, loadFactor, concurrencyLevel, DEFAULT_REFERENCE_TYPE);
  }












  
  public ConcurrentReferenceHashMap(int initialCapacity, float loadFactor, int concurrencyLevel, ReferenceType referenceType) {
    Assert.isTrue((initialCapacity >= 0), "Initial capacity must not be negative");
    Assert.isTrue((loadFactor > 0.0F), "Load factor must be positive");
    Assert.isTrue((concurrencyLevel > 0), "Concurrency level must be positive");
    Assert.notNull(referenceType, "Reference type must not be null");
    this.loadFactor = loadFactor;
    this.shift = calculateShift(concurrencyLevel, 65536);
    int size = 1 << this.shift;
    this.referenceType = referenceType;
    int roundedUpSegmentCapacity = (int)(((initialCapacity + size) - 1L) / size);
    int initialSize = 1 << calculateShift(roundedUpSegmentCapacity, 1073741824);
    Segment[] arrayOfSegment = (Segment[])Array.newInstance(Segment.class, size);
    int resizeThreshold = (int)(initialSize * getLoadFactor());
    for (int i = 0; i < arrayOfSegment.length; i++) {
      arrayOfSegment[i] = new Segment(initialSize, resizeThreshold);
    }
    this.segments = (Segment[])arrayOfSegment;
  }

  
  protected final float getLoadFactor() {
    return this.loadFactor;
  }
  
  protected final int getSegmentsSize() {
    return this.segments.length;
  }
  
  protected final Segment getSegment(int index) {
    return this.segments[index];
  }





  
  protected ReferenceManager createReferenceManager() {
    return new ReferenceManager();
  }







  
  protected int getHash(@Nullable Object o) {
    int hash = (o != null) ? o.hashCode() : 0;
    hash += hash << 15 ^ 0xFFFFCD7D;
    hash ^= hash >>> 10;
    hash += hash << 3;
    hash ^= hash >>> 6;
    hash += (hash << 2) + (hash << 14);
    hash ^= hash >>> 16;
    return hash;
  }

  
  @Nullable
  public V get(@Nullable Object key) {
    Reference<K, V> ref = getReference(key, Restructure.WHEN_NECESSARY);
    Entry<K, V> entry = (ref != null) ? ref.get() : null;
    return (entry != null) ? entry.getValue() : null;
  }

  
  @Nullable
  public V getOrDefault(@Nullable Object key, @Nullable V defaultValue) {
    Reference<K, V> ref = getReference(key, Restructure.WHEN_NECESSARY);
    Entry<K, V> entry = (ref != null) ? ref.get() : null;
    return (entry != null) ? entry.getValue() : defaultValue;
  }

  
  public boolean containsKey(@Nullable Object key) {
    Reference<K, V> ref = getReference(key, Restructure.WHEN_NECESSARY);
    Entry<K, V> entry = (ref != null) ? ref.get() : null;
    return (entry != null && ObjectUtils.nullSafeEquals(entry.getKey(), key));
  }







  
  @Nullable
  protected final Reference<K, V> getReference(@Nullable Object key, Restructure restructure) {
    int hash = getHash(key);
    return getSegmentForHash(hash).getReference(key, hash, restructure);
  }

  
  @Nullable
  public V put(@Nullable K key, @Nullable V value) {
    return put(key, value, true);
  }

  
  @Nullable
  public V putIfAbsent(@Nullable K key, @Nullable V value) {
    return put(key, value, false);
  }
  
  @Nullable
  private V put(@Nullable K key, @Nullable final V value, final boolean overwriteExisting) {
    return doTask(key, new Task<V>(new TaskOption[] { TaskOption.RESTRUCTURE_BEFORE, TaskOption.RESIZE })
        {
          @Nullable
          protected V execute(@Nullable ConcurrentReferenceHashMap.Reference<K, V> ref, @Nullable ConcurrentReferenceHashMap.Entry<K, V> entry, @Nullable ConcurrentReferenceHashMap.Entries<V> entries) {
            if (entry != null) {
              V oldValue = entry.getValue();
              if (overwriteExisting) {
                entry.setValue((V)value);
              }
              return oldValue;
            } 
            Assert.state((entries != null), "No entries segment");
            entries.add((V)value);
            return null;
          }
        });
  }

  
  @Nullable
  public V remove(@Nullable Object key) {
    return doTask(key, new Task<V>(new TaskOption[] { TaskOption.RESTRUCTURE_AFTER, TaskOption.SKIP_IF_EMPTY })
        {
          @Nullable
          protected V execute(@Nullable ConcurrentReferenceHashMap.Reference<K, V> ref, @Nullable ConcurrentReferenceHashMap.Entry<K, V> entry) {
            if (entry != null) {
              if (ref != null) {
                ref.release();
              }
              return entry.value;
            } 
            return null;
          }
        });
  }

  
  public boolean remove(@Nullable Object key, @Nullable final Object value) {
    Boolean result = doTask(key, new Task<Boolean>(new TaskOption[] { TaskOption.RESTRUCTURE_AFTER, TaskOption.SKIP_IF_EMPTY })
        {
          protected Boolean execute(@Nullable ConcurrentReferenceHashMap.Reference<K, V> ref, @Nullable ConcurrentReferenceHashMap.Entry<K, V> entry) {
            if (entry != null && ObjectUtils.nullSafeEquals(entry.getValue(), value)) {
              if (ref != null) {
                ref.release();
              }
              return Boolean.valueOf(true);
            } 
            return Boolean.valueOf(false);
          }
        });
    return Boolean.TRUE.equals(result);
  }

  
  public boolean replace(@Nullable K key, @Nullable final V oldValue, @Nullable final V newValue) {
    Boolean result = doTask(key, new Task<Boolean>(new TaskOption[] { TaskOption.RESTRUCTURE_BEFORE, TaskOption.SKIP_IF_EMPTY })
        {
          protected Boolean execute(@Nullable ConcurrentReferenceHashMap.Reference<K, V> ref, @Nullable ConcurrentReferenceHashMap.Entry<K, V> entry) {
            if (entry != null && ObjectUtils.nullSafeEquals(entry.getValue(), oldValue)) {
              entry.setValue((V)newValue);
              return Boolean.valueOf(true);
            } 
            return Boolean.valueOf(false);
          }
        });
    return Boolean.TRUE.equals(result);
  }

  
  @Nullable
  public V replace(@Nullable K key, @Nullable final V value) {
    return doTask(key, new Task<V>(new TaskOption[] { TaskOption.RESTRUCTURE_BEFORE, TaskOption.SKIP_IF_EMPTY })
        {
          @Nullable
          protected V execute(@Nullable ConcurrentReferenceHashMap.Reference<K, V> ref, @Nullable ConcurrentReferenceHashMap.Entry<K, V> entry) {
            if (entry != null) {
              V oldValue = entry.getValue();
              entry.setValue((V)value);
              return oldValue;
            } 
            return null;
          }
        });
  }

  
  public void clear() {
    for (Segment segment : this.segments) {
      segment.clear();
    }
  }






  
  public void purgeUnreferencedEntries() {
    for (Segment segment : this.segments) {
      segment.restructureIfNecessary(false);
    }
  }


  
  public int size() {
    int size = 0;
    for (Segment segment : this.segments) {
      size += segment.getCount();
    }
    return size;
  }

  
  public boolean isEmpty() {
    for (Segment segment : this.segments) {
      if (segment.getCount() > 0) {
        return false;
      }
    } 
    return true;
  }

  
  public Set<Map.Entry<K, V>> entrySet() {
    Set<Map.Entry<K, V>> entrySet = this.entrySet;
    if (entrySet == null) {
      entrySet = new EntrySet();
      this.entrySet = entrySet;
    } 
    return entrySet;
  }
  
  @Nullable
  private <T> T doTask(@Nullable Object key, Task<T> task) {
    int hash = getHash(key);
    return getSegmentForHash(hash).doTask(hash, key, task);
  }
  
  private Segment getSegmentForHash(int hash) {
    return this.segments[hash >>> 32 - this.shift & this.segments.length - 1];
  }







  
  protected static int calculateShift(int minimumValue, int maximumValue) {
    int shift = 0;
    int value = 1;
    while (value < minimumValue && value < maximumValue) {
      value <<= 1;
      shift++;
    } 
    return shift;
  }





  
  public enum ReferenceType
  {
    SOFT,

    
    WEAK;
  }




  
  protected final class Segment
    extends ReentrantLock
  {
    private final ConcurrentReferenceHashMap<K, V>.ReferenceManager referenceManager;



    
    private final int initialSize;



    
    private volatile ConcurrentReferenceHashMap.Reference<K, V>[] references;


    
    private final AtomicInteger count = new AtomicInteger();


    
    private int resizeThreshold;


    
    public Segment(int initialSize, int resizeThreshold) {
      this.referenceManager = ConcurrentReferenceHashMap.this.createReferenceManager();
      this.initialSize = initialSize;
      this.references = createReferenceArray(initialSize);
      this.resizeThreshold = resizeThreshold;
    }
    
    @Nullable
    public ConcurrentReferenceHashMap.Reference<K, V> getReference(@Nullable Object key, int hash, ConcurrentReferenceHashMap.Restructure restructure) {
      if (restructure == ConcurrentReferenceHashMap.Restructure.WHEN_NECESSARY) {
        restructureIfNecessary(false);
      }
      if (this.count.get() == 0) {
        return null;
      }
      
      ConcurrentReferenceHashMap.Reference<K, V>[] references = this.references;
      int index = getIndex(hash, references);
      ConcurrentReferenceHashMap.Reference<K, V> head = references[index];
      return findInChain(head, key, hash);
    }








    
    @Nullable
    public <T> T doTask(int hash, @Nullable Object key, ConcurrentReferenceHashMap<K, V>.Task<T> task) {
      boolean resize = task.hasOption(ConcurrentReferenceHashMap.TaskOption.RESIZE);
      if (task.hasOption(ConcurrentReferenceHashMap.TaskOption.RESTRUCTURE_BEFORE)) {
        restructureIfNecessary(resize);
      }
      if (task.hasOption(ConcurrentReferenceHashMap.TaskOption.SKIP_IF_EMPTY) && this.count.get() == 0) {
        return task.execute((ConcurrentReferenceHashMap.Reference<K, V>)null, (ConcurrentReferenceHashMap.Entry<K, V>)null, (ConcurrentReferenceHashMap.Entries<V>)null);
      }
      lock();
      try {
        int index = getIndex(hash, this.references);
        ConcurrentReferenceHashMap.Reference<K, V> head = this.references[index];
        ConcurrentReferenceHashMap.Reference<K, V> ref = findInChain(head, key, hash);
        ConcurrentReferenceHashMap.Entry<K, V> entry = (ref != null) ? ref.get() : null;
        ConcurrentReferenceHashMap.Entries<V> entries = value -> {
            ConcurrentReferenceHashMap.Entry<K, V> newEntry = new ConcurrentReferenceHashMap.Entry<>((K)key, (V)value);
            
            ConcurrentReferenceHashMap.Reference<K, V> newReference = this.referenceManager.createReference(newEntry, hash, head);
            this.references[index] = newReference;
            this.count.incrementAndGet();
          };
        return task.execute(ref, entry, entries);
      } finally {
        
        unlock();
        if (task.hasOption(ConcurrentReferenceHashMap.TaskOption.RESTRUCTURE_AFTER)) {
          restructureIfNecessary(resize);
        }
      } 
    }



    
    public void clear() {
      if (this.count.get() == 0) {
        return;
      }
      lock();
      try {
        this.references = createReferenceArray(this.initialSize);
        this.resizeThreshold = (int)(this.references.length * ConcurrentReferenceHashMap.this.getLoadFactor());
        this.count.set(0);
      } finally {
        
        unlock();
      } 
    }






    
    protected final void restructureIfNecessary(boolean allowResize) {
      int currCount = this.count.get();
      boolean needsResize = (allowResize && currCount > 0 && currCount >= this.resizeThreshold);
      ConcurrentReferenceHashMap.Reference<K, V> ref = this.referenceManager.pollForPurge();
      if (ref != null || needsResize) {
        restructure(allowResize, ref);
      }
    }

    
    private void restructure(boolean allowResize, @Nullable ConcurrentReferenceHashMap.Reference<K, V> ref) {
      lock();
      try {
        int countAfterRestructure = this.count.get();
        Set<ConcurrentReferenceHashMap.Reference<K, V>> toPurge = Collections.emptySet();
        if (ref != null) {
          toPurge = new HashSet<>();
          while (ref != null) {
            toPurge.add(ref);
            ref = this.referenceManager.pollForPurge();
          } 
        } 
        countAfterRestructure -= toPurge.size();


        
        boolean needsResize = (countAfterRestructure > 0 && countAfterRestructure >= this.resizeThreshold);
        boolean resizing = false;
        int restructureSize = this.references.length;
        if (allowResize && needsResize && restructureSize < 1073741824) {
          restructureSize <<= 1;
          resizing = true;
        } 


        
        ConcurrentReferenceHashMap.Reference<K, V>[] restructured = resizing ? createReferenceArray(restructureSize) : this.references;

        
        for (int i = 0; i < this.references.length; i++) {
          ref = this.references[i];
          if (!resizing) {
            restructured[i] = null;
          }
          while (ref != null) {
            if (!toPurge.contains(ref)) {
              ConcurrentReferenceHashMap.Entry<K, V> entry = ref.get();
              if (entry != null) {
                int index = getIndex(ref.getHash(), restructured);
                restructured[index] = this.referenceManager.createReference(entry, ref
                    .getHash(), restructured[index]);
              } 
            } 
            ref = ref.getNext();
          } 
        } 

        
        if (resizing) {
          this.references = restructured;
          this.resizeThreshold = (int)(this.references.length * ConcurrentReferenceHashMap.this.getLoadFactor());
        } 
        this.count.set(Math.max(countAfterRestructure, 0));
      } finally {
        
        unlock();
      } 
    }
    
    @Nullable
    private ConcurrentReferenceHashMap.Reference<K, V> findInChain(ConcurrentReferenceHashMap.Reference<K, V> ref, @Nullable Object key, int hash) {
      ConcurrentReferenceHashMap.Reference<K, V> currRef = ref;
      while (currRef != null) {
        if (currRef.getHash() == hash) {
          ConcurrentReferenceHashMap.Entry<K, V> entry = currRef.get();
          if (entry != null) {
            K entryKey = entry.getKey();
            if (ObjectUtils.nullSafeEquals(entryKey, key)) {
              return currRef;
            }
          } 
        } 
        currRef = currRef.getNext();
      } 
      return null;
    }

    
    private ConcurrentReferenceHashMap.Reference<K, V>[] createReferenceArray(int size) {
      return (ConcurrentReferenceHashMap.Reference<K, V>[])new ConcurrentReferenceHashMap.Reference[size];
    }
    
    private int getIndex(int hash, ConcurrentReferenceHashMap.Reference<K, V>[] references) {
      return hash & references.length - 1;
    }



    
    public final int getSize() {
      return this.references.length;
    }



    
    public final int getCount() {
      return this.count.get();
    }
  }




  
  protected static interface Reference<K, V>
  {
    @Nullable
    ConcurrentReferenceHashMap.Entry<K, V> get();




    
    int getHash();




    
    @Nullable
    Reference<K, V> getNext();




    
    void release();
  }




  
  protected static final class Entry<K, V>
    implements Map.Entry<K, V>
  {
    @Nullable
    private final K key;


    
    @Nullable
    private volatile V value;



    
    public Entry(@Nullable K key, @Nullable V value) {
      this.key = key;
      this.value = value;
    }

    
    @Nullable
    public K getKey() {
      return this.key;
    }

    
    @Nullable
    public V getValue() {
      return this.value;
    }

    
    @Nullable
    public V setValue(@Nullable V value) {
      V previous = this.value;
      this.value = value;
      return previous;
    }

    
    public String toString() {
      return (new StringBuilder()).append(this.key).append("=").append(this.value).toString();
    }


    
    public final boolean equals(@Nullable Object other) {
      if (this == other) {
        return true;
      }
      if (!(other instanceof Map.Entry)) {
        return false;
      }
      Map.Entry otherEntry = (Map.Entry)other;
      return (ObjectUtils.nullSafeEquals(getKey(), otherEntry.getKey()) && 
        ObjectUtils.nullSafeEquals(getValue(), otherEntry.getValue()));
    }

    
    public final int hashCode() {
      return ObjectUtils.nullSafeHashCode(this.key) ^ ObjectUtils.nullSafeHashCode(this.value);
    }
  }


  
  private abstract class Task<T>
  {
    private final EnumSet<ConcurrentReferenceHashMap.TaskOption> options;


    
    public Task(ConcurrentReferenceHashMap.TaskOption... options) {
      this.options = (options.length == 0) ? EnumSet.<ConcurrentReferenceHashMap.TaskOption>noneOf(ConcurrentReferenceHashMap.TaskOption.class) : EnumSet.<ConcurrentReferenceHashMap.TaskOption>of(options[0], options);
    }
    
    public boolean hasOption(ConcurrentReferenceHashMap.TaskOption option) {
      return this.options.contains(option);
    }








    
    @Nullable
    protected T execute(@Nullable ConcurrentReferenceHashMap.Reference<K, V> ref, @Nullable ConcurrentReferenceHashMap.Entry<K, V> entry, @Nullable ConcurrentReferenceHashMap.Entries<V> entries) {
      return execute(ref, entry);
    }







    
    @Nullable
    protected T execute(@Nullable ConcurrentReferenceHashMap.Reference<K, V> ref, @Nullable ConcurrentReferenceHashMap.Entry<K, V> entry) {
      return null;
    }
  }




  
  private enum TaskOption
  {
    RESTRUCTURE_BEFORE, RESTRUCTURE_AFTER, SKIP_IF_EMPTY, RESIZE;
  }




  
  private static interface Entries<V>
  {
    void add(@Nullable V param1V);
  }



  
  private class EntrySet
    extends AbstractSet<Map.Entry<K, V>>
  {
    private EntrySet() {}



    
    public Iterator<Map.Entry<K, V>> iterator() {
      return new ConcurrentReferenceHashMap.EntryIterator();
    }

    
    public boolean contains(@Nullable Object o) {
      if (o instanceof Map.Entry) {
        Map.Entry<?, ?> entry = (Map.Entry<?, ?>)o;
        ConcurrentReferenceHashMap.Reference<K, V> ref = ConcurrentReferenceHashMap.this.getReference(entry.getKey(), ConcurrentReferenceHashMap.Restructure.NEVER);
        ConcurrentReferenceHashMap.Entry<K, V> otherEntry = (ref != null) ? ref.get() : null;
        if (otherEntry != null) {
          return ObjectUtils.nullSafeEquals(entry.getValue(), otherEntry.getValue());
        }
      } 
      return false;
    }

    
    public boolean remove(Object o) {
      if (o instanceof Map.Entry) {
        Map.Entry<?, ?> entry = (Map.Entry<?, ?>)o;
        return ConcurrentReferenceHashMap.this.remove(entry.getKey(), entry.getValue());
      } 
      return false;
    }

    
    public int size() {
      return ConcurrentReferenceHashMap.this.size();
    }

    
    public void clear() {
      ConcurrentReferenceHashMap.this.clear();
    }
  }


  
  private class EntryIterator
    implements Iterator<Map.Entry<K, V>>
  {
    private int segmentIndex;
    
    private int referenceIndex;
    
    @Nullable
    private ConcurrentReferenceHashMap.Reference<K, V>[] references;
    
    @Nullable
    private ConcurrentReferenceHashMap.Reference<K, V> reference;
    
    @Nullable
    private ConcurrentReferenceHashMap.Entry<K, V> next;
    
    @Nullable
    private ConcurrentReferenceHashMap.Entry<K, V> last;

    
    public EntryIterator() {
      moveToNextSegment();
    }

    
    public boolean hasNext() {
      getNextIfNecessary();
      return (this.next != null);
    }

    
    public ConcurrentReferenceHashMap.Entry<K, V> next() {
      getNextIfNecessary();
      if (this.next == null) {
        throw new NoSuchElementException();
      }
      this.last = this.next;
      this.next = null;
      return this.last;
    }
    
    private void getNextIfNecessary() {
      while (this.next == null) {
        moveToNextReference();
        if (this.reference == null) {
          return;
        }
        this.next = this.reference.get();
      } 
    }
    
    private void moveToNextReference() {
      if (this.reference != null) {
        this.reference = this.reference.getNext();
      }
      while (this.reference == null && this.references != null) {
        if (this.referenceIndex >= this.references.length) {
          moveToNextSegment();
          this.referenceIndex = 0;
          continue;
        } 
        this.reference = this.references[this.referenceIndex];
        this.referenceIndex++;
      } 
    }

    
    private void moveToNextSegment() {
      this.reference = null;
      this.references = null;
      if (this.segmentIndex < ConcurrentReferenceHashMap.this.segments.length) {
        this.references = (ConcurrentReferenceHashMap.this.segments[this.segmentIndex]).references;
        this.segmentIndex++;
      } 
    }

    
    public void remove() {
      Assert.state((this.last != null), "No element to remove");
      ConcurrentReferenceHashMap.this.remove(this.last.getKey());
      this.last = null;
    }
  }




  
  protected enum Restructure
  {
    WHEN_NECESSARY, NEVER;
  }





  
  protected class ReferenceManager
  {
    private final ReferenceQueue<ConcurrentReferenceHashMap.Entry<K, V>> queue = new ReferenceQueue<>();







    
    public ConcurrentReferenceHashMap.Reference<K, V> createReference(ConcurrentReferenceHashMap.Entry<K, V> entry, int hash, @Nullable ConcurrentReferenceHashMap.Reference<K, V> next) {
      if (ConcurrentReferenceHashMap.this.referenceType == ConcurrentReferenceHashMap.ReferenceType.WEAK) {
        return new ConcurrentReferenceHashMap.WeakEntryReference<>(entry, hash, next, this.queue);
      }
      return new ConcurrentReferenceHashMap.SoftEntryReference<>(entry, hash, next, this.queue);
    }








    
    @Nullable
    public ConcurrentReferenceHashMap.Reference<K, V> pollForPurge() {
      return (ConcurrentReferenceHashMap.Reference)this.queue.poll();
    }
  }


  
  private static final class SoftEntryReference<K, V>
    extends SoftReference<Entry<K, V>>
    implements Reference<K, V>
  {
    private final int hash;

    
    @Nullable
    private final ConcurrentReferenceHashMap.Reference<K, V> nextReference;

    
    public SoftEntryReference(ConcurrentReferenceHashMap.Entry<K, V> entry, int hash, @Nullable ConcurrentReferenceHashMap.Reference<K, V> next, ReferenceQueue<ConcurrentReferenceHashMap.Entry<K, V>> queue) {
      super(entry, queue);
      this.hash = hash;
      this.nextReference = next;
    }

    
    public int getHash() {
      return this.hash;
    }

    
    @Nullable
    public ConcurrentReferenceHashMap.Reference<K, V> getNext() {
      return this.nextReference;
    }

    
    public void release() {
      enqueue();
      clear();
    }
  }


  
  private static final class WeakEntryReference<K, V>
    extends WeakReference<Entry<K, V>>
    implements Reference<K, V>
  {
    private final int hash;

    
    @Nullable
    private final ConcurrentReferenceHashMap.Reference<K, V> nextReference;

    
    public WeakEntryReference(ConcurrentReferenceHashMap.Entry<K, V> entry, int hash, @Nullable ConcurrentReferenceHashMap.Reference<K, V> next, ReferenceQueue<ConcurrentReferenceHashMap.Entry<K, V>> queue) {
      super(entry, queue);
      this.hash = hash;
      this.nextReference = next;
    }

    
    public int getHash() {
      return this.hash;
    }

    
    @Nullable
    public ConcurrentReferenceHashMap.Reference<K, V> getNext() {
      return this.nextReference;
    }

    
    public void release() {
      enqueue();
      clear();
    }
  }
}
