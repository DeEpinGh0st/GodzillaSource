package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.math.IntMath;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.concurrent.LazyInit;
import com.google.j2objc.annotations.RetainedWith;
import java.io.Serializable;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Collector;





























@GwtCompatible(serializable = true, emulated = true)
public abstract class ImmutableSet<E>
  extends ImmutableCollection<E>
  implements Set<E>
{
  static final int SPLITERATOR_CHARACTERISTICS = 1297;
  @LazyInit
  @RetainedWith
  private transient ImmutableList<E> asList;
  static final int MAX_TABLE_SIZE = 1073741824;
  private static final double DESIRED_LOAD_FACTOR = 0.7D;
  private static final int CUTOFF = 751619276;
  static final double HASH_FLOODING_FPP = 0.001D;
  static final int MAX_RUN_MULTIPLIER = 13;
  
  public static <E> Collector<E, ?, ImmutableSet<E>> toImmutableSet() {
    return CollectCollectors.toImmutableSet();
  }





  
  public static <E> ImmutableSet<E> of() {
    return RegularImmutableSet.EMPTY;
  }





  
  public static <E> ImmutableSet<E> of(E element) {
    return new SingletonImmutableSet<>(element);
  }





  
  public static <E> ImmutableSet<E> of(E e1, E e2) {
    return construct(2, new Object[] { e1, e2 });
  }





  
  public static <E> ImmutableSet<E> of(E e1, E e2, E e3) {
    return construct(3, new Object[] { e1, e2, e3 });
  }





  
  public static <E> ImmutableSet<E> of(E e1, E e2, E e3, E e4) {
    return construct(4, new Object[] { e1, e2, e3, e4 });
  }





  
  public static <E> ImmutableSet<E> of(E e1, E e2, E e3, E e4, E e5) {
    return construct(5, new Object[] { e1, e2, e3, e4, e5 });
  }









  
  @SafeVarargs
  public static <E> ImmutableSet<E> of(E e1, E e2, E e3, E e4, E e5, E e6, E... others) {
    Preconditions.checkArgument((others.length <= 2147483641), "the total number of elements must fit in an int");
    
    int paramCount = 6;
    Object[] elements = new Object[6 + others.length];
    elements[0] = e1;
    elements[1] = e2;
    elements[2] = e3;
    elements[3] = e4;
    elements[4] = e5;
    elements[5] = e6;
    System.arraycopy(others, 0, elements, 6, others.length);
    return construct(elements.length, elements);
  }













  
  private static <E> ImmutableSet<E> construct(int n, Object... elements) {
    E elem;
    switch (n) {
      case 0:
        return of();
      
      case 1:
        elem = (E)elements[0];
        return of(elem);
    } 
    SetBuilderImpl<E> builder = new RegularSetBuilderImpl<>(4);
    
    for (int i = 0; i < n; i++) {
      
      E e = (E)Preconditions.checkNotNull(elements[i]);
      builder = builder.add(e);
    } 
    return builder.review().build();
  }


















  
  public static <E> ImmutableSet<E> copyOf(Collection<? extends E> elements) {
    if (elements instanceof ImmutableSet && !(elements instanceof java.util.SortedSet)) {
      
      ImmutableSet<E> set = (ImmutableSet)elements;
      if (!set.isPartialView()) {
        return set;
      }
    } else if (elements instanceof EnumSet) {
      return copyOfEnumSet((EnumSet)elements);
    } 
    Object[] array = elements.toArray();
    return construct(array.length, array);
  }












  
  public static <E> ImmutableSet<E> copyOf(Iterable<? extends E> elements) {
    return (elements instanceof Collection) ? 
      copyOf((Collection<? extends E>)elements) : 
      copyOf(elements.iterator());
  }







  
  public static <E> ImmutableSet<E> copyOf(Iterator<? extends E> elements) {
    if (!elements.hasNext()) {
      return of();
    }
    E first = elements.next();
    if (!elements.hasNext()) {
      return of(first);
    }
    return (new Builder<>()).add(first).addAll(elements).build();
  }








  
  public static <E> ImmutableSet<E> copyOf(E[] elements) {
    switch (elements.length) {
      case 0:
        return of();
      case 1:
        return of(elements[0]);
    } 
    return construct(elements.length, (Object[])elements.clone());
  }


  
  private static ImmutableSet copyOfEnumSet(EnumSet<Enum> enumSet) {
    return ImmutableEnumSet.asImmutable(EnumSet.copyOf(enumSet));
  }



  
  boolean isHashCodeFast() {
    return false;
  }

  
  public boolean equals(Object object) {
    if (object == this)
      return true; 
    if (object instanceof ImmutableSet && 
      isHashCodeFast() && ((ImmutableSet)object)
      .isHashCodeFast() && 
      hashCode() != object.hashCode()) {
      return false;
    }
    return Sets.equalsImpl(this, object);
  }

  
  public int hashCode() {
    return Sets.hashCodeImpl(this);
  }








  
  public ImmutableList<E> asList() {
    ImmutableList<E> result = this.asList;
    return (result == null) ? (this.asList = createAsList()) : result;
  }
  
  ImmutableList<E> createAsList() {
    return new RegularImmutableAsList<>(this, toArray());
  }

  
  static abstract class Indexed<E>
    extends ImmutableSet<E>
  {
    public UnmodifiableIterator<E> iterator() {
      return asList().iterator();
    }

    
    public Spliterator<E> spliterator() {
      return CollectSpliterators.indexed(size(), 1297, this::get);
    }

    
    public void forEach(Consumer<? super E> consumer) {
      Preconditions.checkNotNull(consumer);
      int n = size();
      for (int i = 0; i < n; i++) {
        consumer.accept(get(i));
      }
    }

    
    int copyIntoArray(Object[] dst, int offset) {
      return asList().copyIntoArray(dst, offset);
    }

    
    ImmutableList<E> createAsList() {
      return new ImmutableAsList<E>()
        {
          public E get(int index) {
            return ImmutableSet.Indexed.this.get(index);
          }

          
          ImmutableSet.Indexed<E> delegateCollection() {
            return ImmutableSet.Indexed.this;
          }
        };
    }

    
    abstract E get(int param1Int);
  }

  
  private static class SerializedForm
    implements Serializable
  {
    final Object[] elements;
    private static final long serialVersionUID = 0L;
    
    SerializedForm(Object[] elements) {
      this.elements = elements;
    }
    
    Object readResolve() {
      return ImmutableSet.copyOf(this.elements);
    }
  }



  
  Object writeReplace() {
    return new SerializedForm(toArray());
  }




  
  public static <E> Builder<E> builder() {
    return new Builder<>();
  }












  
  @Beta
  public static <E> Builder<E> builderWithExpectedSize(int expectedSize) {
    CollectPreconditions.checkNonnegative(expectedSize, "expectedSize");
    return new Builder<>(expectedSize);
  }

  
  static Object[] rebuildHashTable(int newTableSize, Object[] elements, int n) {
    Object[] hashTable = new Object[newTableSize];
    int mask = hashTable.length - 1;
    for (int i = 0; i < n; ) {
      Object e = elements[i];
      int j0 = Hashing.smear(e.hashCode());
      int j = j0; for (;; i++) {
        int index = j & mask;
        if (hashTable[index] == null) {
          hashTable[index] = e;
        } else {
          j++; continue;
        } 
      } 
    }  return hashTable;
  }






  
  public static class Builder<E>
    extends ImmutableCollection.Builder<E>
  {
    private ImmutableSet.SetBuilderImpl<E> impl;




    
    boolean forceCopy;





    
    public Builder() {
      this(4);
    }
    
    Builder(int capacity) {
      this.impl = new ImmutableSet.RegularSetBuilderImpl<>(capacity);
    }
    
    Builder(boolean subclass) {
      this.impl = null;
    }
    
    @VisibleForTesting
    void forceJdk() {
      this.impl = new ImmutableSet.JdkBackedSetBuilderImpl<>(this.impl);
    }
    
    final void copyIfNecessary() {
      if (this.forceCopy) {
        copy();
        this.forceCopy = false;
      } 
    }
    
    void copy() {
      this.impl = this.impl.copy();
    }

    
    @CanIgnoreReturnValue
    public Builder<E> add(E element) {
      Preconditions.checkNotNull(element);
      copyIfNecessary();
      this.impl = this.impl.add(element);
      return this;
    }

    
    @CanIgnoreReturnValue
    public Builder<E> add(E... elements) {
      super.add(elements);
      return this;
    }









    
    @CanIgnoreReturnValue
    public Builder<E> addAll(Iterable<? extends E> elements) {
      super.addAll(elements);
      return this;
    }

    
    @CanIgnoreReturnValue
    public Builder<E> addAll(Iterator<? extends E> elements) {
      super.addAll(elements);
      return this;
    }
    
    Builder<E> combine(Builder<E> other) {
      copyIfNecessary();
      this.impl = this.impl.combine(other.impl);
      return this;
    }

    
    public ImmutableSet<E> build() {
      this.forceCopy = true;
      this.impl = this.impl.review();
      return this.impl.build();
    }
  }

  
  private static abstract class SetBuilderImpl<E>
  {
    E[] dedupedElements;
    int distinct;
    
    SetBuilderImpl(int expectedCapacity) {
      this.dedupedElements = (E[])new Object[expectedCapacity];
      this.distinct = 0;
    }

    
    SetBuilderImpl(SetBuilderImpl<E> toCopy) {
      this.dedupedElements = Arrays.copyOf(toCopy.dedupedElements, toCopy.dedupedElements.length);
      this.distinct = toCopy.distinct;
    }




    
    private void ensureCapacity(int minCapacity) {
      if (minCapacity > this.dedupedElements.length) {
        
        int newCapacity = ImmutableCollection.Builder.expandedCapacity(this.dedupedElements.length, minCapacity);
        this.dedupedElements = Arrays.copyOf(this.dedupedElements, newCapacity);
      } 
    }

    
    final void addDedupedElement(E e) {
      ensureCapacity(this.distinct + 1);
      this.dedupedElements[this.distinct++] = e;
    }



    
    abstract SetBuilderImpl<E> add(E param1E);


    
    final SetBuilderImpl<E> combine(SetBuilderImpl<E> other) {
      SetBuilderImpl<E> result = this;
      for (int i = 0; i < other.distinct; i++) {
        result = result.add(other.dedupedElements[i]);
      }
      return result;
    }




    
    abstract SetBuilderImpl<E> copy();




    
    SetBuilderImpl<E> review() {
      return this;
    }







    
    abstract ImmutableSet<E> build();
  }







  
  @VisibleForTesting
  static int chooseTableSize(int setSize) {
    setSize = Math.max(setSize, 2);
    
    if (setSize < 751619276) {
      
      int tableSize = Integer.highestOneBit(setSize - 1) << 1;
      while (tableSize * 0.7D < setSize) {
        tableSize <<= 1;
      }
      return tableSize;
    } 

    
    Preconditions.checkArgument((setSize < 1073741824), "collection too large");
    return 1073741824;
  }






































  
  static boolean hashFloodingDetected(Object[] hashTable) {
    int maxRunBeforeFallback = maxRunBeforeFallback(hashTable.length);
    
    int endOfStartRun;
    
    for (endOfStartRun = 0; endOfStartRun < hashTable.length && 
      hashTable[endOfStartRun] != null; ) {

      
      endOfStartRun++;
      if (endOfStartRun > maxRunBeforeFallback) {
        return true;
      }
    } 
    int startOfEndRun;
    for (startOfEndRun = hashTable.length - 1; startOfEndRun > endOfStartRun && 
      hashTable[startOfEndRun] != null; startOfEndRun--) {

      
      if (endOfStartRun + hashTable.length - 1 - startOfEndRun > maxRunBeforeFallback) {
        return true;
      }
    } 


    
    int testBlockSize = maxRunBeforeFallback / 2;
    int i;
    for (i = endOfStartRun + 1; i + testBlockSize <= startOfEndRun; i += testBlockSize) {
      int j = 0; while (true) { if (j < testBlockSize) {
          if (hashTable[i + j] == null)
            break;  j++;
          continue;
        } 
        return true; }
    
    }  return false;
  }





  
  private static int maxRunBeforeFallback(int tableSize) {
    return 13 * IntMath.log2(tableSize, RoundingMode.UNNECESSARY);
  }

  
  public abstract UnmodifiableIterator<E> iterator();

  
  private static final class RegularSetBuilderImpl<E>
    extends SetBuilderImpl<E>
  {
    private Object[] hashTable;
    
    private int maxRunBeforeFallback;
    
    private int expandTableThreshold;
    private int hashCode;
    
    RegularSetBuilderImpl(int expectedCapacity) {
      super(expectedCapacity);
      int tableSize = ImmutableSet.chooseTableSize(expectedCapacity);
      this.hashTable = new Object[tableSize];
      this.maxRunBeforeFallback = ImmutableSet.maxRunBeforeFallback(tableSize);
      this.expandTableThreshold = (int)(0.7D * tableSize);
    }
    
    RegularSetBuilderImpl(RegularSetBuilderImpl<E> toCopy) {
      super(toCopy);
      this.hashTable = Arrays.copyOf(toCopy.hashTable, toCopy.hashTable.length);
      this.maxRunBeforeFallback = toCopy.maxRunBeforeFallback;
      this.expandTableThreshold = toCopy.expandTableThreshold;
      this.hashCode = toCopy.hashCode;
    }
    
    void ensureTableCapacity(int minCapacity) {
      if (minCapacity > this.expandTableThreshold && this.hashTable.length < 1073741824) {
        int newTableSize = this.hashTable.length * 2;
        this.hashTable = ImmutableSet.rebuildHashTable(newTableSize, (Object[])this.dedupedElements, this.distinct);
        this.maxRunBeforeFallback = ImmutableSet.maxRunBeforeFallback(newTableSize);
        this.expandTableThreshold = (int)(0.7D * newTableSize);
      } 
    }

    
    ImmutableSet.SetBuilderImpl<E> add(E e) {
      Preconditions.checkNotNull(e);
      int eHash = e.hashCode();
      int i0 = Hashing.smear(eHash);
      int mask = this.hashTable.length - 1;
      for (int i = i0; i - i0 < this.maxRunBeforeFallback; i++) {
        int index = i & mask;
        Object tableEntry = this.hashTable[index];
        if (tableEntry == null) {
          addDedupedElement(e);
          this.hashTable[index] = e;
          this.hashCode += eHash;
          ensureTableCapacity(this.distinct);
          return this;
        }  if (tableEntry.equals(e)) {
          return this;
        }
      } 
      
      return (new ImmutableSet.JdkBackedSetBuilderImpl<>(this)).add(e);
    }

    
    ImmutableSet.SetBuilderImpl<E> copy() {
      return new RegularSetBuilderImpl(this);
    }

    
    ImmutableSet.SetBuilderImpl<E> review() {
      int targetTableSize = ImmutableSet.chooseTableSize(this.distinct);
      if (targetTableSize * 2 < this.hashTable.length) {
        this.hashTable = ImmutableSet.rebuildHashTable(targetTableSize, (Object[])this.dedupedElements, this.distinct);
      }
      return ImmutableSet.hashFloodingDetected(this.hashTable) ? new ImmutableSet.JdkBackedSetBuilderImpl<>(this) : this;
    }

    
    ImmutableSet<E> build() {
      switch (this.distinct) {
        case 0:
          return ImmutableSet.of();
        case 1:
          return ImmutableSet.of(this.dedupedElements[0]);
      } 


      
      Object[] elements = (this.distinct == this.dedupedElements.length) ? (Object[])this.dedupedElements : Arrays.<Object>copyOf((Object[])this.dedupedElements, this.distinct);
      return new RegularImmutableSet<>(elements, this.hashCode, this.hashTable, this.hashTable.length - 1);
    }
  }

  
  private static final class JdkBackedSetBuilderImpl<E>
    extends SetBuilderImpl<E>
  {
    private final Set<Object> delegate;

    
    JdkBackedSetBuilderImpl(ImmutableSet.SetBuilderImpl<E> toCopy) {
      super(toCopy);
      this.delegate = Sets.newHashSetWithExpectedSize(this.distinct);
      for (int i = 0; i < this.distinct; i++) {
        this.delegate.add(this.dedupedElements[i]);
      }
    }

    
    ImmutableSet.SetBuilderImpl<E> add(E e) {
      Preconditions.checkNotNull(e);
      if (this.delegate.add(e)) {
        addDedupedElement(e);
      }
      return this;
    }

    
    ImmutableSet.SetBuilderImpl<E> copy() {
      return new JdkBackedSetBuilderImpl(this);
    }

    
    ImmutableSet<E> build() {
      switch (this.distinct) {
        case 0:
          return ImmutableSet.of();
        case 1:
          return ImmutableSet.of(this.dedupedElements[0]);
      } 
      return new JdkBackedImmutableSet<>(this.delegate, 
          ImmutableList.asImmutableList((Object[])this.dedupedElements, this.distinct));
    }
  }
}
